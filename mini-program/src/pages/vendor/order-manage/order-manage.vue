<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useDidShow } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useUserStore } from '@/store/user'
import { get, put } from '@/utils/request'

// ─── Types ─────────────────────────────────────────────────────────────────
interface OrderItem {
  id: string
  name: string
  price: number
  quantity: number
  spec?: string
}

interface VendorOrder {
  id: string
  orderNo: string
  userId: string
  confirmCode: string
  status: 'NEW' | 'ACCEPTED' | 'PREPARING' | 'READY' | 'COMPLETED' | 'REJECTED'
  totalAmount: number
  createdAt: string
  items: OrderItem[]
  remark?: string
}

// ─── Store ──────────────────────────────────────────────────────────────────
const userStore = useUserStore()

// ─── State ─────────────────────────────────────────────────────────────────
const currentTab = ref(0) // 0=全部 1=待接单 2=进行中 3=已完成
const allOrders = ref<VendorOrder[]>([])
const isLoading = ref(false)
const isRefreshing = ref(false)
const actionLoadingId = ref<string | null>(null)

// Tab mapping
const statusMap: Record<number, string[]> = {
  0: [], // 全部
  1: ['NEW'], // 待接单
  2: ['ACCEPTED', 'PREPARING', 'READY'], // 进行中
  3: ['COMPLETED', 'REJECTED'], // 已完成
}

const tabs = ['全部', '待接单', '进行中', '已完成']

// ─── Computed ────────────────────────────────────────────────────────────────
const isVendor = computed(() => userStore.isVendor)

const newOrderCount = computed(() =>
  allOrders.value.filter((o) => o.status === 'NEW').length
)

const filteredOrders = computed(() => {
  const statuses = statusMap[currentTab.value]
  if (statuses.length === 0) return allOrders.value
  return allOrders.value.filter((o) => statuses.includes(o.status))
})

const statusLabel: Record<string, string> = {
  NEW: '待接单',
  ACCEPTED: '已接单',
  PREPARING: '备餐中',
  READY: '待取餐',
  COMPLETED: '已完成',
  REJECTED: '已拒单',
}

const statusClass: Record<string, string> = {
  NEW: 'status-new',
  ACCEPTED: 'status-accepted',
  PREPARING: 'status-preparing',
  READY: 'status-ready',
  COMPLETED: 'status-completed',
  REJECTED: 'status-rejected',
}

// ─── Lifecycle ───────────────────────────────────────────────────────────────
useDidShow(() => {
  if (!isVendor.value) return
  loadOrders()
})

onMounted(() => {
  Taro.setNavigationBarTitle({ title: '订单管理' })
  // Poll for new orders every 30s
  startPolling()
})

onUnmounted(() => {
  stopPolling()
})

let pollTimer: ReturnType<typeof setInterval> | null = null

function startPolling() {
  stopPolling()
  pollTimer = setInterval(() => {
    if (isVendor.value && !isRefreshing.value) {
      loadOrders(false)
    }
  }, 30000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// ─── Pull to Refresh ─────────────────────────────────────────────────────────
async function onPullDownRefresh() {
  await loadOrders(true).finally(() => {
    Taro.stopPullDownRefresh()
  })
}

// ─── Data Loading ────────────────────────────────────────────────────────────
async function loadOrders(showLoading = true) {
  if (showLoading) isLoading.value = true
  isRefreshing.value = true
  try {
    const res = await get<any[]>('/vendor/orders')
    allOrders.value = res.data ?? []

    // Badge notification for new orders
    if (newOrderCount.value > 0) {
      Taro.setTabBarBadge({
        index: 1, // vendor tab
        text: String(newOrderCount.value),
      })
    } else {
      Taro.removeTabBarBadge({ index: 1 })
    }
  } catch (e: any) {
    console.error('[OrderManage] loadOrders failed:', e)
    Taro.showToast({ title: e?.message || '加载失败', icon: 'none' })
  } finally {
    isLoading.value = false
    isRefreshing.value = false
  }
}

// ─── Tab Switching ──────────────────────────────────────────────────────────
function switchTab(index: number) {
  currentTab.value = index
}

// ─── Order Actions ─────────────────────────────────────────────────────────
async function acceptOrder(order: VendorOrder) {
  if (actionLoadingId.value) return
  actionLoadingId.value = order.id
  try {
    await put('/orders/' + order.id + '/accept')
    order.status = 'ACCEPTED'
    Taro.showToast({ title: '接单成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '接单失败', icon: 'none' })
  } finally {
    actionLoadingId.value = null
  }
}

async function rejectOrder(order: VendorOrder) {
  const res = await Taro.showModal({
    title: '确认拒单',
    content: `确定要拒接订单 ${order.orderNo} 吗？`,
    confirmText: '确认拒单',
    confirmColor: '#FF4D4F',
  })
  if (!res.confirm) return

  if (actionLoadingId.value) return
  actionLoadingId.value = order.id
  try {
    await put('/orders/' + order.id + '/reject')
    order.status = 'REJECTED'
    Taro.showToast({ title: '已拒单', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '拒单失败', icon: 'none' })
  } finally {
    actionLoadingId.value = null
  }
}

async function startPreparing(order: VendorOrder) {
  if (actionLoadingId.value) return
  actionLoadingId.value = order.id
  try {
    await put('/orders/' + order.id + '/prepare')
    order.status = 'PREPARING'
    Taro.showToast({ title: '已开始备餐', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '操作失败', icon: 'none' })
  } finally {
    actionLoadingId.value = null
  }
}

async function markReady(order: VendorOrder) {
  if (actionLoadingId.value) return
  actionLoadingId.value = order.id
  try {
    await put('/orders/' + order.id + '/ready')
    order.status = 'READY'
    Taro.showToast({ title: '已备餐完成', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '操作失败', icon: 'none' })
  } finally {
    actionLoadingId.value = null
  }
}

async function confirmPickup(order: VendorOrder) {
  if (actionLoadingId.value) return
  actionLoadingId.value = order.id
  try {
    await put('/orders/' + order.id + '/complete')
    order.status = 'COMPLETED'
    Taro.showToast({ title: '订单完成', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '操作失败', icon: 'none' })
  } finally {
    actionLoadingId.value = null
  }
}

function callCustomer(phone: string) {
  if (!phone) return
  Taro.makePhoneCall({ phoneNumber: phone })
}

function goToCustomerHome() {
  Taro.switchTab({ url: '/pages/customer/index/index' })
}

// ─── Formatting ─────────────────────────────────────────────────────────────
function formatPrice(price: number): string {
  return (price ?? 0).toFixed(2)
}

function formatTime(isoString: string): string {
  if (!isoString) return ''
  const d = new Date(isoString)
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

function getActionLoading(orderId: string): boolean {
  return actionLoadingId.value === orderId
}
</script>

<template>
  <view class="order-manage-page">
    <!-- Not Vendor State -->
    <block v-if="!isVendor">
      <view class="not-vendor">
        <view class="not-vendor-icon">🏪</view>
        <view class="not-vendor-title">您还不是摊主</view>
        <view class="not-vendor-desc">成为摊主后即可管理您的订单</view>
        <button class="btn-primary" @tap="goToCustomerHome">返回首页</button>
      </view>
    </block>

    <!-- Order Management -->
    <block v-else>
      <!-- Tabs -->
      <view class="tabs-bar">
        <view
          v-for="(tab, index) in tabs"
          :key="index"
          class="tab-item"
          :class="{ active: currentTab === index }"
          @tap="switchTab(index)"
        >
          <text class="tab-text">{{ tab }}</text>
          <view
            v-if="index === 1 && newOrderCount > 0"
            class="tab-badge"
          >
            {{ newOrderCount > 99 ? '99+' : newOrderCount }}
          </view>
          <view v-if="currentTab === index" class="tab-indicator" />
        </view>
      </view>

      <!-- Loading -->
      <block v-if="isLoading">
        <view class="loading-state">
          <view class="loading-spinner" />
          <text class="loading-text">加载中...</text>
        </view>
      </block>

      <!-- Empty -->
      <block v-else-if="filteredOrders.length === 0">
        <view class="empty-state">
          <text class="empty-icon">📭</text>
          <text class="empty-title">暂无订单</text>
          <text class="empty-desc">
            {{ currentTab === 1 ? '没有待接单的订单' : '当前分类暂无订单' }}
          </text>
        </view>
      </block>

      <!-- Orders List -->
      <block v-else>
        <scroll-view
          class="orders-scroll"
          scroll-y
          enhanced
          :show-scrollbar="false"
        >
          <view
            v-for="order in filteredOrders"
            :key="order.id"
            class="order-card"
          >
            <!-- Order Header -->
            <view class="order-header">
              <view class="order-id-col">
                <text class="order-no">{{ order.orderNo }}</text>
                <text class="order-time">{{ formatTime(order.createdAt) }}</text>
              </view>
              <view class="order-status" :class="statusClass[order.status]">
                {{ statusLabel[order.status] }}
              </view>
            </view>

            <!-- Confirm Code (prominent display) -->
            <view class="confirm-code-section">
              <text class="confirm-code-label">取餐码</text>
              <text class="confirm-code-value">{{ order.confirmCode }}</text>
            </view>

            <!-- Divider -->
            <view class="card-divider" />

            <!-- Items -->
            <view class="order-items">
              <view
                v-for="item in order.items"
                :key="item.id"
                class="order-item"
              >
                <text class="item-qty">{{ item.quantity }}×</text>
                <text class="item-name">{{ item.name }}</text>
                <text v-if="item.spec" class="item-spec">{{ item.spec }}</text>
                <text class="item-price">¥{{ formatPrice(item.price) }}</text>
              </view>
            </view>

            <!-- Remark -->
            <view v-if="order.remark" class="order-remark">
              <text class="remark-label">备注：</text>
              <text class="remark-text">{{ order.remark }}</text>
            </view>

            <!-- Divider -->
            <view class="card-divider" />

            <!-- Footer -->
            <view class="order-footer">
              <text class="order-total">
                实付
                <text class="total-price">¥{{ formatPrice(order.totalAmount) }}</text>
              </text>
              <view class="order-actions">
                <!-- NEW: 拒单 + 接单 -->
                <template v-if="order.status === 'NEW'">
                  <button
                    class="action-btn reject"
                    :disabled="getActionLoading(order.id)"
                    @tap="rejectOrder(order)"
                  >
                    {{ getActionLoading(order.id) ? '...' : '拒单' }}
                  </button>
                  <button
                    class="action-btn primary"
                    :disabled="getActionLoading(order.id)"
                    @tap="acceptOrder(order)"
                  >
                    {{ getActionLoading(order.id) ? '...' : '接单' }}
                  </button>
                </template>

                <!-- ACCEPTED: 开始备餐 -->
                <template v-else-if="order.status === 'ACCEPTED'">
                  <button
                    class="action-btn primary"
                    :disabled="getActionLoading(order.id)"
                    @tap="startPreparing(order)"
                  >
                    {{ getActionLoading(order.id) ? '...' : '开始备餐' }}
                  </button>
                </template>

                <!-- PREPARING: 备餐完成 -->
                <template v-else-if="order.status === 'PREPARING'">
                  <button
                    class="action-btn primary"
                    :disabled="getActionLoading(order.id)"
                    @tap="markReady(order)"
                  >
                    {{ getActionLoading(order.id) ? '...' : '备餐完成' }}
                  </button>
                </template>

                <!-- READY: 确认取餐 -->
                <template v-else-if="order.status === 'READY'">
                  <button
                    class="action-btn primary"
                    :disabled="getActionLoading(order.id)"
                    @tap="confirmPickup(order)"
                  >
                    {{ getActionLoading(order.id) ? '...' : '确认取餐' }}
                  </button>
                </template>

                <!-- COMPLETED / REJECTED: 查看详情 -->
                <template v-else-if="order.status === 'COMPLETED' || order.status === 'REJECTED'">
                  <button class="action-btn secondary" @tap="goToCustomerHome">
                    查看详情
                  </button>
                </template>
              </view>
            </view>
          </view>
        </scroll-view>
      </block>
    </block>
  </view>
</template>

<style lang="scss">
@import './order-manage.scss';
</style>
