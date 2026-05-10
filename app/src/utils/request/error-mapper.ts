import type { ApiResponse, RequestError } from './types'

export function networkFailure(detail?: unknown): RequestError {
  return { code: -1, message: '网络连接失败，请检查网络设置', detail }
}

export function requestFailure(detail?: unknown): RequestError {
  return { code: -1, message: '网络请求失败', detail }
}

export function businessFailure(statusCode: number, data?: Partial<ApiResponse>): RequestError {
  return {
    code: data?.code || statusCode,
    message: data?.message || '请求失败',
    detail: data,
  }
}

export function httpFailure(statusCode: number, data?: Partial<ApiResponse>): RequestError {
  if (statusCode === 403) {
    return { code: 403, message: '没有权限访问该资源' }
  }
  if (statusCode === 404) {
    return { code: 404, message: '请求的资源不存在' }
  }
  if (statusCode >= 500) {
    return { code: statusCode, message: '服务器错误，请稍后重试' }
  }
  return { code: statusCode, message: data?.message || '请求失败', detail: data }
}
