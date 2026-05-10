<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const showCreate = ref(false)
const { data: products, refresh } = await useAsyncData('vendor-products', () => api.vendorProducts())
const { data: categories, refresh: refreshCategories } = await useAsyncData('vendor-product-categories', () => api.vendorCategories('PRODUCT'))
const { data: specs } = await useAsyncData('vendor-product-specs', () => api.vendorSpecs())

const reloadProducts = async () => {
  showCreate.value = false
  await Promise.all([refresh(), refreshCategories()])
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

    <VendorProductCreateForm
      v-if="showCreate"
      :categories="categories"
      :specs="specs"
      @cancel="showCreate = false"
      @saved="reloadProducts"
    />

    <VendorProductTable :products="products" @status-change="setProductStatus" />
  </AppShell>
</template>
