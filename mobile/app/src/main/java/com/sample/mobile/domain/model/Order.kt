package com.sample.mobile.domain.model

data class Order(
    val id: Long,
    val orderNumber: String,
    val customerName: String,
    val status: OrderStatus,
    val totalAmount: Double,
    val orderItems: List<OrderItem>,
    val createdAt: String
)

data class OrderItem(
    val id: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED
}
