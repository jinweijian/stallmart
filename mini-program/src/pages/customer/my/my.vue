<script setup lang="ts">
import { ref, computed } from 'vue'
import { useDidShow } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useUserStore } from '@/store/user'
import { logout, loginWithWechat, bindPhone } from '@/utils/auth'
import { get } from '@/utils/request'
import { WECHAT_CONFIG } from '@/config'

// ==================== Store ====================
const userStore = useUserStore()

useDidShow(() => {
  userStore.loadUserInfo()
  loadStats()
})

// ==================== User Info ====================
const userInfo = computed(() => userStore.userInfo)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isVendor = computed(() => userStore.isVendor)
const hasPhone = computed(() => !!userInfo.value?.phone)

// ==================== Stats ====================
const stats = ref({
  pending: 0,    // 待接单
  inProgress: 0,  // 进行中
  completed: 0,  // 已完成
  total: 0,      // 全部
})

async function loadStats() {
  if (!isLoggedIn.value) return
  try {
    // Mock data — replace with real API call when backend is ready
    stats.value = {
      pending: 0,
      inProgress: 0,
      completed: 0,
      total: 0,
    }
    const res = await get('/orders/counts')
    if (res?.data) {
      stats.value = res.data
    }
  } catch {
    // keep defaults
  }
}

// ==================== Login / Logout ====================
async function handleLogin() {
  try {
    const { code } = await Taro.login()
    if (!code) {
      Taro.showToast({ title: '微信登录失败', icon: 'none' })
      return
    }
    await loginWithWechat(code)
    Taro.showToast({ title: '登录成功', icon: 'success' })
    await loadStats()
  } catch (e) {
    Taro.showToast({ title: '登录失败，请重试', icon: 'none' })
  }
}

async function handleLogout() {
  const res = await Taro.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    confirmText: '退出',
    confirmColor: '#FF6B35',
  })
  if (res.confirm) {
    await logout()
  }
}

// ==================== Phone Binding ====================
async function handleGetPhoneNumber(e: any) {
  const code = e.detail?.code
  if (!code) {
    Taro.showToast({ title: '获取手机号失败', icon: 'none' })
    return
  }
  try {
    await bindPhone(code)
    Taro.showToast({ title: '绑定成功', icon: 'success' })
    userStore.loadUserInfo()
  } catch {
    Taro.showToast({ title: '绑定失败，请重试', icon: 'none' })
  }
}

// ==================== Navigation ====================
function goToMyOrders() {
  Taro.switchTab({ url: '/pages/customer/my-orders/my-orders' })
}

function goToProfileEdit() {
  Taro.navigateTo({ url: '/pages/customer/profile-edit/profile-edit' })
}

function goToVendorEntry() {
  if (isVendor.value) {
    Taro.navigateTo({ url: '/pages/vendor/my-stall/my-stall' })
  } else {
    Taro.navigateTo({ url: '/pages/vendor/apply/apply' })
  }
}

// ==================== Menu Actions ====================
function showFAQ() {
  Taro.showModal({
    title: '常见问题',
    content: `Q: 如何成为摊主？
A: 点击"我是摊主"入口，填写摊位信息即可申请。

Q: 如何查看订单？
A: 在"我的订单"中可查看全部订单状态。

Q: 如何联系客服？
A: 点击"联系客服"即可拨打客服电话。

Q: 如何修改个人信息？
A: 点击头像旁边的"编辑资料"按钮。`,
    showCancel: false,
    confirmText: '我知道了',
    confirmColor: '#FF6B35',
  })
}

function showCustomerService() {
  Taro.showActionSheet({
    itemList: ['拨打客服电话 400-888-8888', '在线客服（暂未开放）'],
    itemColor: '#FF6B35',
    success: (res) => {
      if (res.tapIndex === 0) {
        Taro.makePhoneCall({
          phoneNumber: '400-888-8888',
          fail: () => {
            Taro.showToast({ title: '拨打失败', icon: 'none' })
          },
        })
      }
    },
  })
}

function showAboutUs() {
  Taro.showModal({
    title: '关于我们',
    content: `摊位商城 v${WECHAT_CONFIG.VERSION}\n\n摊位商城是一个连接摊主与顾客的本地生活服务平台，致力于为用户提供便捷的本地购物体验。\n\n© 2024 StallMart 版权所有`,
    showCancel: false,
    confirmText: '知道了',
    confirmColor: '#FF6B35',
  })
}

// ==================== Phone Mask Helper ====================
function maskPhone(phone: string): string {
  if (!phone || phone.length < 7) return phone || '未绑定'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}
</script>

<template>
  <view class="my-page bg-background min-h-screen">
    <!-- ==================== Profile Header ==================== -->
    <view class="profile-header">
      <!-- Background gradient blob -->
      <view class="header-bg" />

      <view class="header-content">
        <!-- Settings / Edit button -->
        <view class="header-top">
          <view class="spacer" />
          <view v-if="isLoggedIn" class="edit-btn" @tap="goToProfileEdit">
            <text class="edit-btn-text">编辑资料</text>
          </view>
        </view>

        <!-- Avatar + User Info -->
        <view class="user-row">
          <!-- Avatar -->
          <view class="avatar-wrapper">
            <image
              class="avatar-img"
              :src="userInfo?.avatar || '/static/default-avatar.png'"
              mode="aspectFill"
              @error="(e: any) => { e.target.src = '/static/default-avatar.png' }"
            />
            <view v-if="!isLoggedIn" class="avatar-overlay">
              <text class="avatar-overlay-icon">👤</text>
            </view>
          </view>

          <!-- Info block -->
          <view class="user-info-block">
            <template v-if="isLoggedIn">
              <text class="nickname">{{ userInfo?.nickname || '用户' }}</text>
              <text class="phone">{{ hasPhone ? maskPhone(userInfo!.phone) : '未绑定手机' }}</text>
            </template>
            <template v-else>
              <text class="nickname">Hi，欢迎来到摊位商城</text>
              <text class="phone phone-guest">点击下方按钮登录</text>
            </template>
          </view>
        </view>

        <!-- Phone binding prompt when logged in but no phone -->
        <view v-if="isLoggedIn && !hasPhone" class="phone-bind-prompt">
          <text class="phone-bind-tip">绑定手机号，享受完整服务</text>
          <button
            class="phone-bind-btn"
            open-type="getPhoneNumber"
            @getphonenumber="handleGetPhoneNumber"
          >
            立即绑定
          </button>
        </view>
      </view>
    </view>

    <!-- ==================== Quick Stats Row ==================== -->
    <view class="stats-row">
      <view class="stat-item" @tap="goToMyOrders">
        <text class="stat-num">{{ stats.pending }}</text>
        <text class="stat-label">待接单</text>
      </view>
      <view class="stat-divider" />
      <view class="stat-item" @tap="goToMyOrders">
        <text class="stat-num">{{ stats.inProgress }}</text>
        <text class="stat-label">进行中</text>
      </view>
      <view class="stat-divider" />
      <view class="stat-item" @tap="goToMyOrders">
        <text class="stat-num">{{ stats.completed }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-divider" />
      <view class="stat-item" @tap="goToMyOrders">
        <text class="stat-num">{{ stats.total }}</text>
        <text class="stat-label">全部</text>
      </view>
    </view>

    <!-- ==================== Vendor Entry ==================== -->
    <view v-if="isLoggedIn && isVendor" class="vendor-card" @tap="goToVendorEntry">
      <view class="vendor-icon-bg">
        <text class="vendor-icon-emoji">🏪</text>
      </view>
      <view class="vendor-info">
        <text class="vendor-title">我是摊主</text>
        <text class="vendor-desc">管理摊位、查看订单、设置营业状态</text>
      </view>
      <text class="vendor-arrow">›</text>
    </view>

    <!-- ==================== Menu List ==================== -->
    <view class="menu-section">
      <!-- 我的订单 -->
      <view class="menu-item" @tap="goToMyOrders">
        <view class="menu-icon-wrap menu-icon-order">
          <text class="menu-icon-text">📋</text>
        </view>
        <text class="menu-label">我的订单</text>
        <text class="menu-arrow">›</text>
      </view>

      <!-- 分割线 -->
      <view class="menu-divider" />

      <!-- 常见问题 -->
      <view class="menu-item" @tap="showFAQ">
        <view class="menu-icon-wrap menu-icon-faq">
          <text class="menu-icon-text">❓</text>
        </view>
        <text class="menu-label">常见问题</text>
        <text class="menu-arrow">›</text>
      </view>

      <!-- 分割线 -->
      <view class="menu-divider" />

      <!-- 联系客服 -->
      <view class="menu-item" @tap="showCustomerService">
        <view class="menu-icon-wrap menu-icon-service">
          <text class="menu-icon-text">📞</text>
        </view>
        <text class="menu-label">联系客服</text>
        <text class="menu-arrow">›</text>
      </view>

      <!-- 分割线 -->
      <view class="menu-divider" />

      <!-- 关于我们 -->
      <view class="menu-item" @tap="showAboutUs">
        <view class="menu-icon-wrap menu-icon-about">
          <text class="menu-icon-text">ℹ️</text>
        </view>
        <text class="menu-label">关于我们</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- ==================== Login / Logout Button ==================== -->
    <view class="action-area">
      <button v-if="!isLoggedIn" class="btn-login" @tap="handleLogin">
        立即登录
      </button>
      <button v-else class="btn-logout" @tap="handleLogout">
        退出登录
      </button>
    </view>

    <!-- ==================== Version Info ==================== -->
    <view class="version-info">
      <text>v{{ WECHAT_CONFIG.VERSION }}</text>
    </view>
  </view>
</template>

<style scoped>
@import './my.scss';
</style>
