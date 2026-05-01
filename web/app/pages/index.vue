<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'ADMIN',
})

const api = useStallmartApi()
const { data: summary } = await useAsyncData('platform-summary', () => api.platformSummary())
const { data: vendors } = await useAsyncData('platform-vendors-home', () => api.platformVendors())
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>平台总览</h1>
        <p>统一查看商家、用户、订单、购物车和销售数据。</p>
      </div>
      <NuxtLink class="button primary" to="/platform/vendors">进入商家列表</NuxtLink>
    </div>

    <section class="grid cols-4">
      <div class="panel stat">
        <span>商家</span>
        <strong>{{ summary?.storeCount ?? 0 }}</strong>
      </div>
      <div class="panel stat">
        <span>用户</span>
        <strong>{{ summary?.userCount ?? 0 }}</strong>
      </div>
      <div class="panel stat">
        <span>订单</span>
        <strong>{{ summary?.orderCount ?? 0 }}</strong>
      </div>
      <div class="panel stat">
        <span>销售额</span>
        <strong>¥{{ summary?.salesAmount ?? 0 }}</strong>
      </div>
    </section>

    <section class="section">
      <h2>商家运营入口</h2>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>店铺</th>
              <th>分类</th>
              <th>风格</th>
              <th>状态</th>
              <th>地址</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="store in vendors" :key="store.id">
              <td>
                <strong>{{ store.name }}</strong>
                <div class="muted">{{ store.description }}</div>
              </td>
              <td>{{ store.category }}</td>
              <td>{{ store.styleCode }}</td>
              <td><span class="badge">{{ store.status }}</span></td>
              <td>{{ store.address }}</td>
              <td>
                <NuxtLink class="button" :to="`/platform/vendors/${store.id}`">查看模块</NuxtLink>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
