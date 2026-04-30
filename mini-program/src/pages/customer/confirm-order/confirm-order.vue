<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import Taro from '@tarojs/taro'
import { post } from '@/utils/request'

// ============================================================
// Types
// ============================================================
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
  specs?: Array<{ name: string; options: Array<{ name: string; extraPrice: number }> }>
}

interface CartItem {
  product: Product
  quantity: number
}

// ============================================================
// State
// ============================================================
const cartItems = ref<CartItem[]>([])
const remark = ref('')
const isLoading = ref(false)
const isSubmitting = ref(false)
const showSuccessModal = ref(false)
const orderResult = ref<{ orderId: string; orderNo: string; confirmCode: string } | null>(null)

// Store name - read from first cart item or use default
const storeName = ref('老王煎饼摊')
const storeId = ref('1')

// ============================================================
// Computed
// ============================================================
const totalCount = computed(() =>
  cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
)

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => {
    const specsExtra = (item.product.specs || []).reduce((s, spec) => {
      return s + (spec.options?.[0]?.extraPrice || 0)
    }, 0)
    return sum + (item.product.basePrice + specsExtra) * item.quantity
  }, 0)
})

const isEmpty = computed(() => cartItems.value.length === 0)

const canSubmit = computed(() => !isEmpty.value && !isSubmitting.value)

// ============================================================
// Lifecycle
// ============================================================
onMounted(() => {
  loadCartItems()
})

// ============================================================
// Data Loading
// ============================================================
function loadCartItems() {
  isLoading.value = true
  try {
    // Try selected_cart_items first (set by cart page)
    let stored = Taro.getStorageSync('selected_cart_items')
    if (!stored) {
      // Fall back to full cart
      stored = Taro.getStorageSync('cart_items')
    }

    if (stored) {
      const items = JSON.parse(stored) as CartItem[]
      if (items.length === 0) {
        redirectToCart()
        return
      }
      cartItems.value = items
      // Get storeId from storage (set when entering storefront)
      storeId.value = Taro.getStorageSync('current_store_id') || '1'
    } else {
      redirectToCart()
    }
  } catch (e) {
    console.error('[ConfirmOrder] Load cart items failed:', e)
    redirectToCart()
  } finally {
    isLoading.value = false
  }
}

function redirectToCart() {
  Taro.showToast({ title: '购物车是空的', icon: 'none', duration: 1500 })
  setTimeout(() => {
    Taro.switchTab({ url: '/pages/customer/cart/cart' })
  }, 1500)
}

// ============================================================
// Navigation
// ============================================================
function goBack() {
  Taro.navigateBack({ fail: () => {
    Taro.switchTab({ url: '/pages/customer/cart/cart' })
  }})
}

// ============================================================
// Order Submission
// ============================================================
async function submitOrder() {
  if (isEmpty.value) {
    Taro.showToast({ title: '购物车是空的', icon: 'none' })
    return
  }

  if (isSubmitting.value) return

  isSubmitting.value = true

  try {
    // Call API
    const res = await post<any>('/orders', {
      storeId: Number(storeId.value),
      items: cartItems.value.map((item) => ({
        productId: Number(item.product.id),
        quantity: item.quantity,
        specName: getSpecsText(item.product) || undefined,
      })),
      remark: remark.value.trim() || undefined,
    })

    // Success
    orderResult.value = {
      orderId: String(res.data?.orderId ?? res.data?.id ?? ''),
      orderNo: res.data?.orderNo || '',
      confirmCode: res.data?.confirmCode || '',
    }

    // Clear cart storage
    Taro.removeStorageSync('cart_items')
    Taro.removeStorageSync('selected_cart_items')

    // Show success modal
    showSuccessModal.value = true

  } catch (err: any) {
    console.error('[ConfirmOrder] Submit failed:', err)
    Taro.showToast({
      title: err?.message || '订单提交失败，请重试',
      icon: 'none',
      duration: 2000,
    })
  } finally {
    isSubmitting.value = false
  }
}

// ============================================================
// Success Modal
// ============================================================
function onConfirmCodeCopied() {
  if (orderResult.value?.confirmCode) {
    Taro.setClipboardData({
      data: orderResult.value.confirmCode,
      success: () => {
        Taro.showToast({ title: '取餐码已复制', icon: 'none' })
      },
    })
  }
}

function goToOrders() {
  showSuccessModal.value = false
  // Navigate to orders list (tab page or regular page)
  Taro.switchTab({ url: '/pages/customer/my-orders/my-orders' })
}

function goHome() {
  showSuccessModal.value = false
  Taro.switchTab({ url: '/pages/customer/index/index' })
}

// ============================================================
// Helpers
// ============================================================
function formatPrice(price: number): string {
  return price.toFixed(2)
}

function getSpecsText(product: Product): string {
  if (!product.specs || product.specs.length === 0) return ''
  return product.specs
    .map((spec) => `${spec.name}: ${spec.options?.[0]?.name || ''}`)
    .join(' + ')
}

function getItemPrice(item: CartItem): number {
  const specsExtra = (item.product.specs || []).reduce((s, spec) => {
    return s + (spec.options?.[0]?.extraPrice || 0)
  }, 0)
  return item.product.basePrice + specsExtra
}

function getItemSubtotal(item: CartItem): number {
  return getItemPrice(item) * item.quantity
}
</script>

<template>
  <view class="confirm-order-page">
    <!-- ==================== Status Bar Spacer ==================== -->
    <view class="status-bar-spacer" />

    <!-- ==================== Header ==================== -->
    <view class="page-header">
      <view class="header-back" @tap="goBack">
        <text class="back-arrow">←</text>
      </view>
      <view class="header-title">确认订单</view>
      <view class="header-placeholder" />
    </view>

    <!-- ==================== Loading State ==================== -->
    <view v-if="isLoading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- ==================== Main Content ==================== -->
    <scroll-view
      v-else
      class="content-scroll"
      scroll-y
      :show-scrollbar="false"
    >
      <!-- ==================== Store Info Card ==================== -->
      <view class="store-card">
        <view class="store-header">
          <view class="store-icon-wrap">
            <text class="store-icon-emoji">🏪</text>
          </view>
          <view class="store-info">
            <text class="store-name">{{ storeName }}</text>
            <text class="store-subtitle">到店自取</text>
          </view>
        </view>
      </view>

      <!-- ==================== Order Items ==================== -->
      <view class="order-items-card">
        <view class="card-title">订单详情</view>

        <view
          v-for="item in cartItems"
          :key="item.product.id"
          class="order-item"
        >
          <!-- Item Image -->
          <image
            class="item-image"
            :src="item.product.image"
            mode="aspectFill"
            @error="(e: any) => { e.target.src = '/static/product-placeholder.png' }"
          />

          <!-- Item Info -->
          <view class="item-info">
            <text class="item-name line-clamp-2">{{ item.product.name }}</text>
            <text class="item-spec" v-if="getSpecsText(item.product)">
              {{ getSpecsText(item.product) }}
            </text>
          </view>

          <!-- Item Right: Price & Count -->
          <view class="item-right">
            <text class="item-price">
              ¥{{ formatPrice(getItemPrice(item)) }}
            </text>
            <text class="item-count">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>

      <!-- ==================== Remark Input ==================== -->
      <view class="remark-card">
        <text class="remark-label">订单备注</text>
        <input
          class="remark-input"
          v-model="remark"
          placeholder="选填，可备注特殊需求"
          placeholder-class="remark-placeholder"
          maxlength="100"
        />
      </view>

      <!-- ==================== Order Summary ==================== -->
      <view class="summary-card">
        <view class="summary-row">
          <text class="summary-label">商品金额</text>
          <text class="summary-value">¥{{ formatPrice(totalPrice) }}</text>
        </view>
        <view class="summary-row">
          <text class="summary-label">包装费</text>
          <text class="summary-value">¥0.00</text>
        </view>
        <view class="summary-row">
          <text class="summary-label">优惠</text>
          <text class="summary-value discount">-¥0.00</text>
        </view>
        <view class="summary-divider" />
        <view class="summary-row total-row">
          <text class="total-label">合计</text>
          <text class="total-value">
            <text class="total-symbol">¥</text>{{ formatPrice(totalPrice) }}
          </text>
        </view>
      </view>

      <!-- Bottom Safe Area -->
      <view class="bottom-safe-area" />
    </scroll-view>

    <!-- ==================== Bottom Submit Bar ==================== -->
    <view class="submit-bar" v-if="!isLoading">
      <view class="submit-bar-left">
        <text class="submit-total-label">实付金额</text>
        <text class="submit-total-price">
          <text class="submit-total-symbol">¥</text>
          <text class="submit-total-value">{{ formatPrice(totalPrice) }}</text>
        </text>
      </view>
      <view
        class="submit-btn"
        :class="{ loading: isSubmitting }"
        @tap="submitOrder"
      >
        <text v-if="!isSubmitting" class="submit-btn-text">提交订单</text>
        <text v-else class="submit-btn-text">提交中...</text>
      </view>
    </view>

    <!-- ==================== Success Modal ==================== -->
    <view class="success-modal" v-if="showSuccessModal" @tap.self="goToOrders">
      <view class="success-modal-content">
        <!-- Success Icon -->
        <view class="success-icon-wrap">
          <text class="success-icon">✓</text>
        </view>

        <text class="success-title">订单提交成功！</text>
        <text class="success-desc">请等待商家接单</text>

        <!-- Confirm Code -->
        <view class="confirm-code-card" v-if="orderResult">
          <text class="confirm-code-label">取餐码</text>
          <text class="confirm-code-value">{{ orderResult.confirmCode }}</text>
          <view class="confirm-code-tip" @tap="onConfirmCodeCopied">
            <text class="tip-icon">📋</text>
            <text class="tip-text">点击复制</text>
          </view>
        </view>

        <!-- Order Info -->
        <view class="order-info-row" v-if="orderResult">
          <text class="order-info-label">订单号</text>
          <text class="order-info-value">{{ orderResult.orderNo }}</text>
        </view>

        <!-- Action Buttons -->
        <view class="success-actions">
          <view class="action-btn secondary" @tap="goHome">
            <text class="action-btn-text-secondary">返回首页</text>
          </view>
          <view class="action-btn primary" @tap="goToOrders">
            <text class="action-btn-text-primary">查看订单</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss">
@import './confirm-order.scss';
</style>
