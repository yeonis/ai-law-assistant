package com.study.ailawassistant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class LawImportServiceTest {

    @Autowired
    private LawImportService lawImportService;

    @Test
    void importLawTest() {
        lawImportService.importLaw("288525");
    }
}