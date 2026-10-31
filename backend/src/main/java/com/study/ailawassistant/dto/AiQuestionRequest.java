package com.study.ailawassistant.dto;

import java.time.LocalDate;

public record AiQuestionRequest(
        String question,
        LocalDate date
) {
}