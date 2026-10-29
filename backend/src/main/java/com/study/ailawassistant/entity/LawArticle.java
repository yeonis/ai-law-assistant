package com.study.ailawassistant.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "law_article")
public class LawArticle {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "law_article_seq"
    )
    @SequenceGenerator(
            name = "law_article_seq",
            sequenceName = "law_article_id_seq",
            allocationSize = 1
    )
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


    public LawArticle( //외부 API에서 받은 데이터를 새로운 LawArticle 객체로 만들어 DB에 저장
            Long lawSerialNo,
            Integer articleNo,
            Integer articleBranchNo,
            String articleKey,
            String articleTitle,
            String articleContent,
            LocalDate effectiveDate
    ) {
        this.lawSerialNo = lawSerialNo;
        this.articleNo = articleNo;
        this.articleBranchNo = articleBranchNo;
        this.articleKey = articleKey;
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
        this.effectiveDate = effectiveDate;
    }
}
