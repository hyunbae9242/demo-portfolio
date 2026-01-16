import { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios'
import { tokenManager } from '../utils/tokenManager'
import { AppError } from '../utils/errorHandler'

export const setupInterceptors = (instance: AxiosInstance): void => {
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = tokenManager.getToken()
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }

      const correlationId = crypto.randomUUID()
      config.headers['X-Correlation-ID'] = correlationId

      console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
        correlationId,
        data: config.data,
      })

      return config
    },
    (error: AxiosError) => {
      console.error('[API Request Error]', error)
      return Promise.reject(error)
    }
  )

  instance.interceptors.response.use(
    (response) => {
      const correlationId = response.headers['x-correlation-id']
      console.log(`[API Response] ${response.config.method?.toUpperCase()} ${response.config.url}`, {
        status: response.status,
        correlationId,
        data: response.data,
      })
      return response
    },
    async (error: AxiosError) => {
      const correlationId = error.response?.headers['x-correlation-id']
      console.error('[API Response Error]', {
        status: error.response?.status,
        correlationId,
        data: error.response?.data,
      })

      if (error.response?.status === 401) {
        tokenManager.clearTokens()
        window.location.href = '/login'
      }

      if (error.response?.data) {
        throw new AppError(error.response.data as any, error.message)
      }

      throw error
    }
  )
}
