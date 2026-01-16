import { useEffect } from 'react'
import { useOrderStore } from '../store/orderStore'

export const useOrders = (customerId: number) => {
  const { orders, loading, error, fetchOrders } = useOrderStore()

  useEffect(() => {
    if (customerId) {
      fetchOrders(customerId)
    }
  }, [customerId, fetchOrders])

  return { orders, loading, error }
}

export const useOrder = (orderId: number) => {
  const { currentOrder, loading, error, fetchOrder } = useOrderStore()

  useEffect(() => {
    if (orderId) {
      fetchOrder(orderId)
    }
  }, [orderId, fetchOrder])

  return { order: currentOrder, loading, error }
}
