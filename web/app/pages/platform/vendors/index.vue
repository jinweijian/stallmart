<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'ADMIN',
})

const api = useStallmartApi()
const { data: vendors } = await useAsyncData('platform-vendors', () => api.platformVendors())
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>商家列表</h1>
        <p>平台管理从这里进入每个商家的独立运营模块。</p>
      </div>
    </div>

    <div class="grid cols-3">
      <article v-for="store in vendors" :key="store.id" class="card">
        <div class="actions justify-between">
          <span class="badge">{{ store.status }}</span>
          <span class="muted">#{{ store.id }}</span>
        </div>
        <h2>{{ store.name }}</h2>
        <p class="muted">{{ store.description }}</p>
        <p>{{ store.category }} · {{ store.address }}</p>
        <NuxtLink class="button primary" :to="`/platform/vendors/${store.id}`">进入商家模块</NuxtLink>
      </article>
    </div>
  </AppShell>
</template>
