package com.sample.system.application.recommendation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.domain.recommendation.RecommendationService;
import com.sample.system.infrastructure.external.AiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationApplicationService {

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    public List<RecommendationDto> getRecommendations(Long customerId, String orderHistory) {
        log.info("Getting AI recommendations for customer: {}", customerId);

        String prompt = buildPrompt(customerId, orderHistory);

        String aiResponse = aiClient.generateRecommendations(prompt);

        return parseRecommendations(aiResponse);
    }

    private String buildPrompt(Long customerId, String orderHistory) {
        return String.format(
                "Based on the following customer order history, recommend 3-5 products. " +
                "Customer ID: %d. Order History: %s. " +
                "Provide recommendations with reasons and confidence scores.",
                customerId,
                orderHistory != null ? orderHistory : "No order history"
        );
    }

    private List<RecommendationDto> parseRecommendations(String aiResponse) {
        List<RecommendationDto> recommendations = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(aiResponse);
            JsonNode recommendationsNode = root.get("recommendations");

            if (recommendationsNode != null && recommendationsNode.isArray()) {
                for (JsonNode node : recommendationsNode) {
                    recommendations.add(RecommendationDto.builder()
                            .productId(node.get("productId").asLong())
                            .productName(node.get("productName").asText())
                            .reason(node.get("reason").asText())
                            .confidenceScore(node.get("confidenceScore").asDouble())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse AI recommendations", e);
        }

        return recommendations;
    }
}
