import type { ApiRequest } from '~/api/client'
import type { Order } from '~/types/admin'

export const createOrderApi = (request: ApiRequest) => ({
  vendorOrders: () => request<Order[]>('/admin/vendor/me/orders'),
  vendorOrder: (orderId: number) => request<Order>(`/admin/vendor/me/orders/${orderId}`),
  transitionOrder: (orderId: number, action: string) => request<Order>(`/admin/vendor/me/orders/${orderId}/${action}`, {
    method: 'PUT',
  }),
})
