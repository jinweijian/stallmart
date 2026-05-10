import assert from 'node:assert/strict'

const { toSameOriginProxyHeaders } = await import('../server/utils/proxy-headers.mjs')

const headers = toSameOriginProxyHeaders({
  origin: 'http://127.0.0.1:8091',
  'access-control-request-method': 'POST',
  'access-control-request-headers': 'content-type',
  referer: 'http://127.0.0.1:8091/auth/login',
  'content-type': 'application/json',
  authorization: 'Bearer token',
})

assert.equal(headers.origin, undefined)
assert.equal(headers['access-control-request-method'], undefined)
assert.equal(headers['access-control-request-headers'], undefined)
assert.equal(headers.referer, 'http://127.0.0.1:8091/auth/login')
assert.equal(headers['content-type'], 'application/json')
assert.equal(headers.authorization, 'Bearer token')
