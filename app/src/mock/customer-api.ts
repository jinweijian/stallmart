import type { ApiResponse, RequestOptions } from '@/utils/request'
import {
  DEFAULT_STORE_THEME,
  type StorefrontAssetSizes,
  type StorefrontCategoryConfig,
  type StorefrontCategoryIconConfig,
} from '@/config'

type MockMethod = NonNullable<RequestOptions['method']>
type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
type OrderStatus = 'NEW' | 'ACCEPTED' | 'PREPARING' | 'READY' | 'COMPLETED' | 'REJECTED'

interface MockStoreDecorationVO {
  storeId: number
  storeName: string
  logoUrl: string
  coverUrl: string
  banners: string[]
  styleId: number
  styleCode: 'forestFruitTeaCrayon'
  style?: unknown
  layoutVersion?: string
  theme?: unknown
  colors: Record<string, string>
  iconNames: Record<string, string>
  iconUrls: Record<string, string>
  imageUrls: Record<string, string>
  copywriting: Record<string, string>
  categoryIconLibrary?: ReadonlyArray<StorefrontCategoryIconConfig>
  categories: ReadonlyArray<StorefrontCategoryConfig>
  assetSizes?: StorefrontAssetSizes
  pageThemes?: unknown
}

interface MockStoreVO {
  id: number
  ownerId: number
  styleId: number
  styleCode: 'forestFruitTeaCrayon'
  name: string
  category: string
  description: string
  avatarUrl: string
  coverUrl: string
  qrCode: string
  address: string
  status: 'OPEN' | 'CLOSED'
  decoration: MockStoreDecorationVO
  branchName?: string
  distance?: string
  heroEyebrow?: string
  heroTitle?: string
  heroSubtitle?: string
}

interface MockProductSkuVO {
  id: number
  specValues: string[]
  price: number
  stock: number
  status: ProductStatus
}

interface MockProductVO {
  id: number
  storeId: number
  categoryId: number
  categoryName: string
  name: string
  description: string
  price: number
  imageUrl: string
  mainImageUrl: string
  category: string
  status: ProductStatus
  sortOrder: number
  specIds: number[]
  skus: MockProductSkuVO[]
  tags?: string[]
  rank?: number
  isNew?: boolean
  illustration?: string
  stock?: number
  sales?: number
}

interface MockSpecVO {
  id: number
  styleId: number
  name: string
  specType: 'SIZE' | 'SWEET' | 'ICE' | 'OTHER'
  required: boolean
  options: string[]
}

interface MockOrderItemVO {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  specsText?: string
}

interface MockOrderVO {
  id: number
  orderNo: string
  userId: number
  storeId: number
  status: OrderStatus
  confirmCode: string
  totalAmount: number
  remark?: string
  createdAt: string
  items: MockOrderItemVO[]
}

interface MockCartVO {
  id: number
  userId: number
  storeId: number
  status: 'ACTIVE'
  updatedAt: string
  items: MockOrderItemVO[]
}

const mockCategoryIconLibrary = DEFAULT_STORE_THEME.categoryIconLibrary || []

const mockCategories: StorefrontCategoryConfig[] = [
  { id: 'recommend', name: '人气推荐', iconKey: 'recommend', iconUrl: '/static/storefront/forest/icons/recommend.png', fallbackText: '荐', sortOrder: 0, status: 'ACTIVE' },
  { id: '1', name: '清爽柠檬', iconKey: 'category1', iconUrl: '/static/storefront/forest/icons/category-1.png', fallbackText: '柠', sortOrder: 1, status: 'ACTIVE' },
  { id: '2', name: '多肉葡萄', iconKey: 'category2', iconUrl: '/static/storefront/forest/icons/category-2.png', fallbackText: '葡', sortOrder: 2, status: 'ACTIVE' },
  { id: '3', name: '香甜芒果', iconKey: 'mango', iconUrl: '/static/storefront/forest/icons/mango.png', fallbackText: '芒', sortOrder: 3, status: 'ACTIVE' },
]

const mockSpecs: MockSpecVO[] = [
  { id: 1, styleId: 6, name: '杯型', specType: 'SIZE', required: true, options: ['中杯', '大杯'] },
  { id: 2, styleId: 6, name: '甜度', specType: 'SWEET', required: true, options: ['少糖', '标准糖'] },
]

const mockStore: MockStoreVO = {
  id: 1,
  ownerId: 2,
  styleId: 6,
  styleCode: 'forestFruitTeaCrayon',
  name: '小新の水果茶屋',
  category: '饮品',
  description: '自然水果 · 新鲜现制',
  avatarUrl: '/static/default-avatar.png',
  coverUrl: '/static/storefront/forest/cover.png',
  qrCode: 'stall-001',
  address: '上海环球港店',
  status: 'OPEN',
  branchName: '上海环球港店',
  distance: '1.3km',
  heroEyebrow: '小新の',
  heroTitle: '水果茶屋',
  heroSubtitle: '自然水果 · 新鲜现制',
  decoration: {
    storeId: 1,
    storeName: '小新の水果茶屋',
    logoUrl: '/static/default-avatar.png',
    coverUrl: '/static/storefront/forest/cover.png',
    banners: ['/static/storefront/forest/banner-seasonal.jpg', '/static/storefront/forest/banner-tea.jpg'],
    styleId: 6,
    styleCode: 'forestFruitTeaCrayon',
    layoutVersion: DEFAULT_STORE_THEME.layoutVersion,
    theme: DEFAULT_STORE_THEME,
    colors: {
      primary: DEFAULT_STORE_THEME.primary,
      secondary: DEFAULT_STORE_THEME.secondary,
      accent: DEFAULT_STORE_THEME.accent,
      background: DEFAULT_STORE_THEME.background,
      surface: DEFAULT_STORE_THEME.surface,
      text: DEFAULT_STORE_THEME.text,
      mutedText: DEFAULT_STORE_THEME.mutedText,
      border: DEFAULT_STORE_THEME.border,
      price: DEFAULT_STORE_THEME.price || DEFAULT_STORE_THEME.primary,
    },
    iconNames: DEFAULT_STORE_THEME.iconNames || {},
    iconUrls: DEFAULT_STORE_THEME.iconUrls || {},
    imageUrls: DEFAULT_STORE_THEME.imageUrls || {},
    copywriting: DEFAULT_STORE_THEME.copywriting || {},
    categoryIconLibrary: mockCategoryIconLibrary,
    categories: mockCategories,
    assetSizes: DEFAULT_STORE_THEME.assetSizes,
    pageThemes: DEFAULT_STORE_THEME.pageThemes,
  },
}

const mockProducts: MockProductVO[] = [
  {
    id: 1,
    storeId: 1,
    categoryId: 1,
    categoryName: '清爽柠檬',
    name: '柚子柠檬茶',
    description: '西柚果肉 + 香水柠檬 + 茉莉绿茶',
    price: 18,
    imageUrl: '/static/storefront/forest/product-placeholder.png',
    mainImageUrl: '/static/storefront/forest/product-placeholder.png',
    category: '清爽柠檬',
    status: 'ACTIVE',
    sortOrder: 1,
    specIds: [1, 2],
    skus: [
      { id: 1, specValues: ['中杯', '少糖'], price: 16, stock: 99, status: 'ACTIVE' },
      { id: 2, specValues: ['大杯', '标准糖'], price: 18, stock: 99, status: 'ACTIVE' },
    ],
    tags: ['清爽解腻', '维C满满'],
    rank: 1,
    isNew: true,
    illustration: '🍹',
    stock: 99,
    sales: 1280,
  },
  {
    id: 2,
    storeId: 1,
    categoryId: 2,
    categoryName: '多肉葡萄',
    name: '阳光青提多多',
    description: '阳光玫瑰青提 + 乳酸菌 + 绿茶',
    price: 16,
    imageUrl: '/static/storefront/forest/product-placeholder.png',
    mainImageUrl: '/static/storefront/forest/product-placeholder.png',
    category: '多肉葡萄',
    status: 'ACTIVE',
    sortOrder: 2,
    specIds: [1, 2],
    skus: [
      { id: 3, specValues: ['中杯', '标准糖'], price: 16, stock: 99, status: 'ACTIVE' },
      { id: 4, specValues: ['大杯', '标准糖'], price: 19, stock: 99, status: 'ACTIVE' },
    ],
    tags: ['清甜多汁', '人气TOP'],
    rank: 2,
    illustration: '🥤',
    stock: 99,
    sales: 956,
  },
  {
    id: 3,
    storeId: 1,
    categoryId: 3,
    categoryName: '香甜芒果',
    name: '芒果百香绿茶',
    description: '大颗芒果肉 + 百香果 + 茉莉绿茶',
    price: 17,
    imageUrl: '/static/storefront/forest/product-placeholder.png',
    mainImageUrl: '/static/storefront/forest/product-placeholder.png',
    category: '香甜芒果',
    status: 'ACTIVE',
    sortOrder: 3,
    specIds: [1, 2],
    skus: [
      { id: 5, specValues: ['中杯', '少糖'], price: 17, stock: 99, status: 'ACTIVE' },
      { id: 6, specValues: ['大杯', '标准糖'], price: 20, stock: 99, status: 'ACTIVE' },
    ],
    tags: ['香甜浓郁', '果肉满满'],
    rank: 3,
    illustration: '🧋',
    stock: 99,
    sales: 820,
  },
]

const mockOrders: MockOrderVO[] = [
  {
    id: 501,
    orderNo: 'ORD202604300001',
    userId: 9,
    storeId: 1,
    status: 'READY',
    confirmCode: 'A108',
    totalAmount: 34,
    remark: '少冰',
    createdAt: '2026-04-30T12:30:00Z',
    items: [
      { productId: 1, productName: '柚子柠檬茶', quantity: 1, unitPrice: 18, specsText: '大杯 / 标准糖' },
      { productId: 2, productName: '阳光青提多多', quantity: 1, unitPrice: 16, specsText: '中杯 / 标准糖' },
    ],
  },
  {
    id: 502,
    orderNo: 'ORD202604300002',
    userId: 9,
    storeId: 1,
    status: 'PREPARING',
    confirmCode: 'B205',
    totalAmount: 18,
    createdAt: '2026-04-30T12:10:00Z',
    items: [
      { productId: 1, productName: '柚子柠檬茶', quantity: 1, unitPrice: 18, specsText: '大杯 / 标准糖' },
    ],
  },
  {
    id: 503,
    orderNo: 'ORD202604290003',
    userId: 9,
    storeId: 1,
    status: 'COMPLETED',
    confirmCode: 'C033',
    totalAmount: 17,
    createdAt: '2026-04-29T18:05:00Z',
    items: [
      { productId: 3, productName: '芒果百香绿茶', quantity: 1, unitPrice: 17, specsText: '中杯 / 少糖' },
    ],
  },
]

let mockCart: MockCartVO = {
  id: 1,
  userId: 9,
  storeId: 1,
  status: 'ACTIVE',
  updatedAt: new Date().toISOString(),
  items: [],
}

function ok<T>(data: T): ApiResponse<T> {
  return {
    code: 200,
    message: 'success',
    data,
  }
}

function createOrderResponse(data: unknown): MockOrderVO {
  const payload = (data || {}) as { storeId?: number; remark?: string; items?: Array<{ productId: number; quantity: number; specsText?: string }> }
  const items = (payload.items || []).map((item) => {
    const product = mockProducts.find((entry) => entry.id === Number(item.productId)) || mockProducts[0]
    return {
      productId: product.id,
      productName: product.name,
      quantity: item.quantity || 1,
      unitPrice: product.price,
      specsText: item.specsText,
    }
  })
  const totalAmount = items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0)

  return {
    id: 600,
    orderNo: 'ORD202604300099',
    userId: 9,
    storeId: payload.storeId || 1,
    status: 'NEW',
    confirmCode: 'X099',
    totalAmount,
    remark: payload.remark,
    createdAt: new Date().toISOString(),
    items,
  }
}

function addCartItem(data: unknown): MockCartVO {
  const payload = (data || {}) as { storeId?: number; productId?: number; quantity?: number; specsText?: string }
  const product = mockProducts.find((entry) => entry.id === Number(payload.productId)) || mockProducts[0]
  const quantity = Math.max(1, Number(payload.quantity || 1))
  const existing = mockCart.items.find((item) => item.productId === product.id && item.specsText === payload.specsText)

  if (existing) {
    existing.quantity += quantity
  } else {
    mockCart.items.push({
      productId: product.id,
      productName: product.name,
      quantity,
      unitPrice: product.price,
      specsText: payload.specsText,
    })
  }
  mockCart = { ...mockCart, storeId: payload.storeId || 1, updatedAt: new Date().toISOString() }
  return mockCart
}

export function getMockApiResponse(options: RequestOptions): ApiResponse | null {
  const method = (options.method || 'GET') as MockMethod
  const url = options.url

  if (method === 'GET' && /^\/stores\/\d+$/.test(url)) {
    return ok(mockStore)
  }

  if (method === 'GET' && /^\/stores\/qr\/[^/]+$/.test(url)) {
    return ok(mockStore)
  }

  if (method === 'GET' && /^\/stores\/\d+\/products$/.test(url)) {
    return ok(mockProducts)
  }

  if (method === 'GET' && /^\/products\/\d+$/.test(url)) {
    const productId = Number(url.split('/').pop())
    const product = mockProducts.find((item) => item.id === productId)
    return product ? ok(product) : ok(null)
  }

  if (method === 'GET' && /^\/styles\/\d+\/specs$/.test(url)) {
    const styleId = Number(url.split('/')[2])
    return ok(mockSpecs.filter((spec) => spec.styleId === styleId))
  }

  if (method === 'GET' && url === '/orders') {
    return ok(mockOrders)
  }

  if (method === 'POST' && url === '/orders') {
    return ok(createOrderResponse(options.data))
  }

  if (method === 'PUT' && /^\/orders\/\d+\/reject$/.test(url)) {
    return ok(null)
  }

  if (method === 'GET' && url === '/orders/counts') {
    return ok({
      total: mockOrders.length,
      pending: mockOrders.filter((order) => order.status === 'NEW' || order.status === 'ACCEPTED').length,
      preparing: mockOrders.filter((order) => order.status === 'PREPARING' || order.status === 'READY').length,
      completed: mockOrders.filter((order) => order.status === 'COMPLETED').length,
    })
  }

  if (method === 'GET' && url === '/cart') {
    return ok(mockCart)
  }

  if (method === 'POST' && url === '/cart/items') {
    return ok(addCartItem(options.data))
  }

  if (method === 'DELETE' && /^\/cart\/stores\/\d+$/.test(url)) {
    mockCart = { ...mockCart, items: [], updatedAt: new Date().toISOString() }
    return ok(null)
  }

  if (method === 'GET' && url === '/user/profile') {
    return ok({
      id: 9,
      nickname: '小新',
      avatarUrl: '/static/default-avatar.png',
      phone: '138****8000',
      hasPhone: true,
      role: 'CUSTOMER',
    })
  }

  return null
}
