package com.study.ailawassistant.ai;

import com.study.ailawassistant.dto.AiArticleCandidateDto;
import com.study.ailawassistant.dto.AiRerankResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.core.JacksonException;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

@Service
public class AiRerankService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AiRerankService(
            @Value("${openai.api.key}") String apiKey,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization","Bearer "+apiKey)
                .build();
    }

    public AiRerankResponse rerank(
            String question,
            List<AiArticleCandidateDto> candidates
    ) throws JacksonException {

        String candidateText = candidates.stream()
                .map(article -> """
                ID: %s
                법령명: %s
                조문 제목: %s
                조문 내용: %s
                """.formatted(
                        article.articleId(),
                        article.lawName(),
                        article.articleTitle(),
                        article.contentPreview()
                ))
                .collect(Collectors.joining("\n---\n"));


        // TODO: AI 모델에 질문과 후보 조문 전달
        String prompt = """
        너는 LAW:ON 법령 검색 시스템의 조문 재순위화 도우미야.

        사용자 질문에 가장 직접적인 답변 근거가 되는
        조문 ID를 중요도 순서대로 최대 3개 선택해.

        반드시 아래 후보에 있는 ID만 선택해.
        JSON 형식으로만 응답해.

        반드시 selectedArticleIds 필드를 가진 JSON 객체로 응답해.
        selectedArticleIds에는 실제 후보에서 선택한 숫자 ID만 넣어.
        가장 관련성 높은 조문부터 순서대로 최대 3개 선택해.

        사용자 질문:
        %s

        후보 조문:
        %s
        """.formatted(question, candidateText);

        // OPENAI API 호출
        String response = restClient.post()
                .uri("/responses")
                .body(Map.of(
                        "model", "gpt-4.1-mini",
                        "input", prompt
                ))
                .retrieve()
                .body(String.class);

        try {
            JsonNode responseNode = objectMapper.readTree(response);

            String aiText = responseNode
                    .path("output")
                    .path(0)
                    .path("content")
                    .path(0)
                    .path("text")
                    .asText();

            String cleanedText = aiText.trim()
                    .replaceFirst("^```(?:json)?\\s*", "")
                            .replaceFirst("\\s*```$","");

            JsonNode resultNode = objectMapper.readTree(cleanedText);
            JsonNode idsNode = resultNode.path("selectedArticleIds");

            // 응답 확인
            System.out.println("AI가 선택한 조문: " + idsNode);

            Set<Long> validIds = candidates.stream()
                    .map(AiArticleCandidateDto::articleId)
                    .collect(Collectors.toSet());

            List<Long> selectedIds = new ArrayList<>();

            for (JsonNode idNode : idsNode) {
                long id = idNode.asLong();

                if (validIds.contains(id) && !selectedIds.contains(id)) {
                    selectedIds.add(id);
                }

                if (selectedIds.size() == 3) {
                    break;
                }
            }

            return new AiRerankResponse(selectedIds);

        } catch (JacksonException e) {
            throw new IllegalStateException("ai 응답 JSON 파싱 실패", e);
        }
    }
}