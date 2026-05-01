<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

const api = useStallmartApi()
const { data: carts } = await useAsyncData('vendor-carts', () => api.vendorCarts())
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>购物车管理</h1>
        <p>查看用户在商家店铺内未结算的商品。</p>
      </div>
    </div>

    <div class="grid">
      <article v-for="cart in carts" :key="cart.id" class="card">
        <div class="actions" style="justify-content: space-between;">
          <strong>用户 #{{ cart.userId }}</strong>
          <span class="badge">{{ cart.status }}</span>
        </div>
        <p>{{ cart.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</p>
        <p class="muted">更新时间 {{ new Date(cart.updatedAt).toLocaleString() }}</p>
      </article>
      <div v-if="!carts?.length" class="empty">暂无购物车数据</div>
    </div>
  </AppShell>
</template>
