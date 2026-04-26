<script setup lang="ts">
import { ref, computed, useDidShow } from '@tarojs/taro'
import Taro from '@tarojs/taro'

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
const isLoading = ref(false)

// ============================================================
// Computed
// ============================================================
const isEmpty = computed(() => cartItems.value.length === 0)

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

// ============================================================
// Lifecycle
// ============================================================
useDidShow(() => {
  loadCart()
})

// ============================================================
// Cart Operations
// ============================================================
function loadCart() {
  isLoading.value = true
  try {
    const stored = Taro.getStorageSync('cart_items')
    if (stored) {
      cartItems.value = JSON.parse(stored) as CartItem[]
    } else {
      cartItems.value = []
    }
  } catch (e) {
    console.error('[Cart] Load failed:', e)
    cartItems.value = []
  } finally {
    isLoading.value = false
  }
}

function saveCart() {
  Taro.setStorageSync('cart_items', JSON.stringify(cartItems.value))
}

function decreaseQuantity(index: number) {
  const item = cartItems.value[index]
  if (!item) return
  if (item.quantity <= 1) {
    // If quantity is 1, ask to delete
    Taro.showModal({
      title: '确认删除',
      content: `确定要删除「${item.product.name}」吗？`,
      confirmColor: '#FF6B35',
      success: (res) => {
        if (res.confirm) {
          removeItem(index)
        }
      },
    })
    return
  }
  item.quantity--
  saveCart()
}

function increaseQuantity(index: number) {
  const item = cartItems.value[index]
  if (!item) return
  if (item.quantity >= Math.min(item.product.stock || 10, 10)) {
    Taro.showToast({ title: '已达购买上限', icon: 'none' })
    return
  }
  item.quantity++
  saveCart()
}

function removeItem(index: number) {
  cartItems.value.splice(index, 1)
  saveCart()
  Taro.showToast({ title: '已删除', icon: 'none' })
}

function clearAll() {
  Taro.showModal({
    title: '清空购物车',
    content: '确定要清空购物车吗？',
    confirmColor: '#FF6B35',
    success: (res) => {
      if (res.confirm) {
        cartItems.value = []
        saveCart()
        Taro.showToast({ title: '已清空', icon: 'none' })
      }
    },
  })
}

// ============================================================
// Navigation
// ============================================================
function goToStore() {
  Taro.switchTab({ url: '/pages/customer/index/index' })
}

function goToConfirmOrder() {
  if (isEmpty.value) {
    Taro.showToast({ title: '购物车是空的', icon: 'none' })
    return
  }

  // Store selected items for order confirmation (all items)
  Taro.setStorageSync('selected_cart_items', JSON.stringify(cartItems.value))
  Taro.navigateTo({ url: '/pages/customer/confirm-order/confirm-order' })
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
  <view class="cart-page">
    <!-- ==================== Status Bar Spacer ==================== -->
    <view class="status-bar-spacer" />

    <!-- ==================== Header ==================== -->
    <view class="cart-header">
      <view class="header-title">购物车</view>
      <view
        class="header-action"
        v-if="!isEmpty"
        @tap="clearAll"
      >
        <text class="action-text">清空</text>
      </view>
    </view>

    <!-- ==================== Loading State ==================== -->
    <view v-if="isLoading" class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- ==================== Empty State ==================== -->
    <view v-else-if="isEmpty" class="empty-state">
      <view class="empty-illustration">
        <text class="empty-emoji">🛒</text>
      </view>
      <text class="empty-title">购物车是空的</text>
      <text class="empty-desc">快去挑选心仪的商品吧</text>
      <view class="empty-btn" @tap="goToStore">
        <text class="empty-btn-text">去逛逛</text>
      </view>
    </view>

    <!-- ==================== Cart List ==================== -->
    <scroll-view
      v-else
      class="cart-scroll"
      scroll-y
      :show-scrollbar="false"
    >
      <view class="cart-list">
        <!-- ==================== Store Section ==================== -->
        <view class="store-section">
          <view class="store-header">
            <view class="store-info">
              <text class="store-icon">🏪</text>
              <text class="store-name">老王煎饼摊</text>
            </view>
          </view>

          <!-- Cart Items -->
          <view
            v-for="(item, index) in cartItems"
            :key="item.product.id"
            class="cart-item"
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
              <view class="item-bottom">
                <text class="item-price">
                  <text class="price-symbol">¥</text>
                  <text class="price-value">{{ formatPrice(getItemPrice(item)) }}</text>
                </text>
              </view>
            </view>

            <!-- Right Column: Qty + Delete -->
            <view class="item-right-col">
              <!-- Subtotal -->
              <text class="item-subtotal">
                ¥{{ formatPrice(getItemSubtotal(item)) }}
              </text>

              <!-- Quantity Stepper -->
              <view class="qty-stepper">
                <view
                  class="qty-btn qty-decrease"
                  @tap="decreaseQuantity(index)"
                >
                  <text class="qty-btn-text">−</text>
                </view>
                <text class="qty-value">{{ item.quantity }}</text>
                <view
                  class="qty-btn qty-increase"
                  @tap="increaseQuantity(index)"
                >
                  <text class="qty-btn-text">+</text>
                </view>
              </view>

              <!-- Delete Button -->
              <view
                class="delete-btn"
                @tap="() => removeItem(index)"
              >
                <text class="delete-icon">🗑</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- Bottom Safe Area -->
      <view class="bottom-safe-area" />
    </scroll-view>

    <!-- ==================== Bottom Settlement Bar ==================== -->
    <view class="settlement-bar" v-if="!isEmpty">
      <view class="settlement-left">
        <text class="settlement-count">共{{ totalCount }}件</text>
        <view class="settlement-total">
          <text class="total-label">合计:</text>
          <text class="total-price">
            <text class="total-symbol">¥</text>
            <text class="total-value">{{ formatPrice(totalPrice) }}</text>
          </text>
        </view>
      </view>
      <view
        class="settlement-btn"
        :class="{ disabled: isEmpty }"
        @tap="goToConfirmOrder"
      >
        <text class="settlement-btn-text">去结算</text>
      </view>
    </view>
  </view>
</template>

<style>
@import './cart.scss';
</style>
