<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'ADMIN',
})

const api = useStallmartApi()
const { data: styles, refresh } = await useAsyncData('platform-styles', () => api.platformStyles())
const busyStyleId = ref<number | null>(null)
const error = ref('')

const publish = async (styleId: number) => {
  busyStyleId.value = styleId
  error.value = ''
  try {
    await api.platformStylePublish(styleId)
    await refresh()
  } catch (err: any) {
    error.value = err?.data?.message || err?.message || '上架失败'
  } finally {
    busyStyleId.value = null
  }
}

const unpublish = async (styleId: number) => {
  busyStyleId.value = styleId
  error.value = ''
  try {
    await api.platformStyleUnpublish(styleId)
    await refresh()
  } catch (err: any) {
    error.value = err?.data?.message || err?.message || '下架失败'
  } finally {
    busyStyleId.value = null
  }
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>风格包管理</h1>
        <p>平台统一维护小程序可用风格包，商家只能选择已上架风格包。</p>
      </div>
    </div>

    <p v-if="error" class="form-error mb-4">{{ error }}</p>

    <div class="grid cols-3">
      <article v-for="style in styles" :key="style.id" class="card">
        <div class="actions justify-between">
          <span :class="['badge', style.status === 'INACTIVE' ? 'gray' : '']">{{ style.status || 'ACTIVE' }}</span>
          <span class="muted">v{{ style.version || 1 }}</span>
        </div>
        <h2>{{ style.name }}</h2>
        <p class="muted">{{ style.code }} · {{ style.theme.layoutVersion }}</p>
        <div class="mt-3 flex gap-2">
          <span
            v-for="(color, key) in style.theme.colors"
            :key="key"
            class="h-7 w-7 rounded-full border border-ink-200"
            :style="{ backgroundColor: color }"
          />
        </div>
        <div class="actions mt-4">
          <button
            v-if="style.status === 'INACTIVE'"
            class="button primary"
            type="button"
            :disabled="busyStyleId === style.id"
            @click="publish(style.id)"
          >
            上架
          </button>
          <button
            v-else
            class="button danger"
            type="button"
            :disabled="busyStyleId === style.id"
            @click="unpublish(style.id)"
          >
            下架
          </button>
        </div>
      </article>
    </div>
  </AppShell>
</template>
