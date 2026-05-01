import type {
  AdminLoginInput,
  AdminSession,
  AdminSummary,
  ApiResult,
  Asset,
  Cart,
  Category,
  CategoryInput,
  Order,
  Product,
  ProductInput,
  Store,
  StoreDecoration,
  Spec,
  SpecInput,
  UserProfile,
  VendorWorkspace,
} from '~/types/admin'

type FetchOptions = Parameters<typeof $fetch>[1]
const PRODUCT_IMAGE_MAX_SIZE = 10 * 1024 * 1024

export const useStallmartApi = () => {
  const config = useRuntimeConfig()
  const baseURL = String(config.public.apiBase).replace(/\/$/, '')

  const request = async <T>(path: string, options: FetchOptions = {}, appendAuthToken = true, retried = false) => {
    const { getAccessToken, selectedStoreId } = useAdminAuth()
    const token = appendAuthToken ? getAccessToken() : null
    const fetchOptions = {
      ...options,
      headers: {
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(selectedStoreId.value ? { 'X-Store-Id': String(selectedStoreId.value) } : {}),
        ...(options.headers || {}),
      },
    }

    try {
      const response = await $fetch<ApiResult<T>>(`${baseURL}${path}`, fetchOptions)
      if (response.code !== 200) {
        throw new Error(response.message || '请求失败')
      }
      return response.data
    } catch (error: any) {
      if (appendAuthToken && !retried && error?.response?.status === 401) {
        const auth = useAdminAuth()
        const nextSession = await auth.refresh()
        if (nextSession?.accessToken) {
          return request<T>(path, options, appendAuthToken, true)
        }
      }
      throw error
    }
  }

  return {
    login: (payload: AdminLoginInput) => request<AdminSession>('/admin/auth/login', {
      method: 'POST',
      body: payload,
    }, false),
    refresh: (refreshToken: string) => request<AdminSession>('/admin/auth/refresh', {
      method: 'POST',
      body: { refreshToken },
    }, false),
    me: () => request<AdminSession>('/admin/auth/me'),
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
    vendorProduct: (productId: number) => request<Product>(`/admin/vendor/me/products/${productId}`),
    vendorCategories: (module = 'PRODUCT') => request<Category[]>(`/admin/vendor/me/categories?module=${encodeURIComponent(module)}`),
    createCategory: (payload: CategoryInput) => request<Category>('/admin/vendor/me/categories', {
      method: 'POST',
      body: payload,
    }),
    uploadProductImage: (file: File) => {
      if (file.size > PRODUCT_IMAGE_MAX_SIZE) {
        throw new Error('商品主图不能超过 10MB')
      }
      const body = new FormData()
      body.append('file', file)
      return request<Asset>('/admin/vendor/me/assets/product-image', {
        method: 'POST',
        body,
      })
    },
    uploadDecorationImage: (file: File) => {
      if (file.size > PRODUCT_IMAGE_MAX_SIZE) {
        throw new Error('装修图片不能超过 10MB')
      }
      const body = new FormData()
      body.append('file', file)
      return request<Asset>('/admin/vendor/me/assets/decoration-image', {
        method: 'POST',
        body,
      })
    },
    createProduct: (payload: ProductInput) => request<Product>('/admin/vendor/me/products', {
      method: 'POST',
      body: payload,
    }),
    updateProduct: (productId: number, payload: ProductInput) => request<Product>(`/admin/vendor/me/products/${productId}`, {
      method: 'PUT',
      body: payload,
    }),
    productOnSale: (productId: number) => request<Product>(`/admin/vendor/me/products/${productId}/on-sale`, {
      method: 'PUT',
    }),
    productOffSale: (productId: number) => request<Product>(`/admin/vendor/me/products/${productId}/off-sale`, {
      method: 'PUT',
    }),
    productSoldOut: (productId: number) => request<Product>(`/admin/vendor/me/products/${productId}/sold-out`, {
      method: 'PUT',
    }),
    vendorOrders: () => request<Order[]>('/admin/vendor/me/orders'),
    vendorOrder: (orderId: number) => request<Order>(`/admin/vendor/me/orders/${orderId}`),
    transitionOrder: (orderId: number, action: string) => request<Order>(`/admin/vendor/me/orders/${orderId}/${action}`, {
      method: 'PUT',
    }),
    vendorUsers: () => request<UserProfile[]>('/admin/vendor/me/users'),
    vendorUserOrders: (userId: number) => request<Order[]>(`/admin/vendor/me/users/${userId}/orders`),
    vendorCarts: () => request<Cart[]>('/admin/vendor/me/carts'),
    vendorDecoration: () => request<StoreDecoration>('/admin/vendor/me/decoration'),
    updateDecoration: (payload: {
      styleId?: number
      logoUrl?: string
      coverUrl?: string | null
      banners?: string[]
      description?: string
      colors?: Record<string, string>
      iconUrls?: Record<string, string>
      imageUrls?: Record<string, string>
      copywriting?: Record<string, string>
    }) =>
      request<StoreDecoration>('/admin/vendor/me/decoration', {
        method: 'PUT',
        body: payload,
      }),
    vendorSpecs: () => request<Spec[]>('/admin/vendor/me/specs'),
    createSpec: (payload: SpecInput) => request<Spec>('/admin/vendor/me/specs', {
      method: 'POST',
      body: payload,
    }),
    updateSpec: (specId: number, payload: SpecInput) => request<Spec>(`/admin/vendor/me/specs/${specId}`, {
      method: 'PUT',
      body: payload,
    }),
    deleteSpec: (specId: number) => request<void>(`/admin/vendor/me/specs/${specId}`, {
      method: 'DELETE',
    }),
  }
}
