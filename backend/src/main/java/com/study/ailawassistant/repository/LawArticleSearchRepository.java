package com.study.ailawassistant.repository;

import com.study.ailawassistant.dto.LawArticleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LawArticleSearchRepository {

    Page<LawArticleDto> searchByKeywords(
            List<String> keywords,
            LocalDate searchDate,
            Pageable pageable
    );
}