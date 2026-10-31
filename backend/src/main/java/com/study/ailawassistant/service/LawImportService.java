package com.study.ailawassistant.service;


import com.study.ailawassistant.client.LawApiClient;
import com.study.ailawassistant.entity.LawArticle;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.study.ailawassistant.entity.Law;
import com.study.ailawassistant.entity.LawVersion;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.study.ailawassistant.repository.LawRepository;
import com.study.ailawassistant.repository.LawVersionRepository;
import com.study.ailawassistant.repository.LawArticleRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LawImportService {

    private final LawApiClient lawApiClient;
    private final LawRepository lawRepository;
    private final LawVersionRepository lawVersionRepository;
    private final LawArticleRepository lawArticleRepository;

    public LawImportService(
            LawApiClient lawApiClient,
            LawRepository lawRepository,
            LawVersionRepository lawVersionRepository,
            LawArticleRepository lawArticleRepository
    ) {
        this.lawApiClient = lawApiClient;
        this.lawRepository = lawRepository;
        this.lawVersionRepository = lawVersionRepository;
        this.lawArticleRepository = lawArticleRepository;
    }

    @Transactional
    public void importLaw(String mst) {
        Long lawSerialNo = Long.valueOf(mst);

        if (lawVersionRepository.existsById(lawSerialNo)) {
            throw new IllegalStateException(
                    "이미 등록된 법령 버전입니다. MST: " + mst
            );
        }

        String json = lawApiClient.getLawDetail(mst);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(json);

        JsonNode basicInfo = root
                .path("법령")
                .path("기본정보");

        Long lawId = Long.valueOf(
                basicInfo.path("법령ID").asText()
        );

        String lawName = basicInfo.path("법령명_한글").asText();

        String lawType = basicInfo
                .path("법종구분")
                .path("content")
                .asText();

        String lawShortName = basicInfo
                .path("법령명약칭")
                .asText();

        LocalDate promulgationDate = LocalDate.parse(
                basicInfo.path("공포일자").asText(),
                DateTimeFormatter.BASIC_ISO_DATE
        );

        LocalDate lawEffectiveDate = LocalDate.parse(
                basicInfo.path("시행일자").asText(),
                DateTimeFormatter.BASIC_ISO_DATE
        );

        Integer promulgationNo = Integer.valueOf(
                basicInfo.path("공포번호").asText()
        );

        String revisionType = basicInfo.path("제개정구분").asText();

        if (!lawRepository.existsById(lawId)) {
            Law law = new Law(
                    lawId, lawName, lawType, lawShortName
            );

            lawRepository.save(law);
        }

        LawVersion lawVersion = new LawVersion(
                Long.valueOf(mst),
                lawId,
                promulgationDate,
                lawEffectiveDate,
                promulgationNo,
                revisionType
        );

        JsonNode articles = root
                .path("법령")
                .path("조문")
                .path("조문단위");

        for (JsonNode article : articles) {

            if (!"조문".equals(article.path("조문여부").asText())) {
                continue;
            }

//            if (!article.path("호").isEmpty()
//                    && article.path("항").isEmpty()) {
//
//                directItemCount++;
//                System.out.println("발견한 조문: "
//                        + article.path("조문번호").asText());
//            }
        }

        //System.out.println("항 없이 호가 있는 조문 개수: " + directItemCount);

        List<LawArticle> lawArticles = new ArrayList<>();

        for (JsonNode article : articles) {
            if (!"조문".equals(article.path("조문여부").asText())) {
                continue;
            }
            String[] fields = {
                    "조문번호",
                    "조문가지번호",
                    "조문키",
                    "조문제목",
                    "조문내용",
                    "조문시행일자"
            };

            Integer articleNo =
                    Integer.valueOf(article.path("조문번호").asText());

            Integer articleBranchNo =
                    article.path("조문가지번호").asText("").isBlank()
                            ? 0
                            : Integer.valueOf(article.path("조문가지번호").asText());

            String articleKey = article.path("조문키").asText();
            String articleTitle = article.path("조문제목").asText();

            LocalDate effectiveDate = LocalDate.parse(
                    article.path("조문시행일자").asText(),
                    DateTimeFormatter.BASIC_ISO_DATE
            );

            StringBuilder content = new StringBuilder();

            content.append(article.path("조문내용").asText());

            JsonNode paragraphs = article.path("항");

            if (paragraphs.isArray()) {
                for (JsonNode paragraph : paragraphs) {
                    appendParagraph(content, paragraph);
                }
            } else if (paragraphs.isObject()) {
                appendParagraph(content, paragraphs);
            }

            String articleContent = content.toString();

            if (articleNo == 2) {
                System.out.println("===== 관세법 제2조 =====");
                System.out.println(articleContent);
            }

            LawArticle lawArticle = new LawArticle(
                    lawSerialNo,
                    articleNo,
                    articleBranchNo,
                    articleKey,
                    articleTitle,
                    articleContent,
                    effectiveDate
            );

            lawArticles.add(lawArticle);

            //System.out.println("조문번호: " + lawArticle.getArticleNo());
            //System.out.println("조문제목: " + lawArticle.getArticleTitle());
            //System.out.println("원본 조문 JSON:");
            //System.out.println(article.toPrettyString());

        }

        System.out.println("생성된 조문 객체 수: " + lawArticles.size());
        lawVersionRepository.save(lawVersion);
        lawArticleRepository.saveAll(lawArticles);

        System.out.println("법령 DB 저장 완료!");
    }

    private void appendText(StringBuilder content, JsonNode node) {

        if (node.isArray()) {
            for (JsonNode child : node) {
                appendText(content, child);
            }
        } else if (node.isTextual()) {
            content.append("\n    ")
                    .append(node.asText());
        }
    }

    private void appendParagraph(StringBuilder content, JsonNode paragraph) {

        String paragraphContent = paragraph.path("항내용").asText("");

        if (!paragraphContent.isBlank()) {
            content.append("\n").append(paragraphContent);
        }

        JsonNode items = paragraph.path("호");

        if (items.isArray()) {
            for (JsonNode item : items) {
                appendItem(content, item);
            }
        } else if (items.isObject()) {
            appendItem(content, items);
        }
    }

    private void appendItem(StringBuilder content, JsonNode item) {

        String itemContent = item.path("호내용").asText("");

        if (!itemContent.isBlank()) {
            content.append("\n  ").append(itemContent);
        }

        JsonNode subItems = item.path("목");

        if (subItems.isArray()) {
            for (JsonNode subItem : subItems) {
                appendText(content, subItem.path("목내용"));
            }
        } else if (subItems.isObject()) {
            appendText(content, subItems.path("목내용"));
        }
    }
}
