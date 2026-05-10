/**
 * Taro 项目配置文件
 * @description 用于 Taro CLI 识别项目根目录
 */
const path = require('path')

const resolveSrc = (...segments) => path.resolve(__dirname, '..', 'src', ...segments)
const defineEnv = (name, fallback = '') => JSON.stringify(process.env[name] || fallback)
const h5AssetBudgetBytes = 650 * 1024
const h5EntrypointBudgetBytes = 900 * 1024

module.exports = {
  appid: 'wxe4f198ad2958a1fe',
  projectName: 'stallmart-mini',
  dateVersion: '20260404',
  createTimestamp: Date.now(),
  sourceRoot: 'src',
  outputRoot: 'dist',
  framework: 'vue3',
  designWidth: 750,
  deviceRatio: {
    640: 2.34 / 2,
    750: 1,
    828: 1.81 / 2,
  },
  compiler: {
    type: 'webpack5',
    prebundle: {
      enable: false,
    },
  },
  env: {
    TARO_APP_API_BASE_URL: defineEnv('TARO_APP_API_BASE_URL'),
    TARO_APP_ENABLE_API_MOCK: defineEnv('TARO_APP_ENABLE_API_MOCK'),
    TARO_APP_ID: defineEnv('TARO_APP_ID'),
  },
  plugins: [],
  presets: [],
  alias: {
    '@': resolveSrc(),
    '@/config': resolveSrc('app-config'),
  },
  copy: {
    patterns: [
      {
        from: resolveSrc('static'),
        to: path.resolve(__dirname, '..', 'dist', 'static'),
      },
    ],
    options: {},
  },
  h5: {
    publicPath: '/',
    staticDirectory: 'static',
    router: {
      mode: 'hash',
    },
    devServer: {
      host: '0.0.0.0',
      port: Number(process.env.TARO_APP_H5_PORT || 10086),
      allowedHosts: 'all',
      client: {
        overlay: {
          errors: true,
          warnings: false,
        },
      },
    },
    webpackChain(chain) {
      chain.performance
        .hints('warning')
        .maxAssetSize(h5AssetBudgetBytes)
        .maxEntrypointSize(h5EntrypointBudgetBytes)
    },
  },
  webpackChain(chain) {
    chain.resolve.alias.set('@', resolveSrc()).set('@/config', resolveSrc('app-config'))
    chain.performance
      .hints('warning')
      .maxAssetSize(h5AssetBudgetBytes)
      .maxEntrypointSize(h5EntrypointBudgetBytes)
  },
}
