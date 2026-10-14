package com.study.ailawassistant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController //Http요청을 처리하고 반환값을 응답 본문으로 전달
public class HealthController {

    @GetMapping("/api/health") //해당 Url의 Get요청을 메서드에 연결
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "message", "AI Law Assistant is running"
        );
    }
}
