import { ApiError } from '../types/common'

export class AppError extends Error {
  constructor(
    public apiError: ApiError,
    message?: string
  ) {
    super(message || apiError.message)
    this.name = 'AppError'
  }
}

export const errorHandler = {
  handle(error: unknown): ApiError {
    if (error instanceof AppError) {
      return error.apiError
    }

    if (this.isAxiosError(error)) {
      return this.handleAxiosError(error)
    }

    return {
      timestamp: new Date().toISOString(),
      status: 500,
      error: 'Internal Error',
      code: 'UNKNOWN',
      message: error instanceof Error ? error.message : 'An unknown error occurred',
      path: window.location.pathname,
    }
  },

  isAxiosError(error: any): boolean {
    return error?.isAxiosError === true
  },

  handleAxiosError(error: any): ApiError {
    if (error.response?.data) {
      return error.response.data as ApiError
    }

    if (error.request) {
      return {
        timestamp: new Date().toISOString(),
        status: 0,
        error: 'Network Error',
        code: 'NETWORK_ERROR',
        message: 'Unable to connect to the server',
        path: window.location.pathname,
      }
    }

    return {
      timestamp: new Date().toISOString(),
      status: 500,
      error: 'Request Error',
      code: 'REQUEST_ERROR',
      message: error.message || 'Failed to make request',
      path: window.location.pathname,
    }
  },

  getErrorMessage(error: ApiError): string {
    if (error.fieldErrors && error.fieldErrors.length > 0) {
      return error.fieldErrors.map(fe => `${fe.field}: ${fe.message}`).join(', ')
    }
    return error.message
  },
}
