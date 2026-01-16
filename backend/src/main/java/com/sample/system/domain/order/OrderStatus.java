package com.sample.system.domain.order;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPING,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == SHIPPING || newStatus == CANCELLED;
            case SHIPPING -> newStatus == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
