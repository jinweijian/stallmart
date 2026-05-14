<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { findArchitectureCase } from '../content'

const route = useRoute()
const slug = computed(() => {
  const value = route.params.slug

  return Array.isArray(value) ? value[0] : value
})
const entry = computed(() => (slug.value ? findArchitectureCase(slug.value) : undefined))
</script>

<template>
  <article v-if="entry" class="detail-page">
    <RouterLink class="back-link" to="/#cases">← 返回架构案例</RouterLink>

    <header class="detail-hero">
      <p class="eyebrow">{{ entry.kicker }}</p>
      <h1>{{ entry.title }}</h1>
      <p>{{ entry.summary }}</p>
      <div class="tag-list">
        <span v-for="item in entry.stack" :key="item">{{ item }}</span>
      </div>
    </header>

    <div class="detail-layout">
      <section class="detail-block">
        <p class="section-kicker">PROBLEM</p>
        <h2>问题背景</h2>
        <p>{{ entry.problem }}</p>
      </section>

      <section class="detail-block">
        <p class="section-kicker">CONSTRAINTS</p>
        <h2>约束条件</h2>
        <ul>
          <li v-for="item in entry.constraints" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block">
        <p class="section-kicker">DECISIONS</p>
        <h2>架构决策</h2>
        <ul>
          <li v-for="item in entry.decisions" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block">
        <p class="section-kicker">IMPLEMENTATION</p>
        <h2>实现方式</h2>
        <ul>
          <li v-for="item in entry.implementation" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block">
        <p class="section-kicker">VALIDATION</p>
        <h2>验证方式</h2>
        <ul>
          <li v-for="item in entry.validation" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block detail-wide">
        <p class="section-kicker">RETROSPECTIVE</p>
        <h2>复盘收获</h2>
        <p>{{ entry.retrospective }}</p>
      </section>
    </div>
  </article>

  <section v-else class="detail-page empty-state">
    <p class="eyebrow">NOT FOUND</p>
    <h1>没有找到这个案例</h1>
    <RouterLink class="action-link" to="/#cases">返回案例索引</RouterLink>
  </section>
</template>
