import type { UserConfig } from '@tarojs/cli'
import { defineConfig } from 'taro-plugin-vue3'
import type { IConfig } from '@tarojs/service'

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

  plugins: [
    '@tarojs/plugin-framework-vue3',
  ],

  alias: {
    '@': 'C:/Users/myclaw/.openclaw/workspace/projects/StallMart/src/mini-program/src',
    '@/config': 'C:/Users/myclaw/.openclaw/workspace/projects/StallMart/src/mini-program/src/app-config',
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
  
  webpackChain (chain, data) {
    const { entry } = data
    // Force resolve.alias to work around @ scope issue
    chain.resolve.alias
      .set('@', 'C:/Users/myclaw/.openclaw/workspace/projects/StallMart/src/mini-program/src')
      .set('@/config', 'C:/Users/myclaw/.openclaw/workspace/projects/StallMart/src/mini-program/src/app-config')
  },
} as UserConfig)
