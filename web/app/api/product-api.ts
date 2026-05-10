import type { ApiRequest } from '~/api/client'
import type { Asset, Category, CategoryInput, Product, ProductInput, Spec, SpecInput } from '~/types/admin'

const PRODUCT_IMAGE_MAX_SIZE = 10 * 1024 * 1024

export const createProductApi = (request: ApiRequest) => ({
  vendorProducts: () => request<Product[]>('/admin/vendor/me/products'),
  vendorProduct: (productId: number) => request<Product>(`/admin/vendor/me/products/${productId}`),
  vendorCategories: (module = 'PRODUCT') => request<Category[]>(`/admin/vendor/me/categories?module=${encodeURIComponent(module)}`),
  createCategory: (payload: CategoryInput) => request<Category>('/admin/vendor/me/categories', {
    method: 'POST',
    body: payload,
  }),
  updateCategory: (categoryId: number, payload: CategoryInput) => request<Category>(`/admin/vendor/me/categories/${categoryId}`, {
    method: 'PUT',
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
})
