package com.study.ailawassistant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "law_article")
public class LawArticle {
    @Id
    @Column(name="article_id")
    private Long articleId;

    @Column(name="law_serial_no")
    private Long lawSerialNo;

    @Column(name="article_title")
    private String articleTitle;

    @Column(name="article_content")
    private String articleContent;

    @Column(name="effective_date")
    private LocalDate effectiveDate;

    @Column(name="article_no")
    private Integer articleNo;

    @Column(name="article_branch_no")
    private Integer articleBranchNo;

    @Column(name="article_key")
    private String articleKey;

    protected LawArticle(){}

    public Long getLawSerialNo() {
        return lawSerialNo;
    }

    public Long getArticleId() {
        return articleId;
    }

    public Integer getArticleNo() {
        return articleNo;
    }

    public Integer getArticleBranchNo() {
        return articleBranchNo;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleContent() {
        return articleContent;
    }
}
