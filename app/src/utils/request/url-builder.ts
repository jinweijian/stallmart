import { API_BASE_URL } from '@/config'

export function buildUrl(url: string, params?: Record<string, string | number>): string {
  const baseUrl = `${API_BASE_URL}${url}`

  if (!params || Object.keys(params).length === 0) {
    return baseUrl
  }

  const queryString = Object.entries(params)
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&')

  return `${baseUrl}?${queryString}`
}
