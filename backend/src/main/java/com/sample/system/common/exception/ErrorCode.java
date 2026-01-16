package com.sample.system.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD001", "Order not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD001", "Product not found"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "PRD002", "Insufficient stock"),

    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORD002", "Invalid order status transition"),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "ORD003", "Payment amount does not match order total"),

    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "EXT001", "External API call failed"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB001", "Database operation failed"),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VAL001", "Validation failed"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH001", "Authentication required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH002", "Access denied"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS001", "Internal server error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
