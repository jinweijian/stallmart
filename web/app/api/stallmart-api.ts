import type {
  AdminSummary,
  ApiResult,
  Cart,
  Order,
  Product,
  ProductInput,
  Store,
  StoreDecoration,
  UserProfile,
  VendorWorkspace,
} from '~/types/admin'

type FetchOptions = Parameters<typeof $fetch>[1]

export const useStallmartApi = () => {
  const config = useRuntimeConfig()
  const baseURL = String(config.public.apiBase).replace(/\/$/, '')

  const request = async <T>(path: string, options: FetchOptions = {}) => {
    const response = await $fetch<ApiResult<T>>(`${baseURL}${path}`, {
      ...options,
      headers: {
        'X-User-Id': '2',
        ...(options.headers || {}),
      },
    })
    if (response.code !== 200) {
      throw new Error(response.message || '请求失败')
    }
    return response.data
  }

  return {
    platformSummary: () => request<AdminSummary>('/admin/platform/summary'),
    platformVendors: () => request<Store[]>('/admin/platform/vendors'),
    platformVendor: (storeId: number) => request<VendorWorkspace>(`/admin/platform/vendors/${storeId}/summary`),
    vendorSummary: () => request<VendorWorkspace>('/admin/vendor/me/summary'),
    vendorStore: () => request<Store>('/admin/vendor/me/store'),
    updateVendorStore: (payload: { name?: string, description?: string, logoUrl?: string, coverUrl?: string | null, status?: string }) =>
      request<Store>('/admin/vendor/me/store', {
      method: 'PUT',
      body: payload,
    }),
    vendorProducts: () => request<Product[]>('/admin/vendor/me/products'),
    createProduct: (payload: ProductInput) => request<Product>('/admin/vendor/me/products', {
      method: 'POST',
      body: payload,
    }),
    updateProduct: (productId: number, payload: ProductInput) => request<Product>(`/admin/vendor/me/products/${productId}`, {
      method: 'PUT',
      body: payload,
    }),
    vendorOrders: () => request<Order[]>('/admin/vendor/me/orders'),
    transitionOrder: (orderId: number, action: string) => request<Order>(`/admin/vendor/me/orders/${orderId}/${action}`, {
      method: 'PUT',
    }),
    vendorUsers: () => request<UserProfile[]>('/admin/vendor/me/users'),
    vendorCarts: () => request<Cart[]>('/admin/vendor/me/carts'),
    vendorDecoration: () => request<StoreDecoration>('/admin/vendor/me/decoration'),
    updateDecoration: (payload: { styleId?: number, logoUrl?: string, coverUrl?: string | null, description?: string }) =>
      request<StoreDecoration>('/admin/vendor/me/decoration', {
        method: 'PUT',
        body: payload,
      }),
  }
}
