package com.sample.system.application.order;

import com.sample.system.common.exception.BusinessException;
import com.sample.system.common.exception.ErrorCode;
import com.sample.system.domain.order.Order;
import com.sample.system.domain.order.OrderService;
import com.sample.system.infrastructure.persistence.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OrderDto createOrder(CreateOrderCommand command) {
        String orderNumber = generateOrderNumber();

        List<OrderService.OrderItemData> itemDataList = command.orderItems().stream()
                .map(item -> new OrderService.OrderItemData(
                        item.productId(),
                        item.productName(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .collect(Collectors.toList());

        Order order = orderService.createOrder(
                orderNumber,
                command.customerId(),
                command.customerName(),
                itemDataList
        );

        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully: id={}, orderNumber={}",
                savedOrder.getId(), savedOrder.getOrderNumber());

        return OrderDto.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        return OrderDto.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> searchOrders(OrderQuery.SearchCriteria criteria) {
        List<Order> orders = orderRepository.findAllByCustomerId(criteria.customerId());

        return orders.stream()
                .map(OrderDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto confirmOrder(Long orderId) {
        Order order = findOrderById(orderId);

        orderService.confirmOrder(order);
        Order savedOrder = orderRepository.save(order);

        return OrderDto.from(savedOrder);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);

        orderService.cancelOrder(order);
        Order savedOrder = orderRepository.save(order);

        return OrderDto.from(savedOrder);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto shipOrder(Long orderId) {
        Order order = findOrderById(orderId);

        orderService.shipOrder(order);
        Order savedOrder = orderRepository.save(order);

        return OrderDto.from(savedOrder);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderDto deliverOrder(Long orderId) {
        Order order = findOrderById(orderId);

        orderService.deliverOrder(order);
        Order savedOrder = orderRepository.save(order);

        return OrderDto.from(savedOrder);
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
