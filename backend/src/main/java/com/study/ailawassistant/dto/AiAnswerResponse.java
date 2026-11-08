package com.study.ailawassistant.dto;

import java.util.List;
import java.time.LocalDate;
public record AiAnswerResponse(
        String answer,
        LocalDate referenceDate,
        List<Long> selectedArticleIds,
        List<AiArticleReferenceDto> references
) {
}