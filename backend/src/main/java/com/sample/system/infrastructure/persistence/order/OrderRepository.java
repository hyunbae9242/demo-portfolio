package com.sample.system.infrastructure.persistence.order;

import com.sample.system.domain.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAllByCustomerId(Long customerId);
    void delete(Order order);
}
