export interface Product {
  id: string
  storeId?: string | number
  name: string
  basePrice: number
  price?: number
  originalPrice: number
  image: string
  imageUrl?: string
  mainImageUrl?: string
  categoryId?: string | number
  categoryName?: string
  description?: string
  category: string
  stock: number
  sales: number
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
  specIds?: Array<string | number>
  skus?: ProductSku[]
  selectedSkuId?: string | number
  selectedSpecsText?: string
  isHot?: boolean
  isNew?: boolean
  tags?: string[]
  flavor?: string
  rank?: number
  illustration?: string
}

export interface ProductSku {
  id: string | number
  specValues: string[]
  price: number
  stock: number
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
}

export interface StorefrontSpec {
  id: string | number
  styleId: string | number
  name: string
  specType: 'SIZE' | 'SWEET' | 'ICE' | 'OTHER'
  required: boolean
  options: string[]
}

export interface SkuGroup {
  id: string
  name: string
  required: boolean
  options: string[]
}

export interface CartItem {
  product: Product
  quantity: number
}

export function filterProductsByCategory(activeCategory: string, products: Product[]): Product[] {
  if (activeCategory === 'recommend') {
    const recommended = products.filter((product) => product.isHot || product.rank)
    return recommended.length > 0 ? recommended : products
  }

  return products.filter((product) => product.category === activeCategory)
}

export function resolveSkuGroups(product: Product | null, specs: StorefrontSpec[]): SkuGroup[] {
  if (!product) return []

  return (product.specIds || [])
    .map((specId) => specs.find((spec) => String(spec.id) === String(specId)))
    .filter((spec): spec is StorefrontSpec => Boolean(spec))
    .map((spec) => ({
      id: String(spec.id),
      name: spec.name,
      required: spec.required,
      options: spec.options || [],
    }))
}

export function findSelectedSku(
  product: Product | null,
  groups: SkuGroup[],
  selectedOptions: Record<string, string>
): ProductSku | null {
  if (!product?.skus || product.skus.length === 0) return null
  if (groups.some((group) => group.required && !selectedOptions[group.id])) return null

  return product.skus.find((sku) =>
    sku.status === 'ACTIVE' &&
    groups.every((group, index) => sku.specValues[index] === selectedOptions[group.id])
  ) || null
}

export function buildSelectedSpecsText(groups: SkuGroup[], selectedOptions: Record<string, string>): string {
  return groups
    .map((group) => selectedOptions[group.id])
    .filter(Boolean)
    .join(' / ')
}

export function resolveDefaultSkuOptions(product: Product, specs: StorefrontSpec[]): Record<string, string> {
  const groups = resolveSkuGroups(product, specs)
  const activeSku = product.skus?.find((sku) => sku.status === 'ACTIVE' && sku.stock > 0)

  return groups.reduce<Record<string, string>>((options, group, index) => {
    const skuValue = activeSku?.specValues[index]
    options[group.id] = skuValue || group.options[0] || ''
    return options
  }, {})
}

export function isSkuOptionAvailable(
  product: Product | null,
  groups: SkuGroup[],
  selectedOptions: Record<string, string>,
  group: SkuGroup,
  option: string
): boolean {
  if (!product?.skus || product.skus.length === 0) return true
  const groupIndex = groups.findIndex((item) => item.id === group.id)
  if (groupIndex < 0) return true

  return product.skus.some((sku) => {
    if (sku.status !== 'ACTIVE' || sku.stock <= 0 || sku.specValues[groupIndex] !== option) return false
    return groups.every((otherGroup, index) => {
      if (index === groupIndex) return true
      const selected = selectedOptions[otherGroup.id]
      return !selected || sku.specValues[index] === selected
    })
  })
}

export function normalizeProduct(product: Product): Product {
  const normalizedSkus = (product.skus || []).map((sku) => ({
    ...sku,
    price: Number(sku.price || 0),
    stock: Number(sku.stock || 0),
    status: normalizeProductStatus(sku.status),
  }))
  const activeSkuPrices = normalizedSkus
    .filter((sku) => sku.status === 'ACTIVE')
    .map((sku) => sku.price)
    .filter((price) => price > 0)
  const lowestSkuPrice = activeSkuPrices.length > 0 ? Math.min(...activeSkuPrices) : undefined
  const totalSkuStock = normalizedSkus.reduce((sum, sku) => sum + Number(sku.stock || 0), 0)
  const basePrice = Number(product.basePrice ?? product.price ?? lowestSkuPrice ?? 0)
  const normalizedStatus = normalizeProductStatus(product.status)

  return {
    ...product,
    id: String(product.id),
    basePrice,
    originalPrice: product.originalPrice || basePrice,
    image: product.image || product.mainImageUrl || product.imageUrl || '',
    category: String(product.categoryId ?? product.category ?? 'citrus'),
    stock: product.stock ?? (totalSkuStock || 99),
    sales: product.sales ?? 0,
    status: normalizedStatus,
    specIds: product.specIds || [],
    skus: normalizedSkus,
    flavor: product.flavor || product.description || '',
    tags: product.tags || [],
    isHot: product.isHot ?? !!product.rank,
    illustration: product.illustration || '🥤',
  }
}

export function normalizeProductStatus(status: Product['status']): 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' {
  if (status === 'active' || status === 'ACTIVE') return 'ACTIVE'
  if (status === 'sold_out' || status === 'SOLD_OUT') return 'SOLD_OUT'
  return 'INACTIVE'
}

export function getStatusText(product: Product): string {
  const status = normalizeProductStatus(product.status)
  if (status === 'SOLD_OUT' || product.stock <= 0) return '已售罄'
  if (status === 'INACTIVE') return '已下架'
  return ''
}

export function isProductDisabled(product: Product): boolean {
  return normalizeProductStatus(product.status) !== 'ACTIVE' || product.stock <= 0
}

export function createSelectedCartProduct(
  product: Product,
  selectedSku: ProductSku | null,
  selectedPrice: number,
  selectedSpecsText: string
): Product {
  return {
    ...product,
    basePrice: selectedPrice,
    originalPrice: selectedPrice,
    selectedSkuId: selectedSku?.id,
    selectedSpecsText,
    stock: selectedSku?.stock ?? product.stock,
  }
}
