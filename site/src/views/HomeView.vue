<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { architectureCases, capabilityMap, technicalNotes } from '../content'
import { siteProfile } from '../profile'
</script>

<template>
  <section class="hero-section" aria-labelledby="hero-title">
    <div class="hero-background" aria-hidden="true">
      <div class="scan-grid"></div>
      <div class="signal signal-one"></div>
      <div class="signal signal-two"></div>
      <div class="terminal-panel panel-one">
        <span>profile.mode = "full-stack + architecture"</span>
        <span>checks: tests / build / docs / deploy</span>
      </div>
      <div class="terminal-panel panel-two">
        <span>capability.map: engineering.loop</span>
        <span>evidence: decisions + validation</span>
      </div>
    </div>

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

    <div class="hero-metrics" aria-label="核心能力">
      <article v-for="metric in siteProfile.hero.metrics" :key="metric.value" class="metric-card">
        <strong>{{ metric.value }}</strong>
        <span>{{ metric.label }}</span>
      </article>
    </div>
  </section>

  <section id="capabilities" class="content-section capability-section" aria-labelledby="capability-title">
    <div class="section-heading reveal">
      <p class="section-kicker">CAPABILITY MAP</p>
      <h2 id="capability-title">按工程闭环展示技术深度</h2>
      <p>不把技术栈堆成清单，而是按从问题建模到发布验证的路径说明每一层能力如何协作。</p>
    </div>

    <div class="capability-layout">
      <aside class="capability-rail" aria-label="工程闭环">
        <a v-for="(capability, index) in capabilityMap" :key="capability.slug" :href="`#${capability.slug}`">
          <span>0{{ index + 1 }}</span>
          {{ capability.title }}
        </a>
      </aside>

      <div class="capability-list">
        <article
          v-for="(capability, index) in capabilityMap"
          :id="capability.slug"
          :key="capability.slug"
          class="capability-card reveal"
        >
          <div class="card-index">0{{ index + 1 }}</div>
          <div>
            <p class="capability-signal">{{ capability.signal }}</p>
            <h3>{{ capability.title }}</h3>
            <p>{{ capability.summary }}</p>
          </div>

          <div class="stack-layer">
            <span v-for="item in capability.coreStack" :key="item">{{ item }}</span>
          </div>

          <details class="stack-details">
            <summary>展开技术栈与证据</summary>
            <div class="detail-grid">
              <div>
                <h4>扩展技术栈</h4>
                <ul>
                  <li v-for="item in capability.expandedStack" :key="item">{{ item }}</li>
                </ul>
              </div>
              <div>
                <h4>能力证据</h4>
                <ul>
                  <li v-for="item in capability.evidence" :key="item">{{ item }}</li>
                </ul>
              </div>
            </div>
          </details>
        </article>
      </div>
    </div>
  </section>

  <section id="cases" class="content-section cases-section" aria-labelledby="cases-title">
    <div class="section-heading reveal">
      <p class="section-kicker">ARCHITECTURE CASES</p>
      <h2 id="cases-title">匿名架构治理案例</h2>
      <p>每个案例都按问题、约束、决策、实现、验证和复盘展开，强调过程而不是夸张结果。</p>
    </div>

    <div class="case-grid">
      <RouterLink v-for="entry in architectureCases" :key="entry.slug" class="case-card reveal" :to="`/cases/${entry.slug}`">
        <span class="note-tag">{{ entry.kicker }}</span>
        <h3>{{ entry.title }}</h3>
        <p>{{ entry.summary }}</p>
        <div class="tag-list">
          <span v-for="item in entry.stack" :key="item">{{ item }}</span>
        </div>
      </RouterLink>
    </div>
  </section>

  <section id="notes" class="content-section notes-section" aria-labelledby="notes-title">
    <div class="section-heading reveal">
      <p class="section-kicker">TECHNICAL NOTES</p>
      <h2 id="notes-title">可继续扩展的技术笔记</h2>
      <p>这些笔记不是博客目录的装饰，而是把源码阅读、契约、状态、数据、部署和质量方法沉淀下来。</p>
    </div>

    <div class="note-grid">
      <RouterLink v-for="note in technicalNotes" :key="note.slug" class="note-card reveal" :to="`/notes/${note.slug}`">
        <span class="note-tag">{{ note.kicker }}</span>
        <h3>{{ note.title }}</h3>
        <p>{{ note.summary }}</p>
        <div class="tag-list">
          <span v-for="item in note.tags" :key="item">{{ item }}</span>
        </div>
      </RouterLink>
    </div>
  </section>
</template>
