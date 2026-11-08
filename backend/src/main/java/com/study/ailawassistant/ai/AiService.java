package com.study.ailawassistant.ai;

import com.study.ailawassistant.dto.*;
import com.study.ailawassistant.entity.LawArticle;
import com.study.ailawassistant.repository.LawArticleRepository;
import com.study.ailawassistant.repository.LawArticleSearchRepository;
import com.study.ailawassistant.repository.SearchMode;
import com.study.ailawassistant.service.LawArticleService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final LawArticleSearchRepository lawArticleSearchRepository;
    private final AiRerankService aiRerankService;
    private final LawArticleRepository lawArticleRepository;
    private final AiAnswerService aiAnswerService;

    public AiService(
            @Qualifier("lawArticleSearchRepositoryImpl")
            LawArticleSearchRepository lawArticleSearchRepository,
            AiRerankService aiRerankService,
            LawArticleRepository lawArticleRepository,
            AiAnswerService aiAnswerService
    ) {
        this.lawArticleSearchRepository = lawArticleSearchRepository;
        this.aiRerankService = aiRerankService;
        this.lawArticleRepository = lawArticleRepository;
        this.aiAnswerService = aiAnswerService;
    }

    public AiAnswerResponse findRelevantArticles(AiQuestionRequest request) {

        // 1. 질문에서 검색 키워드 추출
        List<String> keywords = Arrays.stream(
                        request.question().trim().split("\\s+")
                )
                .map(word -> word.replaceAll("[?!.]", ""))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOP_WORDS.contains(word))
                .toList();

        // 2. 검색 기준일 설정
        LocalDate searchDate = request.date() != null
                ? request.date()
                : LocalDate.now();

        System.out.println("AI 검색 키워드: " + keywords);
        System.out.println("AI 검색 기준일: " + searchDate);

        // 3. 관련 조문 검색 (최대 5개)
        Page<LawArticleDto> result = lawArticleSearchRepository.searchByKeywords(
                keywords,
                searchDate,
                PageRequest.of(0, 20),
                SearchMode.OR
        );

        // 4. 검색 결과 목록 추출
        List<LawArticleDto> articles = result.getContent();

        // 5. AI 후보 조문 DTO로 변환
        List<AiArticleCandidateDto> candidates = articles.stream()
                .map(article -> {

                    String content = article.articleContent();

                    String preview = content.length() > 200
                            ? content.substring(0, 200) + "..."
                            : content;

                    return new AiArticleCandidateDto(
                            article.articleId(),
                            article.lawName(),
                            article.articleTitle(),
                            preview
                    );
                })
                .toList();

        // 1. AI가 선택한 조문 ID 받기
        AiRerankResponse rerankResult =
                aiRerankService.rerank(request.question(), candidates);

        // 2. 선택된 ID 목록 추출
        List<Long> selectedIds = rerankResult.selectedArticleIds();

        // 3. ID를 이용해 DB에서 조문 전문 조회
        List<LawArticle> selectedArticles =
                lawArticleRepository.findAllById(selectedIds);

        Map<Long, LawArticle> articleMap = selectedArticles.stream()
                .collect(Collectors.toMap(
                        LawArticle::getArticleId,
                        article -> article
                ));

        List<LawArticle> orderedArticles = selectedIds.stream()
                .map(articleMap::get)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, AiArticleCandidateDto> candidateMap =
                candidates.stream()
                        .collect(Collectors.toMap(
                                AiArticleCandidateDto::articleId,
                                candidate -> candidate
                        ));

        List<AiArticleReferenceDto> references = orderedArticles.stream()
                .map(article -> {
                    AiArticleCandidateDto candidate =
                            candidateMap.get(article.getArticleId());

                    return new AiArticleReferenceDto(
                            article.getArticleId(),
                            candidate.lawName(),
                            article.getArticleNo(),
                            article.getArticleTitle(),
                            article.getEffectiveDate()
                    );
                })
                .toList();

        String answer = aiAnswerService.generateAnswer(
                request.question(),
                orderedArticles
        );

        List<Long> finalIds = orderedArticles.stream()
                .map(LawArticle::getArticleId)
                .toList();

        return new AiAnswerResponse(
                answer,
                searchDate,
                finalIds,
                references
        );
    }

    private static final Set<String> STOP_WORDS = Set.of(
            "FTA", "어떤", "무엇", "대한", "관련",
            "궁금해요", "알려줘", "알려주세요",
            "있나요", "인가요", "조건이"
    );
}