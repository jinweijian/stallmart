<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDidShow, usePullDownRefresh, onReachBottom } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { get, put } from '@/utils/request'
import { useUserStore } from '@/store/user'

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

const tabs: StatusTab[] = [
  { label: '全部', value: 'ALL' },
  { label: '待接单', value: 'NEW' },
  { label: '进行中', value: 'IN_PROGRESS' },
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

// ============================================================
// Computed
// ============================================================
const filteredOrders = computed(() => {
  const tab = tabs[currentTabIndex.value]

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

  if (tab.value === 'COMPLETED') {
    return orders.value.filter((o) => o.status === 'COMPLETED' || o.status === 'REJECTED')
  }

  return orders.value
})

// ============================================================
// Lifecycle
// ============================================================
onMounted(() => {
  loadOrders(true)
})

useDidShow(() => {
  // Refresh on page show (e.g., after order is placed)
  loadOrders(true)
})

usePullDownRefresh(() => {
  loadOrders(true)
  setTimeout(() => {
    Taro.stopPullDownRefresh()
  }, 1000)
})

onReachBottom(() => {
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
    const res = await get<any[]>('/orders')
    const list = res.data || []

    if (isRefresh) {
      orders.value = list
    } else {
      orders.value = [...orders.value, ...list]
    }

    isEmpty.value = orders.value.length === 0
    hasMore.value = list.length >= 20

    // Fallback to mock data if API returns empty
    if (orders.value.length === 0 && isRefresh) {
      orders.value = getMockOrders()
      isEmpty.value = false
    }
  } catch (err) {
    console.error('[MyOrders] Load orders failed:', err)
    // Fallback to mock data on error
    if (isRefresh && orders.value.length === 0) {
      orders.value = getMockOrders()
      isEmpty.value = false
    }
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
    confirmColor: '#FF6B35',
    success: async (res) => {
      if (res.confirm) {
        await doCancelOrder(order.id)
      }
    },
  })
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

async function confirmPickup(order: Order) {
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
  return statusMap[order.status] || { label: order.status, color: '#999999' }
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

// ============================================================
// Mock Data (for development fallback)
// ============================================================
function getMockOrders(): Order[] {
  const now = Date.now()
  return [
    {
      id: '1',
      orderNo: 'SM20260402001',
      storeId: 'store-001',
      storeName: '老王煎饼摊',
      status: 'READY',
      confirmCode: 'A108',
      totalAmount: 15.0,
      remark: '不要香菜',
      createdAt: new Date(now - 10 * 60000).toISOString(),
      items: [
        { productId: 'p1', productName: '招牌煎饼果子', quantity: 1, unitPrice: 11.0, specs: [{ name: '加料', option: '加蛋+薄脆', extraPrice: 2 }] },
        { productId: 'p2', productName: '现磨豆浆', quantity: 1, unitPrice: 4.0, specs: [{ name: '温度', option: '热/大杯', extraPrice: 0 }] },
      ],
    },
    {
      id: '2',
      orderNo: 'SM20260402002',
      storeId: 'store-002',
      storeName: '小李烤串',
      status: 'PREPARING',
      confirmCode: 'B205',
      totalAmount: 42.0,
      createdAt: new Date(now - 25 * 60000).toISOString(),
      items: [
        { productId: 'p3', productName: '羊肉串', quantity: 10, unitPrice: 3.0 },
        { productId: 'p4', productName: '烤茄子', quantity: 1, unitPrice: 12.0 },
      ],
    },
    {
      id: '3',
      orderNo: 'SM20260401003',
      storeId: 'store-001',
      storeName: '老王煎饼摊',
      status: 'COMPLETED',
      confirmCode: 'C033',
      totalAmount: 9.0,
      createdAt: new Date(now - 86400000).toISOString(),
      items: [
        { productId: 'p1', productName: '招牌煎饼果子', quantity: 1, unitPrice: 9.0, specs: [{ name: '加料', option: '加蛋', extraPrice: 1 }] },
      ],
    },
    {
      id: '4',
      orderNo: 'SM20260401004',
      storeId: 'store-003',
      storeName: '阿婆凉茶铺',
      status: 'NEW',
      confirmCode: 'D017',
      totalAmount: 22.0,
      createdAt: new Date(now - 5 * 60000).toISOString(),
      items: [
        { productId: 'p5', productName: '茅根竹蔗水', quantity: 2, unitPrice: 11.0 },
      ],
    },
    {
      id: '5',
      orderNo: 'SM20260401005',
      storeId: 'store-002',
      storeName: '小李烤串',
      status: 'REJECTED',
      confirmCode: 'E009',
      totalAmount: 30.0,
      createdAt: new Date(now - 2 * 86400000).toISOString(),
      items: [
        { productId: 'p6', productName: '牛肉串', quantity: 10, unitPrice: 3.0 },
      ],
    },
    {
      id: '6',
      orderNo: 'SM20260331006',
      storeId: 'store-001',
      storeName: '老王煎饼摊',
      status: 'ACCEPTED',
      confirmCode: 'F120',
      totalAmount: 18.0,
      createdAt: new Date(now - 3 * 60000).toISOString(),
      items: [
        { productId: 'p7', productName: '豪华煎饼套餐', quantity: 1, unitPrice: 18.0, specs: [{ name: '套餐', option: '双蛋+生菜', extraPrice: 5 }] },
      ],
    },
  ]
}
</script>

<template>
  <view class="my-orders-page">
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
      <text class="empty-title">暂无订单</text>
      <text class="empty-desc">快去下单，发现身边的美食吧</text>
      <view class="empty-btn" @tap="goShopping">
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
        <view class="order-header" @tap="toggleOrderDetail(order.id)">
          <view class="order-header-left">
            <text class="store-name">{{ order.storeName }}</text>
            <view class="status-badge" :style="{ backgroundColor: getStatusInfo(order).color }">
              <text class="status-text">{{ getStatusInfo(order).label }}</text>
            </view>
          </view>
          <view class="expand-icon" :class="{ rotated: expandedOrderId === order.id }">
            <text>›</text>
          </view>
        </view>

        <!-- ==================== Order Info (collapsed) ==================== -->
        <view class="order-info" @tap="toggleOrderDetail(order.id)">
          <text class="order-no">订单号: {{ order.orderNo }}</text>
          <text class="order-time">{{ formatTime(order.createdAt) }}</text>
        </view>

        <!-- ==================== Items Preview ==================== -->
        <view class="order-items-preview" @tap="toggleOrderDetail(order.id)">
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
        <view class="order-footer" @tap="toggleOrderDetail(order.id)">
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

          <!-- Confirm Code -->
          <view class="confirm-code-row">
            <text class="confirm-code-label">取餐码</text>
            <text class="confirm-code-value">{{ order.confirmCode }}</text>
          </view>

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
@import './my-orders.scss';
</style>
