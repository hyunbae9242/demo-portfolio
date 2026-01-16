import React, { useState } from 'react'
import { CreateOrderRequest, CreateOrderItemRequest } from '../types'

interface OrderFormProps {
  onSubmit: (request: CreateOrderRequest) => Promise<void>
  loading?: boolean
}

export const OrderForm: React.FC<OrderFormProps> = ({ onSubmit, loading = false }) => {
  const [customerId, setCustomerId] = useState('')
  const [customerName, setCustomerName] = useState('')
  const [items, setItems] = useState<CreateOrderItemRequest[]>([
    { productId: 0, productName: '', quantity: 1, unitPrice: 0 },
  ])

  const handleAddItem = () => {
    setItems([...items, { productId: 0, productName: '', quantity: 1, unitPrice: 0 }])
  }

  const handleRemoveItem = (index: number) => {
    setItems(items.filter((_, i) => i !== index))
  }

  const handleItemChange = (index: number, field: keyof CreateOrderItemRequest, value: any) => {
    const newItems = [...items]
    newItems[index] = { ...newItems[index], [field]: value }
    setItems(newItems)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const request: CreateOrderRequest = {
      customerId: Number(customerId),
      customerName,
      items,
    }

    await onSubmit(request)
  }

  const calculateTotal = () => {
    return items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0)
  }

  return (
    <form onSubmit={handleSubmit} className="order-form">
      <div className="form-group">
        <label htmlFor="customerId">Customer ID *</label>
        <input
          type="number"
          id="customerId"
          value={customerId}
          onChange={e => setCustomerId(e.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="customerName">Customer Name *</label>
        <input
          type="text"
          id="customerName"
          value={customerName}
          onChange={e => setCustomerName(e.target.value)}
          required
          maxLength={100}
        />
      </div>

      <div className="form-section">
        <h3>Order Items</h3>
        {items.map((item, index) => (
          <div key={index} className="item-row">
            <input
              type="number"
              placeholder="Product ID"
              value={item.productId || ''}
              onChange={e => handleItemChange(index, 'productId', Number(e.target.value))}
              required
            />
            <input
              type="text"
              placeholder="Product Name"
              value={item.productName}
              onChange={e => handleItemChange(index, 'productName', e.target.value)}
              required
            />
            <input
              type="number"
              placeholder="Quantity"
              value={item.quantity}
              onChange={e => handleItemChange(index, 'quantity', Number(e.target.value))}
              min="1"
              required
            />
            <input
              type="number"
              step="0.01"
              placeholder="Unit Price"
              value={item.unitPrice || ''}
              onChange={e => handleItemChange(index, 'unitPrice', Number(e.target.value))}
              min="0.01"
              required
            />
            {items.length > 1 && (
              <button type="button" onClick={() => handleRemoveItem(index)}>
                Remove
              </button>
            )}
          </div>
        ))}
        <button type="button" onClick={handleAddItem} className="add-item-btn">
          + Add Item
        </button>
      </div>

      <div className="form-total">
        <strong>Total: ${calculateTotal().toFixed(2)}</strong>
      </div>

      <button type="submit" disabled={loading} className="submit-btn">
        {loading ? 'Creating...' : 'Create Order'}
      </button>
    </form>
  )
}
