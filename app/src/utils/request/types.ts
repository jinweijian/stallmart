export type RequestMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'

export interface RequestOptions {
  url: string
  method?: RequestMethod
  data?: any
  params?: Record<string, string | number>
  header?: Record<string, string>
  skipAuth?: boolean
  timeout?: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

export interface RequestError {
  code: number
  message: string
  detail?: unknown
}
