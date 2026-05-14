import { architectureCases, technicalNotes } from './content'

export interface HeroLink {
  label: string
  href: string
}

export interface HeroMetric {
  value: string
  label: string
}

export interface SiteProfile {
  nickname: string
  restrictedTerms: string[]
  navigation: HeroLink[]
  detailLinks: HeroLink[]
  hero: {
    eyebrow: string
    title: string
    subtitle: string
    links: HeroLink[]
    metrics: HeroMetric[]
  }
  footer: string
}

export const siteProfile = {
  nickname: '大白',
  restrictedTerms: ['金钱', '企业', 'SaaS', 'saas', '商业', '变现', '客户', '公司', '营收', '转化', '套餐', '交易'],
  navigation: [
    { label: '能力地图', href: '/#capabilities' },
    { label: '架构案例', href: '/#cases' },
    { label: '技术笔记', href: '/#notes' }
  ],
  detailLinks: [
    ...architectureCases.map((entry) => ({ label: entry.title, href: `/cases/${entry.slug}` })),
    ...technicalNotes.map((entry) => ({ label: entry.title, href: `/notes/${entry.slug}` }))
  ],
  hero: {
    eyebrow: 'ENGINEERING PROFILE / ARCHITECTURE / DELIVERY',
    title: '大白的技术实验室',
    subtitle: '实战全栈与架构深度并重，把需求建模、前端体验、后端服务、数据部署和测试验证串成可持续演进的工程闭环。',
    links: [
      { label: '查看能力地图', href: '/#capabilities' },
      { label: '阅读架构案例', href: '/#cases' }
    ],
    metrics: [
      { value: 'Full Stack', label: '从页面到服务的交付闭环' },
      { value: 'Architecture', label: '边界、契约和演进治理' },
      { value: 'Verification', label: '测试、构建和发布证据' }
    ]
  },
  footer: '大白 / 技术实验室 / 保持清晰，持续打磨。'
} satisfies SiteProfile
