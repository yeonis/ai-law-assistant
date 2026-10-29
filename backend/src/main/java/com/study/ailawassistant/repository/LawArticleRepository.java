package com.study.ailawassistant.repository;

import com.study.ailawassistant.entity.LawArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import com.study.ailawassistant.dto.LawArticleDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LawArticleRepository extends JpaRepository<LawArticle,Long> {
    List<LawArticle> findByLawSerialNoOrderByArticleNoAscArticleBranchNoAsc(Long lawSerialNo);
    List<LawArticle> findByArticleTitleContainingOrArticleContentContaining(String titleKeyword, String contentKeyword);

    @Query(
    value = """
    SELECT new com.study.ailawassistant.dto.LawArticleDto(
        a.articleId,
        a.lawSerialNo,
        a.articleNo,
        a.articleBranchNo,
        a.articleTitle,
        a.articleContent,
        l.lawName
    )
    FROM LawArticle a
    JOIN LawVersion v ON a.lawSerialNo = v.lawSerialNo
    JOIN Law l ON v.lawId = l.lawId
    WHERE a.articleTitle LIKE CONCAT('%', :keyword, '%')
       OR a.articleContent LIKE CONCAT('%', :keyword, '%')
    ORDER BY
        CASE
            WHEN a.articleTitle LIKE CONCAT('%', :keyword, '%') THEN 0
            ELSE 1
        END ASC,
        CASE
            WHEN l.lawName = '관세법' THEN 1
            WHEN l.lawName = '관세법 시행령' THEN 2
            WHEN l.lawName = '관세법 시행규칙' THEN 3
            ELSE 4
        END,
        a.articleNo ASC,
        a.articleBranchNo ASC
    """,
    countQuery = """
            SELECT COUNT(a)
            FROM LawArticle a
            JOIN LawVersion v ON a.lawSerialNo = v.lawSerialNo
            JOIN Law l ON v.lawId = l.lawId
            WHERE a.articleTitle LIKE CONCAT('%', :keyword, '%')
               OR a.articleContent LIKE CONCAT('%', :keyword, '%')
            """)
    Page<LawArticleDto> searchArticlesWithLawName(
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
