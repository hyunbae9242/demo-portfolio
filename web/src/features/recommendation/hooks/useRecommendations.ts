import { useEffect } from 'react'
import { useRecommendationStore } from '../store/recommendationStore'

export const useRecommendations = (customerId: number, orderHistory?: string) => {
  const { recommendations, loading, error, fetchRecommendations } = useRecommendationStore()

  useEffect(() => {
    if (customerId) {
      fetchRecommendations(customerId, orderHistory)
    }
  }, [customerId, orderHistory, fetchRecommendations])

  return { recommendations, loading, error }
}
