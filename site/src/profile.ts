export interface HeroLink {
  label: string
  href: string
}

export interface HeroMetric {
  value: string
  label: string
}

export interface SkillGroup {
  title: string
  summary: string
  items: string[]
}

export interface Principle {
  title: string
  body: string
}

export interface LabNote {
  title: string
  body: string
  tag: string
}

export interface SiteProfile {
  nickname: string
  forbiddenTerms: string[]
  hero: {
    eyebrow: string
    title: string
    subtitle: string
    links: HeroLink[]
    metrics: HeroMetric[]
  }
  skillGroups: SkillGroup[]
  principles: Principle[]
  labNotes: LabNote[]
  footer: string
}

export const siteProfile = {
  nickname: '白哥',
  forbiddenTerms: ['金钱', '企业', '小程序', 'SaaS', 'saas', '商业', '变现', '客户', '公司'],
  hero: {
    eyebrow: 'TECH LAB / NOTES / SYSTEMS',
    title: '白哥的技术实验室',
    subtitle: '记录前端工程、后端架构、系统设计与代码质量实践，把复杂问题拆成清楚、稳定、可演进的实现。',
    links: [
      { label: '技术栈', href: '#stack' },
      { label: '实验记录', href: '#notes' }
    ],
    metrics: [
      { value: 'Vue 3', label: '前端表达' },
      { value: 'TypeScript', label: '类型约束' },
      { value: 'Spring Boot', label: '服务端结构' }
    ]
  },
  skillGroups: [
    {
      title: '前端工程',
      summary: '关注组件边界、状态建模、路由组织、构建体验和交互细节。',
      items: ['Vue 3', 'TypeScript', 'Vite', '组件设计', '状态管理']
    },
    {
      title: '后端架构',
      summary: '用清晰的接口、稳定的数据流和可测试的服务分层支撑长期维护。',
      items: ['Java', 'Spring Boot', 'REST API', '数据建模', '边界设计']
    },
    {
      title: '工程质量',
      summary: '把代码质量前置到日常流程里，用测试、规范和自动化减少反复踩坑。',
      items: ['Vitest', '自动化验证', '代码质量', '文档同步', 'Review']
    }
  ],
  principles: [
    {
      title: '先把边界画清楚',
      body: '模块越清晰，修改越轻松。页面、状态、接口和工具函数各自承担自己的职责。'
    },
    {
      title: '让类型参与设计',
      body: '类型不是补丁，而是表达结构的方式。能在编辑器里发现的问题，就不留到运行时。'
    },
    {
      title: '用验证保护节奏',
      body: '测试、构建和静态检查是持续迭代的护栏，让每次调整都能留下可复查的证据。'
    }
  ],
  labNotes: [
    {
      title: '源码阅读',
      body: '拆解框架和工具链的关键路径，理解设计取舍，而不是只停在 API 用法。',
      tag: 'READ'
    },
    {
      title: '性能优化',
      body: '从资源体积、渲染节奏、交互反馈和数据请求四个角度观察体验瓶颈。',
      tag: 'PERF'
    },
    {
      title: '工程规范',
      body: '把目录、命名、测试和文档约定沉淀下来，让协作成本更低。',
      tag: 'RULE'
    },
    {
      title: '架构笔记',
      body: '记录接口契约、状态流转、模块职责和演进方案，保留思考轨迹。',
      tag: 'ARCH'
    }
  ],
  footer: '白哥 / 技术实验室 / 保持清晰，持续打磨。'
} satisfies SiteProfile
