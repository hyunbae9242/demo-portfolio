export interface ApiError {
  timestamp: string
  status: number
  error: string
  code: string
  message: string
  path: string
  correlationId?: string
  fieldErrors?: FieldError[]
}

export interface FieldError {
  field: string
  message: string
  rejectedValue: any
}

export interface ApiResponse<T> {
  data: T
  error?: ApiError
}

export type AsyncState<T> = {
  data: T | null
  loading: boolean
  error: ApiError | null
}
