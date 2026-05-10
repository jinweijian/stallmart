import type { ApiRequest } from '~/api/client'
import type { AdminSummary, Store, Style, StyleInput, VendorWorkspace } from '~/types/admin'

export const createPlatformApi = (request: ApiRequest) => ({
  platformSummary: () => request<AdminSummary>('/admin/platform/summary'),
  platformVendors: () => request<Store[]>('/admin/platform/vendors'),
  platformVendor: (storeId: number) => request<VendorWorkspace>(`/admin/platform/vendors/${storeId}/summary`),
  platformStyles: () => request<Style[]>('/admin/platform/styles'),
  platformStyle: (styleId: number) => request<Style>(`/admin/platform/styles/${styleId}`),
  createPlatformStyle: (payload: StyleInput) => request<Style>('/admin/platform/styles', {
    method: 'POST',
    body: payload,
  }),
  updatePlatformStyle: (styleId: number, payload: StyleInput) => request<Style>(`/admin/platform/styles/${styleId}`, {
    method: 'PUT',
    body: payload,
  }),
  platformStylePublish: (styleId: number) => request<Style>(`/admin/platform/styles/${styleId}/publish`, {
    method: 'PUT',
  }),
  platformStyleUnpublish: (styleId: number) => request<Style>(`/admin/platform/styles/${styleId}/unpublish`, {
    method: 'PUT',
  }),
  deletePlatformStyle: (styleId: number) => request<void>(`/admin/platform/styles/${styleId}`, {
    method: 'DELETE',
  }),
})
