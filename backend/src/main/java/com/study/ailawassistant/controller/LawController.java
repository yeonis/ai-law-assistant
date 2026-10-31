package com.study.ailawassistant.controller;

import com.study.ailawassistant.dto.LawArticleDto;
import com.study.ailawassistant.dto.LawDto;
import com.study.ailawassistant.dto.LawVersionDto;
import com.study.ailawassistant.service.LawArticleService;
import com.study.ailawassistant.service.LawService;
import com.study.ailawassistant.service.LawVersionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins ="http://localhost:5173")
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
    public Page<LawArticleDto> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate date
    ) {
        return lawArticleService.searchArticles(keyword, page, size, date);
    }
}
