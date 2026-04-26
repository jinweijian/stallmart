<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@tarojs/taro'
import { useUserStore } from '@/store/user'
import { checkAndRefreshToken } from '@/utils/auth'

const userStore = useUserStore()

onLaunch(() => {
  console.log('[App] onLaunch')
  // Token will be validated via checkAndRefreshToken in app.vue setup
})

onShow(() => {
  console.log('[App] onShow')
  checkAndRefreshToken()
})

onHide(() => {
  console.log('[App] onHide')
})
</script>

<template>
  <view class="app-container"></view>
</template>

<style>
@import './app.scss';
</style>
