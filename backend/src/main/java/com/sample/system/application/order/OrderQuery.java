package com.sample.system.application.order;

import com.sample.system.domain.order.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class OrderQuery {

    @Builder
    public record SearchCriteria(
            Long customerId,
            OrderStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Integer page,
            Integer size
    ) {
        public SearchCriteria {
            if (page == null || page < 0) {
                page = 0;
            }
            if (size == null || size <= 0) {
                size = 20;
            }
        }
    }

    @Builder
    public record OrderDetailQuery(
            Long orderId
    ) {}
}
