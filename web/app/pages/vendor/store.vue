<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

const api = useStallmartApi()
const { data: store, refresh } = await useAsyncData('vendor-store', () => api.vendorStore())
const form = reactive({
  name: '',
  description: '',
  logoUrl: '',
  coverUrl: '',
  status: 'OPEN',
})

watchEffect(() => {
  if (!store.value) return
  form.name = store.value.name
  form.description = store.value.description
  form.logoUrl = store.value.avatarUrl
  form.coverUrl = store.value.coverUrl || ''
  form.status = store.value.status
})

const save = async () => {
  await api.updateVendorStore({
    name: form.name,
    description: form.description,
    logoUrl: form.logoUrl,
    coverUrl: form.coverUrl,
    status: form.status,
  })
  await refresh()
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>商家店铺</h1>
        <p>管理店铺基础信息、状态和展示素材。</p>
      </div>
    </div>

    <section class="form-panel">
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label>店铺名称</label>
          <input v-model="form.name" required>
        </div>
        <div class="field">
          <label>状态</label>
          <select v-model="form.status">
            <option value="OPEN">OPEN</option>
            <option value="CLOSED">CLOSED</option>
            <option value="DISABLED">DISABLED</option>
          </select>
        </div>
        <div class="field">
          <label>Logo URL</label>
          <input v-model="form.logoUrl">
        </div>
        <div class="field">
          <label>封面 URL</label>
          <input v-model="form.coverUrl">
        </div>
        <div class="field full">
          <label>店铺描述</label>
          <textarea v-model="form.description" />
        </div>
        <div class="actions full">
          <button class="button primary" type="submit">保存店铺</button>
        </div>
      </form>
    </section>
  </AppShell>
</template>
