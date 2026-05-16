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
  role: 'CUSTOMER' | 'VENDOR' | 'ADMIN'
  createdAt: string
}

export type ViewMode = 'CUSTOMER' | 'VENDOR'

export interface UserState {
  userInfo: UserInfo | null
  isLoggedIn: boolean
  isVendor: boolean
  viewMode: ViewMode
  hasVendorRole: boolean
  isLoading: boolean
}

export const useUserStore = defineStore('user', () => {
  // State
  const userInfo = ref<UserInfo | null>(null)
  const viewMode = ref<ViewMode>('CUSTOMER')
  const isLoading = ref(false)

  // Getters
  const isLoggedIn = computed(() => !!userInfo.value)
  const hasVendorRole = computed(() => userInfo.value?.role === 'VENDOR' || userInfo.value?.role === 'ADMIN')
  const isVendor = computed(() => viewMode.value === 'VENDOR')
  
  const openid = computed(() => userInfo.value?.openid || '')
  
  const phone = computed(() => userInfo.value?.phone || '')

  // Actions
  function setUserInfo(info: UserInfo | null) {
    userInfo.value = info
    if (info) {
      Taro.setStorageSync(STORAGE_KEYS.USER_INFO, JSON.stringify(info))
      if (!Taro.getStorageSync(STORAGE_KEYS.VIEW_MODE)) {
        setViewMode(info.role === 'VENDOR' || info.role === 'ADMIN' ? 'VENDOR' : 'CUSTOMER')
      }
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
    loadViewMode()
  }

  function loadViewMode() {
    try {
      const stored = Taro.getStorageSync(STORAGE_KEYS.VIEW_MODE)
      viewMode.value = stored === 'VENDOR' ? 'VENDOR' : 'CUSTOMER'
    } catch {
      viewMode.value = 'CUSTOMER'
    }
  }

  function setViewMode(mode: ViewMode) {
    viewMode.value = mode
    Taro.setStorageSync(STORAGE_KEYS.VIEW_MODE, mode)
    syncTabBarForViewMode()
  }

  function switchToVendorMode() {
    setViewMode('VENDOR')
  }

  function switchToCustomerMode() {
    setViewMode('CUSTOMER')
  }

  function ignoreTaroTabBarResult(action: () => unknown) {
    try {
      const result = action()
      if (result && typeof (result as { catch?: unknown }).catch === 'function') {
        void (result as Promise<unknown>).catch(() => undefined)
      }
    } catch {
      // H5 may throw plain objects for unsupported native tabBar APIs.
    }
  }

  function syncTabBarForViewMode() {
    const vendorMode = viewMode.value === 'VENDOR'
    ignoreTaroTabBarResult(() =>
      Taro.setTabBarItem({
        index: 1,
        text: vendorMode ? '商品' : '点单',
        iconPath: 'static/tabbar/home.png',
        selectedIconPath: 'static/tabbar/home-active.png',
      })
    )
    ignoreTaroTabBarResult(() =>
      Taro.setTabBarItem({
        index: 2,
        text: vendorMode ? '接单' : '订单',
        iconPath: 'static/tabbar/order.png',
        selectedIconPath: 'static/tabbar/order-active.png',
      })
    )
  }

  function setLoading(loading: boolean) {
    isLoading.value = loading
  }

  function logout() {
    userInfo.value = null
    Taro.removeStorageSync(STORAGE_KEYS.ACCESS_TOKEN)
    Taro.removeStorageSync(STORAGE_KEYS.REFRESH_TOKEN)
    Taro.removeStorageSync(STORAGE_KEYS.USER_INFO)
    setViewMode('CUSTOMER')
    
    // Clear cart
    Taro.removeStorageSync(STORAGE_KEYS.CART)
    
    // Navigate to index
    Taro.reLaunch({ url: '/pages/customer/index/index' })
  }

  // Initialize on store creation
  loadUserInfo()
  syncTabBarForViewMode()

  return {
    // State
    userInfo,
    viewMode,
    isLoading,
    // Getters
    isLoggedIn,
    isVendor,
    hasVendorRole,
    openid,
    phone,
    // Actions
    setUserInfo,
    loadUserInfo,
    loadViewMode,
    setViewMode,
    switchToVendorMode,
    switchToCustomerMode,
    syncTabBarForViewMode,
    setLoading,
    logout,
  }
})
