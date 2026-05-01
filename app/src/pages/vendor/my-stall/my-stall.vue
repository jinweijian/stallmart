<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDidShow, pullDownRefresh } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useUserStore } from '@/store/user'
import { get, put } from '@/utils/request'
import { API_ENDPOINTS } from '@/config'

// ─── Types ────────────────────────────────────────────────────────────────
interface StallInfo {
  storeId: string
  storeName: string
  todayOrders: number
  todayRevenue: number
  pendingOrders: number
  isOpen: boolean
}

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
}

// ─── Store ─────────────────────────────────────────────────────────────────
const userStore = useUserStore()

// ─── State ─────────────────────────────────────────────────────────────────
const stallInfo = ref<StallInfo | null>(null)
const recentOrders = ref<VendorOrder[]>([])
const isLoading = ref(true)
const isToggling = ref(false)

// ─── Computed ───────────────────────────────────────────────────────────────
const isVendor = computed(() => userStore.isVendor)

const statusLabel: Record<string, string> = {
  NEW: '新订单',
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

// ─── Lifecycle ──────────────────────────────────────────────────────────────
useDidShow(() => {
  if (!isVendor.value) return
  loadData()
})

onMounted(() => {
  Taro.setNavigationBarTitle({ title: '我的摊位' })
})

// ─── Pull to Refresh ────────────────────────────────────────────────────────
function onPullDownRefresh() {
  loadData().finally(() => {
    Taro.stopPullDownRefresh()
  })
}

// ─── Data Loading ───────────────────────────────────────────────────────────
async function loadData() {
  isLoading.value = true
  try {
    const storeId = Taro.getStorageSync('vendor_store_id') || '1'
    const [stallRes, ordersRes] = await Promise.all([
      get<any>('/stores/' + storeId),
      get<any[]>('/vendor/orders'),
    ])
    const allOrders = ordersRes.data || []
    stallInfo.value = {
      storeId: stallRes.data.id,
      storeName: stallRes.data.name,
      todayOrders: allOrders.length,
      todayRevenue: allOrders.reduce((sum: number, o: any) => sum + o.totalAmount, 0),
      pendingOrders: allOrders.filter((o: any) => o.status === 'NEW' || o.status === 'ACCEPTED').length,
      isOpen: stallRes.data.isOpen !== false,
    }
    recentOrders.value = allOrders.slice(0, 5)
  } catch (e: any) {
    console.error('[MyStall] loadData failed:', e)
    Taro.showToast({ title: e?.message || '加载失败', icon: 'none' })
  } finally {
    isLoading.value = false
  }
}

// ─── Toggle Open/Closed ─────────────────────────────────────────────────────
async function toggleOpen() {
  if (!stallInfo.value || isToggling.value) return
  isToggling.value = true
  const newStatus = stallInfo.value.isOpen ? 'CLOSED' : 'OPEN'
  try {
    await put(API_ENDPOINTS.STORE_INFO(stallInfo.value.storeId), {
      status: newStatus,
    })
    stallInfo.value.isOpen = !stallInfo.value.isOpen
    Taro.showToast({
      title: stallInfo.value.isOpen ? '已开业' : '已休息',
      icon: 'success',
    })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '切换失败', icon: 'none' })
  } finally {
    isToggling.value = false
  }
}

// ─── Navigation ─────────────────────────────────────────────────────────────
function goToOrderManage() {
  Taro.navigateTo({ url: '/pages/vendor/order-manage/order-manage' })
}

function goToStallSettings() {
  Taro.navigateTo({ url: '/pages/vendor/stall-settings/stall-settings' })
}

function goToProductManage() {
  Taro.showToast({ title: '商品管理开发中', icon: 'none' })
}

function goToCustomerHome() {
  Taro.switchTab({ url: '/pages/customer/index/index' })
}

// ─── Formatting ─────────────────────────────────────────────────────────────
function formatPrice(price: number): string {
  if (price == null) return '0.00'
  return price.toFixed(2)
}

function formatTime(isoString: string): string {
  if (!isoString) return ''
  const d = new Date(isoString)
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

function formatDate(isoString: string): string {
  if (!isoString) return ''
  const d = new Date(isoString)
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${mm}月${dd}日`
}

function getItemSummary(items: OrderItem[]): string {
  if (!items?.length) return ''
  if (items.length <= 2) return items.map((i) => i.name).join('、')
  return `${items[0].name}等${items.length}件`
}
</script>

<template>
  <view class="my-stall-page">
    <!-- Not Vendor State -->
    <block v-if="!isVendor">
      <view class="not-vendor">
        <view class="not-vendor-icon">🏪</view>
        <view class="not-vendor-title">您还不是摊主</view>
        <view class="not-vendor-desc">成为摊主后即可管理您的摊位</view>
        <button class="btn-primary" @tap="goToCustomerHome">返回首页</button>
      </view>
    </block>

    <!-- Vendor Dashboard -->
    <block v-else>
      <!-- Loading State -->
      <block v-if="isLoading">
        <view class="loading-state">
          <view class="loading-spinner" />
          <text class="loading-text">加载中...</text>
        </view>
      </block>

      <!-- Content -->
      <block v-else>
        <!-- Header -->
        <view class="stall-header">
          <view class="stall-header-top">
            <view class="stall-info-row">
              <view class="stall-avatar-placeholder">
                <text class="stall-emoji">🏪</text>
              </view>
              <view class="stall-detail">
                <text class="stall-name">{{ stallInfo?.storeName || '我的摊位' }}</text>
                <view class="stall-status-badge" :class="stallInfo?.isOpen ? 'open' : 'closed'">
                  <text class="status-dot" />
                  {{ stallInfo?.isOpen ? '营业中' : '休息中' }}
                </view>
              </view>
            </view>
            <button
              class="toggle-btn"
              :class="{ loading: isToggling }"
              :disabled="isToggling"
              @tap="toggleOpen"
            >
              {{ stallInfo?.isOpen ? '休息' : '开业' }}
            </button>
          </view>

          <!-- Today's Stats -->
          <view class="today-stats">
            <view class="stat-item">
              <text class="stat-value">{{ stallInfo?.todayOrders ?? 0 }}</text>
              <text class="stat-label">本日订单</text>
            </view>
            <view class="stat-divider" />
            <view class="stat-item">
              <text class="stat-value primary">¥{{ formatPrice(stallInfo?.todayRevenue ?? 0) }}</text>
              <text class="stat-label">本日收入</text>
            </view>
            <view class="stat-divider" />
            <view class="stat-item">
              <text class="stat-value warning">{{ stallInfo?.pendingOrders ?? 0 }}</text>
              <text class="stat-label">待处理</text>
            </view>
          </view>
        </view>

        <!-- Quick Actions -->
        <view class="quick-actions">
          <view class="action-card" @tap="goToOrderManage">
            <view class="action-icon orders-icon">
              <text class="action-emoji">📋</text>
            </view>
            <text class="action-label">处理订单</text>
          </view>
          <view class="action-card" @tap="goToProductManage">
            <view class="action-icon products-icon">
              <text class="action-emoji">📦</text>
            </view>
            <text class="action-label">商品管理</text>
          </view>
          <view class="action-card" @tap="goToStallSettings">
            <view class="action-icon settings-icon">
              <text class="action-emoji">⚙️</text>
            </view>
            <text class="action-label">摊位设置</text>
          </view>
        </view>

        <!-- Recent Orders -->
        <view class="recent-orders">
          <view class="section-header" @tap="goToOrderManage">
            <text class="section-title">最新订单</text>
            <view class="section-more">
              查看全部
              <text class="arrow">›</text>
            </view>
          </view>

          <!-- Empty State -->
          <view v-if="recentOrders.length === 0" class="orders-empty">
            <text class="empty-icon">📭</text>
            <text class="empty-text">暂无订单</text>
          </view>

          <!-- Order Cards -->
          <view
            v-for="order in recentOrders"
            :key="order.id"
            class="order-card"
            @tap="goToOrderManage"
          >
            <view class="order-header">
              <text class="order-no">{{ order.orderNo }}</text>
              <view class="order-status" :class="statusClass[order.status]">
                {{ statusLabel[order.status] }}
              </view>
            </view>
            <view class="order-items-preview">{{ getItemSummary(order.items) }}</view>
            <view class="order-footer">
              <text class="order-time">{{ formatTime(order.createdAt) }}</text>
              <text class="order-total">
                合计
                <text class="price-text">¥{{ formatPrice(order.totalAmount) }}</text>
              </text>
            </view>
          </view>
        </view>
      </block>
    </block>
  </view>
</template>

<style lang="scss">
@import './my-stall.scss';
</style>
