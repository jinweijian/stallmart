import { describe, expect, it } from 'vitest'
import { siteProfile, type SiteProfile } from './profile'

const stringifyPublishedCopy = (profile: SiteProfile) => {
  const { forbiddenTerms, ...publishedCopy } = profile

  return JSON.stringify(publishedCopy)
}

describe('siteProfile', () => {
  it('uses Bai Ge as the personal identity', () => {
    expect(siteProfile.nickname).toBe('白哥')
    expect(siteProfile.hero.title).toContain('白哥')
    expect(siteProfile.hero.title).toContain('技术实验室')
  })

  it('focuses the homepage on technical introduction', () => {
    const content = stringifyPublishedCopy(siteProfile)

    expect(content).toContain('Vue 3')
    expect(content).toContain('TypeScript')
    expect(content).toContain('Spring Boot')
    expect(content).toContain('代码质量')
  })

  it('keeps commercial and mini-program SaaS wording out of the copy', () => {
    const content = stringifyPublishedCopy(siteProfile)

    siteProfile.forbiddenTerms.forEach((term) => {
      expect(content).not.toContain(term)
    })
  })
})
