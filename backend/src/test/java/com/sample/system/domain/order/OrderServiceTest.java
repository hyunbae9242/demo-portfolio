package com.sample.system.domain.order;

import com.sample.system.common.exception.BusinessException;
import com.sample.system.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    private final OrderService orderService = new OrderService();

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00)),
                new OrderService.OrderItemData(2L, "Book B", 1, BigDecimal.valueOf(20.00))
        );

        Order order = orderService.createOrder("ORD-001", 100L, "John Doe", itemDataList);

        assertThat(order).isNotNull();
        assertThat(order.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(order.getCustomerId()).isEqualTo(100L);
        assertThat(order.getCustomerName()).isEqualTo("John Doe");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
        assertThat(order.getOrderItems()).hasSize(2);
    }

    @Test
    @DisplayName("주문 생성 - 빈 아이템 리스트로 실패")
    void createOrder_EmptyItems_Fail() {
        List<OrderService.OrderItemData> emptyItems = List.of();

        assertThatThrownBy(() ->
                orderService.createOrder("ORD-002", 100L, "John Doe", emptyItems))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VALIDATION_ERROR);
    }

    @Test
    @DisplayName("주문 확정 - 성공")
    void confirmOrder_Success() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00))
        );
        Order order = orderService.createOrder("ORD-003", 100L, "John Doe", itemDataList);

        orderService.confirmOrder(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("주문 확정 - PENDING 상태가 아니면 실패")
    void confirmOrder_NotPending_Fail() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00))
        );
        Order order = orderService.createOrder("ORD-004", 100L, "John Doe", itemDataList);
        order.confirm();

        assertThatThrownBy(() -> orderService.confirmOrder(order))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ORDER_STATUS);
    }

    @Test
    @DisplayName("주문 취소 - 성공 (PENDING)")
    void cancelOrder_Pending_Success() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00))
        );
        Order order = orderService.createOrder("ORD-005", 100L, "John Doe", itemDataList);

        orderService.cancelOrder(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("주문 취소 - 성공 (CONFIRMED)")
    void cancelOrder_Confirmed_Success() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00))
        );
        Order order = orderService.createOrder("ORD-006", 100L, "John Doe", itemDataList);
        order.confirm();

        orderService.cancelOrder(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("주문 취소 - 실패 (SHIPPING)")
    void cancelOrder_Shipping_Fail() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00))
        );
        Order order = orderService.createOrder("ORD-007", 100L, "John Doe", itemDataList);
        order.confirm();
        order.ship();

        assertThatThrownBy(() -> orderService.cancelOrder(order))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ORDER_STATUS);
    }

    @Test
    @DisplayName("주문 상태 전환 흐름 - PENDING -> CONFIRMED -> SHIPPING -> DELIVERED")
    void orderStatusTransition_Success() {
        List<OrderService.OrderItemData> itemDataList = List.of(
                new OrderService.OrderItemData(1L, "Book A", 2, BigDecimal.valueOf(10.00))
        );
        Order order = orderService.createOrder("ORD-008", 100L, "John Doe", itemDataList);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        orderService.confirmOrder(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        orderService.shipOrder(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPING);

        orderService.deliverOrder(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }
}
