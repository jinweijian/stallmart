<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { Style } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

type UploadTarget =
  | { kind: 'logo' }
  | { kind: 'cover' }
  | { kind: 'banner', index: number }
  | { kind: 'icon', key: string }
  | { kind: 'image', key: string }

const api = useStallmartApi()
const { data: workspace, refresh } = await useAsyncData('vendor-decoration-workspace', () => api.vendorSummary())
const fileInput = ref<HTMLInputElement | null>(null)
const uploadTarget = ref<UploadTarget | null>(null)
const uploadBusy = ref(false)
const saving = ref(false)
const notice = ref('')
const formError = ref('')
const form = reactive({
  styleId: 1,
  logoUrl: '',
  coverUrl: '',
  description: '',
  banners: [] as string[],
  colors: {} as Record<string, string>,
  copywriting: {} as Record<string, string>,
  iconUrls: {} as Record<string, string>,
  imageUrls: {} as Record<string, string>,
})

const colorLabels: Record<string, string> = {
  primary: '主色调',
  secondary: '副色调',
  accent: '强调色',
  background: '页面背景',
  surface: '卡片底色',
  text: '正文颜色',
  mutedText: '辅助文字',
  border: '边框颜色',
  price: '价格颜色',
}
const copyLabels: Record<string, string> = {
  branchName: '门店名称',
  heroEyebrow: '头图前缀',
  heroTitle: '头图标题',
  heroSubtitle: '头图副标题',
  promoTitle: '活动标题',
  promoSubtitle: '活动副标题',
  promoActionText: '活动按钮文案',
}
const imageLabels: Record<string, string> = {
  heroIllustration: '首页头图',
  mascot: '吉祥物',
  productPlaceholder: '商品占位图',
  promoIllustration: '活动插画',
}
const iconLabels: Record<string, string> = {
  location: '位置图标',
  cart: '购物车图标',
  checkout: '结算图标',
  delivery: '配送图标',
  sectionLeaf: '分区装饰图标',
}

const colorKeys = computed(() => Object.keys(form.colors))
const copyEntries = computed(() => Object.entries(form.copywriting))
const iconEntries = computed(() => Object.entries(form.iconUrls))
const imageEntries = computed(() => Object.entries(form.imageUrls))
const selectedStyle = computed(() => workspace.value?.styles.find(style => style.id === form.styleId))
const previewBanner = computed(() => form.banners.find(Boolean) || form.coverUrl || form.imageUrls.heroIllustration || '')
const previewHeroImage = computed(() => form.imageUrls.heroIllustration || form.coverUrl || previewBanner.value)
const previewPromoImage = computed(() => form.imageUrls.promoIllustration || form.imageUrls.mascot || form.coverUrl || '')
const previewCategories = computed(() => workspace.value?.decoration.categories.slice(0, 6) || [])

const resetRecord = (target: Record<string, string>, source?: Record<string, string>) => {
  Object.keys(target).forEach(key => delete target[key])
  Object.assign(target, source || {})
}

watchEffect(() => {
  if (!workspace.value) return
  const decoration = workspace.value.decoration
  form.styleId = decoration.styleId
  form.logoUrl = decoration.logoUrl
  form.coverUrl = decoration.coverUrl || ''
  form.description = workspace.value.store.description
  form.banners = [...decoration.banners]
  resetRecord(form.colors, decoration.colors)
  resetRecord(form.copywriting, decoration.copywriting)
  resetRecord(form.iconUrls, decoration.iconUrls)
  resetRecord(form.imageUrls, decoration.imageUrls)
})

const selectStyle = (style: Style) => {
  form.styleId = style.id
  resetRecord(form.colors, style.theme.colors)
  resetRecord(form.copywriting, style.theme.copywriting)
  resetRecord(form.iconUrls, style.theme.iconUrls)
  resetRecord(form.imageUrls, style.theme.imageUrls)
  notice.value = `已切换到「${style.name}」，保存后小程序生效`
}

const beginUpload = (target: UploadTarget) => {
  uploadTarget.value = target
  fileInput.value?.click()
}

const applyUploadedUrl = (target: UploadTarget, url: string) => {
  if (target.kind === 'logo') {
    form.logoUrl = url
    return
  }
  if (target.kind === 'cover') {
    form.coverUrl = url
    return
  }
  if (target.kind === 'banner') {
    form.banners[target.index] = url
    return
  }
  if (target.kind === 'icon') {
    form.iconUrls[target.key] = url
    return
  }
  form.imageUrls[target.key] = url
}

const uploadDecorationImage = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  const target = uploadTarget.value
  if (!file || !target) return
  uploadBusy.value = true
  formError.value = ''
  try {
    const asset = await api.uploadDecorationImage(file)
    applyUploadedUrl(target, asset.url)
    notice.value = '图片已上传，保存装修后小程序生效'
  } catch (error: any) {
    formError.value = error?.data?.message || error?.message || '上传失败'
  } finally {
    uploadBusy.value = false
    uploadTarget.value = null
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

const addBanner = () => {
  form.banners.push('')
}

const removeBanner = (index: number) => {
  form.banners.splice(index, 1)
}

const save = async () => {
  formError.value = ''
  saving.value = true
  try {
    await api.updateDecoration({
      styleId: form.styleId,
      logoUrl: form.logoUrl,
      coverUrl: form.coverUrl,
      banners: form.banners.map(item => item.trim()).filter(Boolean),
      description: form.description,
      colors: { ...form.colors },
      copywriting: { ...form.copywriting },
      iconUrls: { ...form.iconUrls },
      imageUrls: { ...form.imageUrls },
    })
    notice.value = '装修配置已保存'
    await refresh()
  } catch (error: any) {
    formError.value = error?.data?.message || error?.message || '保存失败'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <AppShell>
    <div class="page-head sticky top-0 z-30 rounded-lg border border-ink-200 bg-white/95 p-4 shadow-panel backdrop-blur">
      <div>
        <h1>装修设置</h1>
        <p>可视化配置小程序店铺风格、颜色、Banner、图标、主题图片和展示文案。</p>
      </div>
      <button class="button primary" type="button" :disabled="saving" @click="save">
        {{ saving ? '保存中' : '保存装修' }}
      </button>
    </div>

    <input ref="fileInput" class="hidden" type="file" accept="image/*" @change="uploadDecorationImage">

    <div v-if="notice" class="mb-4 rounded-lg border border-brand-100 bg-brand-50 px-4 py-3 text-sm font-semibold text-brand-700">
      {{ notice }}
    </div>
    <p v-if="formError" class="form-error mb-4">{{ formError }}</p>

    <section class="grid cols-3">
      <button
        v-for="style in workspace?.styles"
        :key="style.id"
        class="card"
        type="button"
        @click="selectStyle(style)"
      >
        <div class="actions justify-between">
          <span :class="['badge', form.styleId === style.id ? '' : 'gray']">{{ style.code }}</span>
          <span class="muted">{{ style.theme.layoutVersion }}</span>
        </div>
        <h2>{{ style.name }}</h2>
        <div class="mt-3 flex gap-2">
          <span
            v-for="(color, key) in style.theme.colors"
            :key="key"
            class="h-7 w-7 rounded-full border border-ink-200"
            :style="{ backgroundColor: color }"
          />
        </div>
      </button>
    </section>

    <section class="section grid gap-4 xl:grid-cols-[minmax(0,1.3fr)_360px]">
      <div class="grid gap-4">
        <section class="form-panel">
          <h2>基础展示</h2>
          <div class="form-grid">
            <div class="field">
              <label>Logo</label>
              <div class="flex items-center gap-3">
                <img
                  v-if="form.logoUrl"
                  :src="form.logoUrl"
                  alt="Logo"
                  class="h-20 w-20 rounded-lg border border-ink-200 object-cover"
                >
                <button class="button" type="button" :disabled="uploadBusy" @click="beginUpload({ kind: 'logo' })">
                  上传 Logo
                </button>
              </div>
            </div>
            <div class="field">
              <label>封面</label>
              <div class="flex items-center gap-3">
                <img
                  v-if="form.coverUrl"
                  :src="form.coverUrl"
                  alt="封面"
                  class="h-20 w-32 rounded-lg border border-ink-200 object-cover"
                >
                <button class="button" type="button" :disabled="uploadBusy" @click="beginUpload({ kind: 'cover' })">
                  上传封面
                </button>
              </div>
            </div>
            <div class="field full">
              <label>展示描述</label>
              <textarea v-model="form.description" />
            </div>
          </div>
        </section>

        <section class="form-panel">
          <div class="actions justify-between">
            <h2>颜色主题</h2>
            <span class="badge">{{ selectedStyle?.name || '当前风格' }}</span>
          </div>
          <div class="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-3">
            <label v-for="key in colorKeys" :key="key" class="field rounded-lg border border-ink-200 p-3">
              <span>{{ colorLabels[key] || key }}</span>
              <div class="flex items-center gap-3">
                <input v-model="form.colors[key]" class="h-10 w-14 p-1" type="color">
                <input v-model="form.colors[key]" pattern="^#[0-9A-Fa-f]{6}$">
              </div>
            </label>
          </div>
        </section>

        <section class="form-panel">
          <div class="actions justify-between">
            <h2>Banner 管理</h2>
            <button class="button" type="button" @click="addBanner">新增 Banner</button>
          </div>
          <div class="grid gap-3">
            <div v-for="(banner, index) in form.banners" :key="index" class="grid gap-3 rounded-lg border border-ink-200 p-3 md:grid-cols-[160px_minmax(0,1fr)_auto]">
              <img
                v-if="banner"
                :src="banner"
                alt="Banner"
                class="h-24 w-full rounded-lg border border-ink-200 object-cover md:w-40"
              >
              <div v-else class="empty h-24 p-0 md:w-40">未上传</div>
              <div class="field">
                <label>Banner {{ index + 1 }}</label>
                <input v-model="form.banners[index]" placeholder="上传后自动填入地址">
              </div>
              <div class="actions">
                <button class="button" type="button" :disabled="uploadBusy" @click="beginUpload({ kind: 'banner', index })">上传</button>
                <button class="button danger" type="button" @click="removeBanner(index)">删除</button>
              </div>
            </div>
          </div>
        </section>

        <section class="form-panel">
          <h2>首页文案</h2>
          <div class="form-grid">
            <div v-for="[key] in copyEntries" :key="key" class="field">
              <label>{{ copyLabels[key] || key }}</label>
              <input v-model="form.copywriting[key]">
            </div>
          </div>
        </section>

        <section class="form-panel">
          <h2>图标配置</h2>
          <div class="grid gap-3">
            <div v-for="[key, url] in iconEntries" :key="key" class="grid gap-3 rounded-lg border border-ink-200 p-3 md:grid-cols-[72px_minmax(0,1fr)_auto]">
              <img
                v-if="url"
                :src="url"
                :alt="iconLabels[key] || key"
                class="h-16 w-16 rounded-lg border border-ink-200 object-contain p-2"
              >
              <div v-else class="empty h-16 w-16 p-0">{{ key.slice(0, 1) }}</div>
              <div class="field">
                <label>{{ iconLabels[key] || key }}</label>
                <input v-model="form.iconUrls[key]">
              </div>
              <button class="button" type="button" :disabled="uploadBusy" @click="beginUpload({ kind: 'icon', key })">替换</button>
            </div>
          </div>
        </section>

        <section class="form-panel">
          <h2>主题图片</h2>
          <div class="grid gap-3">
            <div v-for="[key, url] in imageEntries" :key="key" class="grid gap-3 rounded-lg border border-ink-200 p-3 md:grid-cols-[160px_minmax(0,1fr)_auto]">
              <img
                v-if="url"
                :src="url"
                :alt="imageLabels[key] || key"
                class="h-24 w-full rounded-lg border border-ink-200 object-cover md:w-40"
              >
              <div v-else class="empty h-24 p-0 md:w-40">未上传</div>
              <div class="field">
                <label>{{ imageLabels[key] || key }}</label>
                <input v-model="form.imageUrls[key]">
              </div>
              <button class="button" type="button" :disabled="uploadBusy" @click="beginUpload({ kind: 'image', key })">替换</button>
            </div>
          </div>
        </section>
      </div>

      <aside class="panel h-fit xl:sticky xl:top-8">
        <h2>小程序预览</h2>
        <div
          class="overflow-hidden rounded-lg border"
          :style="{ backgroundColor: form.colors.background, borderColor: form.colors.border, color: form.colors.text }"
        >
          <img
            v-if="previewBanner"
            :src="previewBanner"
            alt="Banner 预览"
            class="h-28 w-full object-cover"
          >
          <div class="p-4">
            <div class="flex items-center gap-3">
              <img v-if="form.logoUrl" :src="form.logoUrl" alt="Logo" class="h-12 w-12 rounded-full object-cover">
              <div>
                <div class="text-xs" :style="{ color: form.colors.mutedText }">{{ form.copywriting.branchName }}</div>
                <strong>{{ form.copywriting.heroEyebrow }}{{ form.copywriting.heroTitle }}</strong>
              </div>
            </div>
            <div
              class="mt-4 overflow-hidden rounded-lg"
              :style="{ backgroundColor: form.colors.surface, borderColor: form.colors.border }"
            >
              <img
                v-if="previewHeroImage"
                :src="previewHeroImage"
                alt="首页头图预览"
                class="h-28 w-full object-cover"
              >
              <div class="p-4">
                <p class="m-0 text-sm" :style="{ color: form.colors.mutedText }">{{ form.copywriting.heroSubtitle }}</p>
                <h3 class="mb-2 mt-3 text-xl font-semibold">{{ form.copywriting.promoTitle }}</h3>
                <p class="m-0 text-sm" :style="{ color: form.colors.mutedText }">{{ form.copywriting.promoSubtitle }}</p>
                <div class="mt-4 flex items-center justify-between gap-3">
                  <button
                    class="rounded-lg px-4 py-2 text-sm font-semibold text-white"
                    type="button"
                    :style="{ backgroundColor: form.colors.primary }"
                  >
                    {{ form.copywriting.promoActionText }}
                  </button>
                  <img
                    v-if="previewPromoImage"
                    :src="previewPromoImage"
                    alt="活动图预览"
                    class="h-14 w-14 rounded-lg object-cover"
                  >
                </div>
              </div>
            </div>
            <div class="mt-4 grid grid-cols-3 gap-2">
              <div v-for="category in previewCategories" :key="category.id" class="rounded-lg p-2 text-center text-xs" :style="{ backgroundColor: form.colors.surface }">
                <img v-if="category.iconUrl" :src="category.iconUrl" alt="" class="mx-auto mb-1 h-6 w-6 object-contain">
                <span>{{ category.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </section>
  </AppShell>
</template>
