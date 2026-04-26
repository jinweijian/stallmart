import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Taro from '@tarojs/taro'
import { STORAGE_KEYS } from '@/config'

export interface UserInfo {
  id: string
  openid: string
  nickname: string
  avatar: string
  phone: string
  role: 'customer' | 'vendor' | 'admin'
  createdAt: string
}

export interface UserState {
  userInfo: UserInfo | null
  isLoggedIn: boolean
  isVendor: boolean
  isLoading: boolean
}

export const useUserStore = defineStore('user', () => {
  // State
  const userInfo = ref<UserInfo | null>(null)
  const isLoading = ref(false)

  // Getters
  const isLoggedIn = computed(() => !!userInfo.value)
  
  const isVendor = computed(() => userInfo.value?.role === 'vendor' || userInfo.value?.role === 'admin')
  
  const openid = computed(() => userInfo.value?.openid || '')
  
  const phone = computed(() => userInfo.value?.phone || '')

  // Actions
  function setUserInfo(info: UserInfo | null) {
    userInfo.value = info
    if (info) {
      Taro.setStorageSync(STORAGE_KEYS.USER_INFO, JSON.stringify(info))
    } else {
      Taro.removeStorageSync(STORAGE_KEYS.USER_INFO)
    }
  }

  function loadUserInfo() {
    try {
      const stored = Taro.getStorageSync(STORAGE_KEYS.USER_INFO)
      if (stored) {
        userInfo.value = JSON.parse(stored)
      }
    } catch (e) {
      console.error('[UserStore] Failed to load user info', e)
    }
  }

  function setLoading(loading: boolean) {
    isLoading.value = loading
  }

  function logout() {
    userInfo.value = null
    Taro.removeStorageSync(STORAGE_KEYS.ACCESS_TOKEN)
    Taro.removeStorageSync(STORAGE_KEYS.REFRESH_TOKEN)
    Taro.removeStorageSync(STORAGE_KEYS.USER_INFO)
    
    // Clear cart
    Taro.removeStorageSync(STORAGE_KEYS.CART)
    
    // Navigate to index
    Taro.reLaunch({ url: '/pages/customer/index/index' })
  }

  // Initialize on store creation
  loadUserInfo()

  return {
    // State
    userInfo,
    isLoading,
    // Getters
    isLoggedIn,
    isVendor,
    openid,
    phone,
    // Actions
    setUserInfo,
    loadUserInfo,
    setLoading,
    logout,
  }
})
