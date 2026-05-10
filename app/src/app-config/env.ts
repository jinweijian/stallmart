export const APP_ENV = process.env.NODE_ENV || 'development'

export const API_BASE_URL_DEV = 'http://localhost:8080/api/v1'
export const API_BASE_URL_PROD = 'https://api.stallmart.com/api/v1'

export const API_BASE_URL = process.env.TARO_APP_API_BASE_URL || (APP_ENV === 'production' ? API_BASE_URL_PROD : API_BASE_URL_DEV)

// Local preview mode defaults to mock. Set TARO_APP_ENABLE_API_MOCK=false for H5/API integration.
export const ENABLE_API_MOCK = process.env.TARO_APP_ENABLE_API_MOCK === 'false' ? false : true
