<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'ADMIN',
})

const route = useRoute()
const storeId = Number(route.params.id)
const api = useStallmartApi()
const [{ data: workspace }, { data: logs }] = await Promise.all([
  useAsyncData(`platform-vendor-log-store-${storeId}`, () => api.platformVendor(storeId)),
  useAsyncData(`platform-vendor-operation-logs-${storeId}`, () => api.platformVendorOperationLogs(storeId)),
])

const formatTime = (value: string) => new Date(value).toLocaleString()
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>{{ workspace?.store.name ?? '商户操作日志' }}</h1>
        <p>平台视角查看该商户后台的关键操作记录。</p>
      </div>
      <NuxtLink class="button" :to="`/platform/vendors/${storeId}`">返回商户</NuxtLink>
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
