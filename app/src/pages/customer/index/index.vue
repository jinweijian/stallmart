<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import { useDidShow, usePullDownRefresh } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import {
  API_ENDPOINTS,
  DEFAULT_STORE_THEME,
  STORE_THEME_PACKAGES,
  type StorefrontAssetSizes,
  type StorefrontBannerConfig,
  type StorefrontCategoryConfig,
  type StorefrontCategoryIconConfig,
  type StoreStyleCode,
  type StoreThemePagePackages,
  type StoreThemePackage,
} from '@/config'
import { get } from '@/utils/request'
import { createCustomerThemeVars, persistCustomerTheme } from '@/utils/customer-theme'

interface StorefrontThemePayload {
  code: StoreStyleCode
  name: string
  layoutVersion?: string
  colors?: Record<string, string>
  iconNames?: Record<string, string>
  iconUrls?: Record<string, string>
  imageUrls?: Record<string, string>
  copywriting?: Record<string, string>
  categories?: StorefrontCategoryConfig[]
  categoryIconLibrary?: StorefrontCategoryIconConfig[]
  banners?: Array<StorefrontBannerConfig | string>
  assetSizes?: Partial<StorefrontAssetSizes>
  pageThemes?: StoreThemePagePackages
}

interface StorefrontDecorationPayload {
  layoutVersion?: string
  theme?: StorefrontThemePayload
  colors?: Record<string, string>
  iconNames?: Record<string, string>
  iconUrls?: Record<string, string>
  imageUrls?: Record<string, string>
  copywriting?: Record<string, string>
  categories?: StorefrontCategoryConfig[]
  categoryIconLibrary?: StorefrontCategoryIconConfig[]
  banners?: Array<StorefrontBannerConfig | string>
  assetSizes?: Partial<StorefrontAssetSizes>
  pageThemes?: StoreThemePagePackages
}

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
  decoration?: StorefrontDecorationPayload
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
  mainImageUrl?: string
  categoryId?: string | number
  categoryName?: string
  description?: string
  category: string
  stock: number
  sales: number
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' | 'active' | 'off_sale' | 'sold_out'
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

interface ProductSku {
  id: string | number
  specValues: string[]
  price: number
  stock: number
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' | 'active' | 'off_sale' | 'sold_out'
}

interface StorefrontSpec {
  id: string | number
  styleId: string | number
  name: string
  specType: string
  required: boolean
  options: string[]
}

interface SkuGroup {
  id: string
  name: string
  required: boolean
  options: string[]
}

interface CartItem {
  product: Product
  quantity: number
}

const categories = ref<StorefrontCategoryConfig[]>(resolveFallbackCategories(DEFAULT_STORE_THEME))

const storeInfo = ref<StoreInfo | null>(null)
const products = ref<Product[]>([])
const activeCategory = ref('recommend')
const isLoading = ref(false)
const cartItems = ref<CartItem[]>([])
const cartTotal = ref(0)
const cartCount = ref(0)
const currentBannerIndex = ref(0)
const styleSpecs = ref<StorefrontSpec[]>([])
const selectedProduct = ref<Product | null>(null)
const selectedOptions = ref<Record<string, string>>({})
const selectedQuantity = ref(1)
const selectedRemark = ref('')

const currentTheme = computed(() => {
  const styleCode = storeInfo.value?.styleCode
  const base = styleCode ? STORE_THEME_PACKAGES[styleCode] || DEFAULT_STORE_THEME : DEFAULT_STORE_THEME
  return mergeStorefrontTheme(base, storeInfo.value?.decoration)
})

const themeVars = computed(() => createCustomerThemeVars(currentTheme.value))

const copywriting = computed(() => currentTheme.value.copywriting || {})
const iconUrls = computed(() => currentTheme.value.iconUrls || {})
const imageUrls = computed(() => currentTheme.value.imageUrls || {})
const bannerSlides = computed<StorefrontBannerConfig[]>(() => {
  const configured = currentTheme.value.banners || []
  if (configured.length > 0) return [...configured]

  return [
    {
      id: 'fallback-promo',
      imageUrl: imageUrls.value.promoIllustration || imageUrls.value.productPlaceholder || '',
      title: promoTitle.value,
      subtitle: promoSubtitle.value,
      actionText: promoActionText.value,
      targetCategory: 'recommend',
    },
  ]
})

const filteredProducts = computed(() => {
  if (activeCategory.value === 'recommend') {
    const recommended = products.value.filter((product) => product.isHot || product.rank)
    return recommended.length > 0 ? recommended : products.value
  }

  return products.value.filter((product) => product.category === activeCategory.value)
})

const skuGroups = computed<SkuGroup[]>(() => {
  const product = selectedProduct.value
  if (!product) return []

  return (product.specIds || [])
    .map((specId) => styleSpecs.value.find((spec) => String(spec.id) === String(specId)))
    .filter((spec): spec is StorefrontSpec => Boolean(spec))
    .map((spec) => ({
      id: String(spec.id),
      name: spec.name,
      required: spec.required,
      options: spec.options || [],
    }))
})

const selectedSku = computed<ProductSku | null>(() => {
  const product = selectedProduct.value
  if (!product?.skus || product.skus.length === 0) return null
  if (skuGroups.value.some((group) => group.required && !selectedOptions.value[group.id])) return null

  return product.skus.find((sku) =>
    sku.status === 'ACTIVE' &&
    skuGroups.value.every((group, index) => sku.specValues[index] === selectedOptions.value[group.id])
  ) || null
})

const selectedSpecsText = computed(() =>
  skuGroups.value
    .map((group) => selectedOptions.value[group.id])
    .filter(Boolean)
    .join(' / ')
)

const selectedProductPrice = computed(() => Number(selectedSku.value?.price ?? selectedProduct.value?.basePrice ?? 0))
const selectedProductTotal = computed(() => selectedProductPrice.value * selectedQuantity.value)

const promoTitle = computed(() => copywriting.value.promoTitle || '鲜果时令上新')
const promoSubtitle = computed(() => copywriting.value.promoSubtitle || '当季水果 · 清爽一夏')
const promoActionText = computed(() => copywriting.value.promoActionText || '立即尝鲜')

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
    const storeRes = await get<StoreInfo>(API_ENDPOINTS.STORE_DETAIL(storeId))

    storeInfo.value = normalizeStore(storeRes.data)
    categories.value = resolveCategories(storeInfo.value)
    const [productsRes, specsRes] = await Promise.all([
      get<Product[]>(API_ENDPOINTS.PRODUCTS(storeId)),
      get<StorefrontSpec[]>(API_ENDPOINTS.STYLE_SPECS(storeInfo.value.styleId || 6)),
    ])
    products.value = (productsRes.data || []).map(normalizeProduct)
    styleSpecs.value = specsRes.data || []
    Taro.setStorageSync('current_store_id', storeInfo.value.id)
  } catch (error) {
    storeInfo.value = getMockStore()
    categories.value = resolveCategories(storeInfo.value)
    products.value = getMockProducts()
    styleSpecs.value = []
  } finally {
    persistCustomerTheme(currentTheme.value)
    isLoading.value = false
  }
}

function mergeStorefrontTheme(base: StoreThemePackage, decoration?: StorefrontDecorationPayload): StoreThemePackage {
  if (!decoration) return base
  const theme = decoration.theme
  const colors = decoration.colors || theme?.colors || {}

  return {
    ...base,
    layoutVersion: decoration.layoutVersion || theme?.layoutVersion || base.layoutVersion,
    primary: colors.primary || base.primary,
    secondary: colors.secondary || base.secondary,
    accent: colors.accent || base.accent,
    background: colors.background || base.background,
    surface: colors.surface || base.surface,
    text: colors.text || base.text,
    mutedText: colors.mutedText || base.mutedText,
    border: colors.border || base.border,
    price: colors.price || base.price || colors.primary || base.primary,
    iconNames: { ...(base.iconNames || {}), ...(theme?.iconNames || {}), ...(decoration.iconNames || {}) },
    iconUrls: { ...(base.iconUrls || {}), ...(theme?.iconUrls || {}), ...(decoration.iconUrls || {}) },
    imageUrls: { ...(base.imageUrls || {}), ...(theme?.imageUrls || {}), ...(decoration.imageUrls || {}) },
    copywriting: { ...(base.copywriting || {}), ...(theme?.copywriting || {}), ...(decoration.copywriting || {}) },
    banners: normalizeBannerConfigs(decoration.banners) || normalizeBannerConfigs(theme?.banners) || base.banners,
    categoryIconLibrary: decoration.categoryIconLibrary || theme?.categoryIconLibrary || base.categoryIconLibrary,
    assetSizes: { ...(base.assetSizes || {}), ...(theme?.assetSizes || {}), ...(decoration.assetSizes || {}) } as StorefrontAssetSizes,
    pageThemes: { ...(base.pageThemes || {}), ...(theme?.pageThemes || {}) },
  }
}

function normalizeBannerConfigs(banners?: Array<StorefrontBannerConfig | string>): StorefrontBannerConfig[] | undefined {
  if (!banners || banners.length === 0) return undefined

  return banners
    .map((banner, index) => {
      if (typeof banner === 'string') {
        return {
          id: `decoration-banner-${index}`,
          imageUrl: banner,
          targetCategory: index === 0 ? 'recommend' : 'tea',
        }
      }

      return banner
    })
    .filter((banner) => !!banner.imageUrl)
}

function resolveCategories(store: StoreInfo): StorefrontCategoryConfig[] {
  if (store.decoration?.categories && store.decoration.categories.length > 0) {
    return store.decoration.categories
  }

  return resolveFallbackCategories(mergeStorefrontTheme(STORE_THEME_PACKAGES[store.styleCode] || DEFAULT_STORE_THEME, store.decoration))
}

function resolveFallbackCategories(theme: StoreThemePackage): StorefrontCategoryConfig[] {
  return (theme.categoryIconLibrary || []).map((icon) => ({
    id: icon.key,
    name: icon.name,
    iconKey: icon.key,
    iconUrl: icon.iconUrl,
    fallbackText: icon.fallbackText,
    status: 'ACTIVE',
  }))
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
    heroEyebrow: store.heroEyebrow || store.decoration?.copywriting?.heroEyebrow || '小新の',
    heroTitle: store.heroTitle || store.decoration?.copywriting?.heroTitle || store.name || '水果茶屋',
    heroSubtitle: store.heroSubtitle || store.decoration?.copywriting?.heroSubtitle || store.description || '自然水果 · 新鲜现制',
  }
}

function normalizeProduct(product: Product): Product {
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
    decoration: {
      layoutVersion: DEFAULT_STORE_THEME.layoutVersion,
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
      iconNames: DEFAULT_STORE_THEME.iconNames,
      iconUrls: DEFAULT_STORE_THEME.iconUrls,
      imageUrls: DEFAULT_STORE_THEME.imageUrls,
      copywriting: DEFAULT_STORE_THEME.copywriting,
      categoryIconLibrary: DEFAULT_STORE_THEME.categoryIconLibrary,
      categories: resolveFallbackCategories(DEFAULT_STORE_THEME),
      banners: DEFAULT_STORE_THEME.banners,
      assetSizes: DEFAULT_STORE_THEME.assetSizes,
      pageThemes: DEFAULT_STORE_THEME.pageThemes,
    },
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

function handleBannerChange(event: { detail?: { current?: number } }) {
  currentBannerIndex.value = event.detail?.current ?? 0
}

function handleBannerTap(banner: StorefrontBannerConfig) {
  if (banner.targetCategory) {
    selectCategory(banner.targetCategory)
  }
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

function openProductDetail(product: Product) {
  if (isProductDisabled(product)) {
    Taro.showToast({ title: getStatusText(product) || '暂不可购买', icon: 'none' })
    return
  }

  selectedProduct.value = product
  selectedQuantity.value = 1
  selectedRemark.value = ''
  selectedOptions.value = resolveDefaultSkuOptions(product)
  void Taro.hideTabBar({ animation: true }).catch(() => undefined)
}

function closeProductDetail() {
  selectedProduct.value = null
  selectedOptions.value = {}
  selectedQuantity.value = 1
  selectedRemark.value = ''
  void Taro.showTabBar({ animation: true }).catch(() => undefined)
}

function resolveDefaultSkuOptions(product: Product): Record<string, string> {
  const groups = (product.specIds || [])
    .map((specId) => styleSpecs.value.find((spec) => String(spec.id) === String(specId)))
    .filter((spec): spec is StorefrontSpec => Boolean(spec))
  const activeSku = product.skus?.find((sku) => sku.status === 'ACTIVE' && sku.stock > 0)

  return groups.reduce<Record<string, string>>((options, group, index) => {
    const skuValue = activeSku?.specValues[index]
    options[String(group.id)] = skuValue || group.options[0] || ''
    return options
  }, {})
}

function selectSkuOption(group: SkuGroup, option: string) {
  if (isSkuOptionDisabled(group, option)) return
  selectedOptions.value = {
    ...selectedOptions.value,
    [group.id]: option,
  }
}

function isSkuOptionDisabled(group: SkuGroup, option: string): boolean {
  const product = selectedProduct.value
  if (!product?.skus || product.skus.length === 0) return false
  const groupIndex = skuGroups.value.findIndex((item) => item.id === group.id)
  if (groupIndex < 0) return false

  return !product.skus.some((sku) => {
    if (sku.status !== 'ACTIVE' || sku.stock <= 0 || sku.specValues[groupIndex] !== option) return false
    return skuGroups.value.every((otherGroup, index) => {
      if (index === groupIndex) return true
      const selected = selectedOptions.value[otherGroup.id]
      return !selected || sku.specValues[index] === selected
    })
  })
}

function changeSelectedQuantity(delta: number) {
  const stockLimit = selectedSku.value?.stock ?? selectedProduct.value?.stock ?? 1
  const nextQuantity = selectedQuantity.value + delta
  selectedQuantity.value = Math.min(Math.max(nextQuantity, 1), Math.min(stockLimit, 10))
}

function addSelectedProductToCart() {
  const product = selectedProduct.value
  if (!product) return
  if (product.skus && product.skus.length > 0 && !selectedSku.value) {
    Taro.showToast({ title: '请选择可售规格', icon: 'none' })
    return
  }

  const cartProduct: Product = {
    ...product,
    basePrice: selectedProductPrice.value,
    originalPrice: selectedProductPrice.value,
    selectedSkuId: selectedSku.value?.id,
    selectedSpecsText: selectedSpecsText.value,
    stock: selectedSku.value?.stock ?? product.stock,
  }
  const existing = cartItems.value.find((item) =>
    item.product.id === cartProduct.id &&
    item.product.selectedSkuId === cartProduct.selectedSkuId &&
    item.product.selectedSpecsText === cartProduct.selectedSpecsText
  )

  if (existing) {
    existing.quantity = Math.min(existing.quantity + selectedQuantity.value, 10)
  } else {
    cartItems.value.push({ product: cartProduct, quantity: selectedQuantity.value })
  }

  saveCart()
  closeProductDetail()
  Taro.showToast({ title: '已加入购物车', icon: 'none', duration: 900 })
}

function goToCart() {
  Taro.switchTab({ url: '/pages/customer/cart/cart' })
}

function goToProduct(product: Product) {
  openProductDetail(product)
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

onBeforeUnmount(() => {
  void Taro.showTabBar({ animation: false }).catch(() => undefined)
})
</script>

<template>
  <view class="fruit-tea-page" :style="themeVars">
    <view class="hero-section">
      <image v-if="imageUrls.heroIllustration" class="hero-image" :src="imageUrls.heroIllustration" mode="aspectFill" />
    </view>

    <swiper
      class="banner-swiper"
      :current="currentBannerIndex"
      :autoplay="bannerSlides.length > 1"
      circular
      :interval="3200"
      :duration="500"
      :indicator-dots="bannerSlides.length > 1"
      indicator-color="rgba(111, 150, 70, 0.28)"
      indicator-active-color="#6F9646"
      @change="handleBannerChange"
    >
      <swiper-item v-for="banner in bannerSlides" :key="banner.id" class="banner-slide">
        <view class="promo-card" @tap="handleBannerTap(banner)">
          <image v-if="banner.imageUrl" class="promo-bg-image" :src="banner.imageUrl" mode="aspectFill" />
        </view>
      </swiper-item>
    </swiper>

    <view class="ordering-shell">
      <scroll-view class="category-rail" scroll-y :show-scrollbar="false">
        <view
          v-for="category in categories"
          :key="category.id"
          class="rail-item"
          :class="{ active: activeCategory === category.id }"
          @tap="selectCategory(category.id)"
        >
          <image v-if="category.iconUrl" class="rail-icon-image" :src="category.iconUrl" mode="aspectFit" />
          <text v-else class="rail-icon">{{ category.iconKey ? category.fallbackText : category.name.slice(0, 1) }}</text>
          <text class="rail-text">{{ category.name }}</text>
        </view>
      </scroll-view>

      <view class="menu-panel">
        <view class="section-heading">
          <text class="section-title">人气推荐</text>
          <image v-if="iconUrls.sectionLeaf" class="section-leaf-image" :src="iconUrls.sectionLeaf" mode="aspectFit" />
          <text v-else class="section-leaf">⌁</text>
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
              <image v-if="product.image || imageUrls.productPlaceholder" class="drink-image" :src="product.image || imageUrls.productPlaceholder" mode="aspectFill" />
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
              @tap.stop="openProductDetail(product)"
            >
              <text>+</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <view v-if="selectedProduct" class="product-detail-mask" @tap="closeProductDetail">
      <view class="product-detail-sheet" @tap.stop>
        <view class="detail-close" @tap="closeProductDetail">×</view>

        <view class="detail-product-head">
          <view class="detail-product-image-wrap">
            <image
              v-if="selectedProduct.image || imageUrls.productPlaceholder"
              class="detail-product-image"
              :src="selectedProduct.image || imageUrls.productPlaceholder"
              mode="aspectFit"
            />
            <text v-else class="detail-product-emoji">{{ selectedProduct.illustration || '🥤' }}</text>
          </view>

          <view class="detail-product-copy">
            <view class="detail-title-row">
              <text class="detail-product-name">{{ selectedProduct.name }}</text>
              <text v-if="selectedProduct.isNew" class="new-pill">NEW</text>
            </view>
            <text class="detail-product-desc">{{ selectedProduct.flavor || selectedProduct.description }}</text>
            <text class="detail-product-price">¥{{ formatPrice(selectedProductPrice) }}</text>
          </view>
        </view>

        <view v-for="group in skuGroups" :key="group.id" class="sku-section">
          <view class="sku-section-head">
            <text class="sku-section-title">选择{{ group.name }}</text>
            <text v-if="!group.required" class="sku-section-optional">（可多选）</text>
          </view>
          <view class="sku-options">
            <view
              v-for="option in group.options"
              :key="option"
              class="sku-option"
              :class="{
                active: selectedOptions[group.id] === option,
                disabled: isSkuOptionDisabled(group, option),
              }"
              @tap="selectSkuOption(group, option)"
            >
              <text class="sku-option-name">{{ option }}</text>
              <text v-if="selectedOptions[group.id] === option" class="sku-option-check">✓</text>
            </view>
          </view>
        </view>

        <view class="detail-quantity-row">
          <text class="detail-quantity-title">购买数量</text>
          <view class="detail-stepper">
            <view class="detail-stepper-btn" :class="{ disabled: selectedQuantity <= 1 }" @tap="changeSelectedQuantity(-1)">−</view>
            <text class="detail-stepper-num">{{ selectedQuantity }}</text>
            <view
              class="detail-stepper-btn primary"
              :class="{ disabled: selectedQuantity >= Math.min(selectedSku?.stock || selectedProduct.stock, 10) }"
              @tap="changeSelectedQuantity(1)"
            >
              +
            </view>
          </view>
        </view>

        <view class="detail-remark-row">
          <text class="detail-remark-title">订单备注</text>
          <input
            v-model="selectedRemark"
            class="detail-remark-input"
            placeholder="选填，口味偏好等要求"
            placeholder-class="detail-remark-placeholder"
          />
          <text class="detail-remark-arrow">›</text>
        </view>

        <view class="detail-submit-bar">
          <view class="detail-selected">
            <text class="detail-selected-price">¥{{ formatPrice(selectedProductTotal) }}</text>
            <text class="detail-selected-text">已选：{{ selectedSpecsText || '默认规格' }}</text>
          </view>
          <view class="detail-submit-btn" :class="{ disabled: selectedProduct.skus?.length && !selectedSku }" @tap="addSelectedProductToCart">
            加入购物车
          </view>
        </view>
      </view>
    </view>

    <view class="cart-bar">
      <view
        class="cart-mascot"
        :style="iconUrls.cart ? { backgroundImage: `url(${iconUrls.cart})` } : undefined"
        @tap="goToCart"
      >
        <text v-if="!iconUrls.cart" class="cart-face">购</text>
        <view v-if="cartCount > 0" class="cart-count">{{ cartCount }}</view>
      </view>
      <view class="cart-summary" @tap="goToCart">
        <text class="cart-total">¥{{ formatPrice(cartTotal) }}</text>
        <text class="cart-link">去结算 ›</text>
      </view>
      <view class="checkout-button" :class="{ disabled: cartCount <= 0 }" @tap="goToCheckout">
        <image v-if="iconUrls.checkout" class="checkout-icon-image" :src="iconUrls.checkout" mode="aspectFit" />
        <text v-else class="checkout-icon">▣</text>
        <text>去结算</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss">
@import './index.scss';
</style>
