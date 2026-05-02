const apiProxyTarget = process.env.NUXT_API_PROXY_TARGET || 'http://localhost:8081'
const apiBase = '/api/v1'

export default defineNuxtConfig({
  ssr: false,
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  css: ['~/assets/css/main.css'],
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
  runtimeConfig: {
    apiProxyTarget,
    public: {
      apiBase,
    },
  },
})
