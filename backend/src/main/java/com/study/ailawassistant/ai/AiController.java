package com.study.ailawassistant.ai;

import com.study.ailawassistant.dto.AiQuestionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> ask(
            @RequestBody AiQuestionRequest request
    ){
        return ResponseEntity.ok(
                aiService.findRelevantArticles(request)
        );
    }
}
