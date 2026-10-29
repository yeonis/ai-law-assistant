package com.study.ailawassistant.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Component
public class LawApiClient {

    private final RestClient restClient;
    private final String oc;

    public LawApiClient(RestClient.Builder builder,
                        @Value("${law.api.oc}") String oc){
        this.restClient = builder
                .baseUrl("https://www.law.go.kr/DRF")
                .build();
        this.oc = oc;
    }

    public String getLawDetail(String mst) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/lawService.do")
                        .queryParam("OC", oc)
                        .queryParam("target", "law")
                        .queryParam("type", "JSON")
                        .queryParam("MST", mst) //법령일련번호
                        .build())
                .retrieve()
                .body(String.class);
    }
}
