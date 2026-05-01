import { getRouterParam, proxyRequest } from 'h3'

export default defineEventHandler((event) => {
  const config = useRuntimeConfig()
  const path = getRouterParam(event, 'path') || ''
  const target = `${String(config.apiProxyTarget).replace(/\/$/, '')}/api/v1/uploads/${path}`

  return proxyRequest(event, target)
})
