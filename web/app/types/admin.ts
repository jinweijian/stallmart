export interface ApiResult<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface AdminSummary {
  storeCount: number
  userCount: number
  orderCount: number
  cartCount: number
  salesAmount: number
}

export interface Store {
  id: number
  ownerId: number
  styleId: number
  styleCode: string
  name: string
  category: string
  description: string
  avatarUrl: string
  coverUrl: string | null
  qrCode: string
  address: string
  status: string
}

export interface Product {
  id: number
  storeId: number
  name: string
  description: string
  price: number
  imageUrl: string | null
  category: string
  status: string
  sortOrder: number
}

export interface ProductInput {
  name: string
  description: string
  price: number
  imageUrl: string | null
  category: string
  status: string
  sortOrder: number
}

export interface Style {
  id: number
  name: string
  code: string
  previewUrl: string | null
}

export interface StoreDecoration {
  storeId: number
  storeName: string
  logoUrl: string
  coverUrl: string | null
  styleId: number
  styleCode: string
  style: Style
}

export interface OrderItem {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  specsText: string | null
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  storeId: number
  status: string
  confirmCode: string
  totalAmount: number
  remark: string | null
  createdAt: string
  items: OrderItem[]
}

export interface CartItem {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  specsText: string | null
}

export interface Cart {
  id: number
  userId: number
  storeId: number
  status: string
  updatedAt: string
  items: CartItem[]
}

export interface UserProfile {
  id: number
  nickname: string
  avatarUrl: string
  phone: string | null
  hasPhone: boolean
  role: string
}

export interface VendorWorkspace {
  store: Store
  decoration: StoreDecoration
  products: Product[]
  orders: Order[]
  carts: Cart[]
  users: UserProfile[]
  styles: Style[]
  orderCount: number
  cartCount: number
  salesAmount: number
}
