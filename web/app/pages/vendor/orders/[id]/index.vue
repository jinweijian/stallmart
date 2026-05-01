<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'VENDOR',
})

const route = useRoute()
const orderId = Number(route.params.id)
const api = useStallmartApi()
const toast = ref('')
const toastType = ref<'success' | 'error'>('success')
const pendingAction = ref<string | null>(null)
const { data: order, refresh } = await useAsyncData(`vendor-order-${orderId}`, () => api.vendorOrder(orderId))

const statusFlow = ['NEW', 'ACCEPTED', 'PREPARING', 'READY', 'COMPLETED']
const statusMeta: Record<string, { label: string, hint: string, className: string }> = {
  NEW: { label: '待接单', hint: '请尽快接单或拒单', className: 'bg-warn-50 text-warn-700 border-warn-200' },
  ACCEPTED: { label: '已接单', hint: '下一步开始制作', className: 'bg-brand-50 text-brand-700 border-brand-100' },
  PREPARING: { label: '制作中', hint: '完成制作后标记待取餐', className: 'bg-amber-50 text-amber-700 border-amber-200' },
  READY: { label: '待取餐', hint: '顾客凭取餐码取餐', className: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
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

const showToast = (message: string, type: 'success' | 'error' = 'success') => {
  toast.value = message
  toastType.value = type
  window.setTimeout(() => {
    toast.value = ''
  }, 2400)
}

const confirmTransition = async () => {
  if (!pendingAction.value) return
  const action = pendingAction.value
  pendingAction.value = null
  try {
    await api.transitionOrder(orderId, action)
    showToast(`订单已更新为「${nextStatusLabel[action]}」`)
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
        <h1>{{ order?.orderNo ?? '订单详情' }}</h1>
        <p>查看订单商品、备注、取餐码，并执行备餐相关操作。</p>
      </div>
      <NuxtLink class="button" to="/vendor/orders">返回订单列表</NuxtLink>
    </div>

    <AppToast :message="toast" :show="Boolean(toast)" :type="toastType" @close="toast = ''" />
    <AppConfirmModal
      :message="order && pendingAction ? `确认将订单 ${order.orderNo} 更新为「${nextStatusLabel[pendingAction]}」？` : ''"
      :open="Boolean(pendingAction)"
      confirm-text="确认更新"
      title="确认订单状态"
      @cancel="pendingAction = null"
      @confirm="confirmTransition"
    />

    <section v-if="order" class="panel relative overflow-hidden">
      <div :class="['absolute right-0 top-0 rounded-bl-lg px-4 py-2 text-sm font-semibold', metaOf(order.status).className]">
        {{ metaOf(order.status).label }}
      </div>
      <div class="grid gap-4 pr-24 lg:grid-cols-3">
        <div class="stat">
          <span>当前状态</span>
          <strong>{{ metaOf(order.status).label }}</strong>
          <p class="muted">{{ metaOf(order.status).hint }}</p>
        </div>
        <div class="stat">
          <span>取餐码</span>
          <strong>{{ order.confirmCode }}</strong>
          <p class="muted">用户 #{{ order.userId }}</p>
        </div>
        <div class="stat">
          <span>金额</span>
          <strong>¥{{ order.totalAmount }}</strong>
          <p class="muted">{{ order.remark || '无备注' }}</p>
        </div>
      </div>
      <div class="mt-4 h-2 overflow-hidden rounded-full bg-ink-100">
        <div
          :class="['h-full rounded-full', order.status === 'REJECTED' ? 'bg-danger-600' : 'bg-brand-600']"
          :style="{ width: `${progressOf(order.status)}%` }"
        />
      </div>
    </section>

    <section v-if="order" class="section">
      <h2>商品明细</h2>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>商品</th>
              <th>规格</th>
              <th>数量</th>
              <th>单价</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in order.items" :key="item.productId">
              <td>{{ item.productName }}</td>
              <td>{{ item.specsText || '-' }}</td>
              <td>{{ item.quantity }}</td>
              <td>¥{{ item.unitPrice }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section v-if="order" class="section panel">
      <h2>备餐操作</h2>
      <div class="actions">
        <button
          v-for="action in actionMap[order.status] || []"
          :key="action"
          class="button primary"
          type="button"
          @click="pendingAction = action"
        >
          {{ actionLabel[action] }}
        </button>
        <span v-if="!(actionMap[order.status] || []).length" class="muted">当前状态暂无可执行操作</span>
      </div>
    </section>
  </AppShell>
</template>
