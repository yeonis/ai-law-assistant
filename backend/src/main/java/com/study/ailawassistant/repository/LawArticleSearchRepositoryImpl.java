package com.study.ailawassistant.repository;


import com.study.ailawassistant.dto.LawArticleDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.study.ailawassistant.entity.LawArticle;
import com.study.ailawassistant.entity.LawVersion;
import com.study.ailawassistant.entity.Law;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;

@Repository
public class LawArticleSearchRepositoryImpl
        implements LawArticleSearchRepository {

    private final EntityManager entityManager;

    public LawArticleSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<LawArticleDto> searchByKeywords(
            List<String> keywords,
            LocalDate searchDate,
            Pageable pageable,
            SearchMode searchMode
    ) {

        System.out.println("Repository 검색 모드: " + searchMode);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<LawArticleDto> query =
                cb.createQuery(LawArticleDto.class);

        Root<LawArticle> article = query.from(LawArticle.class);
        Root<LawVersion> version = query.from(LawVersion.class);
        Root<Law> law = query.from(Law.class);

        //검색 조건 담기
        List<Predicate> conditions = new ArrayList<>();

        //법령 테이블 연결 조건
        conditions.add(
                cb.equal(article.get("lawSerialNo"), version.get("lawSerialNo"))
        );

        conditions.add(
                cb.equal(version.get("lawId"), law.get("lawId"))
        );


        // 1. 기준일까지 시행된 버전만 검색
        conditions.add(
                cb.lessThanOrEqualTo(
                        version.<LocalDate>get("effectiveDate"),
                        searchDate
                )
        );

        // 2. 나보다 더 최근에 시행된 버전이 있는지 확인
        Subquery<Integer> subquery = query.subquery(Integer.class);

        Root<LawVersion> newer = subquery.from(LawVersion.class);

        subquery.select(cb.literal(1));

        subquery.where(
                cb.equal(
                        newer.get("lawId"),
                        version.get("lawId")
                ),
                cb.lessThanOrEqualTo(
                        newer.<LocalDate>get("effectiveDate"),
                        searchDate
                ),
                cb.or(
                        cb.greaterThan(
                                newer.<LocalDate>get("effectiveDate"),
                                version.<LocalDate>get("effectiveDate")
                        ),
                        cb.and(
                                cb.equal(
                                        newer.get("effectiveDate"),
                                        version.get("effectiveDate")
                                ),
                                cb.greaterThan(
                                        newer.<Long>get("lawSerialNo"),
                                        version.<Long>get("lawSerialNo")
                                )
                        )
                )
        );

        // 3. 더 최근 버전이 없는 경우만 통과
        conditions.add(cb.not(cb.exists(subquery)));

        // 키워드별 검색 조건
        List<Predicate> keywordConditions = new ArrayList<>();

        for (String keyword : keywords) {

            String pattern = createLikePattern(keyword);

            Predicate titleMatch = cb.like(
                    article.get("articleTitle"),
                    pattern,
                    '\\'
            );

            Predicate contentMatch = cb.like(
                    article.get("articleContent"),
                    pattern,
                    '\\'
            );

            keywordConditions.add(
                    cb.or(titleMatch, contentMatch)
            );
        }

        // 검색 방식에 따라 AND 또는 OR 적용
        if (!keywordConditions.isEmpty()) {

            Predicate keywordPredicate =
                    searchMode == SearchMode.AND
                            ? cb.and(keywordConditions.toArray(new Predicate[0]))
                            : cb.or(keywordConditions.toArray(new Predicate[0]));

            conditions.add(keywordPredicate);
        }

        query.where(cb.and(conditions.toArray(new Predicate[0])));

        query.select(
                cb.construct(
                        LawArticleDto.class,
                        article.get("articleId"),
                        article.get("lawSerialNo"),
                        article.get("articleNo"),
                        article.get("articleBranchNo"),
                        article.get("articleTitle"),
                        article.get("articleContent"),
                        law.get("lawName")
                )
        );

        List<Predicate> titleConditions = new ArrayList<>();

        for (String keyword : keywords) {
            titleConditions.add(
                    cb.like(
                            article.get("articleTitle"),
                            createLikePattern(keyword),
                            '\\'
                    )
            );
        }

        Expression<Integer> titlePriority = cb.<Integer>selectCase()
                .when(
                        searchMode == SearchMode.AND
                                ? cb.and(titleConditions.toArray(new Predicate[0]))
                                : cb.or(titleConditions.toArray(new Predicate[0])),
                        0
                )
                .otherwise(1);

        Expression<Integer> totalScore = cb.literal(0);

        for (String keyword : keywords) {

            String pattern = createLikePattern(keyword);

            Predicate titleMatch = cb.like(
                    article.get("articleTitle"),
                    pattern,
                    '\\'
            );

            Predicate contentMatch = cb.like(
                    article.get("articleContent"),
                    pattern,
                    '\\'
            );

            // 제목 점수: 3점
            Expression<Integer> titleScore = cb.<Integer>selectCase()
                    .when(titleMatch, 3)
                    .otherwise(0);

            // 본문 점수: 1점
            Expression<Integer> contentScore = cb.<Integer>selectCase()
                    .when(contentMatch, 1)
                    .otherwise(0);

            totalScore = cb.sum(totalScore, cb.sum(titleScore, contentScore));
        }

        Expression<Integer> lawPriority = cb.<Integer>selectCase()
                .when(cb.equal(law.get("lawName"), "관세법"), 1)
                .when(cb.equal(law.get("lawName"), "관세법 시행령"), 2)
                .when(cb.equal(law.get("lawName"), "관세법 시행규칙"), 3)
                .otherwise(4);

        if (searchMode == SearchMode.OR) {
            query.orderBy(
                    cb.desc(totalScore),
                    cb.asc(lawPriority),
                    cb.asc(article.get("articleNo")),
                    cb.asc(article.get("articleBranchNo")),
                    cb.asc(article.get("articleId"))
            );
        } else {
            query.orderBy(
                    cb.asc(titlePriority),
                    cb.asc(lawPriority),
                    cb.asc(article.get("articleNo")),
                    cb.asc(article.get("articleBranchNo")),
                    cb.asc(article.get("articleId"))
            );
        }

        TypedQuery<LawArticleDto> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult(
                Math.toIntExact(pageable.getOffset())
        );

        typedQuery.setMaxResults(pageable.getPageSize());

        List<LawArticleDto> results = typedQuery.getResultList();

        System.out.println("검색 결과 건수: " + results.size());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        Root<LawArticle> countArticle = countQuery.from(LawArticle.class);
        Root<LawVersion> countVersion = countQuery.from(LawVersion.class);
        Root<Law> countLaw = countQuery.from(Law.class);

        List<Predicate> countConditions = new ArrayList<>();

        countConditions.add(
                cb.equal(
                        countArticle.get("lawSerialNo"),
                        countVersion.get("lawSerialNo")
                )
        );

        countConditions.add(
                cb.equal(
                        countVersion.get("lawId"),
                        countLaw.get("lawId")
                )
        );

        // 1. 기준일까지 시행된 버전만 검색
        countConditions.add(
                cb.lessThanOrEqualTo(
                        countVersion.<LocalDate>get("effectiveDate"),
                        searchDate
                )
        );

        // 2. 나보다 더 최근에 시행된 버전이 있는지 확인
        Subquery<Integer> countSubquery = countQuery.subquery(Integer.class);

        Root<LawVersion> countNewer = countSubquery.from(LawVersion.class);

        countSubquery.select(cb.literal(1));

        countSubquery.where(
                cb.equal(
                        countNewer.get("lawId"),
                        countVersion.get("lawId")
                ),
                cb.lessThanOrEqualTo(
                        countNewer.<LocalDate>get("effectiveDate"),
                        searchDate
                ),
                cb.or(
                        cb.greaterThan(
                                countNewer.<LocalDate>get("effectiveDate"),
                                countVersion.<LocalDate>get("effectiveDate")
                        ),
                        cb.and(
                                cb.equal(
                                        countNewer.get("effectiveDate"),
                                        countVersion.get("effectiveDate")
                                ),
                                cb.greaterThan(
                                        countNewer.<Long>get("lawSerialNo"),
                                        countVersion.<Long>get("lawSerialNo")
                                )
                        )
                )
        );

        // 3. 더 최근 버전이 없는 경우만 통과햐
        countConditions.add(cb.not(cb.exists(countSubquery)));

        List<Predicate> countKeywordConditions = new ArrayList<>();

        for (String keyword : keywords) {

            String pattern = createLikePattern(keyword);

            Predicate titleMatch = cb.like(
                    countArticle.get("articleTitle"),
                    pattern,
                    '\\'
            );

            Predicate contentMatch = cb.like(
                    countArticle.get("articleContent"),
                    pattern,
                    '\\'
            );

            countKeywordConditions.add(
                    cb.or(titleMatch, contentMatch)
            );
        }

        if (!countKeywordConditions.isEmpty()) {

            Predicate countKeywordPredicate =
                    searchMode == SearchMode.AND
                            ? cb.and(countKeywordConditions.toArray(new Predicate[0]))
                            : cb.or(countKeywordConditions.toArray(new Predicate[0]));

            countConditions.add(countKeywordPredicate);
        }

        countQuery.select(cb.count(countArticle));

        countQuery.where(
                cb.and(countConditions.toArray(new Predicate[0]))
        );

        Long totalCount = entityManager
                .createQuery(countQuery)
                .getSingleResult();

        return new PageImpl<>(results, pageable, totalCount);
    }


    private String createLikePattern(String keyword) {
        String escapedKeyword = keyword
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");

        return "%" + escapedKeyword + "%";
    }
}
