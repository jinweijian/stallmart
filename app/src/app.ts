import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Taro from '@tarojs/taro'
import { API_ENDPOINTS } from '@/config'
import { get } from '@/utils/request'
import { createThemeFromStoreDecoration, persistCustomerTheme } from '@/utils/customer-theme'
import './app.scss'

// Global error handler
const errorHandler = (err: Error, instance: any) => {
  console.error('[Global Error]', err)
  Taro.showToast({ title: '系统错误，请稍后重试', icon: 'none', duration: 2000 })
}

const unhandledRejection = (reason: any) => {
  console.error('[Unhandled Rejection]', reason)
}

function resolveAppId() {
  if (process.env.TARO_APP_ID) {
    return process.env.TARO_APP_ID
  }
  try {
    return Taro.getAccountInfoSync?.().miniProgram?.appId || 'wx-stallmart-demo'
  } catch {
    return 'wx-stallmart-demo'
  }
}

async function loadBootstrapTheme() {
  const appId = resolveAppId()
  try {
    const response = await get<any>(API_ENDPOINTS.APP_BOOTSTRAP, undefined, {
      skipAuth: true,
      params: { appId },
      header: { 'X-App-Id': appId },
    })
    const decoration = response.data?.storefront?.decoration
    if (decoration) {
      persistCustomerTheme(createThemeFromStoreDecoration(decoration))
    }
  } catch (error) {
    console.warn('[App] Bootstrap theme failed, cached/default theme will be used:', error)
  }
}

const App = createApp({
  onLaunch(options: Taro.AppLaunchShowOption) {
    console.log('[App] onLaunch', options)

    // Initialize pinia
    const pinia = createPinia()
    App.use(pinia)
    void loadBootstrapTheme()
  },

  onShow(options: Taro.AppLaunchShowOption) {
    console.log('[App] onShow', options)

    // Handle scan QR code entry
    if (options.scene === 1047 || options.scene === 1048) {
      const qrCode = options.query?.qrCode
      if (qrCode) {
        Taro.eventCenter.trigger('qrcode-entry', qrCode)
      }
    }
  },

  onHide() {
    console.log('[App] onHide')
  },

  onError(err: string) {
    console.error('[App Error]', err)
  },

  onPageNotFound(result: Taro.PageNotFoundResult) {
    console.log('[App] Page not found', result)
    Taro.redirectTo({ url: '/pages/customer/index/index' })
  },
})

App.config.errorHandler = errorHandler
App.config.unhandledRejection = unhandledRejection

export default App
