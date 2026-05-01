<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { Order, UserProfile } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const selectedUser = ref<UserProfile | null>(null)
const selectedOrders = ref<Order[]>([])
const loadingOrders = ref(false)
const { data: users } = await useAsyncData('vendor-users', () => api.vendorUsers())

const statusLabel: Record<string, string> = {
  NEW: '待接单',
  ACCEPTED: '已接单',
  PREPARING: '制作中',
  READY: '待取餐',
  COMPLETED: '已完成',
  REJECTED: '已拒单',
}

const loadUserOrders = async (user: UserProfile) => {
  selectedUser.value = user
  loadingOrders.value = true
  try {
    selectedOrders.value = await api.vendorUserOrders(user.id)
  } finally {
    loadingOrders.value = false
  }
}

const openOrder = (orderId: number) => navigateTo(`/vendor/orders/${orderId}`)
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>用户管理</h1>
        <p>查看与当前商家产生订单或购物车关系的用户，并追踪顾客订单记录。</p>
      </div>
    </div>

    <div class="table-wrap">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户</th>
            <th>手机号</th>
            <th>授权</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>
              <strong>{{ user.nickname }}</strong>
              <div class="muted">#{{ user.id }}</div>
            </td>
            <td>{{ user.phone || '-' }}</td>
            <td><span :class="['badge', user.hasPhone ? '' : 'gray']">{{ user.hasPhone ? '已授权' : '未授权' }}</span></td>
            <td>{{ user.role }}</td>
            <td>
              <button class="button" type="button" @click="loadUserOrders(user)">查看订单</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <section v-if="selectedUser" class="section panel">
      <div class="page-head mb-4">
        <div>
          <h2>{{ selectedUser.nickname }} 的订单记录</h2>
          <p>仅展示当前店铺下该顾客的订单。</p>
        </div>
        <span class="badge">{{ selectedOrders.length }} 笔订单</span>
      </div>

      <div v-if="loadingOrders" class="empty">订单加载中</div>
      <div v-else-if="selectedOrders.length === 0" class="empty">暂无订单记录</div>
      <div v-else class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>取餐码</th>
              <th>商品</th>
              <th>金额</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in selectedOrders" :key="order.id">
              <td>
                <strong>{{ order.orderNo }}</strong>
                <div class="muted">{{ new Date(order.createdAt).toLocaleString() }}</div>
              </td>
              <td>{{ order.confirmCode }}</td>
              <td>{{ order.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</td>
              <td>¥{{ order.totalAmount }}</td>
              <td><span class="badge">{{ statusLabel[order.status] || order.status }}</span></td>
              <td><button class="button" type="button" @click="openOrder(order.id)">详情</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
