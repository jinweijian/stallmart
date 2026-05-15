<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const { data: logs } = await useAsyncData('vendor-operation-logs', () => api.vendorOperationLogs())

const formatTime = (value: string) => new Date(value).toLocaleString()
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>操作日志</h1>
        <p>记录店铺资料、商品、订单、装修和规格等后台操作。</p>
      </div>
    </div>

    <section class="table-wrap">
      <table class="data-table">
        <thead>
          <tr>
            <th>时间</th>
            <th>账号</th>
            <th>操作</th>
            <th>资源</th>
            <th>结果</th>
            <th>来源</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in logs ?? []" :key="log.id">
            <td>{{ formatTime(log.createdAt) }}</td>
            <td>{{ log.actorAccount || '-' }}</td>
            <td>
              <strong>{{ log.action }}</strong>
              <p class="muted mt-1">{{ log.description }}</p>
            </td>
            <td>{{ log.resourceType }} <span v-if="log.resourceId">#{{ log.resourceId }}</span></td>
            <td><span class="badge">{{ log.result }}</span></td>
            <td>{{ log.ipAddress || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </AppShell>
</template>
