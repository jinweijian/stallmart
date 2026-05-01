import { existsSync, readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { resolve } from 'node:path'

const projectRoot = resolve(fileURLToPath(new URL('..', import.meta.url)))
const requiredFiles = [
  'dist/static/default-avatar.png',
  'dist/pages/customer/my/my.wxss',
]

const failures = []

for (const file of requiredFiles) {
  if (!existsSync(resolve(projectRoot, file))) {
    failures.push(`Missing build output: ${file}`)
  }
}

const myWxssPath = resolve(projectRoot, 'dist/pages/customer/my/my.wxss')
if (existsSync(myWxssPath)) {
  const myWxss = readFileSync(myWxssPath, 'utf8')
  if (myWxss.includes('[data-v-')) {
    failures.push('customer/my wxss contains scoped data-v selectors that do not match the WeChat runtime output')
  }
}

if (failures.length > 0) {
  console.error(failures.join('\n'))
  process.exit(1)
}

console.log('Weapp assets and page styles are present')
