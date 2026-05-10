<script setup lang="ts">
import { useVendorDecorationForm } from '~/composables/vendor/useVendorDecorationForm'

definePageMeta({
  role: 'VENDOR',
})

const {
  workspace,
  fileInput,
  uploadBusy,
  saving,
  notice,
  formError,
  form,
  colorLabels,
  copyLabels,
  imageLabels,
  selectedStyle,
  selectedColors,
  selectedCopywriting,
  selectedImageUrls,
  selectedCategoryIcons,
  selectedCategoryIconByKey,
  colorKeys,
  copyEntries,
  imageEntries,
  previewBanner,
  previewHeroImage,
  previewPromoImage,
  previewCategories,
  selectStyle,
  beginUpload,
  uploadDecorationImage,
  addBanner,
  removeBanner,
  save,
} = await useVendorDecorationForm()
</script>

<template>
  <AppShell>
    <div class="page-head sticky top-0 z-30 rounded-lg border border-ink-200 bg-white/95 p-4 shadow-panel backdrop-blur">
      <div>
        <h1>装修设置</h1>
        <p>选择平台风格包，并维护 Logo、封面、Banner 和店铺展示描述。</p>
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
            <h2>风格包预览</h2>
            <span class="badge">{{ selectedStyle?.name || '当前风格包' }}</span>
          </div>
          <div class="grid gap-4">
            <div class="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-3">
              <div v-for="key in colorKeys" :key="key" class="rounded-lg border border-ink-200 p-3">
                <span class="muted block text-xs">{{ colorLabels[key] || key }}</span>
                <div class="mt-2 flex items-center gap-3">
                  <span
                    class="h-9 w-9 rounded-full border border-ink-200"
                    :style="{ backgroundColor: selectedColors[key] }"
                  />
                  <strong>{{ selectedColors[key] }}</strong>
                </div>
              </div>
            </div>
            <div class="form-grid">
              <div v-for="[key, value] in copyEntries" :key="key" class="rounded-lg border border-ink-200 p-3">
                <span class="muted block text-xs">{{ copyLabels[key] || key }}</span>
                <strong>{{ value }}</strong>
              </div>
            </div>
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
          <h2>分类 icon 库</h2>
          <div class="grid gap-3">
            <div v-for="icon in selectedCategoryIcons" :key="icon.key" class="grid gap-3 rounded-lg border border-ink-200 p-3 md:grid-cols-[72px_minmax(0,1fr)]">
              <img
                v-if="icon.iconUrl"
                :src="icon.iconUrl"
                :alt="icon.name"
                class="h-16 w-16 rounded-lg border border-ink-200 object-contain p-2"
              >
              <div v-else class="empty h-16 w-16 p-0">{{ icon.fallbackText }}</div>
              <div>
                <strong>{{ icon.name }}</strong>
                <p class="muted m-0">{{ icon.key }}</p>
              </div>
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
              <div>
                <strong>{{ imageLabels[key] || key }}</strong>
                <p class="muted m-0 break-all">{{ url || '未配置' }}</p>
              </div>
            </div>
          </div>
        </section>
      </div>

      <aside class="panel h-fit xl:sticky xl:top-8">
        <h2>小程序预览</h2>
        <div
          class="overflow-hidden rounded-lg border"
          :style="{ backgroundColor: selectedColors.background, borderColor: selectedColors.border, color: selectedColors.text }"
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
                <div class="text-xs" :style="{ color: selectedColors.mutedText }">{{ selectedCopywriting.branchName }}</div>
                <strong>{{ selectedCopywriting.heroEyebrow }}{{ selectedCopywriting.heroTitle }}</strong>
              </div>
            </div>
            <div
              class="mt-4 overflow-hidden rounded-lg"
              :style="{ backgroundColor: selectedColors.surface, borderColor: selectedColors.border }"
            >
              <img
                v-if="previewHeroImage"
                :src="previewHeroImage"
                alt="首页头图预览"
                class="h-28 w-full object-cover"
              >
              <div class="p-4">
                <p class="m-0 text-sm" :style="{ color: selectedColors.mutedText }">{{ selectedCopywriting.heroSubtitle }}</p>
                <h3 class="mb-2 mt-3 text-xl font-semibold">{{ selectedCopywriting.promoTitle }}</h3>
                <p class="m-0 text-sm" :style="{ color: selectedColors.mutedText }">{{ selectedCopywriting.promoSubtitle }}</p>
                <div class="mt-4 flex items-center justify-between gap-3">
                  <button
                    class="rounded-lg px-4 py-2 text-sm font-semibold text-white"
                    type="button"
                    :style="{ backgroundColor: selectedColors.primary }"
                  >
                    {{ selectedCopywriting.promoActionText }}
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
              <div v-for="category in previewCategories" :key="category.id" class="rounded-lg p-2 text-center text-xs" :style="{ backgroundColor: selectedColors.surface }">
                <img v-if="selectedCategoryIconByKey[category.iconKey]?.iconUrl || category.iconUrl" :src="selectedCategoryIconByKey[category.iconKey]?.iconUrl || category.iconUrl || ''" alt="" class="mx-auto mb-1 h-6 w-6 object-contain">
                <span>{{ category.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </section>
  </AppShell>
</template>
