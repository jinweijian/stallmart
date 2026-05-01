<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useDidShow } from '@tarojs/taro'
import Taro from '@tarojs/taro'
import { useUserStore } from '@/store/user'
import { get, put } from '@/utils/request'

// ─── Types ─────────────────────────────────────────────────────────────────
interface StoreSettings {
  id: string
  name: string
  description: string
  logo: string
  status: 'OPEN' | 'CLOSED'
}

// ─── Store ──────────────────────────────────────────────────────────────────
const userStore = useUserStore()

// ─── State ─────────────────────────────────────────────────────────────────
const settings = ref<StoreSettings>({
  id: '',
  name: '',
  description: '',
  logo: '',
  status: 'OPEN',
})

const isLoading = ref(false)
const isSaving = ref(false)
const isUploadingLogo = ref(false)
const localLogoPath = ref('') // local temp path for preview

// ─── Computed ────────────────────────────────────────────────────────────────
const isVendor = computed(() => userStore.isVendor)
const isOpen = computed(() => settings.value.status === 'OPEN')
const hasChanges = ref(false)

// ─── Lifecycle ───────────────────────────────────────────────────────────────
useDidShow(() => {
  if (!isVendor.value) return
  loadSettings()
})

onMounted(() => {
  Taro.setNavigationBarTitle({ title: '摊位设置' })
})

// ─── Load Settings ──────────────────────────────────────────────────────────
async function loadSettings() {
  isLoading.value = true
  try {
    const storeId = Taro.getStorageSync('vendor_store_id') || '1'
    const res = await get<any>('/stores/' + storeId)
    const data = res.data
    settings.value = {
      id: data.id,
      name: data.name,
      description: data.description,
      logo: data.logo,
      status: data.isOpen !== false ? 'OPEN' : 'CLOSED',
    }
    localLogoPath.value = data.logo || ''
  } catch (e: any) {
    console.error('[StallSettings] loadSettings failed:', e)
    Taro.showToast({ title: e?.message || '加载失败', icon: 'none' })
  } finally {
    isLoading.value = false
    hasChanges.value = false
  }
}

// ─── Logo Upload ─────────────────────────────────────────────────────────────
async function chooseLogo() {
  try {
    const res = await Taro.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
    })

    const tempFilePath = res.tempFilePaths[0]
    localLogoPath.value = tempFilePath
    settings.value.logo = tempFilePath
    hasChanges.value = true

    // In production: upload to server and get URL
    // isUploadingLogo.value = true
    // const uploadRes = await uploadFile(tempFilePath)
    // settings.value.logo = uploadRes.url
    // isUploadingLogo.value = false
  } catch (e: any) {
    console.error('[StallSettings] chooseLogo failed:', e)
    if (e?.errMsg !== 'chooseImage:fail cancel') {
      Taro.showToast({ title: '选择图片失败', icon: 'none' })
    }
  }
}

// ─── Name Input ─────────────────────────────────────────────────────────────
function editName() {
  Taro.showModal({
    title: '修改摊位名称',
    editable: true,
    placeholderText: '请输入摊位名称',
    content: settings.value.name,
    success: (res) => {
      if (res.confirm && res.content) {
        if (res.content.trim() === settings.value.name) return
        settings.value.name = res.content.trim()
        hasChanges.value = true
      }
    },
  })
}

// ─── Description Input ───────────────────────────────────────────────────────
function editDescription() {
  Taro.showModal({
    title: '修改摊位简介',
    editable: true,
    placeholderText: '请输入摊位简介',
    content: settings.value.description,
    success: (res) => {
      if (res.confirm) {
        const content = res.content?.trim() ?? ''
        if (content === settings.value.description) return
        settings.value.description = content
        hasChanges.value = true
      }
    },
  })
}

// ─── Toggle Open/Closed ─────────────────────────────────────────────────────
function toggleOpen() {
  const newStatus = isOpen.value ? 'CLOSED' : 'OPEN'
  settings.value.status = newStatus
  hasChanges.value = true
}

// ─── Save Settings ──────────────────────────────────────────────────────────
async function saveSettings() {
  if (!settings.value.id) {
    Taro.showToast({ title: '摊位信息不完整', icon: 'none' })
    return
  }

  if (isSaving.value) return
  isSaving.value = true

  try {
    await put('/stores/' + settings.value.id, {
      name: settings.value.name,
      description: settings.value.description,
      logo: settings.value.logo,
      isOpen: settings.value.status === 'OPEN',
    })
    hasChanges.value = false
    Taro.showToast({ title: '保存成功', icon: 'success' })
  } catch (e: any) {
    Taro.showToast({ title: e?.message || '保存失败', icon: 'none' })
  } finally {
    isSaving.value = false
  }
}

// ─── Leave Stall (注销摊位) ───────────────────────────────────────────────────
function leaveStall() {
  Taro.showModal({
    title: '注销摊位',
    content: '确定要注销当前摊位吗？此操作不可恢复，所有摊位数据将被清除。',
    confirmText: '确定注销',
    confirmColor: '#FF4D4F',
    success: async (res) => {
      if (!res.confirm) return

      // Second confirmation
      const res2 = await Taro.showModal({
        title: '再次确认',
        content: '注销后无法恢复，确定继续吗？',
        confirmText: '确认注销',
        confirmColor: '#FF4D4F',
      })

      if (!res2.confirm) return

      try {
        await Taro.showLoading({ title: '注销中...' })
        // TODO: call API to leave stall
        // await post('/vendor/leave', {})
        Taro.hideLoading()

        // Clear vendor state
        userStore.setUserInfo(null)
        Taro.showToast({ title: '已注销摊位', icon: 'success' })
        setTimeout(() => {
          Taro.switchTab({ url: '/pages/customer/index/index' })
        }, 1500)
      } catch (e: any) {
        Taro.hideLoading()
        Taro.showToast({ title: e?.message || '注销失败', icon: 'none' })
      }
    },
  })
}

function goToCustomerHome() {
  Taro.switchTab({ url: '/pages/customer/index/index' })
}
</script>

<template>
  <view class="stall-settings-page">
    <!-- Not Vendor State -->
    <block v-if="!isVendor">
      <view class="not-vendor">
        <view class="not-vendor-icon">🏪</view>
        <view class="not-vendor-title">您还不是摊主</view>
        <view class="not-vendor-desc">成为摊主后即可管理您的摊位设置</view>
        <button class="btn-primary" @tap="goToCustomerHome">返回首页</button>
      </view>
    </block>

    <!-- Settings Content -->
    <block v-else>
      <!-- Loading -->
      <block v-if="isLoading">
        <view class="loading-state">
          <view class="loading-spinner" />
          <text class="loading-text">加载中...</text>
        </view>
      </block>

      <!-- Content -->
      <block v-else>
        <scroll-view class="settings-scroll" scroll-y>
          <!-- Logo Section -->
          <view class="section">
            <view class="section-title">摊位头像</view>
            <view class="logo-upload-card" @tap="chooseLogo">
              <view v-if="localLogoPath || settings.logo" class="logo-preview">
                <image
                  class="logo-image"
                  :src="localLogoPath || settings.logo"
                  mode="aspectFill"
                />
                <view class="logo-overlay">
                  <text class="logo-overlay-icon">📷</text>
                  <text class="logo-overlay-text">更换</text>
                </view>
              </view>
              <view v-else class="logo-placeholder">
                <text class="logo-placeholder-icon">🏪</text>
                <text class="logo-placeholder-text">点击上传头像</text>
              </view>
            </view>
          </view>

          <!-- Store Info Section -->
          <view class="section">
            <view class="section-title">基本信息</view>
            <view class="settings-list">
              <!-- Name -->
              <view class="setting-item" @tap="editName">
                <text class="setting-label">摊位名称</text>
                <view class="setting-right">
                  <text class="setting-value" :class="{ placeholder: !settings.name }">
                    {{ settings.name || '点击设置' }}
                  </text>
                  <text class="setting-arrow">›</text>
                </view>
              </view>

              <!-- Description -->
              <view class="setting-item" @tap="editDescription">
                <text class="setting-label">摊位简介</text>
                <view class="setting-right">
                  <text class="setting-value description" :class="{ placeholder: !settings.description }">
                    {{ settings.description || '点击设置' }}
                  </text>
                  <text class="setting-arrow">›</text>
                </view>
              </view>
            </view>
          </view>

          <!-- Open/Closed Toggle -->
          <view class="section">
            <view class="section-title">营业状态</view>
            <view class="settings-list">
              <view class="setting-item toggle-item">
                <view class="setting-left">
                  <view class="toggle-icon" :class="isOpen ? 'icon-open' : 'icon-closed'">
                    {{ isOpen ? '🟢' : '⚫' }}
                  </view>
                  <text class="setting-label">当前状态</text>
                </view>
                <view class="setting-right">
                  <view class="status-tag" :class="isOpen ? 'open' : 'closed'">
                    {{ isOpen ? '营业中' : '休息中' }}
                  </view>
                  <!-- Switch -->
                  <view
                    class="toggle-switch"
                    :class="{ active: isOpen }"
                    @tap.stop="toggleOpen"
                  />
                </view>
              </view>
            </view>
          </view>

          <!-- Leave Stall -->
          <view class="section">
            <view class="settings-list">
              <view class="setting-item danger-item" @tap="leaveStall">
                <text class="danger-text">注销摊位</text>
              </view>
            </view>
          </view>

          <!-- Spacer for button -->
          <view class="bottom-spacer" />
        </scroll-view>

        <!-- Save Button (sticky bottom) -->
        <view class="save-bar">
          <button
            class="btn-primary save-btn"
            :disabled="isSaving"
            @tap="saveSettings"
          >
            {{ isSaving ? '保存中...' : '保存设置' }}
          </button>
        </view>
      </block>
    </block>
  </view>
</template>

<style lang="scss">
@import './stall-settings.scss';
</style>
