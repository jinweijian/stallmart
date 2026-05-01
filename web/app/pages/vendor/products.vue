<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { ProductInput } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

type SkuRow = {
  specValuesText: string
  price: number
  stock: number
  status: string
}

const api = useStallmartApi()
const showCreate = ref(false)
const uploadBusy = ref(false)
const imageInput = ref<HTMLInputElement | null>(null)
const formError = ref('')
const { data: products, refresh } = await useAsyncData('vendor-products', () => api.vendorProducts())
const { data: categories, refresh: refreshCategories } = await useAsyncData('vendor-product-categories', () => api.vendorCategories('PRODUCT'))
const { data: specs } = await useAsyncData('vendor-product-specs', () => api.vendorSpecs())
const form = reactive<ProductInput>({
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
const skuRows = ref<SkuRow[]>([
  { specValuesText: '', price: 0, stock: 99, status: 'ACTIVE' },
])
const statusLabel: Record<string, string> = {
  ACTIVE: '已上架',
  INACTIVE: '已下架',
  SOLD_OUT: '已售罄',
}

const productHref = (productId: number) => `/vendor/products/${productId}`

watchEffect(() => {
  const firstCategory = categories.value?.[0]
  if (!form.categoryId && firstCategory) {
    form.categoryId = firstCategory.id
    form.category = firstCategory.name
  }
})

const syncCategoryName = () => {
  const category = categories.value?.find(item => item.id === form.categoryId)
  form.category = category?.name || ''
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
    await api.createProduct(buildPayload())
    form.name = ''
    form.description = ''
    form.imageUrl = null
    form.mainImageUrl = null
    form.status = 'ACTIVE'
    form.sortOrder += 10
    form.specIds = []
    skuRows.value = [{ specValuesText: '', price: 0, stock: 99, status: 'ACTIVE' }]
    showCreate.value = false
    await Promise.all([refresh(), refreshCategories()])
  } catch (error: any) {
    formError.value = error?.data?.message || error?.message || '保存失败'
  }
}

const setProductStatus = async (productId: number, status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT') => {
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
        <h1>商品管理</h1>
        <p>商品需要绑定分类、主图、规格和 SKU 价格，保存后同步给小程序店铺接口。</p>
      </div>
      <div class="actions">
        <NuxtLink class="button" to="/vendor/categories">管理分类</NuxtLink>
        <NuxtLink class="button" to="/vendor/specs">管理规格</NuxtLink>
        <button class="button primary" type="button" @click="showCreate = !showCreate">
          {{ showCreate ? '收起新增' : '新增商品' }}
        </button>
      </div>
    </div>

    <section v-if="showCreate" class="form-panel">
      <h2>新增商品</h2>
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
          <button class="button primary" type="submit">保存商品</button>
          <button class="button" type="button" @click="showCreate = false">取消</button>
        </div>
      </form>
    </section>

    <section class="section">
      <h2>商品列表</h2>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>商品</th>
              <th>分类</th>
              <th>最低规格价</th>
              <th>规格</th>
              <th>SKU</th>
              <th>状态</th>
              <th>排序</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id" class="cursor-pointer hover:bg-ink-50" @dblclick="navigateTo(productHref(product.id))">
              <td>
                <div class="flex min-w-56 items-center gap-3">
                  <img
                    :src="product.mainImageUrl || product.imageUrl || '/static/storefront/forest/product-placeholder.png'"
                    alt="商品主图"
                    class="h-14 w-14 rounded-lg border border-ink-200 object-cover"
                  >
                  <div>
                    <strong>{{ product.name }}</strong>
                    <div class="muted">{{ product.description }}</div>
                  </div>
                </div>
              </td>
              <td>{{ product.categoryName || product.category }}</td>
              <td>¥{{ product.price }}</td>
              <td>{{ product.specIds.length }} 个</td>
              <td>{{ product.skus.length }} 个</td>
              <td><span :class="['badge', product.status === 'SOLD_OUT' ? 'warn' : product.status === 'INACTIVE' ? 'gray' : '']">{{ statusLabel[product.status] || product.status }}</span></td>
              <td>{{ product.sortOrder }}</td>
              <td>
                <div class="actions">
                  <a class="button" :href="productHref(product.id)">详情</a>
                  <button v-if="product.status !== 'ACTIVE'" class="button" type="button" @click.stop="setProductStatus(product.id, 'ACTIVE')">上架</button>
                  <button v-if="product.status !== 'INACTIVE'" class="button" type="button" @click.stop="setProductStatus(product.id, 'INACTIVE')">下架</button>
                  <button v-if="product.status !== 'SOLD_OUT'" class="button danger" type="button" @click.stop="setProductStatus(product.id, 'SOLD_OUT')">售罄</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
