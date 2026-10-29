package com.study.ailawassistant.service;

import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.repository.LawArticleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class LawArticleService {

    private final LawArticleRepository lawArticleRepository;

    public LawArticleService(LawArticleRepository lawArticleRepository) {
        this.lawArticleRepository = lawArticleRepository;
    }

    public List<LawArticleDto> getArticlesByLawSerialNo(Long lawSerialNo) {
        return lawArticleRepository
                .findByLawSerialNoOrderByArticleNoAscArticleBranchNoAsc(lawSerialNo)
                .stream()
                .map(article -> new LawArticleDto(
                        article.getArticleId(),
                        article.getLawSerialNo(),
                        article.getArticleNo(),
                        article.getArticleBranchNo(),
                        article.getArticleTitle(),
                        article.getArticleContent(),
                        null
                ))
                .toList();
    }

    public Page<LawArticleDto> searchArticles(
            String keyword,
            int page,
            int size
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "검색어를 입력해주세요."
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "올바르지 않은 페이지 요청입니다."
            );
        }

        keyword = keyword.strip();

        Pageable pageable = PageRequest.of(page, size);

        return lawArticleRepository.searchArticlesWithLawName(
                keyword,
                pageable
        );
    }
}
