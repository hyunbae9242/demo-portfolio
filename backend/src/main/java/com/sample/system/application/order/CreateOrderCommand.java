package com.sample.system.application.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CreateOrderCommand(
        @NotNull(message = "Customer ID is required")
        @Positive(message = "Customer ID must be positive")
        Long customerId,

        @NotBlank(message = "Customer name is required")
        @Size(max = 100, message = "Customer name must not exceed 100 characters")
        String customerName,

        @NotNull(message = "Order items are required")
        @Size(min = 1, message = "Order must have at least one item")
        @Valid
        List<OrderItemCommand> orderItems
) {
    @Builder
    public record OrderItemCommand(
            @NotNull(message = "Product ID is required")
            @Positive(message = "Product ID must be positive")
            Long productId,

            @NotBlank(message = "Product name is required")
            @Size(max = 200, message = "Product name must not exceed 200 characters")
            String productName,

            @NotNull(message = "Quantity is required")
            @Positive(message = "Quantity must be positive")
            Integer quantity,

            @NotNull(message = "Unit price is required")
            @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
            BigDecimal unitPrice
    ) {}
}
