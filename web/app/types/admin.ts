// 状态枚举
export type StoreStatus = 'OPEN' | 'CLOSED'
export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
export type SkuStatus = 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
export type CategoryStatus = 'ACTIVE' | 'INACTIVE'
export type CartStatus = 'ACTIVE' | 'CHECKED_OUT'
export type OrderStatus = 'NEW' | 'ACCEPTED' | 'PREPARING' | 'READY' | 'COMPLETED' | 'REJECTED'
export type UserStatus = 'ACTIVE' | 'DISABLED'
export type AdminAccountStatus = 'ACTIVE' | 'DISABLED'
export type StoreStyleStatus = 'ACTIVE' | 'INACTIVE'

// 类型枚举
export type UserRole = 'CUSTOMER' | 'VENDOR' | 'ADMIN'
export type CategoryModule = 'PRODUCT'
export type SpecType = 'SIZE' | 'SWEET' | 'ICE' | 'OTHER'

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
  captchaId?: string
  captchaAnswer?: string
}

export interface AdminCaptcha {
  captchaId: string
  imageBase64: string
}

export interface AdminLoginFailure {
  captchaRequired: boolean
}

export type OperationLogScope = 'PLATFORM' | 'VENDOR'
export type OperationLogResult = 'SUCCESS' | 'FAILURE'

export interface OperationLog {
  id: number
  scope: OperationLogScope
  storeId: number | null
  actorUserId: number | null
  actorAccount: string | null
  actorRole: UserRole | null
  action: string
  resourceType: string
  resourceId: string | null
  description: string
  result: OperationLogResult
  ipAddress: string | null
  userAgent: string | null
  createdAt: string
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
  status: StoreStatus
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
  status: ProductStatus
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
  status: ProductStatus
  sortOrder: number
  specIds: number[]
  skus: ProductSkuInput[]
}

export interface ProductSku {
  id: number
  specValues: string[]
  price: number
  stock: number
  status: SkuStatus
}

export interface ProductSkuInput {
  specValues: string[]
  price: number
  stock: number
  status: SkuStatus
}

export interface Category {
  id: number
  storeId: number
  module: CategoryModule
  name: string
  iconKey: string
  sortOrder: number
  status: CategoryStatus
}

export interface CategoryInput {
  module: CategoryModule
  name: string
  iconKey: string
  sortOrder: number
  status: CategoryStatus
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
  status?: StoreStyleStatus
  version?: number
  theme: StorefrontTheme
}

export interface StyleInput {
  name: string
  code: string
  previewUrl: string | null
  status: StoreStyleStatus
  theme: StorefrontTheme
}

export interface StorefrontCategory {
  id: string
  name: string
  iconKey: string
  iconUrl: string | null
  fallbackText: string
  sortOrder: number
  status: CategoryStatus
}

export interface StorefrontCategoryIcon {
  key: string
  name: string
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
  categoryIconLibrary: StorefrontCategoryIcon[]
  assetSizes?: Record<string, unknown>
  pageThemes?: Record<string, unknown>
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
  categoryIconLibrary: StorefrontCategoryIcon[]
  categories: StorefrontCategory[]
  assetSizes?: Record<string, unknown>
  pageThemes?: Record<string, unknown>
}

export interface Spec {
  id: number
  styleId: number
  name: string
  specType: SpecType
  required: boolean
  options: string[]
}

export interface SpecInput {
  styleId: number
  name: string
  specType: SpecType
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
  status: OrderStatus
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
  status: CartStatus
  updatedAt: string
  items: CartItem[]
}

export interface UserProfile {
  id: number
  nickname: string
  avatarUrl: string
  phone: string | null
  hasPhone: boolean
  role: UserRole
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
