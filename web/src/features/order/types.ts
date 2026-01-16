export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  SHIPPING = 'SHIPPING',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface Order {
  id: number
  orderNumber: string
  customerId: number
  customerName: string
  status: OrderStatus
  totalAmount: number
  orderItems: OrderItem[]
  createdAt: string
  updatedAt: string
}

export interface CreateOrderRequest {
  customerId: number
  customerName: string
  items: CreateOrderItemRequest[]
}

export interface CreateOrderItemRequest {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
}
