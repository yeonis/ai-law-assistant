package com.study.ailawassistant.controller;

import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.dto.LawDto;
import com.study.ailawassistant.dto.LawVersionDto;
import com.study.ailawassistant.service.LawArticleService;
import com.study.ailawassistant.service.LawService;
import com.study.ailawassistant.service.LawVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class LawController {

    private final LawService lawService;
    private final LawVersionService lawVersionService;
    private final LawArticleService lawArticleService;


    public LawController(LawService lawService, LawVersionService lawVersionService,
                         LawArticleService lawArticleService) {
        this.lawService = lawService;
        this.lawVersionService = lawVersionService;
        this.lawArticleService = lawArticleService;
    }

    @GetMapping("/api/laws")
    public List<LawDto> getLaws() {
        return lawService.getLaws();
    }

    @GetMapping("/api/laws/{id}")
    public ResponseEntity<LawDto> getLawById(
            @PathVariable("id") Long id) {

        return lawService.getLawById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/laws/{id}/versions")
    public List<LawVersionDto> getLawVersions(@PathVariable Long id){
        return lawVersionService.getVersionsByLawId(id);
    }

    @GetMapping("/api/laws/{id}/versions/{lawSerialNo}/articles")
    public List<LawArticleDto> getLawArticles(
            @PathVariable Long id,
            @PathVariable Long lawSerialNo
    ) {
        return lawArticleService.getArticlesByLawSerialNo(lawSerialNo);
    }

    @GetMapping("/api/laws/articles/search")
    public List<LawArticleDto> searchArticles(
            @RequestParam String keyword
    ) {
        return lawArticleService.searchArticles(keyword);
    }
}
