import { computed, reactive, ref, watchEffect } from 'vue'
import { useStallmartApi } from '~/api/stallmart-api'
import type { StorefrontCategoryIcon, Style } from '~/types/admin'

type UploadTarget =
  | { kind: 'logo' }
  | { kind: 'cover' }
  | { kind: 'banner', index: number }

const extractErrorMessage = (error: unknown, fallback: string) => {
  if (error instanceof Error && error.message) {
    return error.message
  }
  if (typeof error === 'object' && error !== null) {
    const candidate = error as { data?: { message?: string }, message?: string }
    return candidate.data?.message || candidate.message || fallback
  }
  return fallback
}

export const useVendorDecorationForm = async () => {
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

  const selectedStyle = computed(() => workspace.value?.styles.find(style => style.id === form.styleId))
  const selectedTheme = computed(() => selectedStyle.value?.theme || workspace.value?.decoration.theme)
  const selectedColors = computed(() => selectedTheme.value?.colors || {})
  const selectedCopywriting = computed(() => selectedTheme.value?.copywriting || {})
  const selectedImageUrls = computed(() => selectedTheme.value?.imageUrls || {})
  const selectedCategoryIcons = computed(() => selectedTheme.value?.categoryIconLibrary || [])
  const selectedCategoryIconByKey = computed<Record<string, StorefrontCategoryIcon>>(() =>
    Object.fromEntries(selectedCategoryIcons.value.map(icon => [icon.key, icon])),
  )
  const colorKeys = computed(() => Object.keys(selectedColors.value))
  const copyEntries = computed(() => Object.entries(selectedCopywriting.value))
  const imageEntries = computed(() => Object.entries(selectedImageUrls.value))
  const previewBanner = computed(() => form.banners.find(Boolean) || form.coverUrl || selectedImageUrls.value.heroIllustration || '')
  const previewHeroImage = computed(() => selectedImageUrls.value.heroIllustration || form.coverUrl || previewBanner.value)
  const previewPromoImage = computed(() => selectedImageUrls.value.promoIllustration || selectedImageUrls.value.mascot || form.coverUrl || '')
  const previewCategories = computed(() => workspace.value?.decoration.categories.slice(0, 6) || [])

  watchEffect(() => {
    if (!workspace.value) return
    const decoration = workspace.value.decoration
    form.styleId = decoration.styleId
    form.logoUrl = decoration.logoUrl
    form.coverUrl = decoration.coverUrl || ''
    form.description = workspace.value.store.description
    form.banners = [...decoration.banners]
  })

  const selectStyle = (style: Style) => {
    form.styleId = style.id
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
    form.banners[target.index] = url
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
    } catch (error: unknown) {
      formError.value = extractErrorMessage(error, '上传失败')
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
      })
      notice.value = '装修配置已保存'
      await refresh()
    } catch (error: unknown) {
      formError.value = extractErrorMessage(error, '保存失败')
    } finally {
      saving.value = false
    }
  }

  return {
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
  }
}
