export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_INFO: 'user_info',
  VIEW_MODE: 'view_mode',
  CART: 'cart',
} as const

export const TOKEN_EXPIRY = {
  ACCESS_TOKEN: 2 * 60 * 60 * 1000,
  REFRESH_TOKEN: 7 * 24 * 60 * 60 * 1000,
} as const
