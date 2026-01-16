package com.sample.mobile.data.repository

import com.sample.mobile.domain.model.Order

interface OrderRepository {
    suspend fun getOrders(customerId: Long): List<Order>
    suspend fun getOrder(orderId: Long): Order
    suspend fun confirmOrder(orderId: Long): Order
    suspend fun cancelOrder(orderId: Long): Order
}
