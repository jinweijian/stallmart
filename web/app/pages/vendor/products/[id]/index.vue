<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { Product, ProductInput } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

type SkuRow = {
  specValuesText: string
  price: number
  stock: number
  status: string
}

const route = useRoute()
const productId = Number(route.params.id)
const api = useStallmartApi()
const uploadBusy = ref(false)
const imageInput = ref<HTMLInputElement | null>(null)
const formError = ref('')
const { data: product, refresh } = await useAsyncData(`vendor-product-${productId}`, () => api.vendorProduct(productId))
const { data: categories } = await useAsyncData('vendor-product-detail-categories', () => api.vendorCategories('PRODUCT'))
const { data: specs } = await useAsyncData('vendor-product-detail-specs', () => api.vendorSpecs())
const form = reactive<ProductInput>({
  name: '',
  description: '',
  categoryId: null,
  imageUrl: null,
  mainImageUrl: null,
  category: '',
  status: 'ACTIVE',
  sortOrder: 0,
  specIds: [],
  skus: [],
})
const skuRows = ref<SkuRow[]>([])

const fillForm = (nextProduct: Product | null | undefined) => {
  if (!nextProduct) return
  form.name = nextProduct.name
  form.description = nextProduct.description
  form.categoryId = nextProduct.categoryId
  form.imageUrl = nextProduct.imageUrl
  form.mainImageUrl = nextProduct.mainImageUrl || nextProduct.imageUrl
  form.category = nextProduct.categoryName || nextProduct.category
  form.status = nextProduct.status
  form.sortOrder = nextProduct.sortOrder
  form.specIds = [...nextProduct.specIds]
  skuRows.value = nextProduct.skus.map(sku => ({
    specValuesText: sku.specValues.join('，'),
    price: sku.price,
    stock: sku.stock,
    status: sku.status,
  }))
}

watch(product, fillForm, { immediate: true })

const syncCategoryName = () => {
  const category = categories.value?.find(item => item.id === form.categoryId)
  form.category = category?.name || form.category
}

const addSkuRow = () => {
  skuRows.value.push({ specValuesText: '', price: 0, stock: 99, status: 'ACTIVE' })
}

const removeSkuRow = (index: number) => {
  skuRows.value.splice(index, 1)
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
  } catch (error: any) {
    formError.value = error?.data?.message || error?.message || '上传失败'
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

  const lowestPrice = Math.min(...skus.map(row => row.price))
  return {
    ...form,
    price: lowestPrice,
    skus,
  }
}

const save = async () => {
  formError.value = ''
  try {
    await api.updateProduct(productId, buildPayload())
    await refresh()
  } catch (error: any) {
    formError.value = error?.data?.message || error?.message || '保存失败'
  }
}

const setStatus = async (status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT') => {
  if (status === 'ACTIVE') {
    await api.productOnSale(productId)
  } else if (status === 'SOLD_OUT') {
    await api.productSoldOut(productId)
  } else {
    await api.productOffSale(productId)
  }
  await refresh()
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>{{ product?.name ?? '商品详情' }}</h1>
        <p>编辑商品主图、分类、关联规格、SKU 价格和上下架状态。</p>
      </div>
      <NuxtLink class="button" to="/vendor/products">返回商品列表</NuxtLink>
    </div>

    <section class="form-panel">
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label>商品名称</label>
          <input v-model="form.name" required>
        </div>
        <div class="field">
          <label>分类</label>
          <select v-model.number="form.categoryId" required @change="syncCategoryName">
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
        </div>
        <div class="field">
          <label>状态</label>
          <select v-model="form.status">
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
            <option value="SOLD_OUT">SOLD_OUT</option>
          </select>
        </div>
        <div class="field">
          <label>排序</label>
          <input v-model.number="form.sortOrder" type="number">
        </div>
        <div class="field full">
          <label>主图</label>
          <div class="flex flex-wrap items-center gap-3">
            <img
              v-if="form.mainImageUrl"
              :src="form.mainImageUrl"
              alt="商品主图"
              class="h-24 w-24 rounded-lg border border-ink-200 object-cover"
            >
            <input ref="imageInput" class="hidden" type="file" accept="image/*" @change="uploadMainImage">
            <button class="button" type="button" :disabled="uploadBusy" @click="imageInput?.click()">
              {{ uploadBusy ? '上传中' : '上传主图' }}
            </button>
          </div>
        </div>
        <div class="field full">
          <label>关联规格</label>
          <div class="flex flex-wrap gap-3">
            <label v-for="spec in specs" :key="spec.id" class="inline-flex items-center gap-2 text-sm text-ink-700">
              <input v-model="form.specIds" type="checkbox" :value="spec.id">
              <span>{{ spec.name }}：{{ spec.options.join(' / ') }}</span>
            </label>
          </div>
        </div>
        <div class="field full">
          <label>SKU 价格</label>
          <div class="grid gap-3">
            <div v-for="(row, index) in skuRows" :key="index" class="grid gap-3 rounded-lg border border-ink-200 p-3 lg:grid-cols-[minmax(0,1.4fr)_120px_120px_120px_auto]">
              <input v-model="row.specValuesText" placeholder="规格值，例如：中杯，少糖" required>
              <input v-model.number="row.price" min="0" step="0.01" type="number" placeholder="价格" required>
              <input v-model.number="row.stock" min="0" type="number" placeholder="库存">
              <select v-model="row.status">
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
              </select>
              <button class="button danger" type="button" :disabled="skuRows.length === 1" @click="removeSkuRow(index)">删除</button>
            </div>
            <div>
              <button class="button" type="button" @click="addSkuRow">新增 SKU</button>
            </div>
          </div>
        </div>
        <div class="field full">
          <label>描述</label>
          <textarea v-model="form.description" />
        </div>
        <p v-if="formError" class="form-error full">{{ formError }}</p>
        <div class="actions full">
          <button class="button primary" type="submit">保存</button>
          <button class="button" type="button" @click="setStatus('ACTIVE')">上架</button>
          <button class="button danger" type="button" @click="setStatus('INACTIVE')">下架</button>
          <button class="button danger" type="button" @click="setStatus('SOLD_OUT')">售罄</button>
        </div>
      </form>
    </section>
  </AppShell>
</template>
