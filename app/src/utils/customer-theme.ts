import Taro from '@tarojs/taro'
import { DEFAULT_STORE_THEME, type StoreThemePackage } from '@/config'

export const CUSTOMER_THEME_STORAGE_KEY = 'customer_theme_package'

const RPX_TO_REM = 1 / 40
const RPX_VALUE_PATTERN = /^(-?\d+(?:\.\d+)?)rpx$/i

function getAssetSizes(theme: StoreThemePackage) {
  return theme.assetSizes || DEFAULT_STORE_THEME.assetSizes
}

function toRuntimeCssSize(value: string | undefined, fallback: string): string {
  const size = (value || fallback).trim()
  const isWeb = Taro.getEnv() === Taro.ENV_TYPE.WEB
  if (!isWeb) {
    return size
  }

  const match = size.match(RPX_VALUE_PATTERN)

  if (!match) {
    return size
  }

  const rem = Number(match[1]) * RPX_TO_REM
  return `${Number(rem.toFixed(4))}rem`
}

function normalizeSizePair(
  value: { width: string; height: string } | undefined,
  fallback: { width: string; height: string } | undefined
) {
  return {
    width: toRuntimeCssSize(value?.width, fallback?.width || '0rpx'),
    height: toRuntimeCssSize(value?.height, fallback?.height || '0rpx'),
  }
}

function getSizePair(
  theme: StoreThemePackage,
  key: 'heroBanner' | 'promoBanner' | 'cartHeaderBanner' | 'myInviteBanner'
) {
  const sizes = getAssetSizes(theme)
  const fallback = DEFAULT_STORE_THEME.assetSizes?.[key]
  return normalizeSizePair(sizes?.[key], fallback)
}

function getSize(theme: StoreThemePackage, key: keyof NonNullable<StoreThemePackage['assetSizes']>, fallback: string) {
  const value = getAssetSizes(theme)?.[key]
  return toRuntimeCssSize(typeof value === 'string' ? value : undefined, fallback)
}

export function persistCustomerTheme(theme: StoreThemePackage): void {
  try {
    Taro.setStorageSync(CUSTOMER_THEME_STORAGE_KEY, JSON.stringify(theme))
  } catch (error) {
    console.warn('[CustomerTheme] Persist failed:', error)
  }
}

export function getCurrentCustomerTheme(): StoreThemePackage {
  try {
    const stored = Taro.getStorageSync(CUSTOMER_THEME_STORAGE_KEY)
    return stored ? (JSON.parse(stored) as StoreThemePackage) : DEFAULT_STORE_THEME
  } catch (error) {
    console.warn('[CustomerTheme] Read failed:', error)
    return DEFAULT_STORE_THEME
  }
}

export function createCustomerThemeVars(theme: StoreThemePackage): Record<string, string> {
  const heroBanner = getSizePair(theme, 'heroBanner')
  const promoBanner = getSizePair(theme, 'promoBanner')
  const cartHeaderBanner = getSizePair(theme, 'cartHeaderBanner')
  const myInviteBanner = getSizePair(theme, 'myInviteBanner')

  return {
    '--style-primary': theme.primary,
    '--style-secondary': theme.secondary,
    '--style-accent': theme.accent,
    '--style-background': theme.background,
    '--style-surface': theme.surface,
    '--style-text': theme.text,
    '--style-muted-text': theme.mutedText,
    '--style-border': theme.border,
    '--style-price': theme.price || theme.primary,
    '--style-hero': theme.heroGradient,
    '--asset-hero-banner-width': heroBanner.width,
    '--asset-hero-banner-height': heroBanner.height,
    '--asset-promo-banner-width': promoBanner.width,
    '--asset-promo-banner-height': promoBanner.height,
    '--asset-category-icon-size': getSize(theme, 'categoryIcon', '54rpx'),
    '--asset-location-icon-size': getSize(theme, 'locationIcon', '30rpx'),
    '--asset-section-icon-size': getSize(theme, 'sectionIcon', '30rpx'),
    '--asset-bottom-action-icon-size': getSize(theme, 'bottomActionIcon', '32rpx'),
    '--asset-product-image-size': getSize(theme, 'productImage', '144rpx'),
    '--asset-mascot-size': getSize(theme, 'mascot', '120rpx'),
    '--asset-cart-mascot-size': getSize(theme, 'cartMascot', '140rpx'),
    '--asset-cart-product-image-size': getSize(theme, 'cartProductImage', '148rpx'),
    '--asset-order-product-image-size': getSize(theme, 'orderProductImage', '112rpx'),
    '--asset-profile-avatar-size': getSize(theme, 'profileAvatar', '132rpx'),
    '--asset-menu-icon-size': getSize(theme, 'menuIcon', '44rpx'),
    '--asset-qty-stepper-size': getSize(theme, 'qtyStepper', '44rpx'),
    '--asset-progress-icon-size': getSize(theme, 'progressIcon', '44rpx'),
    '--asset-cart-header-banner-width': cartHeaderBanner.width,
    '--asset-cart-header-banner-height': cartHeaderBanner.height,
    '--asset-my-invite-banner-width': myInviteBanner.width,
    '--asset-my-invite-banner-height': myInviteBanner.height,
    '--asset-bottom-bar-height': getSize(theme, 'bottomBarHeight', '128rpx'),
    '--asset-cart-bar-bottom-offset': getSize(theme, 'cartBarBottomOffset', '60rpx'),
    '--asset-tab-bar-reserve': getSize(theme, 'tabBarReserve', '132rpx'),
  }
}

export function createThemeFromStoreDecoration(decoration: any): StoreThemePackage {
  const theme = decoration?.theme || {}
  const colors = {
    ...(theme.colors || {}),
    ...(decoration?.colors || {}),
  }

  return {
    ...DEFAULT_STORE_THEME,
    code: (decoration?.styleCode || theme.code || DEFAULT_STORE_THEME.code) as StoreThemePackage['code'],
    name: theme.name || decoration?.style?.name || DEFAULT_STORE_THEME.name,
    layoutVersion: decoration?.layoutVersion || theme.layoutVersion || DEFAULT_STORE_THEME.layoutVersion,
    primary: colors.primary || DEFAULT_STORE_THEME.primary,
    secondary: colors.secondary || DEFAULT_STORE_THEME.secondary,
    accent: colors.accent || DEFAULT_STORE_THEME.accent,
    background: colors.background || DEFAULT_STORE_THEME.background,
    surface: colors.surface || DEFAULT_STORE_THEME.surface,
    text: colors.text || DEFAULT_STORE_THEME.text,
    mutedText: colors.mutedText || DEFAULT_STORE_THEME.mutedText,
    border: colors.border || DEFAULT_STORE_THEME.border,
    price: colors.price || colors.primary || DEFAULT_STORE_THEME.price,
    iconNames: {
      ...(theme.iconNames || {}),
      ...(decoration?.iconNames || {}),
    },
    iconUrls: {
      ...(theme.iconUrls || {}),
      ...(decoration?.iconUrls || {}),
    },
    imageUrls: {
      ...(theme.imageUrls || {}),
      ...(decoration?.imageUrls || {}),
    },
    copywriting: {
      ...(theme.copywriting || {}),
      ...(decoration?.copywriting || {}),
    },
    categoryIconLibrary: decoration?.categoryIconLibrary || theme.categoryIconLibrary || DEFAULT_STORE_THEME.categoryIconLibrary,
    assetSizes: decoration?.assetSizes || theme.assetSizes || DEFAULT_STORE_THEME.assetSizes,
    pageThemes: decoration?.pageThemes || theme.pageThemes || DEFAULT_STORE_THEME.pageThemes,
  }
}
