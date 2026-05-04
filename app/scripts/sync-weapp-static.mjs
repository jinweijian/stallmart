import { cpSync, existsSync, mkdirSync, rmSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { resolve } from 'node:path'

const projectRoot = resolve(fileURLToPath(new URL('..', import.meta.url)))
const sourceDir = resolve(projectRoot, 'src/static')
const targetDir = resolve(projectRoot, 'dist/static')

if (!existsSync(sourceDir)) {
  console.warn('No src/static directory found, skip syncing weapp static assets')
  process.exit(0)
}

if (existsSync(targetDir)) {
  rmSync(targetDir, { recursive: true, force: true })
}
mkdirSync(targetDir, { recursive: true })
cpSync(sourceDir, targetDir, { recursive: true })

console.log('Synced weapp static assets to dist/static')
