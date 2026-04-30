/**
 * Taro 项目配置文件
 * @description 用于 Taro CLI 识别项目根目录
 */
const path = require('path')

const resolveSrc = (...segments) => path.resolve(__dirname, '..', 'src', ...segments)

module.exports = {
  appid: 'wxe4f198ad2958a1fe',
  projectName: 'stallmart-mini',
  dateVersion: '20260404',
  createTimestamp: Date.now(),
  sourceRoot: 'src',
  outputRoot: 'dist',
  framework: 'vue3',
  plugins: [],
  presets: [],
  alias: {
    '@': resolveSrc(),
    '@/config': resolveSrc('app-config'),
  },
  webpackChain(chain) {
    chain.resolve.alias.set('@', resolveSrc()).set('@/config', resolveSrc('app-config'))
  },
}
