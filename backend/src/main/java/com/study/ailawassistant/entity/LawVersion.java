package com.study.ailawassistant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name="law_version")
public class LawVersion {
    @Id
    @Column(name="law_serial_no")
    private Long lawSerialNo;

    @Column(name="law_id")
    private Long lawId;

    @Column(name="promulgation_date")
    private LocalDate promulgationDate;

    @Column(name="effective_date")
    private LocalDate effectiveDate;

    @Column(name="promulgation_no")
    private Integer promulgationNo;

    @Column(name="revision_type")
    private String revisionType;

    protected LawVersion() {
    }

    public Long getLawSerialNo() {
        return lawSerialNo;
    }

    public Long getLawId() {
        return lawId;
    }

    public LocalDate getPromulgationDate() {
        return promulgationDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public LawVersion(
            Long lawSerialNo,
            Long lawId,
            LocalDate promulgationDate,
            LocalDate effectiveDate,
            Integer promulgationNo,
            String revisionType
    ) {
        this.lawSerialNo = lawSerialNo;
        this.lawId = lawId;
        this.promulgationDate = promulgationDate;
        this.effectiveDate = effectiveDate;
        this.promulgationNo = promulgationNo;
        this.revisionType = revisionType;
    }
}
