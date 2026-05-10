import Taro from '@tarojs/taro'
import { API_ENDPOINTS } from '@/config'
import { clearAuthTokens, getRefreshToken, setAccessToken } from '@/utils/storage'
import { useUserStore } from '@/store/user'
import { buildUrl } from './url-builder'
import type { ApiResponse, RequestOptions } from './types'

type RetryRequest = (options: RequestOptions, token: string) => Promise<ApiResponse>

let isRefreshing = false
let refreshSubscribers: Array<(token: string) => void> = []

function subscribeTokenRefresh(callback: (token: string) => void) {
  refreshSubscribers.push(callback)
}

function onTokenRefreshed(newToken: string) {
  refreshSubscribers.forEach((callback) => callback(newToken))
  refreshSubscribers = []
}

function clearSessionAfterRefreshFailure() {
  clearAuthTokens()
  const userStore = useUserStore()
  userStore.setUserInfo(null)
  Taro.eventCenter.trigger('auth:logout')
}

export async function refreshTokenAndRetry(
  options: RequestOptions,
  retryRequest: RetryRequest
): Promise<ApiResponse> {
  if (isRefreshing) {
    return new Promise((resolve, reject) => {
      subscribeTokenRefresh((token: string) => {
        retryRequest({ ...options, skipAuth: false }, token).then(resolve).catch(reject)
      })
    })
  }

  isRefreshing = true

  try {
    const refreshToken = getRefreshToken()
    if (!refreshToken) {
      throw new Error('No refresh token')
    }

    const refreshResponse = await Taro.request({
      url: buildUrl(API_ENDPOINTS.AUTH_REFRESH),
      method: 'POST',
      data: { refreshToken },
      header: { 'Content-Type': 'application/json' },
    })

    const refreshData = refreshResponse.data as ApiResponse<{ accessToken?: string }>
    if ((refreshData?.code === 200 || refreshData?.code === 0) && refreshData?.data?.accessToken) {
      const newToken = refreshData.data.accessToken
      setAccessToken(newToken)
      onTokenRefreshed(newToken)
      return retryRequest({ ...options, skipAuth: false }, newToken)
    }

    throw new Error('Refresh failed')
  } catch {
    clearSessionAfterRefreshFailure()
    throw { code: 401, message: '登录已过期，请重新登录' }
  } finally {
    isRefreshing = false
  }
}
