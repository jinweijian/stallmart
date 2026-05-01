<script setup lang="ts">
import { computed, ref } from 'vue'
import { useDidShow, usePullDownRefresh } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { API_ENDPOINTS, DEFAULT_STORE_THEME, STORE_THEME_PACKAGES, type StoreStyleCode } from '@/config'
import { get } from '@/utils/request'

interface StoreInfo {
  id: string
  name: string
  logo?: string
  avatarUrl?: string
  description: string
  rating: number
  sales: number
  styleCode: StoreStyleCode
  styleId?: number
  isOpen?: boolean
  distance?: string
  branchName?: string
  heroEyebrow?: string
  heroTitle?: string
  heroSubtitle?: string
}

interface Product {
  id: string
  storeId?: string | number
  name: string
  basePrice: number
  price?: number
  originalPrice: number
  image: string
  imageUrl?: string
  description?: string
  category: string
  stock: number
  sales: number
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' | 'active' | 'off_sale' | 'sold_out'
  isHot?: boolean
  isNew?: boolean
  tags?: string[]
  flavor?: string
  rank?: number
  illustration?: string
}

interface Category {
  id: string
  name: string
  icon: string
}

interface CartItem {
  product: Product
  quantity: number
}

const categories = ref<Category[]>([
  { id: 'recommend', name: '人气推荐', icon: '★' },
  { id: 'citrus', name: '清爽柠檬', icon: '🍋' },
  { id: 'grape', name: '多肉葡萄', icon: '🍇' },
  { id: 'mango', name: '香甜芒果', icon: '🥭' },
  { id: 'tea', name: '鲜果茶桶', icon: '🥤' },
  { id: 'extra', name: '加料小料', icon: '🍯' },
])

const storeInfo = ref<StoreInfo | null>(null)
const products = ref<Product[]>([])
const activeCategory = ref('recommend')
const isLoading = ref(false)
const cartItems = ref<CartItem[]>([])
const cartTotal = ref(0)
const cartCount = ref(0)

const currentTheme = computed(() => {
  const styleCode = storeInfo.value?.styleCode
  return styleCode ? STORE_THEME_PACKAGES[styleCode] || DEFAULT_STORE_THEME : DEFAULT_STORE_THEME
})

const themeVars = computed(() => ({
  '--style-primary': currentTheme.value.primary,
  '--style-secondary': currentTheme.value.secondary,
  '--style-accent': currentTheme.value.accent,
  '--style-background': currentTheme.value.background,
  '--style-surface': currentTheme.value.surface,
  '--style-text': currentTheme.value.text,
  '--style-muted-text': currentTheme.value.mutedText,
  '--style-border': currentTheme.value.border,
  '--style-hero': currentTheme.value.heroGradient,
}))

const filteredProducts = computed(() => {
  if (activeCategory.value === 'recommend') {
    return products.value.filter((product) => product.isHot || product.rank)
  }

  return products.value.filter((product) => product.category === activeCategory.value)
})

const storeTitle = computed(() => storeInfo.value?.heroTitle || storeInfo.value?.name || '小新の水果茶屋')
const storeSubtitle = computed(() => storeInfo.value?.heroSubtitle || '自然水果 · 新鲜现制')
const branchLabel = computed(() => storeInfo.value?.branchName || storeInfo.value?.name || '上海环球港店')

useDidShow(() => {
  loadData()
  loadCart()
})

usePullDownRefresh(() => {
  loadData().finally(() => {
    Taro.stopPullDownRefresh()
  })
})

async function loadData() {
  isLoading.value = true
  try {
    const storeId = getStoreId()
    const [storeRes, productsRes] = await Promise.all([
      get<StoreInfo>(API_ENDPOINTS.STORE_DETAIL(storeId)),
      get<Product[]>(API_ENDPOINTS.PRODUCTS(storeId)),
    ])

    storeInfo.value = normalizeStore(storeRes.data)
    products.value = (productsRes.data || []).map(normalizeProduct)
    Taro.setStorageSync('current_store_id', storeInfo.value.id)
  } catch (error) {
    storeInfo.value = getMockStore()
    products.value = getMockProducts()
  } finally {
    isLoading.value = false
  }
}

function normalizeStore(store: StoreInfo): StoreInfo {
  return {
    ...store,
    id: String(store.id),
    logo: store.logo || store.avatarUrl || '/static/default-avatar.png',
    rating: store.rating || 4.9,
    sales: store.sales || 3286,
    styleCode: store.styleCode || (store.styleId === 6 ? 'forestFruitTeaCrayon' : 'forestFruitTeaCrayon'),
    isOpen: store.isOpen ?? store.status !== 'closed',
    distance: store.distance || '1.3km',
    branchName: store.branchName || store.name,
    heroEyebrow: store.heroEyebrow || '小新の',
    heroTitle: store.heroTitle || store.name || '水果茶屋',
    heroSubtitle: store.heroSubtitle || store.description || '自然水果 · 新鲜现制',
  }
}

function normalizeProduct(product: Product): Product {
  const basePrice = Number(product.basePrice ?? product.price ?? 0)
  const normalizedStatus = normalizeProductStatus(product.status)

  return {
    ...product,
    id: String(product.id),
    basePrice,
    originalPrice: product.originalPrice || basePrice,
    image: product.image || product.imageUrl || '',
    category: product.category || 'citrus',
    stock: product.stock ?? 99,
    sales: product.sales ?? 0,
    status: normalizedStatus,
    flavor: product.flavor || product.description || '',
    tags: product.tags || [],
    isHot: product.isHot ?? !!product.rank,
    illustration: product.illustration || '🥤',
  }
}

function normalizeProductStatus(status: Product['status']): 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' {
  if (status === 'active' || status === 'ACTIVE') return 'ACTIVE'
  if (status === 'sold_out' || status === 'SOLD_OUT') return 'SOLD_OUT'
  return 'INACTIVE'
}

function getStoreId(): string {
  const pages = Taro.getCurrentPages()
  const current = pages[pages.length - 1]
  const options = (current as { options?: Record<string, string> })?.options || {}
  return options.storeId || Taro.getStorageSync('current_store_id') || '1'
}

function getMockStore(): StoreInfo {
  return {
    id: '1',
    name: '小新の水果茶屋',
    logo: '/static/default-avatar.png',
    description: '当季鲜果茶，清爽一夏',
    rating: 4.9,
    sales: 3286,
    styleCode: 'forestFruitTeaCrayon',
    isOpen: true,
    distance: '1.3km',
    branchName: '上海环球港店',
    heroEyebrow: '小新の',
    heroTitle: '水果茶屋',
    heroSubtitle: '自然水果 · 新鲜现制',
  }
}

function getMockProducts(): Product[] {
  return [
    {
      id: 'fruit-tea-1',
      name: '霸气西柚柠檬茶',
      basePrice: 18,
      originalPrice: 22,
      image: '',
      category: 'citrus',
      stock: 30,
      sales: 1280,
      status: 'ACTIVE',
      isHot: true,
      isNew: true,
      rank: 1,
      flavor: '西柚果肉 + 香水柠檬 + 茉莉绿茶',
      tags: ['清爽解腻', '维C满满'],
      illustration: '🍹',
    },
    {
      id: 'fruit-tea-2',
      name: '阳光青提多多',
      basePrice: 16,
      originalPrice: 19,
      image: '',
      category: 'grape',
      stock: 42,
      sales: 956,
      status: 'ACTIVE',
      isHot: true,
      rank: 2,
      flavor: '阳光玫瑰青提 + 乳酸菌 + 绿茶',
      tags: ['清甜多汁', '人气TOP'],
      illustration: '🥤',
    },
    {
      id: 'fruit-tea-3',
      name: '芒芒百香绿茶',
      basePrice: 17,
      originalPrice: 20,
      image: '',
      category: 'mango',
      stock: 26,
      sales: 820,
      status: 'ACTIVE',
      isHot: true,
      rank: 3,
      flavor: '大颗芒果肉 + 百香果 + 茉莉绿茶',
      tags: ['香甜浓郁', '果肉满满'],
      illustration: '🧋',
    },
    {
      id: 'fruit-tea-4',
      name: '整颗柠檬冰茶桶',
      basePrice: 24,
      originalPrice: 29,
      image: '',
      category: 'tea',
      stock: 18,
      sales: 620,
      status: 'ACTIVE',
      isNew: true,
      flavor: '香水柠檬 + 冰萃绿茶 + 清甜果露',
      tags: ['大杯分享', '冰爽'],
      illustration: '🍋',
    },
    {
      id: 'fruit-tea-5',
      name: '葡萄冻冻加料',
      basePrice: 4,
      originalPrice: 5,
      image: '',
      category: 'extra',
      stock: 88,
      sales: 360,
      status: 'ACTIVE',
      flavor: '手作葡萄冻',
      tags: ['Q弹', '推荐搭配'],
      illustration: '🍇',
    },
  ]
}

function selectCategory(id: string) {
  activeCategory.value = id
}

function loadCart() {
  try {
    const stored = Taro.getStorageSync('cart_items')
    cartItems.value = stored ? (JSON.parse(stored) as CartItem[]) : []
    recalcCart()
  } catch (error) {
    cartItems.value = []
    recalcCart()
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
  if (isProductDisabled(product)) {
    Taro.showToast({ title: getStatusText(product) || '暂不可加购', icon: 'none' })
    return
  }

  const existing = cartItems.value.find((item) => item.product.id === product.id)
  if (existing) {
    if (existing.quantity >= Math.min(product.stock, 10)) {
      Taro.showToast({ title: '已达购买上限', icon: 'none' })
      return
    }
    existing.quantity += 1
  } else {
    cartItems.value.push({ product, quantity: 1 })
  }

  saveCart()
  Taro.showToast({ title: '已加入购物车', icon: 'none', duration: 900 })
}

function goToCart() {
  Taro.switchTab({ url: '/pages/customer/cart/cart' })
}

function goToProduct(product: Product) {
  if (isProductDisabled(product)) {
    Taro.showToast({ title: getStatusText(product) || '暂不可购买', icon: 'none' })
    return
  }
  Taro.showToast({ title: '商品详情开发中', icon: 'none', duration: 1400 })
}

function goToCheckout() {
  if (cartCount.value <= 0) {
    Taro.showToast({ title: '先选一杯吧', icon: 'none' })
    return
  }
  Taro.switchTab({ url: '/pages/customer/cart/cart' })
}

function formatPrice(price: number): string {
  return price.toFixed(0)
}

function getStatusText(product: Product): string {
  const status = normalizeProductStatus(product.status)
  if (status === 'SOLD_OUT' || product.stock <= 0) return '已售罄'
  if (status === 'INACTIVE') return '已下架'
  return ''
}

function isProductDisabled(product: Product): boolean {
  return normalizeProductStatus(product.status) !== 'ACTIVE' || product.stock <= 0
}
</script>

<template>
  <view class="fruit-tea-page" :style="themeVars">
    <view class="hero-section">
      <view class="hero-leaves hero-leaves-left" />
      <view class="hero-leaves hero-leaves-right" />

      <view class="mini-program-pill">
        <text class="pill-dot">●●●</text>
        <view class="pill-line" />
        <text class="pill-ring">◎</text>
      </view>

      <view class="location-card">
        <text class="location-pin">⌖</text>
        <view class="location-copy">
          <text class="location-store">{{ branchLabel }}</text>
          <text class="location-distance">距离您 {{ storeInfo?.distance || '1.3km' }}</text>
        </view>
      </view>

      <view class="hero-title-block">
        <text class="hero-eyebrow">{{ storeInfo?.heroEyebrow || '小新の' }}</text>
        <text class="hero-title">{{ storeTitle }}</text>
        <text class="hero-subtitle">{{ storeSubtitle }}</text>
      </view>

      <view class="hero-stage">
        <view class="fruit-basket">
          <text class="basket-fruits">🍍🍎🍊🍇</text>
        </view>
        <view class="mascot-pair">
          <view class="white-pet">◔ᴥ◔</view>
          <view class="crayon-boy">
            <view class="boy-head">
              <view class="boy-brow boy-brow-left" />
              <view class="boy-brow boy-brow-right" />
              <view class="boy-eye boy-eye-left" />
              <view class="boy-eye boy-eye-right" />
              <view class="boy-mouth" />
            </view>
            <view class="boy-shirt" />
          </view>
        </view>
      </view>
    </view>

    <view class="promo-card">
      <view class="promo-copy">
        <text class="promo-title">鲜果时令上新</text>
        <text class="promo-subtitle">当季水果 · 清爽一夏</text>
        <view class="promo-button" @tap="selectCategory('recommend')">
          <text>立即尝鲜</text>
          <text class="promo-arrow">›</text>
        </view>
      </view>
      <view class="promo-drink">
        <text class="promo-cup">🍹</text>
        <text class="promo-label">小新の水果茶屋</text>
      </view>
    </view>

    <view class="carousel-dots">
      <view class="carousel-dot active" />
      <view class="carousel-dot" />
    </view>

    <view class="ordering-shell">
      <scroll-view class="category-rail" scroll-y :show-scrollbar="false">
        <view
          v-for="category in categories"
          :key="category.id"
          class="rail-item"
          :class="{ active: activeCategory === category.id }"
          @tap="selectCategory(category.id)"
        >
          <text class="rail-icon">{{ category.icon }}</text>
          <text class="rail-text">{{ category.name }}</text>
        </view>
      </scroll-view>

      <view class="menu-panel">
        <view class="section-heading">
          <text class="section-title">人气推荐</text>
          <text class="section-leaf">⌁</text>
        </view>

        <view v-if="isLoading" class="loading-state">
          <text>加载中...</text>
        </view>

        <view v-else-if="filteredProducts.length === 0" class="empty-state">
          <text class="empty-icon">🍵</text>
          <text class="empty-text">暂无商品</text>
        </view>

        <scroll-view v-else class="product-list" scroll-y :show-scrollbar="false">
          <view
            v-for="product in filteredProducts"
            :key="product.id"
            class="product-row"
            :class="{ disabled: isProductDisabled(product) }"
            @tap="goToProduct(product)"
          >
            <view v-if="product.rank" class="rank-badge">
              <text>TOP</text>
              <text>{{ product.rank }}</text>
            </view>

            <view class="drink-art">
              <image v-if="product.image" class="drink-image" :src="product.image" mode="aspectFill" />
              <text v-else class="drink-emoji">{{ product.illustration || '🥤' }}</text>
            </view>

            <view class="product-info">
              <view class="product-title-row">
                <text class="product-name">{{ product.name }}</text>
                <text v-if="product.isNew" class="new-pill">NEW</text>
              </view>
              <text class="product-flavor">{{ product.flavor }}</text>
              <view class="product-tags">
                <text v-for="tag in product.tags || []" :key="tag" class="product-tag">{{ tag }}</text>
              </view>
              <text class="product-price">¥{{ formatPrice(product.basePrice) }}</text>
            </view>

            <view
              class="add-button"
              :class="{ disabled: isProductDisabled(product) }"
              @tap.stop="addToCart(product)"
            >
              <text>+</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <view class="cart-bar">
      <view class="cart-mascot" @tap="goToCart">
        <text class="cart-face">しん</text>
        <view v-if="cartCount > 0" class="cart-count">{{ cartCount }}</view>
      </view>
      <view class="cart-summary" @tap="goToCart">
        <text class="cart-total">¥{{ formatPrice(cartTotal) }}</text>
        <text class="cart-link">去结算 ›</text>
      </view>
      <view class="checkout-button" :class="{ disabled: cartCount <= 0 }" @tap="goToCheckout">
        <text class="checkout-icon">▣</text>
        <text>去结算</text>
      </view>
      <view class="delivery-button">
        <text class="delivery-icon">🛵</text>
        <text>外卖</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss">
@import './index.scss';
</style>
