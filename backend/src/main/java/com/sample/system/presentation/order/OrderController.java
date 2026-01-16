package com.sample.system.presentation.order;

import com.sample.system.application.order.OrderApplicationService;
import com.sample.system.application.order.OrderDto;
import com.sample.system.application.order.OrderQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Received create order request: customerId={}", request.customerId());

        OrderDto orderDto = orderApplicationService.createOrder(request.toCommand());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderDto);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId) {
        log.info("Received get order request: orderId={}", orderId);

        OrderDto orderDto = orderApplicationService.getOrder(orderId);

        return ResponseEntity.ok(orderDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> searchOrders(@RequestParam Long customerId) {
        log.info("Received search orders request: customerId={}", customerId);

        OrderQuery.SearchCriteria criteria = OrderQuery.SearchCriteria.builder()
                .customerId(customerId)
                .build();

        List<OrderDto> orders = orderApplicationService.searchOrders(criteria);

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable Long orderId) {
        log.info("Received confirm order request: orderId={}", orderId);

        OrderDto orderDto = orderApplicationService.confirmOrder(orderId);

        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId) {
        log.info("Received cancel order request: orderId={}", orderId);

        OrderDto orderDto = orderApplicationService.cancelOrder(orderId);

        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderDto> shipOrder(@PathVariable Long orderId) {
        log.info("Received ship order request: orderId={}", orderId);

        OrderDto orderDto = orderApplicationService.shipOrder(orderId);

        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<OrderDto> deliverOrder(@PathVariable Long orderId) {
        log.info("Received deliver order request: orderId={}", orderId);

        OrderDto orderDto = orderApplicationService.deliverOrder(orderId);

        return ResponseEntity.ok(orderDto);
    }
}
