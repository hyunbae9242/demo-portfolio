import { useState, useCallback } from 'react'
import { ApiError, AsyncState } from '../types/common'
import { errorHandler } from '../utils/errorHandler'

export const useAsync = <T,>() => {
  const [state, setState] = useState<AsyncState<T>>({
    data: null,
    loading: false,
    error: null,
  })

  const execute = useCallback(async (asyncFunction: () => Promise<T>) => {
    setState({ data: null, loading: true, error: null })

    try {
      const data = await asyncFunction()
      setState({ data, loading: false, error: null })
      return data
    } catch (error) {
      const apiError = errorHandler.handle(error)
      setState({ data: null, loading: false, error: apiError })
      throw error
    }
  }, [])

  const reset = useCallback(() => {
    setState({ data: null, loading: false, error: null })
  }, [])

  return { ...state, execute, reset }
}
