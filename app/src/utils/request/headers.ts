import Taro from '@tarojs/taro'
import { getAccessToken } from '@/utils/storage'

export function createRequestHeader(
  header: Record<string, string> = {},
  skipAuth = false,
  overrideToken?: string
): Record<string, string> {
  const requestHeader = {
    'Content-Type': 'application/json',
    'X-Client-Version': Taro.getSystemInfoSync().SDKVersion || '1.0.0',
    'X-Platform': 'weapp',
    ...header,
  }

  if (!skipAuth) {
    const token = overrideToken || getAccessToken()
    if (token) {
      requestHeader.Authorization = `Bearer ${token}`
    }
  }

  return requestHeader
}
