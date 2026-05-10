import { ENABLE_API_MOCK } from '@/config'
import { getMockApiResponse } from '@/mock/customer-api'
import type { ApiResponse, RequestOptions } from './types'

export function resolveMockResponse(options: RequestOptions): ApiResponse | null {
  if (!ENABLE_API_MOCK) {
    return null
  }

  return getMockApiResponse(options)
}
