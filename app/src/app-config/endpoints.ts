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
  APP_BOOTSTRAP: '/app/bootstrap',
  STORE_GET_BY_QR: (qrCode: string) => `/stores/qr/${qrCode}`,
  STORE_DETAIL: (id: string) => `/stores/${id}`,

  // Products
  PRODUCTS: (storeId: string) => `/stores/${storeId}/products`,
  PRODUCT_DETAIL: (id: string) => `/products/${id}`,
  STYLE_SPECS: (styleId: string | number) => `/styles/${styleId}/specs`,

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
