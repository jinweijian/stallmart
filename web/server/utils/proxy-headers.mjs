const CORS_REQUEST_HEADERS = new Set([
  'access-control-request-headers',
  'access-control-request-method',
  'origin',
])

export function toSameOriginProxyHeaders(input = {}) {
  const headers = {}
  const entries = typeof input.entries === 'function'
    ? input.entries()
    : Array.isArray(input)
      ? input
      : Object.entries(input)

  for (const [key, value] of entries) {
    if (value === undefined || CORS_REQUEST_HEADERS.has(key.toLowerCase())) {
      continue
    }
    headers[key] = value
  }

  return headers
}

export function sameOriginProxyFetch(input, init = {}) {
  return fetch(input, {
    ...init,
    headers: toSameOriginProxyHeaders(init.headers),
  })
}
