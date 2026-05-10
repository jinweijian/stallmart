import Taro from '@tarojs/taro'
import { buildUrl } from './request/url-builder'
import { createRequestHeader } from './request/headers'
import { resolveMockResponse } from './request/mock-adapter'
import { businessFailure, httpFailure, networkFailure, requestFailure } from './request/error-mapper'
import { refreshTokenAndRetry } from './request/token-refresh'
import type { ApiResponse, RequestOptions } from './request/types'

export type { ApiResponse, RequestOptions } from './request/types'

function isSuccessfulBusinessResponse(data?: ApiResponse): data is ApiResponse {
  return data?.code === 200 || data?.code === 0
}

async function handleResponse(
  response: Taro.request.SuccessCallbackResult<ApiResponse>,
  options: RequestOptions
): Promise<ApiResponse> {
  const { statusCode, data, errMsg } = response

  if (statusCode === undefined || errMsg) {
    throw networkFailure(errMsg)
  }

  if (statusCode >= 200 && statusCode < 300) {
    if (isSuccessfulBusinessResponse(data)) {
      return data
    }
    throw businessFailure(statusCode, data)
  }

  if (statusCode === 401) {
    return refreshTokenAndRetry(options, doRequest)
  }

  throw httpFailure(statusCode, data)
}

async function doRequest(options: RequestOptions, token?: string): Promise<ApiResponse> {
  const { url, method = 'GET', data, params, header = {}, timeout, skipAuth } = options
  const mockResponse = resolveMockResponse(options)
  if (mockResponse) {
    return mockResponse
  }

  return new Promise((resolve, reject) => {
    Taro.request({
      url: buildUrl(url, params),
      method,
      data,
      header: createRequestHeader(header, skipAuth, token),
      timeout: timeout || 30000,
      success: (res) => {
        handleResponse(res as Taro.request.SuccessCallbackResult<ApiResponse>, options)
          .then(resolve)
          .catch(reject)
      },
      fail: (error) => {
        reject(requestFailure(error))
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
