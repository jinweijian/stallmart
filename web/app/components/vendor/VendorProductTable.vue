<script setup lang="ts">
import type { Product } from '~/types/admin'

defineProps<{
  products: Product[] | null | undefined
}>()

const emit = defineEmits<{
  statusChange: [productId: number, status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT']
}>()

const statusLabel: Record<string, string> = {
  ACTIVE: '已上架',
  INACTIVE: '已下架',
  SOLD_OUT: '已售罄',
}

const productHref = (productId: number) => `/vendor/products/${productId}`
</script>

<template>
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
          <tr
            v-for="product in products"
            :key="product.id"
            class="cursor-pointer hover:bg-ink-50"
            @dblclick="navigateTo(productHref(product.id))"
          >
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
            <td>
              <span :class="['badge', product.status === 'SOLD_OUT' ? 'warn' : product.status === 'INACTIVE' ? 'gray' : '']">
                {{ statusLabel[product.status] || product.status }}
              </span>
            </td>
            <td>{{ product.sortOrder }}</td>
            <td>
              <div class="actions">
                <a class="button" :href="productHref(product.id)">详情</a>
                <button
                  v-if="product.status !== 'ACTIVE'"
                  class="button"
                  type="button"
                  @click.stop="emit('statusChange', product.id, 'ACTIVE')"
                >
                  上架
                </button>
                <button
                  v-if="product.status !== 'INACTIVE'"
                  class="button"
                  type="button"
                  @click.stop="emit('statusChange', product.id, 'INACTIVE')"
                >
                  下架
                </button>
                <button
                  v-if="product.status !== 'SOLD_OUT'"
                  class="button danger"
                  type="button"
                  @click.stop="emit('statusChange', product.id, 'SOLD_OUT')"
                >
                  售罄
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
