package com.sample.system.domain.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    public List<ProductRecommendation> generateRecommendations(Long customerId, String context) {
        log.info("Generating AI recommendations for customer: {}", customerId);
        return List.of();
    }

    public record ProductRecommendation(
            Long productId,
            String productName,
            String reason,
            Double confidenceScore
    ) {}
}
