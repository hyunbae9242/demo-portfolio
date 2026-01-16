package com.sample.system.application.order;

import com.sample.system.domain.order.Order;
import com.sample.system.domain.order.OrderItem;
import com.sample.system.domain.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record OrderDto(
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemDto> orderItems,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderDto from(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemDto::from)
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    @Builder
    public record OrderItemDto(
            Long id,
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        public static OrderItemDto from(OrderItem orderItem) {
            return OrderItemDto.builder()
                    .id(orderItem.getId())
                    .productId(orderItem.getProductId())
                    .productName(orderItem.getProductName())
                    .quantity(orderItem.getQuantity())
                    .unitPrice(orderItem.getUnitPrice())
                    .totalPrice(orderItem.calculateTotalPrice())
                    .build();
        }
    }
}
