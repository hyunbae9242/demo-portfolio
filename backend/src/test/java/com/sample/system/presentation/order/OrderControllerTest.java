package com.sample.system.presentation.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.system.application.order.OrderApplicationService;
import com.sample.system.application.order.OrderDto;
import com.sample.system.domain.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController 슬라이스 테스트")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderApplicationService orderApplicationService;

    @Test
    @DisplayName("POST /api/orders - 주문 생성 성공")
    void createOrder_Success() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(100L)
                .customerName("John Doe")
                .items(List.of(
                        CreateOrderRequest.OrderItemRequest.builder()
                                .productId(1L)
                                .productName("Book A")
                                .quantity(2)
                                .unitPrice(BigDecimal.valueOf(10.00))
                                .build()
                ))
                .build();

        OrderDto mockResponse = OrderDto.builder()
                .id(1L)
                .orderNumber("ORD-TEST")
                .customerId(100L)
                .customerName("John Doe")
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(20.00))
                .orderItems(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderApplicationService.createOrder(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST"))
                .andExpect(jsonPath("$.customerId").value(100L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderApplicationService).createOrder(any());
    }

    @Test
    @DisplayName("POST /api/orders - 유효성 검증 실패 (빈 customerId)")
    void createOrder_Validation_Fail() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(null)
                .customerName("John Doe")
                .items(List.of(
                        CreateOrderRequest.OrderItemRequest.builder()
                                .productId(1L)
                                .productName("Book A")
                                .quantity(2)
                                .unitPrice(BigDecimal.valueOf(10.00))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderApplicationService, never()).createOrder(any());
    }

    @Test
    @DisplayName("GET /api/orders/{orderId} - 주문 조회 성공")
    void getOrder_Success() throws Exception {
        Long orderId = 1L;
        OrderDto mockResponse = OrderDto.builder()
                .id(orderId)
                .orderNumber("ORD-TEST")
                .customerId(100L)
                .customerName("John Doe")
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(20.00))
                .orderItems(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderApplicationService.getOrder(orderId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST"));

        verify(orderApplicationService).getOrder(orderId);
    }

    @Test
    @DisplayName("POST /api/orders/{orderId}/confirm - 주문 확정 성공")
    void confirmOrder_Success() throws Exception {
        Long orderId = 1L;
        OrderDto mockResponse = OrderDto.builder()
                .id(orderId)
                .orderNumber("ORD-TEST")
                .customerId(100L)
                .customerName("John Doe")
                .status(OrderStatus.CONFIRMED)
                .totalAmount(BigDecimal.valueOf(20.00))
                .orderItems(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderApplicationService.confirmOrder(orderId)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/orders/{orderId}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderApplicationService).confirmOrder(orderId);
    }
}
