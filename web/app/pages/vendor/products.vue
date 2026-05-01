<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { ProductInput } from '~/types/admin'

const api = useStallmartApi()
const { data: products, refresh } = await useAsyncData('vendor-products', () => api.vendorProducts())
const form = reactive<ProductInput>({
  name: '',
  description: '',
  price: 0,
  imageUrl: null,
  category: '饮品',
  status: 'ACTIVE',
  sortOrder: 10,
})

const save = async () => {
  await api.createProduct({ ...form })
  form.name = ''
  form.description = ''
  form.price = 0
  form.imageUrl = null
  form.category = '饮品'
  form.status = 'ACTIVE'
  form.sortOrder += 1
  await refresh()
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>商品管理</h1>
        <p>维护商家商品，保存后小程序店铺商品接口同步可见。</p>
      </div>
      <NuxtLink class="button" to="/vendor">商家 H5</NuxtLink>
    </div>

    <section class="form-panel">
      <h2>新增商品</h2>
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label>商品名称</label>
          <input v-model="form.name" required>
        </div>
        <div class="field">
          <label>分类</label>
          <input v-model="form.category">
        </div>
        <div class="field">
          <label>价格</label>
          <input v-model.number="form.price" min="0" step="0.01" type="number" required>
        </div>
        <div class="field">
          <label>状态</label>
          <select v-model="form.status">
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
            <option value="SOLD_OUT">SOLD_OUT</option>
          </select>
        </div>
        <div class="field full">
          <label>描述</label>
          <textarea v-model="form.description" />
        </div>
        <div class="actions full">
          <button class="button primary" type="submit">保存商品</button>
        </div>
      </form>
    </section>

    <section class="section">
      <h2>商品列表</h2>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>商品</th>
              <th>分类</th>
              <th>价格</th>
              <th>状态</th>
              <th>排序</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>
                <strong>{{ product.name }}</strong>
                <div class="muted">{{ product.description }}</div>
              </td>
              <td>{{ product.category }}</td>
              <td>¥{{ product.price }}</td>
              <td><span class="badge">{{ product.status }}</span></td>
              <td>{{ product.sortOrder }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
