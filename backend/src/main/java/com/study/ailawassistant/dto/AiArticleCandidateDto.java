package com.study.ailawassistant.dto;

public record AiArticleCandidateDto(
        Long articleId,
        String lawName,
        String articleTitle,
        String contentPreview
) {
}