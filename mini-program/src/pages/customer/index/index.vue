<script setup lang="ts">
import { ref, computed, onLoad } from 'vue'
import { useDidShow, usePullDownRefresh } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { get } from '@/utils/request'

// ============================================================
// Types
// ============================================================
interface StoreInfo {
  id: string
  name: string
  logo: string
  description: string
  rating: number
  sales: number
  styleCode: 'hawaiian' | 'BBQ' | 'market' | 'ocean' | 'fresh'
  isOpen?: boolean
  distance?: string
}

interface Product {
  id: string
  name: string
  basePrice: number
  originalPrice: number
  image: string
  category: string
  stock: number
  sales: number
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT'
  isHot?: boolean
  isNew?: boolean
}

interface Category {
  id: string
  name: string
}

interface CartItem {
  product: Product
  quantity: number
}

// ============================================================
// Style Package Color Map
// ============================================================
const STYLE_COLORS: Record<string, { primary: string; gradient: string }> = {
  hawaiian: { primary: '#FF6B35', gradient: 'linear-gradient(135deg, #FF6B35 0%, #FF8E5E 100%)' },
  BBQ: { primary: '#E67E22', gradient: 'linear-gradient(135deg, #E67E22 0%, #F0A04B 100%)' },
  market: { primary: '#F39C12', gradient: 'linear-gradient(135deg, #F39C12 0%, #F5B841 100%)' },
  ocean: { primary: '#2EC4B6', gradient: 'linear-gradient(135deg, #2EC4B6 0%, #5DD4C8 100%)' },
  fresh: { primary: '#52C41A', gradient: 'linear-gradient(135deg, #52C41A 0%, #73D13D 100%)' },
}

const DEFAULT_STYLE = STYLE_COLORS['hawaiian']

// ============================================================
// State
// ============================================================
const storeInfo = ref<StoreInfo | null>(null)
const products = ref<Product[]>([])
const categories = ref<Category[]>([
  { id: 'all', name: '全部' },
  { id: 'hot', name: '🔥 热销' },
  { id: 'drink', name: '饮品' },
  { id: 'snack', name: '小吃' },
  { id: 'meal', name: '主食' },
  { id: 'extra', name: '加料' },
  { id: 'other', name: '其他' },
])
const activeCategory = ref('all')
const searchQuery = ref('')
const isLoading = ref(false)
const showSearch = ref(false)
const cartItems = ref<CartItem[]>([])
const cartTotal = ref(0)
const cartCount = ref(0)

// ============================================================
// Computed
// ============================================================
const styleColors = computed(() => {
  if (!storeInfo.value?.styleCode) return DEFAULT_STYLE
  return STYLE_COLORS[storeInfo.value.styleCode] || DEFAULT_STYLE
})

const filteredProducts = computed(() => {
  let list = products.value

  // Category filter
  if (activeCategory.value === 'hot') {
    list = list.filter((p) => p.isHot)
  } else if (activeCategory.value !== 'all') {
    // Map category id to category name
    const catMap: Record<string, string> = {
      drink: '饮品',
      snack: '小吃',
      meal: '主食',
      extra: '加料',
      other: '其他',
    }
    const catName = catMap[activeCategory.value] || activeCategory.value
    list = list.filter((p) => p.category === catName)
  }

  // Search filter
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    list = list.filter((p) => p.name.toLowerCase().includes(q))
  }

  return list
})

// ============================================================
// Lifecycle
// ============================================================
useDidShow(() => {
  loadData()
  loadCart()
})

usePullDownRefresh(() => {
  loadData().finally(() => {
    Taro.stopPullDownRefresh()
  })
})

onLoad(() => {
  // Support onLoad params if storeId passed
})

// ============================================================
// Data Loading
// ============================================================
async function loadData() {
  isLoading.value = true
  try {
    const storeId = Taro.getStorageSync('current_store_id') || '1'

    const [storeRes, productsRes] = await Promise.all([
      get<StoreInfo>('/stores/' + storeId),
      get<Product[]>('/stores/' + storeId + '/products'),
    ])

    storeInfo.value = storeRes.data
    products.value = productsRes.data || []
  } catch (e) {
    // fallback to mock
    storeInfo.value = getMockStore()
    products.value = getMockProducts()
  } finally {
    isLoading.value = false
  }
}

function getStoreId(): string {
  const pages = Taro.getCurrentPages()
  const current = pages[pages.length - 1]
  const options = (current as any)?.options || {}
  return options.storeId || '1'
}

// ============================================================
// Mock Data
// ============================================================
function getMockStore(): StoreInfo {
  return {
    id: '1',
    name: '老王煎饼摊',
    logo: 'https://picsum.photos/200?random=store1',
    description: '专注传统煎饼20年，用料实在，口碑保证',
    rating: 4.8,
    sales: 2341,
    styleCode: 'BBQ',
    isOpen: true,
    distance: '320m',
  }
}

function getMockProducts(): Product[] {
  return [
    {
      id: '1',
      name: '招牌煎饼果子',
      basePrice: 8.0,
      originalPrice: 10.0,
      image: 'https://picsum.photos/300/200?random=p1',
      category: '主食',
      stock: 50,
      sales: 1280,
      status: 'ACTIVE',
      isHot: true,
    },
    {
      id: '2',
      name: '现磨豆浆（甜/咸）',
      basePrice: 3.0,
      originalPrice: 4.0,
      image: 'https://picsum.photos/300/200?random=p2',
      category: '饮品',
      stock: 100,
      sales: 856,
      status: 'ACTIVE',
      isHot: true,
    },
    {
      id: '3',
      name: '香酥油条',
      basePrice: 2.0,
      originalPrice: 2.5,
      image: 'https://picsum.photos/300/200?random=p3',
      category: '小吃',
      stock: 60,
      sales: 620,
      status: 'ACTIVE',
      isNew: true,
    },
    {
      id: '4',
      name: '鲜肉包子（3个）',
      basePrice: 4.0,
      originalPrice: 5.0,
      image: 'https://picsum.photos/300/200?random=p4',
      category: '主食',
      stock: 40,
      sales: 430,
      status: 'ACTIVE',
    },
    {
      id: '5',
      name: '秘制辣酱',
      basePrice: 1.0,
      originalPrice: 1.5,
      image: 'https://picsum.photos/300/200?random=p5',
      category: '加料',
      stock: 200,
      sales: 380,
      status: 'ACTIVE',
    },
    {
      id: '6',
      name: '卤蛋',
      basePrice: 2.0,
      originalPrice: 2.5,
      image: 'https://picsum.photos/300/200?random=p6',
      category: '加料',
      stock: 80,
      sales: 290,
      status: 'ACTIVE',
    },
    {
      id: '7',
      name: '紫米粥',
      basePrice: 3.5,
      originalPrice: 4.5,
      image: 'https://picsum.photos/300/200?random=p7',
      category: '饮品',
      stock: 30,
      sales: 210,
      status: 'ACTIVE',
    },
    {
      id: '8',
      name: '炸鸡柳',
      basePrice: 5.0,
      originalPrice: 7.0,
      image: 'https://picsum.photos/300/200?random=p8',
      category: '小吃',
      stock: 25,
      sales: 175,
      status: 'ACTIVE',
      isNew: true,
    },
    {
      id: '9',
      name: '生菜沙拉',
      basePrice: 3.0,
      originalPrice: 4.0,
      image: 'https://picsum.photos/300/200?random=p9',
      category: '其他',
      stock: 0,
      sales: 50,
      status: 'SOLD_OUT',
    },
  ]
}

// ============================================================
// Cart Operations
// ============================================================
function loadCart() {
  try {
    const stored = Taro.getStorageSync('cart_items')
    if (stored) {
      const items = JSON.parse(stored) as CartItem[]
      cartItems.value = items
      recalcCart()
    }
  } catch (e) {
    console.error('[Cart] Load failed:', e)
  }
}

function saveCart() {
  Taro.setStorageSync('cart_items', JSON.stringify(cartItems.value))
  recalcCart()
}

function recalcCart() {
  cartCount.value = cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  cartTotal.value = cartItems.value.reduce((sum, item) => sum + item.product.basePrice * item.quantity, 0)
}

function addToCart(product: Product) {
  if (product.status === 'SOLD_OUT') {
    Taro.showToast({ title: '已售罄', icon: 'none' })
    return
  }
  if (product.stock <= 0) {
    Taro.showToast({ title: '库存不足', icon: 'none' })
    return
  }

  const existing = cartItems.value.find((item) => item.product.id === product.id)
  if (existing) {
    if (existing.quantity >= Math.min(product.stock, 10)) {
      Taro.showToast({ title: '已达购买上限', icon: 'none' })
      return
    }
    existing.quantity++
  } else {
    cartItems.value.push({ product, quantity: 1 })
  }

  saveCart()
  Taro.showToast({ title: '已加入购物车', icon: 'none', duration: 1000 })
}

function getProductQuantity(productId: string): number {
  const item = cartItems.value.find((i) => i.product.id === productId)
  return item?.quantity || 0
}

// ============================================================
// Navigation
// ============================================================
function goToProduct(id: string) {
  // Navigate to product detail; if page not yet created, show a toast
  const url = `/pages/customer/product/detail?id=${id}`
  const pages = Taro.getCurrentPages()
  const hasPage = pages.some((p) => p.route?.includes('product/detail'))

  if (hasPage) {
    Taro.navigateTo({ url })
  } else {
    // Product detail page not yet built — show placeholder toast
    Taro.showToast({ title: '商品详情开发中', icon: 'none', duration: 1500 })
  }
}

function goToCart() {
  Taro.switchTab({ url: '/pages/customer/cart/cart' })
}

function goToSearch() {
  showSearch.value = true
}

function closeSearch() {
  showSearch.value = false
  searchQuery.value = ''
}

// ============================================================
// Category Selection
// ============================================================
function selectCategory(id: string) {
  activeCategory.value = id
}

// ============================================================
// Formatting
// ============================================================
function formatPrice(price: number): string {
  return price.toFixed(2)
}

function formatSales(sales: number): string {
  if (sales >= 1000) {
    return (sales / 1000).toFixed(1) + 'k'
  }
  return sales.toString()
}

function getStatusText(product: Product): string {
  switch (product.status) {
    case 'SOLD_OUT':
      return '售罄'
    case 'INACTIVE':
      return '下架'
    default:
      return ''
  }
}

function isProductDisabled(product: Product): boolean {
  return product.status === 'SOLD_OUT' || product.status === 'INACTIVE' || product.stock <= 0
}
</script>

<template>
  <view class="storefront-page" :style="{ '--style-primary': styleColors.primary, '--style-gradient': styleColors.gradient }">
    <!-- ==================== Status Bar Spacer ==================== -->
    <view class="status-bar-spacer" />

    <!-- ==================== Top Navigation ==================== -->
    <view class="top-nav">
      <view class="nav-left">
        <!-- Back button -->
        <view class="nav-icon-btn" @tap="Taro.navigateBack({ fail: () => {} })">
          <text class="nav-arrow">←</text>
        </view>
      </view>

      <view class="nav-title-wrap">
        <text class="nav-title">{{ storeInfo?.name || '店铺主页' }}</text>
        <view class="nav-rating" v-if="storeInfo">
          <text class="star-icon">★</text>
          <text class="rating-text">{{ storeInfo.rating }}</text>
        </view>
      </view>

      <view class="nav-right">
        <!-- Search icon -->
        <view class="nav-icon-btn" @tap="goToSearch">
          <text class="nav-icon-text">🔍</text>
        </view>
      </view>
    </view>

    <!-- ==================== Store Header Banner ==================== -->
    <view class="store-banner">
      <view class="banner-bg" />
      <view class="banner-content">
        <view class="store-info-row">
          <image
            class="store-logo"
            :src="storeInfo?.logo || '/static/default-avatar.png'"
            mode="aspectFill"
            @error="(e: any) => { e.target.src = '/static/default-avatar.png' }"
          />
          <view class="store-details">
            <text class="store-name">{{ storeInfo?.name }}</text>
            <text class="store-desc">{{ storeInfo?.description }}</text>
            <view class="store-tags">
              <text class="store-tag open" v-if="storeInfo?.isOpen !== false">营业中</text>
              <text class="store-tag closed" v-else>休息中</text>
              <text class="store-distance" v-if="storeInfo?.distance">📍 {{ storeInfo.distance }}</text>
            </view>
          </view>
        </view>

        <view class="store-stats">
          <view class="stat-item">
            <text class="stat-value">{{ storeInfo?.rating || '0.0' }}</text>
            <text class="stat-label">评分</text>
          </view>
          <view class="stat-divider" />
          <view class="stat-item">
            <text class="stat-value">{{ storeInfo?.sales || 0 }}</text>
            <text class="stat-label">已售</text>
          </view>
          <view class="stat-divider" />
          <view class="stat-item">
            <text class="stat-value">{{ products.length }}</text>
            <text class="stat-label">商品</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ==================== Search Bar (Sticky) ==================== -->
    <view class="search-bar-wrap">
      <view class="search-bar" @tap="goToSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索商品...</text>
      </view>
    </view>

    <!-- ==================== Category Tabs ==================== -->
    <view class="category-tabs-wrap">
      <scroll-view class="category-scroll" scroll-x enable-flex>
        <view
          v-for="cat in categories"
          :key="cat.id"
          class="category-tab"
          :class="{ active: activeCategory === cat.id }"
          @tap="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </view>
      </scroll-view>
    </view>

    <!-- ==================== Product Grid ==================== -->
    <scroll-view
      class="product-scroll"
      scroll-y
      enhanced
      :show-scrollbar="false"
      :thumbs="[{ direction: 'vertical' }]"
      @scrolltolower="() => {}"
    >
      <!-- Loading State -->
      <view v-if="isLoading" class="loading-state">
        <text class="loading-text">加载中...</text>
      </view>

      <!-- Empty State -->
      <view v-else-if="filteredProducts.length === 0" class="empty-state">
        <text class="empty-icon">📦</text>
        <text class="empty-text">暂无商品</text>
      </view>

      <!-- Products Grid -->
      <view v-else class="product-grid">
        <view
          v-for="product in filteredProducts"
          :key="product.id"
          class="product-card"
          :class="{ disabled: isProductDisabled(product) }"
          @tap="goToProduct(product.id)"
        >
          <!-- Product Image -->
          <view class="product-image-wrap">
            <image
              class="product-image"
              :src="product.image"
              mode="aspectFill"
              lazy-load
              @error="(e: any) => { e.target.src = '/static/product-placeholder.png' }"
            />
            <!-- Tags -->
            <view class="product-tags">
              <text class="product-tag hot" v-if="product.isHot">🔥 热销</text>
              <text class="product-tag new" v-if="product.isNew">新品</text>
            </view>
            <!-- Sold out overlay -->
            <view class="soldout-overlay" v-if="product.status === 'SOLD_OUT'">
              <text class="soldout-text">已售罄</text>
            </view>
          </view>

          <!-- Product Content -->
          <view class="product-content">
            <text class="product-name line-clamp-2">{{ product.name }}</text>

            <view class="product-bottom">
              <view class="price-wrap">
                <text class="price-symbol">¥</text>
                <text class="price-integer">{{ formatPrice(product.basePrice).split('.')[0] }}</text>
                <text class="price-decimal">.{{ formatPrice(product.basePrice).split('.')[1] }}</text>
                <text class="original-price" v-if="product.originalPrice > product.basePrice">
                  ¥{{ formatPrice(product.originalPrice) }}
                </text>
              </view>

              <view
                class="add-btn"
                :class="{ soldout: isProductDisabled(product) }"
                @tap.stop="addToCart(product)"
              >
                <text v-if="getProductQuantity(product.id) > 0" class="add-count">
                  {{ getProductQuantity(product.id) }}
                </text>
                <text v-else class="add-icon">+</text>
              </view>
            </view>

            <view class="product-meta">
              <text class="sales-count">已售 {{ formatSales(product.sales) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Bottom Safe Area -->
      <view class="bottom-safe-area" />
    </scroll-view>

    <!-- ==================== Floating Cart Button ==================== -->
    <view
      class="floating-cart-btn"
      :class="{ 'has-items': cartCount > 0 }"
      @tap="goToCart"
    >
      <text class="cart-icon">🛒</text>
      <view class="cart-badge" v-if="cartCount > 0">
        <text class="cart-badge-text">{{ cartCount > 99 ? '99+' : cartCount }}</text>
      </view>
    </view>

    <!-- ==================== Search Modal ==================== -->
    <view class="search-modal" :class="{ active: showSearch }" @tap.self="closeSearch">
      <view class="search-modal-content">
        <view class="search-input-row">
          <view class="search-input-wrap">
            <text class="search-icon-sm">🔍</text>
            <input
              class="search-input"
              v-model="searchQuery"
              placeholder="搜索商品..."
              confirm-type="search"
              :focus="showSearch"
              @confirm="closeSearch"
            />
          </view>
          <text class="search-cancel-btn" @tap="closeSearch">取消</text>
        </view>

        <!-- Search results preview -->
        <view class="search-results" v-if="searchQuery.trim() && filteredProducts.length > 0">
          <view
            v-for="product in filteredProducts.slice(0, 5)"
            :key="product.id"
            class="search-result-item"
            @tap="goToProduct(product.id); closeSearch()"
          >
            <text class="search-result-name">{{ product.name }}</text>
            <text class="search-result-price">¥{{ formatPrice(product.basePrice) }}</text>
          </view>
        </view>

        <view class="search-empty" v-else-if="searchQuery.trim() && filteredProducts.length === 0">
          <text class="search-empty-text">未找到相关商品</text>
        </view>
      </view>
    </view>
  </view>
</template>

<style>
@import './index.scss';
</style>
