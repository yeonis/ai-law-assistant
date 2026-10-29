package com.study.ailawassistant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "law")
public class Law {
    @Id
    @Column(name = "law_id")
    private Long lawId;

    @Column(name = "law_name")
    private String lawName;

    @Column(name = "law_type")
    private String lawType;

    @Column(name = "law_short_name")
    private String lawShortName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected Law() { //생성자, 매개변수없음, jpa가 사용할 수 있도록 허용하면서 외부 접근 제한
    }
    public Long getLawId() {
        return lawId;
    }

    public String getLawName() {
        return lawName;
    }

    public String getLawType() {
        return lawType;
    }

    public Law(
            Long lawId,
            String lawName,
            String lawType,
            String lawShortName
    ) {
        this.lawId = lawId;
        this.lawName = lawName;
        this.lawType = lawType;
        this.lawShortName = lawShortName;
        this.createdAt = LocalDateTime.now();
    }
}
