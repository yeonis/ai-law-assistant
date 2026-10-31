package com.study.ailawassistant.dto;

public record LawArticleCompareDto(
        LawArticleDto oldArticle,
        LawArticleDto newArticle
) {

}
