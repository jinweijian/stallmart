<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { Order } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const toast = ref('')
const toastType = ref<'success' | 'error'>('success')
const pendingTransition = ref<{ order: Order, action: string } | null>(null)
const { data: orders, refresh } = await useAsyncData('vendor-orders', () => api.vendorOrders())

const statusFlow = ['NEW', 'ACCEPTED', 'PREPARING', 'READY', 'COMPLETED']
const statusMeta: Record<string, { label: string, hint: string, className: string }> = {
  NEW: { label: '待接单', hint: '新订单，需要尽快接单或拒单', className: 'bg-warn-50 text-warn-700 border-warn-200' },
  ACCEPTED: { label: '已接单', hint: '已接单，下一步开始制作', className: 'bg-brand-50 text-brand-700 border-brand-100' },
  PREPARING: { label: '制作中', hint: '正在备餐，完成后标记待取餐', className: 'bg-amber-50 text-amber-700 border-amber-200' },
  READY: { label: '待取餐', hint: '提醒顾客凭取餐码取餐', className: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
  COMPLETED: { label: '已完成', hint: '订单已完成', className: 'bg-ink-100 text-ink-600 border-ink-200' },
  REJECTED: { label: '已拒单', hint: '订单已关闭', className: 'bg-danger-50 text-danger-700 border-danger-200' },
}

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

const nextStatusLabel: Record<string, string> = {
  accept: '已接单',
  reject: '已拒单',
  prepare: '制作中',
  ready: '待取餐',
  complete: '已完成',
}

const metaOf = (status: string) => statusMeta[status] || { label: status, hint: '-', className: 'bg-ink-100 text-ink-600 border-ink-200' }
const progressOf = (status: string) => {
  if (status === 'REJECTED') return 100
  const index = statusFlow.indexOf(status)
  return index < 0 ? 0 : Math.round(((index + 1) / statusFlow.length) * 100)
}
const openOrder = (orderId: number) => navigateTo(`/vendor/orders/${orderId}`)

const askTransition = (order: Order, action: string) => {
  pendingTransition.value = { order, action }
}

const showToast = (message: string, type: 'success' | 'error' = 'success') => {
  toast.value = message
  toastType.value = type
  window.setTimeout(() => {
    toast.value = ''
  }, 2400)
}

const confirmTransition = async () => {
  if (!pendingTransition.value) return
  const { order, action } = pendingTransition.value
  pendingTransition.value = null
  try {
    await api.transitionOrder(order.id, action)
    showToast(`订单 ${order.orderNo} 已更新为「${nextStatusLabel[action]}」`)
    await refresh()
  } catch (error: any) {
    showToast(error?.data?.message || error?.message || '订单状态更新失败', 'error')
  }
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

    <AppToast :message="toast" :show="Boolean(toast)" :type="toastType" @close="toast = ''" />
    <AppConfirmModal
      :message="pendingTransition ? `确认将订单 ${pendingTransition.order.orderNo} 更新为「${nextStatusLabel[pendingTransition.action]}」？` : ''"
      :open="Boolean(pendingTransition)"
      confirm-text="确认更新"
      title="确认订单状态"
      @cancel="pendingTransition = null"
      @confirm="confirmTransition"
    />

    <div class="grid">
      <article
        v-for="order in orders"
        :key="order.id"
        class="card relative overflow-hidden"
      >
        <div :class="['absolute right-0 top-0 rounded-bl-lg px-3 py-1 text-xs font-semibold', metaOf(order.status).className]">
          {{ metaOf(order.status).label }}
        </div>

        <div class="flex flex-col gap-3 pr-24 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <strong>{{ order.orderNo }}</strong>
            <div class="muted">取餐码 {{ order.confirmCode }} · 用户 #{{ order.userId }}</div>
          </div>
          <div class="text-left sm:text-right">
            <div class="text-lg font-semibold text-ink-900">¥{{ order.totalAmount }}</div>
            <div class="muted">{{ metaOf(order.status).hint }}</div>
          </div>
        </div>

        <div class="mt-4 h-2 overflow-hidden rounded-full bg-ink-100">
          <div
            :class="['h-full rounded-full', order.status === 'REJECTED' ? 'bg-danger-600' : 'bg-brand-600']"
            :style="{ width: `${progressOf(order.status)}%` }"
          />
        </div>

        <p>{{ order.items.map(item => `${item.productName} x${item.quantity}`).join('，') }}</p>
        <p class="muted">{{ order.remark || '无备注' }}</p>
        <div class="actions">
          <button class="button" type="button" @click="openOrder(order.id)">详情</button>
          <button
            v-for="action in actionMap[order.status] || []"
            :key="action"
            class="button primary"
            type="button"
            @click="askTransition(order, action)"
          >
            {{ actionLabel[action] }}
          </button>
        </div>
      </article>
    </div>
  </AppShell>
</template>
