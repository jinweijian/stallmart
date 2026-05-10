import type { ApiRequest } from '~/api/client'
import type { Cart, Order, Store, UserProfile, VendorWorkspace } from '~/types/admin'

export const createVendorApi = (request: ApiRequest) => ({
  vendorSummary: () => request<VendorWorkspace>('/admin/vendor/me/summary'),
  vendorStore: () => request<Store>('/admin/vendor/me/store'),
  updateVendorStore: (payload: { name?: string, description?: string, logoUrl?: string, coverUrl?: string | null, status?: string }) =>
    request<Store>('/admin/vendor/me/store', {
      method: 'PUT',
      body: payload,
    }),
  vendorUsers: () => request<UserProfile[]>('/admin/vendor/me/users'),
  vendorUserOrders: (userId: number) => request<Order[]>(`/admin/vendor/me/users/${userId}/orders`),
  vendorCarts: () => request<Cart[]>('/admin/vendor/me/carts'),
})
