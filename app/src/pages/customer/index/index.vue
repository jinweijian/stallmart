<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import { useDidShow, usePullDownRefresh } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import {
  API_ENDPOINTS,
  DEFAULT_STORE_THEME,
  type StorefrontBannerConfig,
  type StorefrontCategoryConfig,
} from '@/config'
import { useUserStore } from '@/store/user'
import { get } from '@/utils/request'
import { createCustomerThemeVars, persistCustomerTheme } from '@/utils/customer-theme'
import {
  createMockStore,
  normalizeStore,
  resolveCategories,
  resolveCurrentTheme,
  resolveFallbackCategories,
  type StoreInfo,
} from '@/domain/customer/storefront-theme'
import {
  buildSelectedSpecsText,
  createSelectedCartProduct,
  filterProductsByCategory,
  findSelectedSku,
  getMockProducts,
  getStatusText,
  isProductDisabled,
  isSkuOptionAvailable,
  normalizeProduct,
  resolveDefaultSkuOptions,
  resolveSkuGroups,
  type CartItem,
  type Product,
  type ProductSku,
  type SkuGroup,
  type StorefrontSpec,
} from '@/domain/customer/product-catalog'

const userStore = useUserStore()
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
  return resolveCurrentTheme(storeInfo.value)
})

const isVendorMode = computed(() => userStore.isVendor)
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
  return filterProductsByCategory(activeCategory.value, products.value)
})

const skuGroups = computed<SkuGroup[]>(() => {
  return resolveSkuGroups(selectedProduct.value, styleSpecs.value)
})

const selectedSku = computed<ProductSku | null>(() => {
  return findSelectedSku(selectedProduct.value, skuGroups.value, selectedOptions.value)
})

const selectedSpecsText = computed(() =>
  buildSelectedSpecsText(skuGroups.value, selectedOptions.value)
)

const selectedProductPrice = computed(() => Number(selectedSku.value?.price ?? selectedProduct.value?.basePrice ?? 0))
const selectedProductTotal = computed(() => selectedProductPrice.value * selectedQuantity.value)

const promoTitle = computed(() => copywriting.value.promoTitle || '鲜果时令上新')
const promoSubtitle = computed(() => copywriting.value.promoSubtitle || '当季水果 · 清爽一夏')
const promoActionText = computed(() => copywriting.value.promoActionText || '立即尝鲜')

useDidShow(() => {
  userStore.loadViewMode()
  userStore.syncTabBarForViewMode()
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
  } catch {
    storeInfo.value = createMockStore()
    categories.value = resolveCategories(storeInfo.value)
    products.value = getMockProducts()
    styleSpecs.value = []
  } finally {
    persistCustomerTheme(currentTheme.value)
    isLoading.value = false
  }
}

function getStoreId(): string {
  const pages = Taro.getCurrentPages()
  const current = pages[pages.length - 1]
  const options = (current as { options?: Record<string, string> })?.options || {}
  return options.storeId || Taro.getStorageSync('current_store_id') || '1'
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
  } catch {
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

function ignoreNativeTabBarResult(action: () => unknown) {
  try {
    const result = action()
    if (result && typeof (result as { catch?: unknown }).catch === 'function') {
      void (result as Promise<unknown>).catch(() => undefined)
    }
  } catch {
    // H5 may throw plain objects for unsupported native tabBar APIs.
  }
}

function openProductDetail(product: Product) {
  if (!isVendorMode.value && isProductDisabled(product)) {
    Taro.showToast({ title: getStatusText(product) || '暂不可购买', icon: 'none' })
    return
  }

  selectedProduct.value = product
  selectedQuantity.value = 1
  selectedRemark.value = ''
  selectedOptions.value = resolveDefaultSkuOptions(product, styleSpecs.value)
  ignoreNativeTabBarResult(() => Taro.hideTabBar({ animation: true }))
}

function closeProductDetail() {
  selectedProduct.value = null
  selectedOptions.value = {}
  selectedQuantity.value = 1
  selectedRemark.value = ''
  ignoreNativeTabBarResult(() => Taro.showTabBar({ animation: true }))
}

function selectSkuOption(group: SkuGroup, option: string) {
  if (isVendorMode.value) return
  if (isSkuOptionDisabled(group, option)) return
  selectedOptions.value = {
    ...selectedOptions.value,
    [group.id]: option,
  }
}

function isSkuOptionDisabled(group: SkuGroup, option: string): boolean {
  return !isSkuOptionAvailable(selectedProduct.value, skuGroups.value, selectedOptions.value, group, option)
}

function changeSelectedQuantity(delta: number) {
  if (isVendorMode.value) return
  const stockLimit = selectedSku.value?.stock ?? selectedProduct.value?.stock ?? 1
  const nextQuantity = selectedQuantity.value + delta
  selectedQuantity.value = Math.min(Math.max(nextQuantity, 1), Math.min(stockLimit, 10))
}

function addSelectedProductToCart() {
  if (isVendorMode.value) return
  const product = selectedProduct.value
  if (!product) return
  if (product.skus && product.skus.length > 0 && !selectedSku.value) {
    Taro.showToast({ title: '请选择可售规格', icon: 'none' })
    return
  }

  const cartProduct = createSelectedCartProduct(product, selectedSku.value, selectedProductPrice.value, selectedSpecsText.value)
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
  if (isVendorMode.value) {
    Taro.showToast({ title: '商家视角不使用购物车', icon: 'none' })
    return
  }
  Taro.switchTab({ url: '/pages/customer/cart/cart' })
}

function goToProduct(product: Product) {
  openProductDetail(product)
}

function goToCheckout() {
  if (isVendorMode.value) {
    Taro.showToast({ title: '切回用户视角即可下单', icon: 'none' })
    return
  }
  if (cartCount.value <= 0) {
    Taro.showToast({ title: '先选一杯吧', icon: 'none' })
    return
  }
  Taro.switchTab({ url: '/pages/customer/cart/cart' })
}

function formatPrice(price: number): string {
  return price.toFixed(0)
}

onBeforeUnmount(() => {
  ignoreNativeTabBarResult(() => Taro.showTabBar({ animation: false }))
})
</script>

<template>
  <view class="fruit-tea-page" :class="{ 'vendor-mode': isVendorMode }" :style="themeVars">
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
          <text class="section-title">{{ isVendorMode ? '商品浏览' : '人气推荐' }}</text>
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
              <text>{{ isVendorMode ? '看' : '+' }}</text>
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
            <text class="sku-section-title">{{ isVendorMode ? group.name : `选择${group.name}` }}</text>
            <text v-if="!group.required && !isVendorMode" class="sku-section-optional">（可多选）</text>
          </view>
          <view class="sku-options">
            <view
              v-for="option in group.options"
              :key="option"
              class="sku-option"
              :class="{
                active: selectedOptions[group.id] === option,
                readonly: isVendorMode,
                disabled: !isVendorMode && isSkuOptionDisabled(group, option),
              }"
              @tap="selectSkuOption(group, option)"
            >
              <text class="sku-option-name">{{ option }}</text>
              <text v-if="selectedOptions[group.id] === option" class="sku-option-check">✓</text>
            </view>
          </view>
        </view>

        <view v-if="isVendorMode && selectedProduct" class="vendor-product-meta">
          <view class="vendor-meta-item">
            <text class="vendor-meta-label">商品状态</text>
            <text class="vendor-meta-value">{{ getStatusText(selectedProduct) || '在售' }}</text>
          </view>
          <view class="vendor-meta-item">
            <text class="vendor-meta-label">当前库存</text>
            <text class="vendor-meta-value">{{ selectedSku?.stock ?? selectedProduct.stock ?? 0 }}</text>
          </view>
          <view class="vendor-meta-item">
            <text class="vendor-meta-label">销量</text>
            <text class="vendor-meta-value">{{ selectedProduct.sales || 0 }}</text>
          </view>
        </view>

        <view v-if="!isVendorMode" class="detail-quantity-row">
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

        <view v-if="!isVendorMode" class="detail-remark-row">
          <text class="detail-remark-title">订单备注</text>
          <input
            v-model="selectedRemark"
            class="detail-remark-input"
            placeholder="选填，口味偏好等要求"
            placeholder-class="detail-remark-placeholder"
          />
          <text class="detail-remark-arrow">›</text>
        </view>

        <view v-if="!isVendorMode" class="detail-submit-bar">
          <view class="detail-selected">
            <text class="detail-selected-price">¥{{ formatPrice(selectedProductTotal) }}</text>
            <text class="detail-selected-text">已选：{{ selectedSpecsText || '默认规格' }}</text>
          </view>
          <view class="detail-submit-btn" :class="{ disabled: selectedProduct.skus?.length && !selectedSku }" @tap="addSelectedProductToCart">
            加入购物车
          </view>
        </view>

        <view v-else class="vendor-detail-footer">
          <text>商家视角仅查看商品信息，切回用户视角后可加入购物车。</text>
        </view>
      </view>
    </view>

    <view v-if="!isVendorMode" class="cart-bar">
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
@use './index.scss';
</style>
