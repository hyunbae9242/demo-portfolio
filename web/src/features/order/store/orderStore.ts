import { create } from 'zustand'
import { Order, CreateOrderRequest } from '../types'
import { apiClient } from '@/shared/api/client'
import { endpoints } from '@/shared/api/endpoints'
import { ApiError } from '@/shared/types/common'
import { errorHandler } from '@/shared/utils/errorHandler'

interface OrderState {
  orders: Order[]
  currentOrder: Order | null
  loading: boolean
  error: ApiError | null

  fetchOrders: (customerId: number) => Promise<void>
  fetchOrder: (orderId: number) => Promise<void>
  createOrder: (request: CreateOrderRequest) => Promise<Order>
  confirmOrder: (orderId: number) => Promise<void>
  cancelOrder: (orderId: number) => Promise<void>
  clearError: () => void
}

export const useOrderStore = create<OrderState>((set, get) => ({
  orders: [],
  currentOrder: null,
  loading: false,
  error: null,

  fetchOrders: async (customerId: number) => {
    set({ loading: true, error: null })
    try {
      const orders = await apiClient.get<Order[]>(endpoints.orders.list(customerId))
      set({ orders, loading: false })
    } catch (error) {
      const apiError = errorHandler.handle(error)
      set({ error: apiError, loading: false })
    }
  },

  fetchOrder: async (orderId: number) => {
    set({ loading: true, error: null })
    try {
      const order = await apiClient.get<Order>(endpoints.orders.detail(orderId))
      set({ currentOrder: order, loading: false })
    } catch (error) {
      const apiError = errorHandler.handle(error)
      set({ error: apiError, loading: false })
    }
  },

  createOrder: async (request: CreateOrderRequest) => {
    set({ loading: true, error: null })
    try {
      const order = await apiClient.post<Order>(endpoints.orders.create, request)
      set(state => ({
        orders: [order, ...state.orders],
        loading: false,
      }))
      return order
    } catch (error) {
      const apiError = errorHandler.handle(error)
      set({ error: apiError, loading: false })
      throw error
    }
  },

  confirmOrder: async (orderId: number) => {
    set({ loading: true, error: null })
    try {
      const order = await apiClient.post<Order>(endpoints.orders.confirm(orderId))
      set(state => ({
        orders: state.orders.map(o => o.id === orderId ? order : o),
        currentOrder: state.currentOrder?.id === orderId ? order : state.currentOrder,
        loading: false,
      }))
    } catch (error) {
      const apiError = errorHandler.handle(error)
      set({ error: apiError, loading: false })
      throw error
    }
  },

  cancelOrder: async (orderId: number) => {
    set({ loading: true, error: null })
    try {
      const order = await apiClient.post<Order>(endpoints.orders.cancel(orderId))
      set(state => ({
        orders: state.orders.map(o => o.id === orderId ? order : o),
        currentOrder: state.currentOrder?.id === orderId ? order : state.currentOrder,
        loading: false,
      }))
    } catch (error) {
      const apiError = errorHandler.handle(error)
      set({ error: apiError, loading: false })
      throw error
    }
  },

  clearError: () => set({ error: null }),
}))
