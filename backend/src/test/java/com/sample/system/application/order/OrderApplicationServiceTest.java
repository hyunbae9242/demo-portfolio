package com.sample.system.application.order;

import com.sample.system.common.exception.BusinessException;
import com.sample.system.common.exception.ErrorCode;
import com.sample.system.domain.order.Order;
import com.sample.system.domain.order.OrderService;
import com.sample.system.domain.order.OrderStatus;
import com.sample.system.infrastructure.persistence.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderApplicationService 단위 테스트")
class OrderApplicationServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_Success() {
        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId(100L)
                .customerName("John Doe")
                .orderItems(List.of(
                        CreateOrderCommand.OrderItemCommand.builder()
                                .productId(1L)
                                .productName("Book A")
                                .quantity(2)
                                .unitPrice(BigDecimal.valueOf(10.00))
                                .build()
                ))
                .build();

        Order mockOrder = new Order("ORD-TEST", 100L, "John Doe");
        when(orderService.createOrder(any(), any(), any(), any())).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        OrderDto result = orderApplicationService.createOrder(command);

        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(100L);
        assertThat(result.customerName()).isEqualTo("John Doe");
        verify(orderService).createOrder(any(), eq(100L), eq("John Doe"), any());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 조회 - 성공")
    void getOrder_Success() {
        Long orderId = 1L;
        Order mockOrder = new Order("ORD-TEST", 100L, "John Doe");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        OrderDto result = orderApplicationService.getOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.customerId()).isEqualTo(100L);
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("주문 조회 - 존재하지 않으면 실패")
    void getOrder_NotFound_Fail() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderApplicationService.getOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("주문 확정 - 성공")
    void confirmOrder_Success() {
        Long orderId = 1L;
        Order mockOrder = new Order("ORD-TEST", 100L, "John Doe");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        OrderDto result = orderApplicationService.confirmOrder(orderId);

        assertThat(result).isNotNull();
        verify(orderService).confirmOrder(mockOrder);
        verify(orderRepository).save(mockOrder);
    }

    @Test
    @DisplayName("주문 취소 - 성공")
    void cancelOrder_Success() {
        Long orderId = 1L;
        Order mockOrder = new Order("ORD-TEST", 100L, "John Doe");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        OrderDto result = orderApplicationService.cancelOrder(orderId);

        assertThat(result).isNotNull();
        verify(orderService).cancelOrder(mockOrder);
        verify(orderRepository).save(mockOrder);
    }
}
