import { readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, join } from 'node:path'
import assert from 'node:assert/strict'

const here = dirname(fileURLToPath(import.meta.url))
const appRoot = join(here, '..')
const pkg = JSON.parse(readFileSync(join(appRoot, 'package.json'), 'utf8'))

const expectedVersion = '4.2.0'
const taroPackages = [
  '@tarojs/api',
  '@tarojs/components',
  '@tarojs/plugin-framework-react',
  '@tarojs/plugin-framework-vue3',
  '@tarojs/plugin-platform-weapp',
  '@tarojs/runtime',
  '@tarojs/taro',
  '@tarojs/cli',
  '@tarojs/plugin-platform-h5',
  '@tarojs/webpack5-runner',
]

for (const name of taroPackages) {
  const version = pkg.dependencies?.[name] ?? pkg.devDependencies?.[name]
  assert.equal(version, expectedVersion, `${name} must be pinned to ${expectedVersion}`)
}
