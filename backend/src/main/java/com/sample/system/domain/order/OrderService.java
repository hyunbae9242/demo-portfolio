package com.sample.system.domain.order;

import com.sample.system.common.exception.BusinessException;
import com.sample.system.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    public Order createOrder(String orderNumber, Long customerId, String customerName,
                            List<OrderItemData> itemDataList) {
        validateOrderItems(itemDataList);

        Order order = new Order(orderNumber, customerId, customerName);

        for (OrderItemData itemData : itemDataList) {
            OrderItem orderItem = new OrderItem(
                    itemData.productId(),
                    itemData.productName(),
                    itemData.quantity(),
                    itemData.unitPrice()
            );
            order.addOrderItem(orderItem);
        }

        log.info("Order created: orderNumber={}, customerId={}, totalAmount={}",
                orderNumber, customerId, order.getTotalAmount());

        return order;
    }

    public void confirmOrder(Order order) {
        if (!order.isPending()) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }

        order.confirm();
        log.info("Order confirmed: orderNumber={}", order.getOrderNumber());
    }

    public void cancelOrder(Order order) {
        if (!order.isCancellable()) {
            throw new BusinessException(
                    ErrorCode.INVALID_ORDER_STATUS,
                    "Order cannot be cancelled in current status: " + order.getStatus()
            );
        }

        order.cancel();
        log.info("Order cancelled: orderNumber={}", order.getOrderNumber());
    }

    public void shipOrder(Order order) {
        order.ship();
        log.info("Order shipped: orderNumber={}", order.getOrderNumber());
    }

    public void deliverOrder(Order order) {
        order.deliver();
        log.info("Order delivered: orderNumber={}", order.getOrderNumber());
    }

    public BigDecimal calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .map(OrderItem::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateOrderItems(List<OrderItemData> itemDataList) {
        if (itemDataList == null || itemDataList.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    "Order must have at least one item"
            );
        }
    }

    public record OrderItemData(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ) {}
}
