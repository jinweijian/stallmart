import { getRequestURL, proxyRequest } from 'h3'

export default defineEventHandler((event) => {
  const config = useRuntimeConfig()
  const url = getRequestURL(event)
  const target = `${String(config.apiProxyTarget).replace(/\/$/, '')}${url.pathname}${url.search}`

  return proxyRequest(event, target)
})
