<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const { data: workspace } = await useAsyncData('vendor-h5-summary', () => api.vendorSummary())
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>{{ workspace?.store.name ?? '商家工作台' }}</h1>
        <p>商家管理移动端优先，同时适配 PC 端批量运营。</p>
      </div>
    </div>

    <section class="grid cols-3">
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
    </section>

    <section class="section grid grid-cols-2 sm:grid-cols-3 xl:grid-cols-6">
      <NuxtLink class="button" to="/vendor/orders">订单</NuxtLink>
      <NuxtLink class="button" to="/vendor/products">商品</NuxtLink>
      <NuxtLink class="button" to="/vendor/store">店铺</NuxtLink>
      <NuxtLink class="button" to="/vendor/decoration">装修</NuxtLink>
      <NuxtLink class="button" to="/vendor/users">用户</NuxtLink>
      <NuxtLink class="button" to="/vendor/carts">购物车</NuxtLink>
    </section>

    <section class="section">
      <h2>近期订单</h2>
      <div class="grid xl:grid-cols-2">
        <article v-for="order in workspace?.orders" :key="order.id" class="card">
          <div class="actions justify-between">
            <strong>{{ order.confirmCode }}</strong>
            <span class="badge warn">{{ order.status }}</span>
          </div>
          <p class="mt-3 text-sm text-ink-700">{{ order.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</p>
          <p class="muted mt-2">¥{{ order.totalAmount }} · {{ order.remark || '无备注' }}</p>
        </article>
      </div>
    </section>
  </AppShell>
</template>
