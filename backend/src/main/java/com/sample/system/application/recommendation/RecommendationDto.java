package com.sample.system.application.recommendation;

import lombok.Builder;

@Builder
public record RecommendationDto(
        Long productId,
        String productName,
        String reason,
        Double confidenceScore
) {}
