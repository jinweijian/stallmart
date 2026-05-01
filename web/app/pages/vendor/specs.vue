<script setup lang="ts">
import { useStallmartApi } from '~/api/stallmart-api'
import type { SpecInput } from '~/types/admin'

definePageMeta({
  role: 'VENDOR',
})

const api = useStallmartApi()
const showCreate = ref(false)
const formError = ref('')
const { data: workspace } = await useAsyncData('vendor-spec-workspace', () => api.vendorSummary())
const { data: specs, refresh } = await useAsyncData('vendor-specs', () => api.vendorSpecs())
const form = reactive<SpecInput>({
  styleId: 1,
  name: '',
  specType: 'OTHER',
  required: false,
  options: [],
})
const optionsText = ref('')
const specTypeOptions = [
  { label: '杯型/尺寸', value: 'SIZE' },
  { label: '甜度', value: 'SWEET' },
  { label: '辣度', value: 'SPICE' },
  { label: '其他', value: 'OTHER' },
]
const specTypeLabel = (value: string) => specTypeOptions.find(option => option.value === value)?.label || value
const linkedSpecIds = computed(() => new Set((workspace.value?.products || []).flatMap(product => product.specIds)))

watchEffect(() => {
  if (workspace.value) {
    form.styleId = workspace.value.store.styleId
  }
})

const save = async () => {
  formError.value = ''
  form.options = optionsText.value.split('\n').map(option => option.trim()).filter(Boolean)
  await api.createSpec({ ...form })
  form.name = ''
  form.specType = 'OTHER'
  form.required = false
  optionsText.value = ''
  showCreate.value = false
  await refresh()
}

const remove = async (specId: number) => {
  formError.value = ''
  try {
    await api.deleteSpec(specId)
    await Promise.all([refresh(), refreshNuxtData('vendor-spec-workspace')])
  } catch (error: any) {
    formError.value = linkedSpecIds.value.has(specId)
      ? '该规格已被商品关联，不能删除'
      : (error?.data?.message || error?.message || '删除失败')
  }
}
</script>

<template>
  <AppShell>
    <div class="page-head">
      <div>
        <h1>商品规格管理</h1>
        <p>管理当前店铺风格包下可选规格，例如杯型、甜度、加料。</p>
      </div>
      <button class="button primary" type="button" @click="showCreate = !showCreate">
        {{ showCreate ? '收起新增' : '新增规格' }}
      </button>
    </div>

    <section v-if="showCreate" class="form-panel">
      <h2>新增规格</h2>
      <form class="form-grid" @submit.prevent="save">
        <div class="field">
          <label>规格名称</label>
          <input v-model="form.name" required>
        </div>
        <div class="field">
          <label>类型</label>
          <select v-model="form.specType">
            <option v-for="option in specTypeOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>
        <label class="field">
          <span>是否必选</span>
          <input v-model="form.required" type="checkbox">
        </label>
        <div class="field full">
          <label>选项，每行一个</label>
          <textarea v-model="optionsText" />
        </div>
        <div class="actions full">
          <button class="button primary" type="submit">保存规格</button>
          <button class="button" type="button" @click="showCreate = false">取消</button>
        </div>
      </form>
    </section>

    <section class="section">
      <h2>规格列表</h2>
      <p v-if="formError" class="form-error mb-4">{{ formError }}</p>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>名称</th>
              <th>类型</th>
              <th>必选</th>
              <th>选项</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="spec in specs" :key="spec.id">
              <td>{{ spec.name }}</td>
              <td>{{ specTypeLabel(spec.specType) }}</td>
              <td>{{ spec.required ? '是' : '否' }}</td>
              <td>{{ spec.options.join('，') }}</td>
              <td>
                <button
                  class="button danger"
                  type="button"
                  :disabled="linkedSpecIds.has(spec.id)"
                  @click="remove(spec.id)"
                >
                  {{ linkedSpecIds.has(spec.id) ? '已关联' : '删除' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
