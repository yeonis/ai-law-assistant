package com.study.ailawassistant.dto;

import java.time.LocalDate;

public record LawVersionDto(
        Long lawSerialNo,
        Long lawId,
        LocalDate promulgationDate,
        LocalDate effectiveDate
) {
}
