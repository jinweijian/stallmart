/**
 * App Configuration
 * Contains API base URLs, app constants, and environment settings
 */

export const APP_ENV = import.meta.env.NODE_ENV || 'development'

// API Base URLs
export const API_BASE_URL_DEV = 'http://localhost:8080/api/v1'
export const API_BASE_URL_PROD = 'https://api.stallmart.com/api/v1'

export const API_BASE_URL = APP_ENV === 'production' ? API_BASE_URL_PROD : API_BASE_URL_DEV

// Token Keys
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_INFO: 'user_info',
  CART: 'cart',
} as const

// Token Expiration
export const TOKEN_EXPIRY = {
  ACCESS_TOKEN: 2 * 60 * 60 * 1000, // 2 hours in ms
  REFRESH_TOKEN: 7 * 24 * 60 * 60 * 1000, // 7 days in ms
} as const

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  AUTH_WECHAT_LOGIN: '/auth/wechat/login',
  AUTH_REFRESH: '/auth/refresh',
  AUTH_LOGOUT: '/auth/logout',
  AUTH_BIND_PHONE: '/auth/phone/bind',

  // User
  USER_PROFILE: '/user/profile',
  USER_UPDATE: '/user/profile',

  // Store
  STORE_GET: '/stores',
  STORE_GET_BY_QR: '/stores/qr',
  STORE_DETAIL: (id: string) => `/stores/${id}`,

  // Products
  PRODUCTS: (storeId: string) => `/stores/${storeId}/products`,
  PRODUCT_DETAIL: (id: string) => `/products/${id}`,

  // Orders
  ORDERS: '/orders',
  ORDER_DETAIL: (id: string) => `/orders/${id}`,
  ORDER_CREATE: '/orders',
  ORDER_STATUS: (id: string) => `/orders/${id}/status`,
  ORDER_ACCEPT: (id: string) => `/orders/${id}/accept`,
  ORDER_REJECT: (id: string) => `/orders/${id}/reject`,
  ORDER_PREPARE: (id: string) => `/orders/${id}/prepare`,
  ORDER_READY: (id: string) => `/orders/${id}/ready`,
  ORDER_COMPLETE: (id: string) => `/orders/${id}/complete`,

  // Vendor
  VENDOR_STALL: '/vendor/stall',
  VENDOR_ORDERS: '/vendor/orders',
  VENDOR_SETTINGS: '/vendor/settings',
} as const

// Design System Colors
export const COLORS = {
  PRIMARY: '#FF6B35',
  SECONDARY: '#2EC4B6',
  BACKGROUND: '#FFF8F0',
  FOREGROUND: '#333333',
  SURFACE: '#FFFFFF',
  MUTED: '#F5F5F5',
  BORDER: '#E5E5E5',
  TEXT_SECONDARY: '#666666',
  TEXT_MUTED: '#999999',
  SUCCESS: '#52C41A',
  WARNING: '#FAAD14',
  ERROR: '#F5222D',
  INFO: '#1890FF',
} as const

// WeChat Mini Program Config
export const WECHAT_CONFIG = {
  APP_NAME: '摊位商城',
  VERSION: '1.0.0',
  SHARE_TITLE: '摊位商城 - 发现身边的好物',
  SHARE_PATH: '/pages/customer/index/index',
  LOCATION_DESC: '你的位置信息将用于展示附近摊位',
} as const

// Pagination Defaults
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
} as const

// Cart Config
export const CART_CONFIG = {
  MAX_ITEMS: 99,
  MAX_PER_ITEM: 10,
} as const
