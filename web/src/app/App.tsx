import React, { useState } from 'react'
import { ErrorBoundary } from '../shared/components/ErrorBoundary'
import { LoadingSpinner } from '../shared/components/LoadingSpinner'
import { OrderList } from '../features/order/components/OrderList'
import { OrderForm } from '../features/order/components/OrderForm'
import { RecommendationList } from '../features/recommendation/components/RecommendationList'
import { useOrderStore } from '../features/order/store/orderStore'
import { useOrders } from '../features/order/hooks/useOrders'
import { useRecommendations } from '../features/recommendation/hooks/useRecommendations'
import { errorHandler } from '../shared/utils/errorHandler'

function App() {
  const [customerId] = useState(100)
  const [showForm, setShowForm] = useState(false)
  const [showRecommendations, setShowRecommendations] = useState(false)
  const { orders, loading, error } = useOrders(customerId)
  const { recommendations, loading: recLoading, error: recError } = useRecommendations(
    showRecommendations ? customerId : 0
  )
  const { createOrder, confirmOrder, cancelOrder } = useOrderStore()

  const handleCreateOrder = async (request: any) => {
    try {
      await createOrder(request)
      setShowForm(false)
      alert('Order created successfully!')
    } catch (error) {
      alert(errorHandler.getErrorMessage(errorHandler.handle(error)))
    }
  }

  const handleOrderClick = (orderId: number) => {
    console.log('Order clicked:', orderId)
  }

  const handleConfirm = async (orderId: number) => {
    try {
      await confirmOrder(orderId)
      alert('Order confirmed!')
    } catch (error) {
      alert(errorHandler.getErrorMessage(errorHandler.handle(error)))
    }
  }

  const handleCancel = async (orderId: number) => {
    try {
      await cancelOrder(orderId)
      alert('Order cancelled!')
    } catch (error) {
      alert(errorHandler.getErrorMessage(errorHandler.handle(error)))
    }
  }

  return (
    <ErrorBoundary>
      <div className="app">
        <header className="app-header">
          <h1>Sample Order System</h1>
          <p>Frontend Architecture Sample</p>
        </header>

        <main className="app-main">
          <div className="actions">
            <button onClick={() => setShowForm(!showForm)}>
              {showForm ? 'Cancel' : 'Create New Order'}
            </button>
            <button onClick={() => setShowRecommendations(!showRecommendations)}>
              {showRecommendations ? 'Hide' : 'Get AI Recommendations'}
            </button>
          </div>

          {showForm && (
            <div className="form-container">
              <h2>Create Order</h2>
              <OrderForm onSubmit={handleCreateOrder} loading={loading} />
            </div>
          )}

          {loading && <LoadingSpinner message="Loading orders..." />}

          {error && (
            <div className="error-message">
              <h3>Error: {error.code}</h3>
              <p>{errorHandler.getErrorMessage(error)}</p>
            </div>
          )}

          {showRecommendations && (
            <div className="recommendations-container">
              {recLoading && <LoadingSpinner message="Getting AI recommendations..." />}
              {recError && (
                <div className="error-message">
                  <h3>Recommendation Error: {recError.code}</h3>
                  <p>{errorHandler.getErrorMessage(recError)}</p>
                </div>
              )}
              {!recLoading && !recError && (
                <RecommendationList
                  recommendations={recommendations}
                  onAddToCart={(productId) => console.log('Add to cart:', productId)}
                />
              )}
            </div>
          )}

          {!loading && !error && (
            <div className="orders-container">
              <h2>Orders for Customer {customerId}</h2>
              <OrderList orders={orders} onOrderClick={handleOrderClick} />
            </div>
          )}
        </main>

        <footer className="app-footer">
          <p>Sample Web Application - Not for Production</p>
        </footer>
      </div>
    </ErrorBoundary>
  )
}

export default App
