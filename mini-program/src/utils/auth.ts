import Taro from '@tarojs/taro'
import { API_BASE_URL, API_ENDPOINTS } from '@/config'
import { 
  getAccessToken, 
  getRefreshToken, 
  setAccessToken, 
  setRefreshToken, 
  clearAuthTokens,
  setStorage,
} from './storage'
import { request } from './request'
import { useUserStore } from '@/store/user'

export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  user: {
    id: string
    openid: string
    nickname: string
    avatar: string
    phone?: string
    role: 'customer' | 'vendor' | 'admin'
  }
}

export interface RefreshResult {
  accessToken: string
  expiresIn: number
}

let isRefreshing = false
let refreshSubscribers: Array<(token: string) => void> = []

/**
 * Subscribe to token refresh
 */
function subscribeTokenRefresh(callback: (token: string) => void) {
  refreshSubscribers.push(callback)
}

/**
 * Notify all subscribers of new token
 */
function onTokenRefreshed(newToken: string) {
  refreshSubscribers.forEach((callback) => callback(newToken))
  refreshSubscribers = []
}

/**
 * WeChat Login - Get code from WeChat
 */
export function wxLogin(): Promise<string> {
  return new Promise((resolve, reject) => {
    Taro.login({
      success: (res) => {
        if (res.code) {
          resolve(res.code)
        } else {
          reject(new Error('Failed to get WeChat code'))
        }
      },
      fail: reject,
    })
  })
}

/**
 * Get user profile with WeChat permission
 */
export function getUserProfile(): Promise<WechatMiniprogram.UserProfile> {
  return new Promise((resolve, reject) => {
    Taro.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        resolve(res)
      },
      fail: reject,
    })
  })
}

/**
 * Server-side login with WeChat code
 */
export async function loginWithWechat(code: string, userInfo?: { nickname: string; avatar: string }): Promise<LoginResult> {
  try {
    const response = await request({
      url: API_ENDPOINTS.AUTH_WECHAT_LOGIN,
      method: 'POST',
      data: {
        code,
        nickname: userInfo?.nickname,
        avatar: userInfo?.avatar,
      },
    })

    const result = response.data as LoginResult

    // Store tokens
    setAccessToken(result.accessToken)
    setRefreshToken(result.refreshToken)

    // Store user info
    const userStore = useUserStore()
    userStore.setUserInfo({
      id: result.user.id,
      openid: result.user.openid,
      nickname: result.user.nickname,
      avatar: result.user.avatar,
      phone: result.user.phone || '',
      role: result.user.role,
      createdAt: new Date().toISOString(),
    })

    return result
  } catch (error) {
    console.error('[Auth] Login failed:', error)
    throw error
  }
}

/**
 * Refresh access token
 */
export async function refreshAccessToken(): Promise<RefreshResult | null> {
  if (isRefreshing) {
    // Wait for refresh to complete
    return new Promise((resolve) => {
      subscribeTokenRefresh((token) => {
        resolve({ accessToken: token, expiresIn: 7200 })
      })
    })
  }

  isRefreshing = true

  try {
    const refreshToken = getRefreshToken()
    if (!refreshToken) {
      throw new Error('No refresh token')
    }

    const response = await request({
      url: API_ENDPOINTS.AUTH_REFRESH,
      method: 'POST',
      data: { refreshToken },
      skipAuth: true, // Skip auth header for refresh request
    })

    const result = response.data as RefreshResult
    setAccessToken(result.accessToken)
    
    isRefreshing = false
    onTokenRefreshed(result.accessToken)
    
    return result
  } catch (error) {
    isRefreshing = false
    clearAuthTokens()
    
    // Navigate to login
    const userStore = useUserStore()
    userStore.logout()
    
    return null
  }
}

/**
 * Check and refresh token if needed
 */
export async function checkAndRefreshToken(): Promise<string | null> {
  const token = getAccessToken()
  
  if (!token) {
    return null
  }

  // Check token expiry from storage
  const tokenExpiry = Taro.getStorageSync('token_expiry') as number
  if (tokenExpiry && Date.now() < tokenExpiry - 60000) {
    // Token still valid (with 1 min buffer)
    return token
  }

  // Try to refresh
  const result = await refreshAccessToken()
  return result?.accessToken || null
}

/**
 * Bind phone number
 */
export async function bindPhone(phoneCode: string): Promise<boolean> {
  try {
    await request({
      url: API_ENDPOINTS.USER_BIND_PHONE,
      method: 'POST',
      data: { code: phoneCode },
    })
    
    // Refresh user info
    const userStore = useUserStore()
    await userStore.loadUserInfo()
    
    return true
  } catch (error) {
    console.error('[Auth] Bind phone failed:', error)
    return false
  }
}

/**
 * Get phone number from WeChat
 */
export function getPhoneNumber(event: any): Promise<string | null> {
  return new Promise((resolve) => {
    if (event.detail?.code) {
      resolve(event.detail.code)
    } else {
      resolve(null)
    }
  })
}

/**
 * Logout
 */
export async function logout(): Promise<void> {
  try {
    await request({
      url: API_ENDPOINTS.AUTH_LOGOUT,
      method: 'POST',
    })
  } catch (e) {
    // Ignore errors, clear local state anyway
  }
  
  clearAuthTokens()
  
  const userStore = useUserStore()
  userStore.logout()
}
