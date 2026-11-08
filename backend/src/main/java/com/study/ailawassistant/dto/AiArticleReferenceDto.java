package com.study.ailawassistant.dto;

import java.time.LocalDate;

public record AiArticleReferenceDto(
        Long articleId,
        String lawName,
        Integer articleNo,
        String articleTitle,
        LocalDate effectiveDate
) {
}