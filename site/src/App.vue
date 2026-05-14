<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { siteProfile } from './profile'
</script>

<template>
  <main class="site-shell">
    <header class="site-header">
      <RouterLink class="brand" to="/" aria-label="返回首页">{{ siteProfile.nickname }}</RouterLink>
      <nav class="nav-links" aria-label="页面导航">
        <a v-for="link in siteProfile.navigation" :key="link.href" :href="link.href">{{ link.label }}</a>
      </nav>
    </header>

    <RouterView />

    <footer class="site-footer">
      <span>{{ siteProfile.footer }}</span>
      <div class="footer-links" aria-label="详情入口">
        <RouterLink v-for="link in siteProfile.detailLinks.slice(0, 4)" :key="link.href" :to="link.href">
          {{ link.label }}
        </RouterLink>
      </div>
    </footer>
  </main>
</template>
