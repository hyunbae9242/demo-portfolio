package com.sample.system.presentation.recommendation;

import com.sample.system.application.recommendation.RecommendationApplicationService;
import com.sample.system.application.recommendation.RecommendationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationApplicationService recommendationApplicationService;

    @GetMapping
    public ResponseEntity<List<RecommendationDto>> getRecommendations(
            @RequestParam Long customerId,
            @RequestParam(required = false) String orderHistory
    ) {
        log.info("Received recommendation request for customer: {}", customerId);

        List<RecommendationDto> recommendations =
                recommendationApplicationService.getRecommendations(customerId, orderHistory);

        return ResponseEntity.ok(recommendations);
    }
}
