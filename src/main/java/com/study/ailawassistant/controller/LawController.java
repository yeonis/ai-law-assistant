package com.study.ailawassistant.controller;

import com.study.ailawassistant.dto.LawDto;
import com.study.ailawassistant.service.LawService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class LawController {

    private final LawService lawService;

    public LawController(LawService lawService){
        this.lawService = lawService;
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
}
