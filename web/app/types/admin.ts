export interface ApiResult<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface AdminSession {
  accessToken: string
  refreshToken: string
  user: UserProfile
  storeId: number | null
  entryPath: string
}

export interface AdminLoginInput {
  account: string
  password: string
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
  categoryId: number
  categoryName: string
  name: string
  description: string
  price: number
  imageUrl: string | null
  mainImageUrl: string | null
  category: string
  status: string
  sortOrder: number
  specIds: number[]
  skus: ProductSku[]
}

export interface ProductInput {
  name: string
  description: string
  price?: number
  categoryId: number | null
  imageUrl: string | null
  mainImageUrl: string | null
  category: string
  status: string
  sortOrder: number
  specIds: number[]
  skus: ProductSkuInput[]
}

export interface ProductSku {
  id: number
  specValues: string[]
  price: number
  stock: number
  status: string
}

export interface ProductSkuInput {
  specValues: string[]
  price: number
  stock: number
  status: string
}

export interface Category {
  id: number
  storeId: number
  module: string
  name: string
  sortOrder: number
  status: string
}

export interface CategoryInput {
  module: string
  name: string
  sortOrder: number
  status: string
}

export interface Asset {
  url: string
  filename: string
  size: number
}

export interface Style {
  id: number
  name: string
  code: string
  previewUrl: string | null
  theme: StorefrontTheme
}

export interface StorefrontCategory {
  id: string
  name: string
  iconName: string
  iconUrl: string | null
  fallbackText: string
}

export interface StorefrontTheme {
  code: string
  name: string
  layoutVersion: string
  colors: Record<string, string>
  iconNames: Record<string, string>
  iconUrls: Record<string, string>
  imageUrls: Record<string, string>
  copywriting: Record<string, string>
  categories: StorefrontCategory[]
}

export interface StoreDecoration {
  storeId: number
  storeName: string
  logoUrl: string
  coverUrl: string | null
  banners: string[]
  styleId: number
  styleCode: string
  style: Style
  layoutVersion: string
  theme: StorefrontTheme
  colors: Record<string, string>
  iconNames: Record<string, string>
  iconUrls: Record<string, string>
  imageUrls: Record<string, string>
  copywriting: Record<string, string>
  categories: StorefrontCategory[]
}

export interface Spec {
  id: number
  styleId: number
  name: string
  specType: string
  required: boolean
  options: string[]
}

export interface SpecInput {
  styleId: number
  name: string
  specType: string
  required: boolean
  options: string[]
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
