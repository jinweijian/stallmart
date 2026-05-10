<script setup lang="ts">
import { toRef } from 'vue'
import { useVendorProductCreateForm } from '~/composables/vendor/useVendorProductCreateForm'
import type { Category, Spec } from '~/types/admin'

const props = defineProps<{
  categories: Category[] | null | undefined
  specs: Spec[] | null | undefined
}>()

const emit = defineEmits<{
  saved: []
  cancel: []
}>()

const {
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
} = useVendorProductCreateForm({
  categories: toRef(props, 'categories'),
  specs: toRef(props, 'specs'),
  afterSaved: () => emit('saved'),
})
</script>

<template>
  <section class="form-panel">
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
          <div
            v-for="(row, index) in skuRows"
            :key="index"
            class="grid gap-3 rounded-lg border border-ink-200 p-3 lg:grid-cols-[minmax(0,1.4fr)_120px_120px_120px_auto]"
          >
            <input v-model="row.specValuesText" placeholder="规格值，例如：中杯，少糖" required>
            <input v-model.number="row.price" min="0" step="0.01" type="number" placeholder="价格" required>
            <input v-model.number="row.stock" min="0" type="number" placeholder="库存">
            <select v-model="row.status">
              <option value="ACTIVE">ACTIVE</option>
              <option value="INACTIVE">INACTIVE</option>
            </select>
            <button class="button danger" type="button" :disabled="skuRows.length === 1" @click="removeSkuRow(index)">
              删除
            </button>
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
        <button class="button primary" type="submit" :disabled="saving">
          {{ saving ? '保存中' : '保存商品' }}
        </button>
        <button class="button" type="button" @click="emit('cancel')">取消</button>
      </div>
    </form>
  </section>
</template>
