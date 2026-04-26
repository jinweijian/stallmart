import Taro from '@tarojs/taro'
import { API_BASE_URL, API_ENDPOINTS } from '@/config'
import { getAccessToken, setAccessToken, getRefreshToken, clearAuthTokens } from './storage'
import { useUserStore } from '@/store/user'

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
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

let isRefreshing = false
let refreshSubscribers: Array<(token: string) => void> = []

function subscribeTokenRefresh(callback: (token: string) => void) {
  refreshSubscribers.push(callback)
}

function onTokenRefreshed(newToken: string) {
  refreshSubscribers.forEach((cb) => cb(newToken))
  refreshSubscribers = []
}

function buildUrl(url: string, params?: Record<string, string | number>): string {
  const baseUrl = `${API_BASE_URL}${url}`

  if (!params || Object.keys(params).length === 0) {
    return baseUrl
  }

  const queryString = Object.entries(params)
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&')

  return `${baseUrl}?${queryString}`
}

function requestInterceptor(config: any) {
  const { skipAuth, header = {} } = config

  config.header = {
    'Content-Type': 'application/json',
    'X-Client-Version': Taro.getSystemInfoSync().SDKVersion || '1.0.0',
    'X-Platform': 'weapp',
    ...header,
  }

  if (!skipAuth) {
    const token = getAccessToken()
    if (token) {
      config.header['Authorization'] = `Bearer ${token}`
    }
  }

  return config
}

function responseInterceptor(
  response: Taro.request.SuccessCallbackResult<ApiResponse>,
  options: RequestOptions
): Promise<ApiResponse> {
  return new Promise(async (resolve, reject) => {
    const { statusCode, data, errMsg } = response

    if (statusCode === undefined || errMsg) {
      reject({ code: -1, message: '网络连接失败，请检查网络设置', detail: errMsg })
      return
    }

    if (statusCode >= 200 && statusCode < 300) {
      if (data?.code === 0) {
        resolve(data)
      } else {
        reject({ code: data?.code || statusCode, message: data?.message || '请求失败', detail: data })
      }
      return
    }

    if (statusCode === 401) {
      if (isRefreshing) {
        return new Promise((res) => {
          subscribeTokenRefresh((token: string) => {
            doRequest({ ...options, skipAuth: false }, token).then(resolve).catch(reject)
          })
        })
      }

      isRefreshing = true

      try {
        const refreshToken = getRefreshToken()
        if (!refreshToken) throw new Error('No refresh token')

        const refreshResponse = await Taro.request({
          url: buildUrl(API_ENDPOINTS.AUTH_REFRESH),
          method: 'POST',
          data: { refreshToken },
          header: { 'Content-Type': 'application/json' },
        })

        const refreshData = refreshResponse.data as ApiResponse

        if (refreshData?.code === 0 && refreshData?.data?.accessToken) {
          const newToken = refreshData.data.accessToken
          setAccessToken(newToken)
          onTokenRefreshed(newToken)
          isRefreshing = false
          const retryResponse = await doRequest({ ...options, skipAuth: false }, newToken)
          resolve(retryResponse)
        } else {
          throw new Error('Refresh failed')
        }
      } catch {
        isRefreshing = false
        clearAuthTokens()
        const userStore = useUserStore()
        userStore.setUserInfo(null)
        Taro.eventCenter.trigger('auth:logout')
        reject({ code: 401, message: '登录已过期，请重新登录' })
      }
      return
    }

    if (statusCode === 403) {
      reject({ code: 403, message: '没有权限访问该资源' })
      return
    }

    if (statusCode === 404) {
      reject({ code: 404, message: '请求的资源不存在' })
      return
    }

    if (statusCode >= 500) {
      reject({ code: statusCode, message: '服务器错误，请稍后重试' })
      return
    }

    reject({ code: statusCode, message: data?.message || '请求失败', detail: data })
  })
}

async function doRequest(options: RequestOptions, token?: string): Promise<ApiResponse> {
  const { url, method = 'GET', data, params, header = {}, timeout } = options

  const fullUrl = buildUrl(url, params)

  const config: any = {
    url: fullUrl,
    method,
    data,
    header,
    timeout: timeout || 30000,
  }

  if (token) {
    config.header['Authorization'] = `Bearer ${token}`
  }

  const interceptedConfig = requestInterceptor(config)

  return new Promise((resolve, reject) => {
    Taro.request({
      ...interceptedConfig,
      success: (res) => {
        responseInterceptor(res as any, options).then(resolve).catch(reject)
      },
      fail: (err) => {
        reject({ code: -1, message: '网络请求失败', detail: err })
      },
    })
  })
}

export function request<T = any>(options: RequestOptions): Promise<ApiResponse<T>> {
  return doRequest(options) as Promise<ApiResponse<T>>
}

export function get<T = any>(
  url: string,
  data?: any,
  options?: Partial<RequestOptions>
): Promise<ApiResponse<T>> {
  return request<T>({ url, method: 'GET', data, ...options })
}

export function post<T = any>(
  url: string,
  data?: any,
  options?: Partial<RequestOptions>
): Promise<ApiResponse<T>> {
  return request<T>({ url, method: 'POST', data, ...options })
}

export function put<T = any>(
  url: string,
  data?: any,
  options?: Partial<RequestOptions>
): Promise<ApiResponse<T>> {
  return request<T>({ url, method: 'PUT', data, ...options })
}

export function del<T = any>(
  url: string,
  data?: any,
  options?: Partial<RequestOptions>
): Promise<ApiResponse<T>> {
  return request<T>({ url, method: 'DELETE', data, ...options })
}

export function patch<T = any>(
  url: string,
  data?: any,
  options?: Partial<RequestOptions>
): Promise<ApiResponse<T>> {
  return request<T>({ url, method: 'PATCH', data, ...options })
}

export default { request, get, post, put, delete: del, patch }
