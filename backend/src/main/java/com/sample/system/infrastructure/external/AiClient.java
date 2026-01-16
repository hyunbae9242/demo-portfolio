package com.sample.system.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AiClient {

    @Value("${ai.api.url:https://api.anthropic.com/v1}")
    private String aiApiUrl;

    @Value("${ai.api.key:dummy_key}")
    private String aiApiKey;

    public String generateRecommendations(String prompt) {
        log.debug("Calling AI API with prompt: {}", prompt);

        String mockResponse = """
        {
          "recommendations": [
            {
              "productId": 1,
              "productName": "Spring Boot in Action",
              "reason": "Based on your previous orders, you seem interested in Java development books",
              "confidenceScore": 0.92
            },
            {
              "productId": 2,
              "productName": "Clean Architecture",
              "reason": "Customers who bought similar items also purchased this book",
              "confidenceScore": 0.87
            },
            {
              "productId": 3,
              "productName": "Domain-Driven Design",
              "reason": "Recommended for advanced software architecture learning",
              "confidenceScore": 0.81
            }
          ]
        }
        """;

        return mockResponse;
    }

    public Map<String, Object> callAiApi(String model, String prompt) {
        log.info("AI API call - Model: {}, URL: {}", model, aiApiUrl);
        return Map.of("status", "success", "mockData", true);
    }
}
