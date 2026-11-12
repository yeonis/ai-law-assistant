package com.study.ailawassistant.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(properties = {
        "openai.api.key=test-key"
})
class LawHistoryImportServiceTest {

    @Autowired
    private LawHistoryImportService lawHistoryImportService;

    @Test
    void importHistoryTest() {

        List<String> mstList = List.of(
                "276067",
                "269801",
                "268725",
                "267541",
                "257969",
                "248479",
                "247427",
                "244623",
                "237871",
                "237107",
                "224985",
                "224813",
                "224469",
                "224305",
                "219567"
        );

        lawHistoryImportService.importHistory(mstList);
    }
}