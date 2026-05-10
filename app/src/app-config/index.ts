/**
 * App Configuration
 * Contains API base URLs, app constants, and environment settings
 */

export * from './env'
export * from './storage-keys'
export * from './endpoints'

// Design System Colors
export const COLORS = {
  PRIMARY: '#FF6B35',
  SECONDARY: '#2EC4B6',
  BACKGROUND: '#FFF8F0',
  FOREGROUND: '#333333',
  SURFACE: '#FFFFFF',
  MUTED: '#F5F5F5',
  BORDER: '#E5E5E5',
  TEXT_SECONDARY: '#666666',
  TEXT_MUTED: '#999999',
  SUCCESS: '#52C41A',
  WARNING: '#FAAD14',
  ERROR: '#F5222D',
  INFO: '#1890FF',
} as const

// Storefront style packages. Backend can return one of these style codes on store detail.
export const STORE_STYLE_CODES = {
  HAWAIIAN: 'hawaiian',
  BBQ: 'BBQ',
  MARKET: 'market',
  OCEAN: 'ocean',
  FRESH: 'fresh',
  FOREST_FRUIT_TEA_CRAYON: 'forestFruitTeaCrayon',
} as const

export type StoreStyleCode = (typeof STORE_STYLE_CODES)[keyof typeof STORE_STYLE_CODES]

export interface StorefrontCategoryConfig {
  id: string
  name: string
  iconKey?: string
  iconUrl?: string | null
  fallbackText: string
  sortOrder?: number
  status?: string
}

export interface StorefrontCategoryIconConfig {
  key: string
  name: string
  iconUrl?: string | null
  fallbackText: string
}

export interface StorefrontBannerConfig {
  id: string
  imageUrl: string
  title?: string
  subtitle?: string
  actionText?: string
  targetCategory?: string
}

export interface StorefrontAssetSizes {
  heroBanner: {
    width: string
    height: string
  }
  promoBanner: {
    width: string
    height: string
  }
  categoryIcon: string
  locationIcon: string
  sectionIcon: string
  bottomActionIcon: string
  productImage: string
  mascot: string
  cartMascot: string
  cartProductImage: string
  orderProductImage: string
  profileAvatar: string
  menuIcon: string
  qtyStepper: string
  progressIcon: string
  cartHeaderBanner: {
    width: string
    height: string
  }
  myInviteBanner: {
    width: string
    height: string
  }
  bottomBarHeight: string
  cartBarBottomOffset: string
  tabBarReserve: string
}

export interface StoreThemePageBanner {
  imageUrl: string
  title?: string
  subtitle?: string
  actionText?: string
}

export interface StoreThemePagePackages {
  home?: {
    sectionTitle?: string
  }
  cart?: {
    headerBanner?: StoreThemePageBanner
    storeName?: string
    distanceText?: string
    emptyTitle?: string
    emptySubtitle?: string
  }
  orders?: {
    headerBanner?: StoreThemePageBanner
    emptyTitle?: string
    emptySubtitle?: string
    statusColors?: Record<string, string>
  }
  my?: {
    greeting?: string
    inviteBanner?: StoreThemePageBanner
    menuIcons?: Record<string, string>
  }
}

export interface StoreThemePackage {
  code: StoreStyleCode
  name: string
  layoutVersion?: string
  primary: string
  secondary: string
  accent: string
  background: string
  surface: string
  text: string
  mutedText: string
  border: string
  price?: string
  heroGradient: string
  iconNames?: Record<string, string>
  iconUrls?: Record<string, string>
  imageUrls?: Record<string, string>
  copywriting?: Record<string, string>
  banners?: ReadonlyArray<StorefrontBannerConfig>
  categoryIconLibrary?: ReadonlyArray<StorefrontCategoryIconConfig>
  assetSizes?: StorefrontAssetSizes
  pageThemes?: StoreThemePagePackages
}

export const STORE_THEME_PACKAGES: Record<StoreStyleCode, StoreThemePackage> = {
  hawaiian: {
    code: STORE_STYLE_CODES.HAWAIIAN,
    name: '夏威夷风',
    primary: '#2ECC71',
    secondary: '#F9D66E',
    accent: '#FF8E5E',
    background: '#FEF9E7',
    surface: '#FFFFFF',
    text: '#2D3436',
    mutedText: '#7A7A6A',
    border: '#E8E2C8',
    heroGradient: 'linear-gradient(135deg, #FEF9E7 0%, #DFF5C8 100%)',
  },
  BBQ: {
    code: STORE_STYLE_CODES.BBQ,
    name: '烧烤风',
    primary: '#E74C3C',
    secondary: '#F39C12',
    accent: '#8E3B2D',
    background: '#FDEDEC',
    surface: '#FFFFFF',
    text: '#2D3436',
    mutedText: '#8A6A64',
    border: '#F0C8BE',
    heroGradient: 'linear-gradient(135deg, #FDEDEC 0%, #F7B08A 100%)',
  },
  market: {
    code: STORE_STYLE_CODES.MARKET,
    name: '市集风',
    primary: '#F39C12',
    secondary: '#8BC34A',
    accent: '#D35400',
    background: '#FFF8E7',
    surface: '#FFFFFF',
    text: '#2D3436',
    mutedText: '#7E725A',
    border: '#EADDBB',
    heroGradient: 'linear-gradient(135deg, #FFF8E7 0%, #FFE1A6 100%)',
  },
  ocean: {
    code: STORE_STYLE_CODES.OCEAN,
    name: '海洋风',
    primary: '#3498DB',
    secondary: '#2EC4B6',
    accent: '#FFB84D',
    background: '#EBF5FB',
    surface: '#FFFFFF',
    text: '#263A4A',
    mutedText: '#6D8090',
    border: '#CFE6F2',
    heroGradient: 'linear-gradient(135deg, #EBF5FB 0%, #C8EEF0 100%)',
  },
  fresh: {
    code: STORE_STYLE_CODES.FRESH,
    name: '清新风',
    primary: '#27AE60',
    secondary: '#B8E986',
    accent: '#F7C948',
    background: '#F4FCF8',
    surface: '#FFFFFF',
    text: '#263A2B',
    mutedText: '#6B806F',
    border: '#D7ECDD',
    heroGradient: 'linear-gradient(135deg, #F4FCF8 0%, #DFF5E6 100%)',
  },
  forestFruitTeaCrayon: {
    code: STORE_STYLE_CODES.FOREST_FRUIT_TEA_CRAYON,
    name: '森系水果茶-小白款',
    layoutVersion: 'customer-storefront-v1',
    primary: '#6F9646',
    secondary: '#B8C77A',
    accent: '#F2B94B',
    background: '#FBFAEF',
    surface: '#FFFDF4',
    text: '#4C6040',
    mutedText: '#7A866D',
    border: '#DCE6C7',
    price: '#6F9646',
    heroGradient: 'linear-gradient(180deg, #F9FAEA 0%, #EAF4D8 48%, #F9F2D8 100%)',
    iconNames: {
      location: 'forest-location',
      cart: 'forest-cart',
      checkout: 'forest-checkout',
      delivery: 'forest-delivery',
      sectionLeaf: 'forest-leaf',
    },
    iconUrls: {
      location: '/static/storefront/forest/icons/location.png',
      cart: '/static/storefront/forest/icons/cart.png',
      checkout: '/static/storefront/forest/icons/checkout.png',
      delivery: '/static/storefront/forest/icons/delivery.png',
      sectionLeaf: '/static/storefront/forest/icons/leaf.png',
    },
    imageUrls: {
      heroIllustration: '/static/storefront/forest/hero-forest-tea.jpg',
      mascot: '/static/storefront/forest/mascot.png',
      productPlaceholder: '/static/storefront/forest/product-placeholder.png',
      promoIllustration: '/static/storefront/forest/promo-drink.png',
    },
    banners: [
      {
        id: 'seasonal-fruit',
        imageUrl: '/static/storefront/forest/banner-seasonal.jpg',
        title: '鲜果时令上新',
        subtitle: '当季水果 · 清爽一夏',
        actionText: '立即尝鲜',
        targetCategory: 'recommend',
      },
      {
        id: 'tea-special',
        imageUrl: '/static/storefront/forest/banner-tea.jpg',
        title: '茶香果味特调',
        subtitle: '新鲜现制 · 每日限量',
        actionText: '去点单',
        targetCategory: 'tea',
      },
    ],
    categoryIconLibrary: [
      { key: 'recommend', name: '人气推荐', iconUrl: '/static/storefront/forest/icons/recommend.png', fallbackText: '荐' },
      { key: 'citrus', name: '清爽柠檬', iconUrl: '/static/storefront/forest/icons/citrus.png', fallbackText: '柠' },
      { key: 'grape', name: '多肉葡萄', iconUrl: '/static/storefront/forest/icons/grape.png', fallbackText: '葡' },
      { key: 'mango', name: '香甜芒果', iconUrl: '/static/storefront/forest/icons/mango.png', fallbackText: '芒' },
      { key: 'tea', name: '鲜果茶桶', iconUrl: '/static/storefront/forest/icons/tea.png', fallbackText: '茶' },
      { key: 'extra', name: '加料小料', iconUrl: '/static/storefront/forest/icons/extra.png', fallbackText: '料' },
      { key: 'category1', name: '类目图标 1', iconUrl: '/static/storefront/forest/icons/category-1.png', fallbackText: '类' },
      { key: 'category2', name: '类目图标 2', iconUrl: '/static/storefront/forest/icons/category-2.png', fallbackText: '类' },
    ],
    assetSizes: {
      heroBanner: { width: '750rpx', height: '536rpx' },
      promoBanner: { width: '692rpx', height: '220rpx' },
      categoryIcon: '54rpx',
      locationIcon: '30rpx',
      sectionIcon: '30rpx',
      bottomActionIcon: '32rpx',
      productImage: '144rpx',
      mascot: '120rpx',
      cartMascot: '140rpx',
      cartProductImage: '148rpx',
      orderProductImage: '112rpx',
      profileAvatar: '132rpx',
      menuIcon: '44rpx',
      qtyStepper: '44rpx',
      progressIcon: '44rpx',
      cartHeaderBanner: { width: '692rpx', height: '120rpx' },
      myInviteBanner: { width: '692rpx', height: '124rpx' },
      bottomBarHeight: '128rpx',
      cartBarBottomOffset: '60rpx',
      tabBarReserve: '132rpx',
    },
    pageThemes: {
      home: {
        sectionTitle: '人气推荐',
      },
      cart: {
        headerBanner: {
          imageUrl: '/static/storefront/forest/banner-tea.jpg',
          title: '自然水果 · 新鲜现制',
          subtitle: '清爽果茶，马上带走',
        },
        storeName: '上海环球港店',
        distanceText: '距离您 1.3km',
        emptyTitle: '购物车是空的',
        emptySubtitle: '去首页挑一杯新鲜果茶吧',
      },
      orders: {
        headerBanner: {
          imageUrl: '/static/storefront/forest/banner-seasonal.jpg',
          title: '订单状态',
          subtitle: '新鲜现制，请留意取餐进度',
        },
        emptyTitle: '暂无订单',
        emptySubtitle: '快去点一杯清爽果茶吧',
        statusColors: {
          NEW: '#F2B94B',
          ACCEPTED: '#6F9646',
          PREPARING: '#D98B45',
          READY: '#3F8D5A',
          COMPLETED: '#6F9646',
          REJECTED: '#9EA58F',
        },
      },
      my: {
        greeting: 'Hi~ 欢迎回来！',
        inviteBanner: {
          imageUrl: '/static/storefront/forest/banner-seasonal.jpg',
          title: '邀请好友喝杯果茶',
          subtitle: '一起发现身边的新鲜小店',
          actionText: '立即邀请',
        },
        menuIcons: {
          orders: '/static/storefront/forest/icons/cart.png',
          faq: '/static/storefront/forest/icons/leaf.png',
          service: '/static/storefront/forest/icons/location.png',
          about: '/static/storefront/forest/icons/tea.png',
        },
      },
    },
    copywriting: {
      branchName: '上海环球港店',
      heroEyebrow: '小新の',
      heroTitle: '水果茶屋',
      heroSubtitle: '自然水果 · 新鲜现制',
      promoTitle: '鲜果时令上新',
      promoSubtitle: '当季水果 · 清爽一夏',
      promoActionText: '立即尝鲜',
    },
  },
} as const

export const DEFAULT_STORE_THEME = STORE_THEME_PACKAGES.forestFruitTeaCrayon

// WeChat Mini Program Config
export const WECHAT_CONFIG = {
  APP_NAME: '摊位商城',
  VERSION: '1.0.0',
  SHARE_TITLE: '摊位商城 - 发现身边的好物',
  SHARE_PATH: '/pages/customer/index/index',
  LOCATION_DESC: '你的位置信息将用于展示附近摊位',
} as const

// Pagination Defaults
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
} as const

// Cart Config
export const CART_CONFIG = {
  MAX_ITEMS: 99,
  MAX_PER_ITEM: 10,
} as const
