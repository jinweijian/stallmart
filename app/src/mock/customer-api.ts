import type { ApiResponse, RequestOptions } from '@/utils/request'

type MockMethod = NonNullable<RequestOptions['method']>

interface MockStoreVO {
  id: number
  name: string
  description: string
  avatarUrl: string
  qrCode: string
  styleId: number
  styleCode: 'forestFruitTeaCrayon'
  status: 'active' | 'closed'
  branchName: string
  distance: string
  heroEyebrow: string
  heroTitle: string
  heroSubtitle: string
}

interface MockProductVO {
  id: number
  storeId: number
  name: string
  description: string
  price: number
  imageUrl: string
  specIds: string
  status: 'active' | 'off_sale' | 'sold_out'
  sortOrder: number
  category: string
  tags: string[]
  rank?: number
  isNew?: boolean
  illustration: string
  stock: number
  sales: number
}

interface MockOrderItemVO {
  id: number
  productId: number
  productName: string
  price: number
  quantity: number
  subtotal: number
  specName?: string
}

interface MockOrderVO {
  id: number
  orderNo: string
  customerId: number
  storeId: number
  storeName: string
  totalAmount: number
  status: 'pending' | 'accepted' | 'preparing' | 'ready' | 'completed' | 'rejected'
  confirmCode: string
  remark?: string
  createdAt: string
  items: MockOrderItemVO[]
}

const mockStore: MockStoreVO = {
  id: 1,
  name: '小新の水果茶屋',
  description: '自然水果 · 新鲜现制',
  avatarUrl: '/static/default-avatar.png',
  qrCode: 'qr-fruit-tea',
  styleId: 6,
  styleCode: 'forestFruitTeaCrayon',
  status: 'active',
  branchName: '上海环球港店',
  distance: '1.3km',
  heroEyebrow: '小新の',
  heroTitle: '水果茶屋',
  heroSubtitle: '自然水果 · 新鲜现制',
}

const mockProducts: MockProductVO[] = [
  {
    id: 101,
    storeId: 1,
    name: '霸气西柚柠檬茶',
    description: '西柚果肉 + 香水柠檬 + 茉莉绿茶',
    price: 18,
    imageUrl: '',
    specIds: '1,2',
    status: 'active',
    sortOrder: 1,
    category: 'citrus',
    tags: ['清爽解腻', '维C满满'],
    rank: 1,
    isNew: true,
    illustration: '🍹',
    stock: 30,
    sales: 1280,
  },
  {
    id: 102,
    storeId: 1,
    name: '阳光青提多多',
    description: '阳光玫瑰青提 + 乳酸菌 + 绿茶',
    price: 16,
    imageUrl: '',
    specIds: '1,3',
    status: 'active',
    sortOrder: 2,
    category: 'grape',
    tags: ['清甜多汁', '人气TOP'],
    rank: 2,
    illustration: '🥤',
    stock: 42,
    sales: 956,
  },
  {
    id: 103,
    storeId: 1,
    name: '芒芒百香绿茶',
    description: '大颗芒果肉 + 百香果 + 茉莉绿茶',
    price: 17,
    imageUrl: '',
    specIds: '2,3',
    status: 'active',
    sortOrder: 3,
    category: 'mango',
    tags: ['香甜浓郁', '果肉满满'],
    rank: 3,
    illustration: '🧋',
    stock: 26,
    sales: 820,
  },
  {
    id: 104,
    storeId: 1,
    name: '整颗柠檬冰茶桶',
    description: '香水柠檬 + 冰萃绿茶 + 清甜果露',
    price: 24,
    imageUrl: '',
    specIds: '4',
    status: 'active',
    sortOrder: 4,
    category: 'tea',
    tags: ['大杯分享', '冰爽'],
    isNew: true,
    illustration: '🍋',
    stock: 18,
    sales: 620,
  },
  {
    id: 105,
    storeId: 1,
    name: '葡萄冻冻加料',
    description: '手作葡萄冻',
    price: 4,
    imageUrl: '',
    specIds: '5',
    status: 'active',
    sortOrder: 5,
    category: 'extra',
    tags: ['Q弹', '推荐搭配'],
    illustration: '🍇',
    stock: 88,
    sales: 360,
  },
]

const mockOrders: MockOrderVO[] = [
  {
    id: 501,
    orderNo: 'ORD202604300001',
    customerId: 9,
    storeId: 1,
    storeName: mockStore.name,
    totalAmount: 34,
    status: 'ready',
    confirmCode: 'A108',
    remark: '少冰',
    createdAt: '2026-04-30T12:30:00',
    items: [
      { id: 9001, productId: 101, productName: '霸气西柚柠檬茶', price: 18, quantity: 1, subtotal: 18, specName: '标准杯' },
      { id: 9002, productId: 102, productName: '阳光青提多多', price: 16, quantity: 1, subtotal: 16, specName: '少糖' },
    ],
  },
  {
    id: 502,
    orderNo: 'ORD202604300002',
    customerId: 9,
    storeId: 1,
    storeName: mockStore.name,
    totalAmount: 18,
    status: 'preparing',
    confirmCode: 'B205',
    createdAt: '2026-04-30T12:10:00',
    items: [
      { id: 9003, productId: 101, productName: '霸气西柚柠檬茶', price: 18, quantity: 1, subtotal: 18 },
    ],
  },
  {
    id: 503,
    orderNo: 'ORD202604290003',
    customerId: 9,
    storeId: 1,
    storeName: mockStore.name,
    totalAmount: 17,
    status: 'completed',
    confirmCode: 'C033',
    createdAt: '2026-04-29T18:05:00',
    items: [
      { id: 9004, productId: 103, productName: '芒芒百香绿茶', price: 17, quantity: 1, subtotal: 17 },
    ],
  },
]

function ok<T>(data: T): ApiResponse<T> {
  return {
    code: 200,
    message: 'success',
    data,
  }
}

function createOrderResponse(data: unknown): MockOrderVO {
  const payload = (data || {}) as { remark?: string }
  return {
    id: 600,
    orderNo: 'ORD202604300099',
    customerId: 9,
    storeId: 1,
    storeName: mockStore.name,
    totalAmount: 34,
    status: 'pending',
    confirmCode: 'X099',
    remark: payload.remark,
    createdAt: new Date().toISOString(),
    items: mockOrders[0].items,
  }
}

export function getMockApiResponse(options: RequestOptions): ApiResponse | null {
  const method = (options.method || 'GET') as MockMethod
  const url = options.url

  if (method === 'GET' && /^\/stores\/\d+$/.test(url)) {
    return ok(mockStore)
  }

  if (method === 'GET' && /^\/stores\/\d+\/products$/.test(url)) {
    return ok(mockProducts)
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
      pending: mockOrders.filter((order) => order.status === 'pending').length,
      inProgress: mockOrders.filter((order) => ['accepted', 'preparing', 'ready'].includes(order.status)).length,
      completed: mockOrders.filter((order) => order.status === 'completed').length,
      total: mockOrders.length,
    })
  }

  if (method === 'GET' && url === '/user/profile') {
    return ok({
      id: 9,
      nickname: '小新',
      avatarUrl: '/static/default-avatar.png',
      phone: '138****8000',
      hasPhone: true,
      role: 'customer',
    })
  }

  return null
}
