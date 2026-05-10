import { reactive, ref, toValue, watchEffect } from 'vue'
import { useStallmartApi } from '~/api/stallmart-api'
import type { Category, ProductInput, Spec } from '~/types/admin'
import type { MaybeRefOrGetter } from 'vue'

export type VendorProductSkuRow = {
  specValuesText: string
  price: number
  stock: number
  status: string
}

type UseVendorProductCreateFormOptions = {
  categories: MaybeRefOrGetter<Category[] | null | undefined>
  specs: MaybeRefOrGetter<Spec[] | null | undefined>
  afterSaved: () => Promise<void> | void
}

const initialSkuRow = (): VendorProductSkuRow => ({
  specValuesText: '',
  price: 0,
  stock: 99,
  status: 'ACTIVE',
})

const initialForm = (): ProductInput => ({
  name: '',
  description: '',
  categoryId: null,
  imageUrl: null,
  mainImageUrl: null,
  category: '',
  status: 'ACTIVE',
  sortOrder: 10,
  specIds: [],
  skus: [],
})

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

export const useVendorProductCreateForm = (options: UseVendorProductCreateFormOptions) => {
  const api = useStallmartApi()
  const imageInput = ref<HTMLInputElement | null>(null)
  const uploadBusy = ref(false)
  const saving = ref(false)
  const formError = ref('')
  const form = reactive<ProductInput>(initialForm())
  const skuRows = ref<VendorProductSkuRow[]>([initialSkuRow()])

  watchEffect(() => {
    const firstCategory = toValue(options.categories)?.[0]
    if (!form.categoryId && firstCategory) {
      form.categoryId = firstCategory.id
      form.category = firstCategory.name
    }
  })

  const syncCategoryName = () => {
    const category = toValue(options.categories)?.find(item => item.id === form.categoryId)
    form.category = category?.name || ''
  }

  const addSkuRow = () => {
    skuRows.value.push(initialSkuRow())
  }

  const removeSkuRow = (index: number) => {
    skuRows.value.splice(index, 1)
  }

  const reset = () => {
    const nextSortOrder = form.sortOrder + 10
    Object.assign(form, initialForm(), { sortOrder: nextSortOrder })
    skuRows.value = [initialSkuRow()]
    syncCategoryName()
  }

  const uploadMainImage = async (event: Event) => {
    const file = (event.target as HTMLInputElement).files?.[0]
    if (!file) return

    uploadBusy.value = true
    formError.value = ''
    try {
      const asset = await api.uploadProductImage(file)
      form.mainImageUrl = asset.url
      form.imageUrl = asset.url
    } catch (error: unknown) {
      formError.value = extractErrorMessage(error, '上传失败')
    } finally {
      uploadBusy.value = false
      if (imageInput.value) {
        imageInput.value.value = ''
      }
    }
  }

  const buildPayload = () => {
    syncCategoryName()
    const skus = skuRows.value
      .map(row => ({
        specValues: row.specValuesText.split(/[，,\n]/).map(value => value.trim()).filter(Boolean),
        price: Number(row.price),
        stock: Number(row.stock),
        status: row.status,
      }))
      .filter(row => row.specValues.length > 0)

    if (!form.categoryId) {
      throw new Error('请选择商品分类')
    }
    if (!form.mainImageUrl) {
      throw new Error('请上传商品主图')
    }
    if (form.specIds.length === 0) {
      throw new Error('请选择商品关联规格')
    }
    if (skus.length === 0) {
      throw new Error('请至少维护一个 SKU 价格')
    }

    const availableSpecIds = new Set(toValue(options.specs)?.map(spec => spec.id) || [])
    if (form.specIds.some(specId => !availableSpecIds.has(specId))) {
      throw new Error('存在无效商品规格，请刷新页面后重试')
    }

    const lowestPrice = Math.min(...skus.map(row => row.price))
    return {
      ...form,
      price: lowestPrice,
      skus,
    }
  }

  const save = async () => {
    formError.value = ''
    saving.value = true
    try {
      await api.createProduct(buildPayload())
      reset()
      await options.afterSaved()
    } catch (error: unknown) {
      formError.value = extractErrorMessage(error, '保存失败')
    } finally {
      saving.value = false
    }
  }

  return {
    imageInput,
    uploadBusy,
    saving,
    formError,
    form,
    skuRows,
    addSkuRow,
    removeSkuRow,
    save,
    syncCategoryName,
    uploadMainImage,
  }
}
