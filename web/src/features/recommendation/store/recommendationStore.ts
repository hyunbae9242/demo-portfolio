import { create } from 'zustand'
import { Recommendation } from '../types'
import { apiClient } from '@/shared/api/client'
import { ApiError } from '@/shared/types/common'
import { errorHandler } from '@/shared/utils/errorHandler'

interface RecommendationState {
  recommendations: Recommendation[]
  loading: boolean
  error: ApiError | null

  fetchRecommendations: (customerId: number, orderHistory?: string) => Promise<void>
  clearRecommendations: () => void
  clearError: () => void
}

export const useRecommendationStore = create<RecommendationState>((set) => ({
  recommendations: [],
  loading: false,
  error: null,

  fetchRecommendations: async (customerId: number, orderHistory?: string) => {
    set({ loading: true, error: null })
    try {
      const params = new URLSearchParams({
        customerId: customerId.toString(),
        ...(orderHistory && { orderHistory }),
      })

      const recommendations = await apiClient.get<Recommendation[]>(
        `/api/recommendations?${params.toString()}`
      )

      set({ recommendations, loading: false })
    } catch (error) {
      const apiError = errorHandler.handle(error)
      set({ error: apiError, loading: false })
    }
  },

  clearRecommendations: () => set({ recommendations: [] }),
  clearError: () => set({ error: null }),
}))
