import {
  DEFAULT_STORE_THEME,
  STORE_THEME_PACKAGES,
  type StorefrontAssetSizes,
  type StorefrontBannerConfig,
  type StorefrontCategoryConfig,
  type StorefrontCategoryIconConfig,
  type StoreStyleCode,
  type StoreThemePagePackages,
  type StoreThemePackage,
} from '@/config'

export interface StorefrontThemePayload {
  code: StoreStyleCode
  name: string
  layoutVersion?: string
  colors?: Record<string, string>
  iconNames?: Record<string, string>
  iconUrls?: Record<string, string>
  imageUrls?: Record<string, string>
  copywriting?: Record<string, string>
  categories?: StorefrontCategoryConfig[]
  categoryIconLibrary?: StorefrontCategoryIconConfig[]
  banners?: Array<StorefrontBannerConfig | string>
  assetSizes?: Partial<StorefrontAssetSizes>
  pageThemes?: StoreThemePagePackages
}

export interface StorefrontDecorationPayload {
  layoutVersion?: string
  theme?: StorefrontThemePayload
  colors?: Record<string, string>
  iconNames?: Record<string, string>
  iconUrls?: Record<string, string>
  imageUrls?: Record<string, string>
  copywriting?: Record<string, string>
  categories?: StorefrontCategoryConfig[]
  categoryIconLibrary?: StorefrontCategoryIconConfig[]
  banners?: Array<StorefrontBannerConfig | string>
  assetSizes?: Partial<StorefrontAssetSizes>
  pageThemes?: StoreThemePagePackages
}

export interface StoreInfo {
  id: string
  name: string
  logo?: string
  avatarUrl?: string
  description: string
  rating: number
  sales: number
  status?: string
  styleCode: StoreStyleCode
  styleId?: number
  isOpen?: boolean
  distance?: string
  branchName?: string
  heroEyebrow?: string
  heroTitle?: string
  heroSubtitle?: string
  decoration?: StorefrontDecorationPayload
}

export function resolveCurrentTheme(store?: StoreInfo | null): StoreThemePackage {
  const styleCode = store?.styleCode
  const base = styleCode ? STORE_THEME_PACKAGES[styleCode] || DEFAULT_STORE_THEME : DEFAULT_STORE_THEME
  return mergeStorefrontTheme(base, store?.decoration)
}

export function mergeStorefrontTheme(
  base: StoreThemePackage,
  decoration?: StorefrontDecorationPayload
): StoreThemePackage {
  if (!decoration) return base
  const theme = decoration.theme
  const colors = decoration.colors || theme?.colors || {}

  return {
    ...base,
    layoutVersion: decoration.layoutVersion || theme?.layoutVersion || base.layoutVersion,
    primary: colors.primary || base.primary,
    secondary: colors.secondary || base.secondary,
    accent: colors.accent || base.accent,
    background: colors.background || base.background,
    surface: colors.surface || base.surface,
    text: colors.text || base.text,
    mutedText: colors.mutedText || base.mutedText,
    border: colors.border || base.border,
    price: colors.price || base.price || colors.primary || base.primary,
    iconNames: { ...(base.iconNames || {}), ...(theme?.iconNames || {}), ...(decoration.iconNames || {}) },
    iconUrls: { ...(base.iconUrls || {}), ...(theme?.iconUrls || {}), ...(decoration.iconUrls || {}) },
    imageUrls: { ...(base.imageUrls || {}), ...(theme?.imageUrls || {}), ...(decoration.imageUrls || {}) },
    copywriting: { ...(base.copywriting || {}), ...(theme?.copywriting || {}), ...(decoration.copywriting || {}) },
    banners: normalizeBannerConfigs(decoration.banners) || normalizeBannerConfigs(theme?.banners) || base.banners,
    categoryIconLibrary: decoration.categoryIconLibrary || theme?.categoryIconLibrary || base.categoryIconLibrary,
    assetSizes: { ...(base.assetSizes || {}), ...(theme?.assetSizes || {}), ...(decoration.assetSizes || {}) } as StorefrontAssetSizes,
    pageThemes: { ...(base.pageThemes || {}), ...(theme?.pageThemes || {}) },
  }
}

export function normalizeBannerConfigs(
  banners?: Array<StorefrontBannerConfig | string>
): StorefrontBannerConfig[] | undefined {
  if (!banners || banners.length === 0) return undefined

  return banners
    .map((banner, index) => {
      if (typeof banner === 'string') {
        return {
          id: `decoration-banner-${index}`,
          imageUrl: banner,
          targetCategory: index === 0 ? 'recommend' : 'tea',
        }
      }

      return banner
    })
    .filter((banner) => !!banner.imageUrl)
}

export function resolveCategories(store: StoreInfo): StorefrontCategoryConfig[] {
  if (store.decoration?.categories && store.decoration.categories.length > 0) {
    return store.decoration.categories
  }

  return resolveFallbackCategories(
    mergeStorefrontTheme(STORE_THEME_PACKAGES[store.styleCode] || DEFAULT_STORE_THEME, store.decoration)
  )
}

export function resolveFallbackCategories(theme: StoreThemePackage): StorefrontCategoryConfig[] {
  return (theme.categoryIconLibrary || []).map((icon) => ({
    id: icon.key,
    name: icon.name,
    iconKey: icon.key,
    iconUrl: icon.iconUrl,
    fallbackText: icon.fallbackText,
    status: 'ACTIVE',
  }))
}

export function normalizeStore(store: StoreInfo): StoreInfo {
  return {
    ...store,
    id: String(store.id),
    logo: store.logo || store.avatarUrl || '/static/default-avatar.png',
    rating: store.rating || 4.9,
    sales: store.sales || 3286,
    styleCode: store.styleCode || 'forestFruitTeaCrayon',
    isOpen: store.isOpen ?? store.status !== 'closed',
    distance: store.distance || '1.3km',
    branchName: store.branchName || store.name,
    heroEyebrow: store.heroEyebrow || store.decoration?.copywriting?.heroEyebrow || '小新の',
    heroTitle: store.heroTitle || store.decoration?.copywriting?.heroTitle || store.name || '水果茶屋',
    heroSubtitle: store.heroSubtitle || store.decoration?.copywriting?.heroSubtitle || store.description || '自然水果 · 新鲜现制',
  }
}

export function createMockStore(): StoreInfo {
  return {
    id: '1',
    name: '小新の水果茶屋',
    logo: '/static/default-avatar.png',
    description: '当季鲜果茶，清爽一夏',
    rating: 4.9,
    sales: 3286,
    styleCode: 'forestFruitTeaCrayon',
    isOpen: true,
    distance: '1.3km',
    branchName: '上海环球港店',
    heroEyebrow: '小新の',
    heroTitle: '水果茶屋',
    heroSubtitle: '自然水果 · 新鲜现制',
    decoration: {
      layoutVersion: DEFAULT_STORE_THEME.layoutVersion,
      colors: {
        primary: DEFAULT_STORE_THEME.primary,
        secondary: DEFAULT_STORE_THEME.secondary,
        accent: DEFAULT_STORE_THEME.accent,
        background: DEFAULT_STORE_THEME.background,
        surface: DEFAULT_STORE_THEME.surface,
        text: DEFAULT_STORE_THEME.text,
        mutedText: DEFAULT_STORE_THEME.mutedText,
        border: DEFAULT_STORE_THEME.border,
        price: DEFAULT_STORE_THEME.price || DEFAULT_STORE_THEME.primary,
      },
      iconNames: DEFAULT_STORE_THEME.iconNames,
      iconUrls: DEFAULT_STORE_THEME.iconUrls,
      imageUrls: DEFAULT_STORE_THEME.imageUrls,
      copywriting: DEFAULT_STORE_THEME.copywriting,
      categoryIconLibrary: DEFAULT_STORE_THEME.categoryIconLibrary,
      categories: resolveFallbackCategories(DEFAULT_STORE_THEME),
      banners: DEFAULT_STORE_THEME.banners,
      assetSizes: DEFAULT_STORE_THEME.assetSizes,
      pageThemes: DEFAULT_STORE_THEME.pageThemes,
    },
  }
}
