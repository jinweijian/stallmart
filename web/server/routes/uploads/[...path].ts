import { getRouterParam, proxyRequest } from 'h3'
import { sameOriginProxyFetch } from '../../utils/proxy-headers.mjs'

export default defineEventHandler((event) => {
  const config = useRuntimeConfig()
  const path = getRouterParam(event, 'path') || ''
  const target = `${String(config.apiProxyTarget).replace(/\/$/, '')}/api/v1/uploads/${path}`

  return proxyRequest(event, target, { fetch: sameOriginProxyFetch })
})
