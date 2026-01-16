package com.sample.system.domain.order;

import com.sample.system.common.exception.BusinessException;
import com.sample.system.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Order(String orderNumber, Long customerId, String customerName) {
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        recalculateTotalAmount();
    }

    public void confirm() {
        changeStatus(OrderStatus.CONFIRMED);
    }

    public void ship() {
        changeStatus(OrderStatus.SHIPPING);
    }

    public void deliver() {
        changeStatus(OrderStatus.DELIVERED);
    }

    public void cancel() {
        changeStatus(OrderStatus.CANCELLED);
    }

    private void changeStatus(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessException(
                    ErrorCode.INVALID_ORDER_STATUS,
                    this.status,
                    newStatus
            );
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    private void recalculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = LocalDateTime.now();
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean isCancellable() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }
}
