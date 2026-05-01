<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

const api = useStallmartApi()
const { data: workspace } = await useAsyncData('vendor-h5-summary', () => api.vendorSummary())
</script>

<template>
  <main class="phone-page">
    <div class="phone-shell">
      <header class="phone-header">
        <h1>{{ workspace?.store.name ?? '商家工作台' }}</h1>
        <p>手机端 H5 管理页面</p>
      </header>
      <section class="phone-body">
        <div class="grid cols-3">
          <div class="panel stat">
            <span>订单</span>
            <strong>{{ workspace?.orderCount ?? 0 }}</strong>
          </div>
          <div class="panel stat">
            <span>商品</span>
            <strong>{{ workspace?.products.length ?? 0 }}</strong>
          </div>
          <div class="panel stat">
            <span>销售额</span>
            <strong>¥{{ workspace?.salesAmount ?? 0 }}</strong>
          </div>
        </div>
        <nav class="mobile-nav">
          <NuxtLink to="/vendor/orders">订单</NuxtLink>
          <NuxtLink to="/vendor/products">商品</NuxtLink>
          <NuxtLink to="/vendor/store">店铺</NuxtLink>
          <NuxtLink to="/vendor/decoration">装修</NuxtLink>
          <NuxtLink to="/vendor/users">用户</NuxtLink>
          <NuxtLink to="/vendor/carts">购物车</NuxtLink>
        </nav>
        <article v-for="order in workspace?.orders" :key="order.id" class="card">
          <div class="actions" style="justify-content: space-between;">
            <strong>{{ order.confirmCode }}</strong>
            <span class="badge warn">{{ order.status }}</span>
          </div>
          <p>{{ order.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</p>
          <p class="muted">¥{{ order.totalAmount }} · {{ order.remark || '无备注' }}</p>
        </article>
      </section>
    </div>
  </main>
</template>
