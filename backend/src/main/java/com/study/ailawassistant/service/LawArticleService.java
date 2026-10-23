package com.study.ailawassistant.service;

import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.repository.LawArticleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
                        article.getArticleContent()
                ))
                .toList();
    }

    public List<LawArticleDto> searchArticles(String keyword)  {

        if(keyword == null || keyword.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요.");
        }

        keyword = keyword.strip();

        return lawArticleRepository
                .findByArticleTitleContainingOrArticleContentContaining(keyword,keyword)
                .stream()
                .map(article -> new LawArticleDto(
                        article.getArticleId(),
                        article.getLawSerialNo(),
                        article.getArticleNo(),
                        article.getArticleBranchNo(),
                        article.getArticleTitle(),
                        article.getArticleContent()
                ))
                .toList();
    }
}
