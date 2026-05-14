import { describe, expect, it } from 'vitest'
import { architectureCases, capabilityMap, technicalNotes } from './content'
import { siteProfile, type SiteProfile } from './profile'

const stringifyPublishedCopy = (profile: SiteProfile) => {
  const { restrictedTerms, ...publishedCopy } = profile

  return JSON.stringify(publishedCopy)
}

describe('siteProfile', () => {
  it('uses Da Bai as the personal identity', () => {
    expect(siteProfile.nickname).toBe('大白')
    expect(siteProfile.hero.title).toContain('大白')
    expect(siteProfile.hero.title).toContain('技术实验室')
  })

  it('focuses the homepage on technical introduction', () => {
    const content = [
      stringifyPublishedCopy(siteProfile),
      JSON.stringify(capabilityMap),
      JSON.stringify(architectureCases),
      JSON.stringify(technicalNotes)
    ].join('\n')

    expect(content).toContain('Vue 3')
    expect(content).toContain('TypeScript')
    expect(content).toContain('Spring Boot')
    expect(content).toContain('代码质量')
  })

  it('publishes a complete engineering capability map', () => {
    expect(capabilityMap).toHaveLength(5)
    expect(capabilityMap.map((capability) => capability.title)).toEqual([
      '需求建模',
      '前端体验',
      '后端服务',
      '数据与部署',
      '测试验证'
    ])

    capabilityMap.forEach((capability) => {
      expect(capability.summary.length).toBeGreaterThan(12)
      expect(capability.coreStack.length).toBeGreaterThanOrEqual(2)
      expect(capability.expandedStack.length).toBeGreaterThanOrEqual(2)
      expect(capability.evidence.length).toBeGreaterThanOrEqual(2)
    })
  })

  it('publishes case and note detail entries with stable unique slugs', () => {
    expect(architectureCases).toHaveLength(4)
    expect(technicalNotes).toHaveLength(6)

    const slugs = [...architectureCases, ...technicalNotes].map((entry) => entry.slug)
    expect(new Set(slugs).size).toBe(slugs.length)
    expect(siteProfile.detailLinks.map((link) => link.href)).toEqual([
      ...architectureCases.map((entry) => `/cases/${entry.slug}`),
      ...technicalNotes.map((entry) => `/notes/${entry.slug}`)
    ])

    architectureCases.forEach((entry) => {
      expect(entry.problem.length).toBeGreaterThan(20)
      expect(entry.decisions).toHaveLength(3)
      expect(entry.validation).toHaveLength(3)
      expect(entry.retrospective.length).toBeGreaterThan(20)
    })

    technicalNotes.forEach((entry) => {
      expect(entry.coreQuestion.length).toBeGreaterThan(16)
      expect(entry.breakdown).toHaveLength(3)
      expect(entry.practiceChecklist).toHaveLength(3)
      expect(entry.commonPitfalls).toHaveLength(3)
    })
  })

  it('keeps commercial wording out of the published technical copy', () => {
    const content = [
      stringifyPublishedCopy(siteProfile),
      JSON.stringify(capabilityMap),
      JSON.stringify(architectureCases),
      JSON.stringify(technicalNotes)
    ].join('\n')

    siteProfile.restrictedTerms.forEach((term) => {
      expect(content).not.toContain(term)
    })
  })
})
