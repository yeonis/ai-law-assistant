package com.study.ailawassistant.dto;

public record LawArticleDto(
    Long articleId,
    Long lawSerialNo,
    Integer articleNo,
    Integer articleBranchNo,
    String articleTitle,
    String articleContent
){
}
