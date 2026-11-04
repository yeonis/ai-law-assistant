package com.study.ailawassistant.dto;

import java.util.List;

public record AiRerankResponse(
        List<Long> selectedArticleIds
) {
}