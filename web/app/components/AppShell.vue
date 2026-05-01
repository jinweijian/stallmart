<template>
  <div class="layout">
    <aside class="sidebar">
      <NuxtLink class="brand" to="/">
        <strong>StallMart 管理台</strong>
        <span>平台与商家运营</span>
      </NuxtLink>
      <nav class="nav">
        <NuxtLink v-if="isPlatformAdmin" to="/">平台总览</NuxtLink>
        <NuxtLink v-if="isPlatformAdmin" to="/platform/vendors">商家列表</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor">商家工作台</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor/products">商品管理</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor/categories">分类管理</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor/specs">规格管理</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor/orders">订单管理</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor/users">用户管理</NuxtLink>
        <NuxtLink v-if="isVendorAdmin" to="/vendor/decoration">装修设置</NuxtLink>
      </nav>
      <div class="sidebar-user">
        <strong>{{ auth.session.value?.user.nickname }}</strong>
        <span>{{ auth.session.value?.user.role }}</span>
        <button class="button" type="button" @click="auth.signout()">退出登录</button>
      </div>
    </aside>
    <main class="content">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
const auth = useAdminAuth()
auth.restore()
const isPlatformAdmin = computed(() => auth.session.value?.user.role === 'ADMIN')
const isVendorAdmin = computed(() => auth.session.value?.user.role === 'VENDOR')
</script>
