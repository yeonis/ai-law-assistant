package com.study.ailawassistant.service;

import com.study.ailawassistant.dto.LawArticleCompareDto;
import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.entity.LawArticle;
import com.study.ailawassistant.entity.LawVersion;
import com.study.ailawassistant.repository.LawArticleRepository;
import com.study.ailawassistant.repository.LawVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class LawArticleService {

    private final LawArticleRepository lawArticleRepository;
    private final LawVersionRepository lawVersionRepository;

    public LawArticleService(
            LawArticleRepository lawArticleRepository,
            LawVersionRepository lawVersionRepository
    ) {
        this.lawArticleRepository = lawArticleRepository;
        this.lawVersionRepository = lawVersionRepository;
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
            int size,
            LocalDate date
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

        List<String> keywords = Arrays.asList(keyword.split("\\s+"));

        Pageable pageable = PageRequest.of(page, size);

        LocalDate searchDate = date != null
                ? date
                : LocalDate.now(ZoneId.of("Asia/Seoul"));

        return lawArticleRepository.searchByKeywords(
                keywords,
                searchDate,
                pageable
        );
    }
    public LawArticleCompareDto compareArticles(
            Long oldMst,
            Long newMst,
            Integer articleNo,
            Integer branchNo
    ) {

        LawVersion oldVersion = lawVersionRepository.findById(oldMst)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "과거 법령 버전을 찾을 수 없습니다."
                ));

        LawVersion newVersion = lawVersionRepository.findById(newMst)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "비교 대상 법령 버전을 찾을 수 없습니다."
                ));

        if (!oldVersion.getLawId().equals(newVersion.getLawId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "서로 다른 법령은 비교할 수 없습니다."
            );
        }

        LawArticle oldArticle = lawArticleRepository
                .findByLawSerialNoAndArticleNoAndArticleBranchNo(
                        oldMst,
                        articleNo,
                        branchNo
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "과거 버전의 조문을 찾을 수 없습니다."
                ));

        LawArticle newArticle = lawArticleRepository
                .findByLawSerialNoAndArticleNoAndArticleBranchNo(
                        newMst,
                        articleNo,
                        branchNo
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "비교 대상 버전의 조문을 찾을 수 없습니다."
                ));

        LawArticleDto oldDto = toDto(oldArticle);
        LawArticleDto newDto = toDto(newArticle);

        return new LawArticleCompareDto(oldDto, newDto);
    }

    private LawArticleDto toDto(LawArticle article) {
        return new LawArticleDto(
                article.getArticleId(),
                article.getLawSerialNo(),
                article.getArticleNo(),
                article.getArticleBranchNo(),
                article.getArticleTitle(),
                article.getArticleContent(),
                null
        );
    }

    public LawVersion getPreviousVersion(Long currentMst) {

        LawVersion currentVersion = lawVersionRepository.findById(currentMst)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "현재 법령 버전을 찾을 수 없습니다."
                ));

        return lawVersionRepository
                .findFirstByLawIdAndEffectiveDateLessThanOrderByEffectiveDateDescLawSerialNoDesc(
                        currentVersion.getLawId(),
                        currentVersion.getEffectiveDate()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "이전 법령 버전이 없습니다."
                ));
    }

    public LawArticleCompareDto compareWithPrevious(
            Long currentMst,
            Integer articleNo,
            Integer branchNo
    ) {
        // 현재 버전의 바로 이전 버전 조회
        LawVersion previousVersion = getPreviousVersion(currentMst);

        // 기존 비교 로직 재사용
        return compareArticles(
                previousVersion.getLawSerialNo(),
                currentMst,
                articleNo,
                branchNo
        );
    }

}
