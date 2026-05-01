<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'ADMIN',
})

const route = useRoute()
const storeId = Number(route.params.id)
const api = useStallmartApi()
const { data: workspace } = await useAsyncData(`platform-vendor-${storeId}`, () => api.platformVendor(storeId))
</script>

<template>
  <AppShell>
    <div v-if="workspace" class="page-head">
      <div>
        <h1>{{ workspace.store.name }}</h1>
        <p>平台视角的商家模块：用户、装修、订单、购物车和商品。</p>
      </div>
      <NuxtLink class="button" to="/platform/vendors">返回列表</NuxtLink>
    </div>

    <section v-if="workspace" class="grid cols-4">
      <div class="panel stat">
        <span>商品</span>
        <strong>{{ workspace.products.length }}</strong>
      </div>
      <div class="panel stat">
        <span>订单</span>
        <strong>{{ workspace.orderCount }}</strong>
      </div>
      <div class="panel stat">
        <span>购物车</span>
        <strong>{{ workspace.cartCount }}</strong>
      </div>
      <div class="panel stat">
        <span>销售额</span>
        <strong>¥{{ workspace.salesAmount }}</strong>
      </div>
    </section>

    <section v-if="workspace" class="section grid cols-3">
      <div class="panel">
        <h2>装修设置</h2>
        <p>{{ workspace.decoration.style.name }} · {{ workspace.decoration.styleCode }}</p>
        <p class="muted">{{ workspace.store.description }}</p>
      </div>
      <div class="panel">
        <h2>用户管理</h2>
        <p>{{ workspace.users.length }} 位关联用户</p>
        <p class="muted">包含下单用户和仍有购物车的用户。</p>
      </div>
      <div class="panel">
        <h2>商家订单</h2>
        <p>{{ workspace.orders.length }} 笔订单</p>
        <p class="muted">商家端可继续处理接单、备餐和完成。</p>
      </div>
    </section>

    <section v-if="workspace" class="section">
      <h2>最近订单</h2>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>用户</th>
              <th>状态</th>
              <th>金额</th>
              <th>商品</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in workspace.orders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>#{{ order.userId }}</td>
              <td><span class="badge warn">{{ order.status }}</span></td>
              <td>¥{{ order.totalAmount }}</td>
              <td>{{ order.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
