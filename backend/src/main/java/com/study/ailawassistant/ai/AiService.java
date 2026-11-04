package com.study.ailawassistant.ai;

import com.study.ailawassistant.dto.AiArticleCandidateDto;
import com.study.ailawassistant.dto.AiQuestionRequest;
import com.study.ailawassistant.dto.AiRerankResponse;
import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.repository.LawArticleRepository;
import com.study.ailawassistant.repository.LawArticleSearchRepository;
import com.study.ailawassistant.repository.SearchMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class AiService {

    private final LawArticleSearchRepository lawArticleSearchRepository;
    private final AiRerankService aiRerankService;

    public AiService(
            @Qualifier("lawArticleSearchRepositoryImpl")
            LawArticleSearchRepository lawArticleSearchRepository,
            AiRerankService aiRerankService
    ) {
        this.lawArticleSearchRepository = lawArticleSearchRepository;
        this.aiRerankService = aiRerankService;
    }

    public AiRerankResponse findRelevantArticles(AiQuestionRequest request) {

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

        // 6. 변환된 후보 목록 반환
        return aiRerankService.rerank(request.question(), candidates);
    }

    private static final Set<String> STOP_WORDS = Set.of(
            "FTA", "어떤", "무엇", "대한", "관련",
            "궁금해요", "알려줘", "알려주세요",
            "있나요", "인가요", "조건이"
    );
}