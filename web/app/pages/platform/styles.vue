<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { StorefrontTheme, Style, StyleInput } from '~/types/admin'

definePageMeta({
  role: 'ADMIN',
})

const api = useStallmartApi()
const { data: styles, refresh } = await useAsyncData('platform-styles', () => api.platformStyles())
const busyStyleId = ref<number | null>(null)
const saving = ref(false)
const error = ref('')
const notice = ref('')
const editingStyleId = ref<number | null>(null)

const form = reactive({
  name: '',
  code: '',
  previewUrl: '',
  status: 'INACTIVE',
  primary: '#6F9646',
})

const fallbackTheme = (): StorefrontTheme => ({
  code: form.code || 'newStyle',
  name: form.name || '新风格包',
  layoutVersion: 'customer-storefront-v1',
  colors: {
    primary: form.primary,
    secondary: '#B8C77A',
    accent: '#F2B94B',
    background: '#FBFAEF',
    surface: '#FFFDF4',
    text: '#4C6040',
    mutedText: '#7A866D',
    border: '#DCE6C7',
    price: form.primary,
  },
  iconNames: {
    location: 'location',
    cart: 'cart',
    checkout: 'checkout',
    delivery: 'delivery',
    sectionLeaf: 'section-leaf',
  },
  iconUrls: {},
  imageUrls: {},
  copywriting: {
    heroEyebrow: '今日推荐',
    heroTitle: form.name || '新风格包',
    heroSubtitle: '新鲜现制 · 即点即取',
    promoTitle: '本店上新',
    promoSubtitle: '精选好物限时供应',
    promoActionText: '去看看',
  },
  categoryIconLibrary: [
    {
      key: 'recommend',
      name: '人气推荐',
      iconUrl: null,
      fallbackText: '荐',
    },
  ],
  assetSizes: {
    tabBarReserve: '132rpx',
  },
  pageThemes: {
    home: {
      sectionTitle: '人气推荐',
    },
  },
})

const cloneTheme = (theme?: StorefrontTheme): StorefrontTheme => JSON.parse(JSON.stringify(theme || fallbackTheme()))

const resetForm = () => {
  editingStyleId.value = null
  form.name = ''
  form.code = ''
  form.previewUrl = ''
  form.status = 'INACTIVE'
  form.primary = '#6F9646'
  error.value = ''
  notice.value = ''
}

const editStyle = (style: Style) => {
  editingStyleId.value = style.id
  form.name = style.name
  form.code = style.code
  form.previewUrl = style.previewUrl || ''
  form.status = style.status || 'ACTIVE'
  form.primary = style.theme.colors.primary || '#6F9646'
  error.value = ''
  notice.value = ''
}

const buildPayload = (): StyleInput => {
  const source = editingStyleId.value
    ? styles.value?.find(style => style.id === editingStyleId.value)?.theme
    : styles.value?.find(style => style.code === 'forestFruitTeaCrayon')?.theme || styles.value?.[0]?.theme
  const theme = cloneTheme(source)
  theme.code = form.code.trim()
  theme.name = form.name.trim()
  theme.colors = {
    ...(theme.colors || {}),
    primary: form.primary,
    price: form.primary,
  }
  theme.copywriting = {
    ...(theme.copywriting || {}),
    heroTitle: form.name.trim(),
  }
  return {
    name: form.name.trim(),
    code: form.code.trim(),
    previewUrl: form.previewUrl.trim() || null,
    status: form.status,
    theme,
  }
}

const saveStyle = async () => {
  saving.value = true
  error.value = ''
  notice.value = ''
  try {
    const payload = buildPayload()
    if (editingStyleId.value) {
      await api.updatePlatformStyle(editingStyleId.value, payload)
      notice.value = '风格包已更新'
    } else {
      await api.createPlatformStyle(payload)
      notice.value = '风格包已创建'
    }
    await refresh()
    resetForm()
  } catch (err: any) {
    error.value = err?.data?.message || err?.message || '保存失败'
  } finally {
    saving.value = false
  }
}

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

const deleteStyle = async (styleId: number) => {
  if (!window.confirm('确认删除这个风格包？已被店铺使用的风格包会被服务端拒绝删除。')) {
    return
  }
  busyStyleId.value = styleId
  error.value = ''
  notice.value = ''
  try {
    await api.deletePlatformStyle(styleId)
    notice.value = '风格包已删除'
    if (editingStyleId.value === styleId) {
      resetForm()
    }
    await refresh()
  } catch (err: any) {
    error.value = err?.data?.message || err?.message || '删除失败'
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
    <p v-if="notice" class="form-success mb-4">{{ notice }}</p>

    <form class="panel mb-6" @submit.prevent="saveStyle">
      <div class="section-head">
        <div>
          <h2>{{ editingStyleId ? '编辑风格包' : '新增风格包' }}</h2>
          <p>第一版支持基础信息、状态和主色编辑，完整主题结构会沿用当前森系风格契约。</p>
        </div>
        <button v-if="editingStyleId" class="button ghost" type="button" @click="resetForm">取消编辑</button>
      </div>

      <div class="form-grid">
        <label>
          <span>名称</span>
          <input v-model="form.name" class="input" required placeholder="例如：森系水果茶-小白款" />
        </label>
        <label>
          <span>编码</span>
          <input v-model="form.code" class="input" required placeholder="例如：forestFruitTeaCrayon" />
        </label>
        <label>
          <span>预览图</span>
          <input v-model="form.previewUrl" class="input" placeholder="/static/storefront/forest/preview.png" />
        </label>
        <label>
          <span>状态</span>
          <select v-model="form.status" class="input">
            <option value="INACTIVE">下架</option>
            <option value="ACTIVE">上架</option>
          </select>
        </label>
        <label>
          <span>主色</span>
          <input v-model="form.primary" class="input" type="color" />
        </label>
      </div>

      <div class="actions mt-4">
        <button class="button primary" type="submit" :disabled="saving">
          {{ editingStyleId ? '保存修改' : '添加风格包' }}
        </button>
      </div>
    </form>

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
          <button class="button ghost" type="button" @click="editStyle(style)">编辑</button>
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
          <button
            class="button danger"
            type="button"
            :disabled="busyStyleId === style.id"
            @click="deleteStyle(style.id)"
          >
            删除
          </button>
        </div>
      </article>
    </div>
  </AppShell>
</template>
