import type { UserConfig } from '@tarojs/cli'
import { defineConfig } from 'taro-plugin-vue3'
import path from 'node:path'

const resolveSrc = (...segments: string[]) => path.resolve(__dirname, 'src', ...segments)
const defineEnv = (name: string, fallback = '') => JSON.stringify(process.env[name] || fallback)
const h5AssetBudgetBytes = 650 * 1024
const h5EntrypointBudgetBytes = 900 * 1024

export default defineConfig({
  appid: process.env.WECHAT_APP_ID || 'touristappid',
  appType: 'vue3',
  framework: 'vue3',
  platform: 'weapp',
  compiler: {
    type: 'webpack5',
    prebundle: {
      enable: false,
    },
  },
  env: {
    TARO_APP_API_BASE_URL: defineEnv('TARO_APP_API_BASE_URL'),
    TARO_APP_ID: defineEnv('TARO_APP_ID'),
  },

  projectName: 'stallmart-mini',

  designWidth: 750,
  deviceRatio: {
    640: 2.34 / 2,
    750: 1,
    828: 1.81 / 2,
  },

  sourceRoot: 'src',
  outputRoot: 'dist',

  plugins: ['@tarojs/plugin-framework-vue3'],

  copy: {
    patterns: [
      {
        from: resolveSrc('static'),
        to: path.resolve(__dirname, 'dist/static'),
      },
    ],
    options: {},
  },

  alias: {
    '@': resolveSrc(),
    '@/config': resolveSrc('app-config'),
  },

  mini: {
    compile: {
      exclude: ['**/node_modules/**'],
    },
    hot: true,
    terser: {
      enable: false,
    },
    babel: {
      include: ['src/**/*'],
      exclude: ['node_modules/**'],
    },
    postcss: {
      pxtransform: {
        enable: true,
        config: {
          selectorBlackList: ['van-', 'nut-'],
        },
      },
      cssModules: {
        enable: false,
      },
    },
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
} as UserConfig)
