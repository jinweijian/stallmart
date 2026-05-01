/**
 * App Configuration
 * Contains API base URLs, app constants, and environment settings
 */

export const APP_ENV = process.env.NODE_ENV || 'development'

// API Base URLs
export const API_BASE_URL_DEV = 'http://localhost:8080/api/v1'
export const API_BASE_URL_PROD = 'https://api.stallmart.com/api/v1'

export const API_BASE_URL = APP_ENV === 'production' ? API_BASE_URL_PROD : API_BASE_URL_DEV

// Local preview mode: keep true until WeChat request legal domains are configured.
export const ENABLE_API_MOCK = true

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

// Storefront style packages. Backend can return one of these style codes on store detail.
export const STORE_STYLE_CODES = {
  HAWAIIAN: 'hawaiian',
  BBQ: 'BBQ',
  MARKET: 'market',
  OCEAN: 'ocean',
  FRESH: 'fresh',
  FOREST_FRUIT_TEA_CRAYON: 'forestFruitTeaCrayon',
} as const

export type StoreStyleCode = (typeof STORE_STYLE_CODES)[keyof typeof STORE_STYLE_CODES]

export interface StorefrontCategoryConfig {
  id: string
  name: string
  iconName: string
  iconUrl?: string | null
  fallbackText: string
}

export interface StoreThemePackage {
  code: StoreStyleCode
  name: string
  layoutVersion?: string
  primary: string
  secondary: string
  accent: string
  background: string
  surface: string
  text: string
  mutedText: string
  border: string
  price?: string
  heroGradient: string
  iconNames?: Record<string, string>
  iconUrls?: Record<string, string>
  imageUrls?: Record<string, string>
  copywriting?: Record<string, string>
  categories?: ReadonlyArray<StorefrontCategoryConfig>
}

export const STORE_THEME_PACKAGES: Record<StoreStyleCode, StoreThemePackage> = {
  hawaiian: {
    code: STORE_STYLE_CODES.HAWAIIAN,
    name: '夏威夷风',
    primary: '#2ECC71',
    secondary: '#F9D66E',
    accent: '#FF8E5E',
    background: '#FEF9E7',
    surface: '#FFFFFF',
    text: '#2D3436',
    mutedText: '#7A7A6A',
    border: '#E8E2C8',
    heroGradient: 'linear-gradient(135deg, #FEF9E7 0%, #DFF5C8 100%)',
  },
  BBQ: {
    code: STORE_STYLE_CODES.BBQ,
    name: '烧烤风',
    primary: '#E74C3C',
    secondary: '#F39C12',
    accent: '#8E3B2D',
    background: '#FDEDEC',
    surface: '#FFFFFF',
    text: '#2D3436',
    mutedText: '#8A6A64',
    border: '#F0C8BE',
    heroGradient: 'linear-gradient(135deg, #FDEDEC 0%, #F7B08A 100%)',
  },
  market: {
    code: STORE_STYLE_CODES.MARKET,
    name: '市集风',
    primary: '#F39C12',
    secondary: '#8BC34A',
    accent: '#D35400',
    background: '#FFF8E7',
    surface: '#FFFFFF',
    text: '#2D3436',
    mutedText: '#7E725A',
    border: '#EADDBB',
    heroGradient: 'linear-gradient(135deg, #FFF8E7 0%, #FFE1A6 100%)',
  },
  ocean: {
    code: STORE_STYLE_CODES.OCEAN,
    name: '海洋风',
    primary: '#3498DB',
    secondary: '#2EC4B6',
    accent: '#FFB84D',
    background: '#EBF5FB',
    surface: '#FFFFFF',
    text: '#263A4A',
    mutedText: '#6D8090',
    border: '#CFE6F2',
    heroGradient: 'linear-gradient(135deg, #EBF5FB 0%, #C8EEF0 100%)',
  },
  fresh: {
    code: STORE_STYLE_CODES.FRESH,
    name: '清新风',
    primary: '#27AE60',
    secondary: '#B8E986',
    accent: '#F7C948',
    background: '#F4FCF8',
    surface: '#FFFFFF',
    text: '#263A2B',
    mutedText: '#6B806F',
    border: '#D7ECDD',
    heroGradient: 'linear-gradient(135deg, #F4FCF8 0%, #DFF5E6 100%)',
  },
  forestFruitTeaCrayon: {
    code: STORE_STYLE_CODES.FOREST_FRUIT_TEA_CRAYON,
    name: '森系水果茶',
    layoutVersion: 'customer-storefront-v1',
    primary: '#6F9646',
    secondary: '#B8C77A',
    accent: '#F2B94B',
    background: '#FBFAEF',
    surface: '#FFFDF4',
    text: '#4C6040',
    mutedText: '#7A866D',
    border: '#DCE6C7',
    price: '#6F9646',
    heroGradient: 'linear-gradient(180deg, #F9FAEA 0%, #EAF4D8 48%, #F9F2D8 100%)',
    iconNames: {
      location: 'forest-location',
      cart: 'forest-cart',
      checkout: 'forest-checkout',
      delivery: 'forest-delivery',
      sectionLeaf: 'forest-leaf',
    },
    iconUrls: {
      location: '/static/storefront/forest/icons/location.png',
      cart: '/static/storefront/forest/icons/cart.png',
      checkout: '/static/storefront/forest/icons/checkout.png',
      delivery: '/static/storefront/forest/icons/delivery.png',
      sectionLeaf: '/static/storefront/forest/icons/leaf.png',
    },
    imageUrls: {
      heroIllustration: '/static/storefront/forest/hero-forest-tea.png',
      mascot: '/static/storefront/forest/mascot.png',
      productPlaceholder: '/static/storefront/forest/product-placeholder.png',
      promoIllustration: '/static/storefront/forest/promo-drink.png',
    },
    copywriting: {
      branchName: '上海环球港店',
      heroEyebrow: '小新の',
      heroTitle: '水果茶屋',
      heroSubtitle: '自然水果 · 新鲜现制',
      promoTitle: '鲜果时令上新',
      promoSubtitle: '当季水果 · 清爽一夏',
      promoActionText: '立即尝鲜',
    },
    categories: [
      { id: 'recommend', name: '人气推荐', iconName: 'forest-recommend', iconUrl: '/static/storefront/forest/icons/recommend.png', fallbackText: '荐' },
      { id: 'citrus', name: '清爽柠檬', iconName: 'forest-citrus', iconUrl: '/static/storefront/forest/icons/citrus.png', fallbackText: '柠' },
      { id: 'grape', name: '多肉葡萄', iconName: 'forest-grape', iconUrl: '/static/storefront/forest/icons/grape.png', fallbackText: '葡' },
      { id: 'mango', name: '香甜芒果', iconName: 'forest-mango', iconUrl: '/static/storefront/forest/icons/mango.png', fallbackText: '芒' },
      { id: 'tea', name: '鲜果茶桶', iconName: 'forest-tea', iconUrl: '/static/storefront/forest/icons/tea.png', fallbackText: '茶' },
      { id: 'extra', name: '加料小料', iconName: 'forest-extra', iconUrl: '/static/storefront/forest/icons/extra.png', fallbackText: '料' },
    ],
  },
} as const

export const DEFAULT_STORE_THEME = STORE_THEME_PACKAGES.forestFruitTeaCrayon

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
