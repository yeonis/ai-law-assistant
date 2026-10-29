
package com.study.ailawassistant.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LawApiClientTest {

    @Autowired
    private LawApiClient lawApiClient;

    @Test
    void getLawDetailTest() throws Exception {

        String mst = "290673";

        String result = lawApiClient.getLawDetail(mst);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(result);

        String lawName = root
                .path("법령")
                .path("기본정보")
                .path("법령명_한글")
                .asText();

        System.out.println("법령명: " + lawName);

        JsonNode articles = root
                .path("법령")
                .path("조문")
                .path("조문단위");

        int count = 0;

        for (JsonNode article : articles) {

            if (!"조문".equals(article.path("조문여부").asText())) {
                continue;
            }
        }
    }
}