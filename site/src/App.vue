<script setup lang="ts">
import { siteProfile } from './profile'

const accentClasses = ['accent-mint', 'accent-amber', 'accent-cyan']
</script>

<template>
  <main class="site-shell">
    <section class="hero-section" aria-labelledby="hero-title">
      <div class="hero-background" aria-hidden="true">
        <div class="scan-grid"></div>
        <div class="signal signal-one"></div>
        <div class="signal signal-two"></div>
        <div class="terminal-panel panel-one">
          <span>const clarity = design(boundaries)</span>
          <span>run: tests / build / review</span>
        </div>
        <div class="terminal-panel panel-two">
          <span>module: frontend.engineering</span>
          <span>contract: api.first</span>
        </div>
      </div>

      <nav class="topbar" aria-label="页面导航">
        <a class="brand" href="#hero-title">{{ siteProfile.nickname }}</a>
        <div class="nav-links">
          <a href="#stack">技术栈</a>
          <a href="#principles">方法</a>
          <a href="#notes">记录</a>
        </div>
      </nav>

      <div class="hero-content">
        <p class="eyebrow">{{ siteProfile.hero.eyebrow }}</p>
        <h1 id="hero-title">{{ siteProfile.hero.title }}</h1>
        <p class="hero-subtitle">{{ siteProfile.hero.subtitle }}</p>

        <div class="hero-actions" aria-label="主要链接">
          <a v-for="link in siteProfile.hero.links" :key="link.href" class="action-link" :href="link.href">
            {{ link.label }}
            <span aria-hidden="true">→</span>
          </a>
        </div>
      </div>

      <div class="hero-metrics" aria-label="核心技术">
        <article v-for="metric in siteProfile.hero.metrics" :key="metric.value" class="metric-card">
          <strong>{{ metric.value }}</strong>
          <span>{{ metric.label }}</span>
        </article>
      </div>
    </section>

    <section id="stack" class="content-section stack-section" aria-labelledby="stack-title">
      <div class="section-heading reveal">
        <p class="section-kicker">STACK</p>
        <h2 id="stack-title">稳定表达技术能力</h2>
        <p>技术栈不是清单，而是一套把想法变成可维护实现的方法。</p>
      </div>

      <div class="skill-grid">
        <article
          v-for="(group, index) in siteProfile.skillGroups"
          :key="group.title"
          class="skill-card reveal"
          :class="accentClasses[index % accentClasses.length]"
        >
          <div class="card-index">0{{ index + 1 }}</div>
          <h3>{{ group.title }}</h3>
          <p>{{ group.summary }}</p>
          <div class="tag-list">
            <span v-for="item in group.items" :key="item">{{ item }}</span>
          </div>
        </article>
      </div>
    </section>

    <section id="principles" class="content-section principle-section" aria-labelledby="principle-title">
      <div class="section-heading reveal">
        <p class="section-kicker">METHOD</p>
        <h2 id="principle-title">把复杂度放进边界里</h2>
      </div>

      <div class="principle-list">
        <article v-for="principle in siteProfile.principles" :key="principle.title" class="principle-row reveal">
          <span aria-hidden="true"></span>
          <div>
            <h3>{{ principle.title }}</h3>
            <p>{{ principle.body }}</p>
          </div>
        </article>
      </div>
    </section>

    <section id="notes" class="content-section notes-section" aria-labelledby="notes-title">
      <div class="section-heading reveal">
        <p class="section-kicker">LAB NOTES</p>
        <h2 id="notes-title">持续记录技术实验</h2>
        <p>每一次阅读、优化和复盘，都应该留下能被再次理解的痕迹。</p>
      </div>

      <div class="note-grid">
        <article v-for="note in siteProfile.labNotes" :key="note.title" class="note-card reveal">
          <span class="note-tag">{{ note.tag }}</span>
          <h3>{{ note.title }}</h3>
          <p>{{ note.body }}</p>
        </article>
      </div>
    </section>

    <footer class="site-footer">
      {{ siteProfile.footer }}
    </footer>
  </main>
</template>
