import Taro from '@tarojs/taro'
import { STORAGE_KEYS } from '@/config'

/**
 * Storage utility wrapper for WeChat Mini Program
 * Provides type-safe access to localStorage with error handling
 */

/**
 * Set item to storage
 */
export function setStorage<T>(key: string, value: T): Promise<boolean> {
  return new Promise((resolve, reject) => {
    try {
      const stringValue = typeof value === 'string' ? value : JSON.stringify(value)
      Taro.setStorage({
        key,
        data: stringValue,
        success: () => resolve(true),
        fail: (err) => {
          console.error(`[Storage] Failed to set ${key}:`, err)
          reject(err)
        },
      })
    } catch (e) {
      reject(e)
    }
  })
}

/**
 * Get item from storage
 */
export function getStorage<T>(key: string, defaultValue?: T): Promise<T | undefined> {
  return new Promise((resolve, reject) => {
    Taro.getStorage({
      key,
      success: (res) => {
        try {
          const data = res.data
          if (typeof data === 'string') {
            // Try to parse as JSON
            try {
              resolve(JSON.parse(data) as T)
            } catch {
              // Return as string if not valid JSON
              resolve(data as unknown as T)
            }
          } else {
            resolve(data as T)
          }
        } catch (e) {
          resolve(defaultValue)
        }
      },
      fail: () => {
        resolve(defaultValue)
      },
    })
  })
}

/**
 * Remove item from storage
 */
export function removeStorage(key: string): Promise<void> {
  return new Promise((resolve, reject) => {
    Taro.removeStorage({
      key,
      success: () => resolve(),
      fail: (err) => {
        console.error(`[Storage] Failed to remove ${key}:`, err)
        reject(err)
      },
    })
  })
}

/**
 * Clear all storage
 */
export function clearStorage(): Promise<void> {
  return new Promise((resolve, reject) => {
    Taro.clearStorage({
      success: () => resolve(),
      fail: (err) => {
        console.error('[Storage] Failed to clear storage:', err)
        reject(err)
      },
    })
  })
}

// ============================================
// Convenience wrappers for specific tokens
// ============================================

/**
 * Get access token
 */
export function getAccessToken(): string {
  return Taro.getStorageSync(STORAGE_KEYS.ACCESS_TOKEN) || ''
}

/**
 * Set access token
 */
export function setAccessToken(token: string): void {
  Taro.setStorageSync(STORAGE_KEYS.ACCESS_TOKEN, token)
}

/**
 * Get refresh token
 */
export function getRefreshToken(): string {
  return Taro.getStorageSync(STORAGE_KEYS.REFRESH_TOKEN) || ''
}

/**
 * Set refresh token
 */
export function setRefreshToken(token: string): void {
  Taro.setStorageSync(STORAGE_KEYS.REFRESH_TOKEN, token)
}

/**
 * Clear all auth tokens
 */
export function clearAuthTokens(): void {
  Taro.removeStorageSync(STORAGE_KEYS.ACCESS_TOKEN)
  Taro.removeStorageSync(STORAGE_KEYS.REFRESH_TOKEN)
}

/**
 * Check if user has valid tokens
 */
export function hasValidTokens(): boolean {
  const accessToken = getAccessToken()
  const refreshToken = getRefreshToken()
  return !!(accessToken && refreshToken)
}
