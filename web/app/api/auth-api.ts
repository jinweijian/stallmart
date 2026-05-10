import type { ApiRequest } from '~/api/client'
import type { AdminLoginInput, AdminSession } from '~/types/admin'

export const createAuthApi = (request: ApiRequest) => ({
  login: (payload: AdminLoginInput) => request<AdminSession>('/admin/auth/login', {
    method: 'POST',
    body: payload,
  }, false),
  refresh: (refreshToken: string) => request<AdminSession>('/admin/auth/refresh', {
    method: 'POST',
    body: { refreshToken },
  }, false),
  me: () => request<AdminSession>('/admin/auth/me'),
})
