import type { UserConfig } from '@tarojs/cli'
import { defineConfig } from 'taro-plugin-vue3'
import path from 'node:path'

const resolveSrc = (...segments: string[]) => path.resolve(__dirname, 'src', ...segments)

export default defineConfig({
  appType: 'vue3',
  framework: 'vue3',
  platform: 'weapp',
  compiler: 'webpack5',

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
        from: resolveSrc('static/default-avatar.png'),
        to: path.resolve(__dirname, 'dist/static/default-avatar.png'),
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
  },

  webpackChain(chain) {
    chain.resolve.alias.set('@', resolveSrc()).set('@/config', resolveSrc('app-config'))
  },
} as UserConfig)
