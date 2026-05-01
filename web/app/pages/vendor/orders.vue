<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

const api = useStallmartApi()
const { data: orders, refresh } = await useAsyncData('vendor-orders', () => api.vendorOrders())

const actionMap: Record<string, string[]> = {
  NEW: ['accept', 'reject'],
  ACCEPTED: ['prepare'],
  PREPARING: ['ready'],
  READY: ['complete'],
}

const actionLabel: Record<string, string> = {
  accept: '接单',
  reject: '拒单',
  prepare: '开始制作',
  ready: '待取餐',
  complete: '完成',
}

const transition = async (orderId: number, action: string) => {
  await api.transitionOrder(orderId, action)
  await refresh()
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>商家订单管理</h1>
        <p>处理接单、制作、待取餐和完成状态。</p>
      </div>
    </div>

    <div class="grid">
      <article v-for="order in orders" :key="order.id" class="card">
        <div class="actions" style="justify-content: space-between;">
          <div>
            <strong>{{ order.orderNo }}</strong>
            <div class="muted">取餐码 {{ order.confirmCode }}</div>
          </div>
          <span class="badge warn">{{ order.status }}</span>
        </div>
        <p>{{ order.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</p>
        <p class="muted">¥{{ order.totalAmount }} · {{ order.remark || '无备注' }}</p>
        <div class="actions">
          <button
            v-for="action in actionMap[order.status] || []"
            :key="action"
            class="button primary"
            type="button"
            @click="transition(order.id, action)"
          >
            {{ actionLabel[action] }}
          </button>
        </div>
      </article>
    </div>
  </AppShell>
</template>
