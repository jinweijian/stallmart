import type { ApiRequest } from '~/api/client'
import type { Asset, StoreDecoration } from '~/types/admin'

const DECORATION_IMAGE_MAX_SIZE = 10 * 1024 * 1024

export const createDecorationApi = (request: ApiRequest) => ({
  uploadDecorationImage: (file: File) => {
    if (file.size > DECORATION_IMAGE_MAX_SIZE) {
      throw new Error('装修图片不能超过 10MB')
    }
    const body = new FormData()
    body.append('file', file)
    return request<Asset>('/admin/vendor/me/assets/decoration-image', {
      method: 'POST',
      body,
    })
  },
  vendorDecoration: () => request<StoreDecoration>('/admin/vendor/me/decoration'),
  updateDecoration: (payload: {
    styleId?: number
    logoUrl?: string
    coverUrl?: string | null
    banners?: string[]
    description?: string
  }) =>
    request<StoreDecoration>('/admin/vendor/me/decoration', {
      method: 'PUT',
      body: payload,
    }),
})
