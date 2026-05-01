import { createError, getRouterParam, setHeader } from 'h3'
import { createReadStream, existsSync } from 'node:fs'
import { extname, resolve, sep } from 'node:path'

const staticRoot = resolve(process.cwd(), '..', 'app', 'src', 'static')
const contentTypes: Record<string, string> = {
  '.gif': 'image/gif',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.png': 'image/png',
  '.svg': 'image/svg+xml',
  '.webp': 'image/webp',
}

export default defineEventHandler((event) => {
  const path = getRouterParam(event, 'path') || ''
  const target = resolve(staticRoot, path)

  if (!target.startsWith(`${staticRoot}${sep}`) || !existsSync(target)) {
    throw createError({ statusCode: 404, statusMessage: 'Static asset not found' })
  }

  setHeader(event, 'Content-Type', contentTypes[extname(target).toLowerCase()] || 'application/octet-stream')
  setHeader(event, 'Cache-Control', 'public, max-age=3600')
  return createReadStream(target)
})
