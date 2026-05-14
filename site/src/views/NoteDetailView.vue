<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { findTechnicalNote } from '../content'

const route = useRoute()
const slug = computed(() => {
  const value = route.params.slug

  return Array.isArray(value) ? value[0] : value
})
const entry = computed(() => (slug.value ? findTechnicalNote(slug.value) : undefined))
</script>

<template>
  <article v-if="entry" class="detail-page">
    <RouterLink class="back-link" to="/#notes">← 返回技术笔记</RouterLink>

    <header class="detail-hero">
      <p class="eyebrow">{{ entry.kicker }}</p>
      <h1>{{ entry.title }}</h1>
      <p>{{ entry.summary }}</p>
      <div class="tag-list">
        <span v-for="item in entry.tags" :key="item">{{ item }}</span>
      </div>
    </header>

    <div class="detail-layout">
      <section class="detail-block detail-wide">
        <p class="section-kicker">QUESTION</p>
        <h2>核心问题</h2>
        <p>{{ entry.coreQuestion }}</p>
      </section>

      <section class="detail-block">
        <p class="section-kicker">BREAKDOWN</p>
        <h2>技术拆解</h2>
        <ul>
          <li v-for="item in entry.breakdown" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block">
        <p class="section-kicker">CHECKLIST</p>
        <h2>实践清单</h2>
        <ul>
          <li v-for="item in entry.practiceChecklist" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block">
        <p class="section-kicker">PITFALLS</p>
        <h2>常见坑</h2>
        <ul>
          <li v-for="item in entry.commonPitfalls" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="detail-block">
        <p class="section-kicker">NEXT</p>
        <h2>延伸方向</h2>
        <ul>
          <li v-for="item in entry.nextDirections" :key="item">{{ item }}</li>
        </ul>
      </section>
    </div>
  </article>

  <section v-else class="detail-page empty-state">
    <p class="eyebrow">NOT FOUND</p>
    <h1>没有找到这篇笔记</h1>
    <RouterLink class="action-link" to="/#notes">返回笔记索引</RouterLink>
  </section>
</template>
