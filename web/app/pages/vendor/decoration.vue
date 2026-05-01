<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

const api = useStallmartApi()
const { data: workspace, refresh } = await useAsyncData('vendor-decoration-workspace', () => api.vendorSummary())
const form = reactive({
  styleId: 1,
  logoUrl: '',
  coverUrl: '',
  description: '',
})

watchEffect(() => {
  if (!workspace.value) return
  form.styleId = workspace.value.decoration.styleId
  form.logoUrl = workspace.value.decoration.logoUrl
  form.coverUrl = workspace.value.decoration.coverUrl || ''
  form.description = workspace.value.store.description
})

const save = async () => {
  await api.updateDecoration({
    styleId: form.styleId,
    logoUrl: form.logoUrl,
    coverUrl: form.coverUrl,
    description: form.description,
  })
  await refresh()
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>装修设置</h1>
        <p>管理小程序店铺风格、Logo、封面和描述。</p>
      </div>
    </div>

    <section class="grid cols-3">
      <button
        v-for="style in workspace?.styles"
        :key="style.id"
        class="card"
        type="button"
        @click="form.styleId = style.id"
      >
        <span :class="['badge', form.styleId === style.id ? '' : 'gray']">{{ style.code }}</span>
        <h2>{{ style.name }}</h2>
        <p class="muted">应用后小程序店铺展示会同步切换。</p>
      </button>
    </section>

    <section class="section form-panel">
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label>Logo URL</label>
          <input v-model="form.logoUrl">
        </div>
        <div class="field">
          <label>封面 URL</label>
          <input v-model="form.coverUrl">
        </div>
        <div class="field full">
          <label>展示描述</label>
          <textarea v-model="form.description" />
        </div>
        <div class="actions full">
          <button class="button primary" type="submit">保存装修</button>
        </div>
      </form>
    </section>
  </AppShell>
</template>
