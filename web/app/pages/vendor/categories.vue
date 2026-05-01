<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { CategoryInput } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const showCreate = ref(false)
const activeModule = ref('PRODUCT')
const { data: categories, refresh } = await useAsyncData('vendor-categories', () => api.vendorCategories(activeModule.value), {
  watch: [activeModule],
})
const form = reactive<CategoryInput>({
  module: 'PRODUCT',
  name: '',
  sortOrder: 10,
  status: 'ACTIVE',
})

watch(activeModule, module => {
  form.module = module
})

const save = async () => {
  await api.createCategory({ ...form })
  form.name = ''
  form.sortOrder += 10
  showCreate.value = false
  await refresh()
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>分类管理</h1>
        <p>按模块维护分类，商品模块分类会直接用于商品创建与小程序店铺展示。</p>
      </div>
      <div class="actions">
        <select v-model="activeModule" class="button">
          <option value="PRODUCT">商品模块</option>
          <option value="BANNER">Banner 模块</option>
          <option value="DECORATION">装修模块</option>
        </select>
        <button class="button primary" type="button" @click="showCreate = !showCreate">
          {{ showCreate ? '收起新增' : '新增分类' }}
        </button>
      </div>
    </div>

    <section v-if="showCreate" class="form-panel">
      <h2>新增分类</h2>
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label>模块</label>
          <select v-model="form.module">
            <option value="PRODUCT">商品模块</option>
            <option value="BANNER">Banner 模块</option>
            <option value="DECORATION">装修模块</option>
          </select>
        </div>
        <div class="field">
          <label>分类名称</label>
          <input v-model="form.name" required>
        </div>
        <div class="field">
          <label>排序</label>
          <input v-model.number="form.sortOrder" type="number">
        </div>
        <div class="field">
          <label>状态</label>
          <select v-model="form.status">
            <option value="ACTIVE">ACTIVE</option>
            <option value="INACTIVE">INACTIVE</option>
          </select>
        </div>
        <div class="actions full">
          <button class="button primary" type="submit">保存分类</button>
          <button class="button" type="button" @click="showCreate = false">取消</button>
        </div>
      </form>
    </section>

    <section class="section">
      <h2>分类列表</h2>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>分类</th>
              <th>模块</th>
              <th>状态</th>
              <th>排序</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="category in categories" :key="category.id">
              <td><strong>{{ category.name }}</strong></td>
              <td>{{ category.module }}</td>
              <td><span class="badge">{{ category.status }}</span></td>
              <td>{{ category.sortOrder }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
