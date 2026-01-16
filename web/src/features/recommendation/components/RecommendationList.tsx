import React from 'react'
import { Recommendation } from '../types'

interface RecommendationListProps {
  recommendations: Recommendation[]
  onAddToCart?: (productId: number) => void
}

export const RecommendationList: React.FC<RecommendationListProps> = ({
  recommendations,
  onAddToCart,
}) => {
  if (recommendations.length === 0) {
    return (
      <div className="empty-state">
        <p>No AI recommendations available at the moment.</p>
      </div>
    )
  }

  return (
    <div className="recommendation-list">
      <div className="recommendation-header">
        <h2>🤖 AI-Powered Product Recommendations</h2>
        <p>Based on your order history and preferences</p>
      </div>

      {recommendations.map((rec, index) => (
        <div key={rec.productId} className="recommendation-card">
          <div className="recommendation-rank">#{index + 1}</div>
          <div className="recommendation-content">
            <h3>{rec.productName}</h3>
            <p className="recommendation-reason">{rec.reason}</p>
            <div className="recommendation-footer">
              <div className="confidence-score">
                <span className="confidence-label">Confidence:</span>
                <div className="confidence-bar">
                  <div
                    className="confidence-fill"
                    style={{ width: `${rec.confidenceScore * 100}%` }}
                  />
                </div>
                <span className="confidence-value">
                  {(rec.confidenceScore * 100).toFixed(0)}%
                </span>
              </div>
              {onAddToCart && (
                <button
                  className="add-to-cart-btn"
                  onClick={() => onAddToCart(rec.productId)}
                >
                  Add to Cart
                </button>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}
