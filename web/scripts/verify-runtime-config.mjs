import { readFileSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { dirname, join } from 'node:path'
import assert from 'node:assert/strict'

const here = dirname(fileURLToPath(import.meta.url))
const root = join(here, '..', '..')

const read = (...parts) => readFileSync(join(root, ...parts), 'utf8')

const nuxtConfig = read('web', 'nuxt.config.ts')
const dockerCompose = read('docker', 'docker-compose.yml')

assert.match(nuxtConfig, /apiBase\s*=\s*['"]\/api\/v1['"]|apiBase:\s*['"]\/api\/v1['"]/, 'public apiBase must stay same-origin /api/v1')
assert.doesNotMatch(nuxtConfig, /NUXT_PUBLIC_API_BASE/, 'public runtime config must not expose backend URL overrides')
assert.doesNotMatch(nuxtConfig, /proxy:\s*`\$\{apiProxyTarget\}\/api\/v1\/\*\*`/, 'proxy must not duplicate /api/v1 for /api/v1 requests')
assert.doesNotMatch(dockerCompose, /NUXT_PUBLIC_API_BASE/, 'Docker must not inject a browser-visible backend API URL')
assert.match(dockerCompose, /NUXT_API_PROXY_TARGET:\s*http:\/\/api:8080/, 'Docker admin-web must proxy to the API service over the compose network')
