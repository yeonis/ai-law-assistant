package com.study.ailawassistant.ai;

import com.study.ailawassistant.dto.AiQuestionRequest;
import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.repository.LawArticleRepository;
import com.study.ailawassistant.repository.LawArticleSearchRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class AiService {

    private final LawArticleSearchRepository lawArticleSearchRepository;

    public AiService(
            @Qualifier("lawArticleSearchRepositoryImpl")
            LawArticleSearchRepository lawArticleSearchRepository
    ) {
        this.lawArticleSearchRepository = lawArticleSearchRepository;
    }

    public List<LawArticleDto> findRelevantArticles(AiQuestionRequest request) {

        // 1. 질문에서 검색 키워드 추출
        List<String> keywords = Arrays.stream(request.question().trim().split("\\s+"))
                .filter(word -> !word.isBlank())
                .toList();

        // 2. 검색 기준일 설정
        LocalDate searchDate = request.date() != null
                ? request.date()
                : LocalDate.now();

        // 3. 관련 조문 검색 (최대 5개)
        Page<LawArticleDto> result = lawArticleSearchRepository.searchByKeywords(
                keywords,
                searchDate,
                PageRequest.of(0, 5)
        );

        // 4. 검색 결과 반환
        return result.getContent();
    }
}