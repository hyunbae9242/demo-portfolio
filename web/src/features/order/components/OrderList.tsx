import React from 'react'
import { Order, OrderStatus } from '../types'

interface OrderListProps {
  orders: Order[]
  onOrderClick: (orderId: number) => void
}

export const OrderList: React.FC<OrderListProps> = ({ orders, onOrderClick }) => {
  if (orders.length === 0) {
    return <div className="empty-state">No orders found</div>
  }

  return (
    <div className="order-list">
      {orders.map(order => (
        <div
          key={order.id}
          className="order-card"
          onClick={() => onOrderClick(order.id)}
        >
          <div className="order-header">
            <h3>{order.orderNumber}</h3>
            <span className={`status-badge status-${order.status.toLowerCase()}`}>
              {order.status}
            </span>
          </div>
          <div className="order-body">
            <p><strong>Customer:</strong> {order.customerName}</p>
            <p><strong>Total:</strong> ${order.totalAmount.toFixed(2)}</p>
            <p><strong>Items:</strong> {order.orderItems.length}</p>
            <p className="order-date">
              {new Date(order.createdAt).toLocaleDateString()}
            </p>
          </div>
        </div>
      ))}
    </div>
  )
}

export const getStatusColor = (status: OrderStatus): string => {
  switch (status) {
    case OrderStatus.PENDING:
      return '#FFA500'
    case OrderStatus.CONFIRMED:
      return '#4169E1'
    case OrderStatus.SHIPPING:
      return '#9370DB'
    case OrderStatus.DELIVERED:
      return '#32CD32'
    case OrderStatus.CANCELLED:
      return '#DC143C'
    default:
      return '#808080'
  }
}
