<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDidShow, usePullDownRefresh, useReachBottom } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { get, put } from '@/utils/request'
import { useUserStore } from '@/store/user'
import { createCustomerThemeVars, getCurrentCustomerTheme } from '@/utils/customer-theme'

// ============================================================
// Types
// ============================================================
type OrderStatus = 'NEW' | 'ACCEPTED' | 'PREPARING' | 'READY' | 'COMPLETED' | 'REJECTED'

interface OrderItemSpec {
  name: string
  option: string
  extraPrice: number
}

interface OrderItem {
  productId: string
  productName: string
  quantity: number
  unitPrice: number
  specs?: OrderItemSpec[]
}

interface Order {
  id: string
  orderNo: string
  storeId: string
  storeName: string
  status: OrderStatus
  confirmCode: string
  totalAmount: number
  remark?: string
  createdAt: string
  items: OrderItem[]
}

interface BackendOrderItem {
  id?: number | string
  productId: number | string
  productName: string
  price?: number
  unitPrice?: number
  quantity: number
  specName?: string
  specsText?: string
  specs?: OrderItemSpec[]
}

interface BackendOrder {
  id: number | string
  orderNo: string
  storeId: number | string
  storeName?: string
  status: string
  confirmCode: string
  totalAmount: number
  remark?: string
  createdAt: string
  items?: BackendOrderItem[]
}

interface StatusTab {
  label: string
  value: string // 'ALL' | 'NEW' | 'IN_PROGRESS' | 'COMPLETED' | 'REJECTED'
}

// ============================================================
// Constants
// ============================================================
const statusMap: Record<OrderStatus, { label: string; color: string }> = {
  'NEW': { label: '待接单', color: '#F59E0B' },
  'ACCEPTED': { label: '已接单', color: '#1890FF' },
  'PREPARING': { label: '备餐中', color: '#FF6B35' },
  'READY': { label: '待取餐', color: '#2EC4B6' },
  'COMPLETED': { label: '已完成', color: '#52C41A' },
  'REJECTED': { label: '已取消', color: '#999999' },
}

const customerTabs: StatusTab[] = [
  { label: '全部', value: 'ALL' },
  { label: '待接单', value: 'NEW' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
]

const vendorTabs: StatusTab[] = [
  { label: '全部', value: 'ALL' },
  { label: '新订单', value: 'NEW' },
  { label: '制作中', value: 'MAKING' },
  { label: '待取餐', value: 'READY' },
  { label: '已完成', value: 'COMPLETED' },
]

// ============================================================
// Store
// ============================================================
const userStore = useUserStore()

// ============================================================
// State
// ============================================================
const currentTabIndex = ref(0)
const orders = ref<Order[]>([])
const isLoading = ref(false)
const isLoadingMore = ref(false)
const isEmpty = ref(false)
const expandedOrderId = ref<string | null>(null)
const currentPage = ref(1)
const hasMore = ref(true)
const currentTheme = ref(getCurrentCustomerTheme())
const actionLoadingId = ref<string | null>(null)

// ============================================================
// Computed
// ============================================================
const isVendorMode = computed(() => userStore.isVendor)
const tabs = computed(() => isVendorMode.value ? vendorTabs : customerTabs)
const filteredOrders = computed(() => {
  const tab = tabs.value[currentTabIndex.value] || tabs.value[0]

  if (tab.value === 'ALL') {
    return orders.value
  }

  if (tab.value === 'NEW') {
    return orders.value.filter((o) => o.status === 'NEW')
  }

  if (tab.value === 'IN_PROGRESS') {
    return orders.value.filter((o) =>
      ['ACCEPTED', 'PREPARING', 'READY'].includes(o.status)
    )
  }

  if (tab.value === 'MAKING') {
    return orders.value.filter((o) =>
      ['ACCEPTED', 'PREPARING'].includes(o.status)
    )
  }

  if (tab.value === 'READY') {
    return orders.value.filter((o) => o.status === 'READY')
  }

  if (tab.value === 'COMPLETED') {
    return orders.value.filter((o) => o.status === 'COMPLETED' || o.status === 'REJECTED')
  }

  return orders.value
})
const themeVars = computed(() => createCustomerThemeVars(currentTheme.value))
const pageTheme = computed(() => currentTheme.value.pageThemes?.orders || {})

// ============================================================
// Lifecycle
// ============================================================
onMounted(() => {
  userStore.loadViewMode()
  loadOrders(true)
})

useDidShow(() => {
  // Refresh on page show (e.g., after order is placed)
  currentTheme.value = getCurrentCustomerTheme()
  userStore.loadViewMode()
  userStore.syncTabBarForViewMode()
  Taro.setNavigationBarTitle({ title: isVendorMode.value ? '接单管理' : '我的订单' })
  loadOrders(true)
})

usePullDownRefresh(() => {
  loadOrders(true)
  setTimeout(() => {
    Taro.stopPullDownRefresh()
  }, 1000)
})

useReachBottom(() => {
  if (!isLoadingMore.value && hasMore.value) {
    loadOrders(false)
  }
})

// ============================================================
// Data Loading
// ============================================================
async function loadOrders(isRefresh = false) {
  if (isRefresh) {
    isLoading.value = true
    currentPage.value = 1
    hasMore.value = true
  } else {
    isLoadingMore.value = true
    currentPage.value++
  }

  try {
    const res = await get<BackendOrder[]>(isVendorMode.value ? '/vendor/orders' : '/orders')
    const list = (res.data || []).map(normalizeOrder)

    if (isRefresh) {
      orders.value = list
    } else {
      orders.value = [...orders.value, ...list]
    }

    isEmpty.value = orders.value.length === 0
    hasMore.value = list.length >= 20
  } catch (err) {
    console.error('[MyOrders] Load orders failed:', err)
    if (isRefresh) isEmpty.value = orders.value.length === 0
    Taro.showToast({ title: '加载失败，请下拉刷新', icon: 'none' })
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

// ============================================================
// Tab Switching
// ============================================================
function switchTab(index: number) {
  currentTabIndex.value = index
  expandedOrderId.value = null
}

// ============================================================
// Order Card Actions
// ============================================================
function toggleOrderDetail(orderId: string) {
  expandedOrderId.value = expandedOrderId.value === orderId ? null : orderId
}

async function cancelOrder(order: Order) {
  Taro.showModal({
    title: '确认取消',
    content: '确定要取消该订单吗？',
    confirmColor: currentTheme.value.primary,
    success: async (res) => {
      if (res.confirm) {
        await doCancelOrder(order.id)
      }
    },
  })
}

async function acceptOrder(order: Order) {
  await transitionVendorOrder(order, 'ACCEPTED', `/orders/${order.id}/accept`, '已接单')
}

async function rejectOrder(order: Order) {
  const res = await Taro.showModal({
    title: '确认拒单',
    content: `确定要拒接订单 ${order.orderNo} 吗？`,
    confirmText: '拒单',
    confirmColor: '#D14343',
  })
  if (res.confirm) {
    await transitionVendorOrder(order, 'REJECTED', `/orders/${order.id}/reject`, '已拒单')
  }
}

async function startPreparing(order: Order) {
  await transitionVendorOrder(order, 'PREPARING', `/orders/${order.id}/prepare`, '开始备餐')
}

async function markReady(order: Order) {
  await transitionVendorOrder(order, 'READY', `/orders/${order.id}/ready`, '已标记待取餐')
}

async function completeOrder(order: Order) {
  await transitionVendorOrder(order, 'COMPLETED', `/orders/${order.id}/complete`, '订单已完成')
}

async function transitionVendorOrder(order: Order, nextStatus: OrderStatus, endpoint: string, toastTitle: string) {
  if (actionLoadingId.value) return
  actionLoadingId.value = order.id
  try {
    await put(endpoint)
    order.status = nextStatus
    Taro.showToast({ title: toastTitle, icon: 'success' })
  } catch (err) {
    console.error('[MyOrders] Vendor transition failed:', err)
    Taro.showToast({ title: '操作失败，请重试', icon: 'none' })
  } finally {
    actionLoadingId.value = null
  }
}

async function doCancelOrder(orderId: string) {
  try {
    await put('/orders/' + orderId + '/reject')

    // Update local state
    const order = orders.value.find((o) => o.id === orderId)
    if (order) {
      order.status = 'REJECTED'
    }

    Taro.showToast({ title: '订单已取消', icon: 'success' })
    expandedOrderId.value = null
  } catch (err) {
    console.error('[MyOrders] Cancel order failed:', err)
    Taro.showToast({ title: '取消失败，请重试', icon: 'none' })
  }
}

async function reorderItems(order: Order) {
  try {
    // Add each item back to cart storage
    const cartItems = order.items.map((item) => ({
      product: {
        id: item.productId,
        name: item.productName,
        basePrice: item.unitPrice,
        image: '/static/product-placeholder.png',
        specs: item.specs?.map((spec) => ({
          name: spec.name,
          options: [{ name: spec.option, extraPrice: spec.extraPrice }],
        })),
      },
      quantity: item.quantity,
    }))

    Taro.setStorageSync('selected_cart_items', JSON.stringify(cartItems))

    // Navigate to confirm order
    Taro.navigateTo({ url: '/pages/customer/confirm-order/confirm-order' })
  } catch (err) {
    console.error('[MyOrders] Reorder failed:', err)
    Taro.showToast({ title: '再来一单失败', icon: 'none' })
  }
}

async function confirmPickup(_order: Order) {
  Taro.showToast({ title: '已确认取餐', icon: 'success' })
}

function goShopping() {
  Taro.switchTab({ url: '/pages/customer/index/index' })
}

// ============================================================
// Helpers
// ============================================================
function formatPrice(price: number): string {
  return price.toFixed(2)
}

function formatTime(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const todayStr = now.toDateString()
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)

  const dateStr2 = date.toDateString()

  let timePart = `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`

  if (dateStr2 === todayStr) {
    return `今天 ${timePart}`
  } else if (dateStr2 === yesterday.toDateString()) {
    return `昨天 ${timePart}`
  } else {
    return `${date.getMonth() + 1}/${date.getDate()} ${timePart}`
  }
}

function getStatusInfo(order: Order) {
  const info = statusMap[order.status] || { label: order.status, color: '#999999' }
  const label = isVendorMode.value && order.status === 'REJECTED' ? '已拒单' : info.label
  return {
    ...info,
    label,
    color: pageTheme.value.statusColors?.[order.status] || info.color,
  }
}

function getOrderTitle(order: Order): string {
  return isVendorMode.value ? `取餐码 ${order.confirmCode}` : order.storeName
}

function normalizeOrder(raw: BackendOrder): Order {
  return {
    id: String(raw.id),
    orderNo: raw.orderNo,
    storeId: String(raw.storeId),
    storeName: raw.storeName || '小新の水果茶屋',
    status: normalizeOrderStatus(raw.status),
    confirmCode: raw.confirmCode,
    totalAmount: Number(raw.totalAmount || 0),
    remark: raw.remark,
    createdAt: raw.createdAt,
    items: (raw.items || []).map((item) => ({
      productId: String(item.productId),
      productName: item.productName,
      quantity: item.quantity,
      unitPrice: Number(item.unitPrice ?? item.price ?? 0),
      specs: item.specs || normalizeOrderItemSpecs(item),
    })),
  }
}

function normalizeOrderItemSpecs(item: BackendOrderItem): OrderItemSpec[] | undefined {
  const specsText = item.specsText || item.specName
  if (!specsText) return undefined

  return specsText
    .split('/')
    .map((option) => option.trim())
    .filter(Boolean)
    .map((option) => ({ name: '规格', option, extraPrice: 0 }))
}

function normalizeOrderStatus(status: string): OrderStatus {
  const normalized = status.toUpperCase()
  if (normalized === 'PENDING') return 'NEW'
  if (normalized === 'ACCEPTED') return 'ACCEPTED'
  if (normalized === 'PREPARING') return 'PREPARING'
  if (normalized === 'READY') return 'READY'
  if (normalized === 'COMPLETED') return 'COMPLETED'
  if (normalized === 'REJECTED' || normalized === 'CANCELLED') return 'REJECTED'
  return 'NEW'
}

function getSpecsText(item: OrderItem): string {
  if (!item.specs || item.specs.length === 0) return ''
  return item.specs.map((s) => `${s.name}: ${s.option}`).join(' + ')
}

function getProgressStep(order: Order): number {
  switch (order.status) {
    case 'NEW': return 0
    case 'ACCEPTED': return 1
    case 'PREPARING': return 2
    case 'READY': return 3
    case 'COMPLETED': return 4
    default: return -1
  }
}

function canCancel(order: Order): boolean {
  return order.status === 'NEW'
}

function canReorder(order: Order): boolean {
  return order.status === 'COMPLETED' || order.status === 'REJECTED'
}

function canConfirmPickup(order: Order): boolean {
  return order.status === 'READY'
}

function isActionLoading(order: Order): boolean {
  return actionLoadingId.value === order.id
}
</script>

<template>
  <view class="my-orders-page" :style="themeVars">
    <view v-if="pageTheme.headerBanner?.imageUrl" class="orders-theme-banner">
      <image class="orders-theme-banner-image" :src="pageTheme.headerBanner.imageUrl" mode="aspectFill" />
      <view class="orders-theme-banner-copy">
        <text class="orders-theme-banner-title">{{ pageTheme.headerBanner.title }}</text>
        <text class="orders-theme-banner-subtitle">{{ pageTheme.headerBanner.subtitle }}</text>
      </view>
    </view>

    <!-- ==================== Status Tabs ==================== -->
    <view class="tabs-wrapper">
      <view class="tabs">
        <view
          v-for="(tab, index) in tabs"
          :key="tab.value"
          class="tab-item"
          :class="{ active: currentTabIndex === index }"
          @tap="switchTab(index)"
        >
          <text class="tab-label">{{ tab.label }}</text>
          <view v-if="currentTabIndex === index" class="tab-indicator" />
        </view>
      </view>
    </view>

    <!-- ==================== Loading Skeleton ==================== -->
    <view v-if="isLoading" class="orders-scroll">
      <view v-for="i in 3" :key="i" class="order-card skeleton-card">
        <view class="skeleton-header">
          <view class="skeleton skeleton-store" />
          <view class="skeleton skeleton-badge" />
        </view>
        <view class="skeleton-items">
          <view class="skeleton-item">
            <view class="skeleton skeleton-image" />
            <view class="skeleton-item-info">
              <view class="skeleton skeleton-name" />
              <view class="skeleton skeleton-spec" />
            </view>
            <view class="skeleton skeleton-price" />
          </view>
        </view>
        <view class="skeleton-footer">
          <view class="skeleton skeleton-time" />
          <view class="skeleton skeleton-total" />
        </view>
      </view>
    </view>

    <!-- ==================== Empty State ==================== -->
    <view v-else-if="isEmpty" class="empty-state">
      <view class="empty-icon-wrap">
        <text class="empty-icon">📋</text>
      </view>
      <text class="empty-title">{{ isVendorMode ? '暂无待处理订单' : (pageTheme.emptyTitle || '暂无订单') }}</text>
      <text class="empty-desc">{{ isVendorMode ? '新订单会自动出现在接单列表中' : (pageTheme.emptySubtitle || '快去点一杯清爽果茶吧') }}</text>
      <view v-if="!isVendorMode" class="empty-btn" @tap="goShopping">
        <text class="empty-btn-text">去逛逛</text>
      </view>
    </view>

    <!-- ==================== Orders List ==================== -->
    <scroll-view
      v-else
      class="orders-scroll"
      scroll-y
      :show-scrollbar="false"
      :enhanced="true"
    >
      <view
        v-for="order in filteredOrders"
        :key="order.id"
        class="order-card"
        :class="{ expanded: expandedOrderId === order.id }"
      >
        <!-- ==================== Card Header ==================== -->
        <view class="order-header">
          <view class="order-header-left">
            <text class="store-name">{{ getOrderTitle(order) }}</text>
            <view class="status-badge" :style="{ backgroundColor: getStatusInfo(order).color }">
              <text class="status-text">{{ getStatusInfo(order).label }}</text>
            </view>
          </view>
          <view class="expand-icon" :class="{ rotated: expandedOrderId === order.id }">
            <text @tap.stop="toggleOrderDetail(order.id)">›</text>
          </view>
        </view>

        <!-- ==================== Order Info (collapsed) ==================== -->
        <view class="order-info">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text class="order-time">{{ formatTime(order.createdAt) }}</text>
        </view>

        <!-- ==================== Pickup Code (always visible) ==================== -->
        <view class="pickup-code-card">
          <text class="pickup-code-label">取餐码</text>
          <text class="pickup-code-value">{{ order.confirmCode }}</text>
        </view>

        <!-- ==================== Items Preview ==================== -->
        <view class="order-items-preview">
          <view
            v-for="(item, idx) in order.items.slice(0, 2)"
            :key="idx"
            class="item-row"
          >
            <text class="item-name line-clamp-1">{{ item.productName }}</text>
            <text class="item-qty">x{{ item.quantity }}</text>
          </view>
          <view v-if="order.items.length > 2" class="more-items">
            <text>还有 {{ order.items.length - 2 }} 件</text>
          </view>
        </view>

        <!-- ==================== Order Footer (collapsed) ==================== -->
        <view class="order-footer">
          <view class="footer-left">
            <text class="item-count-text">共 {{ order.items.reduce((s, i) => s + i.quantity, 0) }} 件</text>
          </view>
          <view class="footer-right">
            <text class="total-label">合计</text>
            <text class="total-price">¥{{ formatPrice(order.totalAmount) }}</text>
          </view>
        </view>

        <!-- ==================== Progress Steps (for IN_PROGRESS) ==================== -->
        <view v-if="['ACCEPTED', 'PREPARING', 'READY'].includes(order.status)" class="progress-steps">
          <view
            v-for="(step, idx) in ['接单', '备餐', '待取餐']"
            :key="idx"
            class="step-item"
            :class="{
              active: getProgressStep(order) >= idx + 1,
              current: getProgressStep(order) === idx + 1,
            }"
          >
            <view class="step-dot" />
            <text class="step-label">{{ step }}</text>
          </view>
        </view>

        <!-- ==================== Expanded Detail ==================== -->
        <view v-if="expandedOrderId === order.id" class="order-detail">
          <view class="detail-divider" />

          <!-- Full Items -->
          <view class="detail-items">
            <view
              v-for="(item, idx) in order.items"
              :key="idx"
              class="detail-item"
            >
              <view class="detail-item-left">
                <text class="detail-item-name">{{ item.productName }}</text>
                <text v-if="getSpecsText(item)" class="detail-item-spec">
                  {{ getSpecsText(item) }}
                </text>
              </view>
              <view class="detail-item-right">
                <text class="detail-item-price">¥{{ formatPrice(item.unitPrice) }}</text>
                <text class="detail-item-qty">x{{ item.quantity }}</text>
              </view>
            </view>
          </view>

          <!-- Remark -->
          <view v-if="order.remark" class="detail-remark">
            <text class="remark-label">备注：</text>
            <text class="remark-text">{{ order.remark }}</text>
          </view>

          <!-- Order Meta -->
          <view class="detail-meta">
            <text class="meta-text">下单时间：{{ order.createdAt }}</text>
          </view>

          <!-- Action Buttons -->
          <view class="detail-actions">
            <template v-if="isVendorMode">
              <template v-if="order.status === 'NEW'">
                <view
                  class="action-btn danger"
                  :class="{ disabled: isActionLoading(order) }"
                  @tap.stop="rejectOrder(order)"
                >
                  <text>{{ isActionLoading(order) ? '处理中' : '拒单' }}</text>
                </view>
                <view
                  class="action-btn primary"
                  :class="{ disabled: isActionLoading(order) }"
                  @tap.stop="acceptOrder(order)"
                >
                  <text>{{ isActionLoading(order) ? '处理中' : '接单' }}</text>
                </view>
              </template>
              <view
                v-else-if="order.status === 'ACCEPTED'"
                class="action-btn primary"
                :class="{ disabled: isActionLoading(order) }"
                @tap.stop="startPreparing(order)"
              >
                <text>{{ isActionLoading(order) ? '处理中' : '开始备餐' }}</text>
              </view>
              <view
                v-else-if="order.status === 'PREPARING'"
                class="action-btn primary"
                :class="{ disabled: isActionLoading(order) }"
                @tap.stop="markReady(order)"
              >
                <text>{{ isActionLoading(order) ? '处理中' : '备餐完成' }}</text>
              </view>
              <view
                v-else-if="order.status === 'READY'"
                class="action-btn primary"
                :class="{ disabled: isActionLoading(order) }"
                @tap.stop="completeOrder(order)"
              >
                <text>{{ isActionLoading(order) ? '处理中' : '完成订单' }}</text>
              </view>
            </template>

            <template v-else>
              <!-- 待接单: 取消订单 -->
              <view
                v-if="canCancel(order)"
                class="action-btn secondary"
                @tap.stop="cancelOrder(order)"
              >
                <text>取消订单</text>
              </view>

              <!-- 待取餐: 确认取餐 -->
              <view
                v-if="canConfirmPickup(order)"
                class="action-btn primary"
                @tap.stop="confirmPickup(order)"
              >
                <text>确认取餐</text>
              </view>

              <!-- 已完成/已取消: 再来一单 -->
              <view
                v-if="canReorder(order)"
                class="action-btn primary"
                @tap.stop="reorderItems(order)"
              >
                <text>再来一单</text>
              </view>
            </template>
          </view>
        </view>
      </view>

      <!-- ==================== Loading More ==================== -->
      <view v-if="isLoadingMore" class="loading-more">
        <text class="loading-more-text">加载中...</text>
      </view>

      <!-- ==================== No More ==================== -->
      <view v-if="!hasMore && filteredOrders.length > 0" class="no-more">
        <text class="no-more-text">— 没有更多了 —</text>
      </view>

      <view class="bottom-safe-area" />
    </scroll-view>
  </view>
</template>

<style lang="scss">
@use './my-orders.scss';
</style>
