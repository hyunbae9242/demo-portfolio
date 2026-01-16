export const endpoints = {
  orders: {
    list: (customerId: number) => `/api/orders?customerId=${customerId}`,
    detail: (orderId: number) => `/api/orders/${orderId}`,
    create: '/api/orders',
    confirm: (orderId: number) => `/api/orders/${orderId}/confirm`,
    cancel: (orderId: number) => `/api/orders/${orderId}/cancel`,
    ship: (orderId: number) => `/api/orders/${orderId}/ship`,
    deliver: (orderId: number) => `/api/orders/${orderId}/deliver`,
  },
  products: {
    list: '/api/products',
    detail: (productId: number) => `/api/products/${productId}`,
  },
  recommendations: {
    list: (customerId: number, orderHistory?: string) =>
      `/api/recommendations?customerId=${customerId}${orderHistory ? `&orderHistory=${orderHistory}` : ''}`,
  },
}
