import { existsSync, readFileSync, statSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import { resolve } from 'node:path'

const projectRoot = resolve(fileURLToPath(new URL('..', import.meta.url)))
const requiredFiles = [
  'dist/static/default-avatar.png',
  'dist/pages/customer/my/my.wxss',
]
const requiredSourceAssets = [
  'src/static/storefront/forest/hero-forest-tea.jpg',
  'src/static/storefront/forest/banner-seasonal.jpg',
  'src/static/storefront/forest/banner-tea.jpg',
  'src/static/storefront/forest/cover.png',
  'src/static/storefront/forest/preview.png',
  'src/static/storefront/forest/mascot.png',
  'src/static/storefront/forest/product-placeholder.png',
  'src/static/storefront/forest/promo-drink.png',
  'src/static/storefront/forest/icons/cart.png',
  'src/static/storefront/forest/icons/checkout.png',
  'src/static/storefront/forest/icons/citrus.png',
  'src/static/storefront/forest/icons/category-1.png',
  'src/static/storefront/forest/icons/category-2.png',
  'src/static/storefront/forest/icons/delivery.png',
  'src/static/storefront/forest/icons/extra.png',
  'src/static/storefront/forest/icons/grape.png',
  'src/static/storefront/forest/icons/leaf.png',
  'src/static/storefront/forest/icons/location.png',
  'src/static/storefront/forest/icons/mango.png',
  'src/static/storefront/forest/icons/recommend.png',
  'src/static/storefront/forest/icons/tea.png',
  'src/static/tabbar/home.png',
  'src/static/tabbar/home-active.png',
  'src/static/tabbar/cart.png',
  'src/static/tabbar/cart-active.png',
  'src/static/tabbar/order.png',
  'src/static/tabbar/order-active.png',
  'src/static/tabbar/my.png',
  'src/static/tabbar/my-active.png',
]
const sourceStyleFiles = [
  'src/pages/customer/my/my.scss',
  'src/pages/customer/index/index.scss',
  'src/pages/customer/cart/cart.scss',
  'src/pages/customer/confirm-order/confirm-order.scss',
  'src/pages/customer/my-orders/my-orders.scss',
]
const maxDisplayAssetBytes = 512 * 1024
const minNonBlankAssetBytes = 512
const maxTabbarAssetBytes = 40 * 1024

const failures = []

for (const file of requiredFiles) {
  if (!existsSync(resolve(projectRoot, file))) {
    failures.push(`Missing build output: ${file}`)
  }
}

for (const file of requiredSourceAssets) {
  const assetPath = resolve(projectRoot, file)
  if (!existsSync(assetPath)) {
    failures.push(`Missing storefront source asset: ${file}`)
    continue
  }
  const assetBytes = statSync(assetPath).size
  if (/\.(jpg|jpeg|png)$/i.test(file) && assetBytes < minNonBlankAssetBytes) {
    failures.push(`Display asset looks blank or placeholder-sized: ${file}`)
  }
  if (file.includes('/forest/') && /\.(jpg|jpeg|png)$/i.test(file) && assetBytes > maxDisplayAssetBytes) {
    failures.push(`Storefront display asset is too large for mini program local rendering: ${file}`)
  }
  if (file.includes('/tabbar/') && /\.(png)$/i.test(file) && assetBytes > maxTabbarAssetBytes) {
    failures.push(`Tabbar icon exceeds WeChat 40KB limit: ${file}`)
  }
}

const myWxssPath = resolve(projectRoot, 'dist/pages/customer/my/my.wxss')
if (existsSync(myWxssPath)) {
  const myWxss = readFileSync(myWxssPath, 'utf8')
  if (myWxss.includes('[data-v-')) {
    failures.push('customer/my wxss contains scoped data-v selectors that do not match the WeChat runtime output')
  }
}

for (const file of sourceStyleFiles) {
  const sourcePath = resolve(projectRoot, file)
  if (!existsSync(sourcePath)) {
    failures.push(`Missing source style file: ${file}`)
    continue
  }

  const source = readFileSync(sourcePath, 'utf8')
  const pxMatches = source.match(/\b-?\d+(?:\.\d+)?px\b/g) ?? []
  if (pxMatches.length > 0) {
    failures.push(`${file} uses px units (${[...new Set(pxMatches)].join(', ')}); use rpx for mini program sizing`)
  }
}

const appConfigPath = resolve(projectRoot, 'src/app-config/index.ts')
const packageJsonPath = resolve(projectRoot, 'package.json')
const tabbarOptimizerPath = resolve(projectRoot, 'scripts/optimize-tabbar-icons.mjs')
const mockCustomerApiPath = resolve(projectRoot, 'src/mock/customer-api.ts')
const customerIndexVuePath = resolve(projectRoot, 'src/pages/customer/index/index.vue')
const customerIndexScssPath = resolve(projectRoot, 'src/pages/customer/index/index.scss')
const requiredSharedThemeFiles = [
  'src/utils/customer-theme.ts',
]
const customerThemePages = [
  'src/pages/customer/index/index.vue',
  'src/pages/customer/cart/cart.vue',
  'src/pages/customer/confirm-order/confirm-order.vue',
  'src/pages/customer/my-orders/my-orders.vue',
  'src/pages/customer/my/my.vue',
]

if (existsSync(appConfigPath)) {
  const source = readFileSync(appConfigPath, 'utf8')
  const requiredConfigSnippets = [
    'interface StorefrontBannerConfig',
    'interface StorefrontAssetSizes',
    'interface StorefrontCategoryIconConfig',
    'interface StoreThemePagePackages',
    'banners?: ReadonlyArray<StorefrontBannerConfig>',
    'categoryIconLibrary?: ReadonlyArray<StorefrontCategoryIconConfig>',
    'assetSizes?: StorefrontAssetSizes',
    'pageThemes?: StoreThemePagePackages',
    'iconKey?: string',
    "name: '森系水果茶-小白款'",
    "heroBanner: { width: '750rpx', height: '536rpx' }",
    "promoBanner: { width: '692rpx', height: '220rpx' }",
    "categoryIcon: '54rpx'",
    "bottomActionIcon: '32rpx'",
    "cartMascot: '140rpx'",
    "cartProductImage: '148rpx'",
    "orderProductImage: '112rpx'",
    "profileAvatar: '132rpx'",
    "menuIcon: '44rpx'",
    "qtyStepper: '44rpx'",
    "progressIcon: '44rpx'",
    "cartBarBottomOffset: '60rpx'",
    "tabBarReserve: '132rpx'",
    "banner-seasonal.jpg",
    "banner-tea.jpg",
    "STYLE_SPECS: (styleId: string | number) => `/styles/${styleId}/specs`",
  ]

  for (const snippet of requiredConfigSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`src/app-config/index.ts missing storefront contract snippet: ${snippet}`)
    }
  }

  const forbiddenConfigSnippets = [
    'categories?: ReadonlyArray<StorefrontCategoryConfig>',
    'categories: [',
  ]

  for (const snippet of forbiddenConfigSnippets) {
    if (source.includes(snippet)) {
      failures.push(`src/app-config/index.ts still binds concrete categories in the style package: ${snippet}`)
    }
  }
}

if (existsSync(packageJsonPath)) {
  const source = readFileSync(packageJsonPath, 'utf8')
  if (!source.includes('"optimize:tabbar": "node scripts/optimize-tabbar-icons.mjs"')) {
    failures.push('package.json missing optimize:tabbar script for tabBar icon compression')
  }
}

if (!existsSync(tabbarOptimizerPath)) {
  failures.push('Missing tabBar optimizer script: scripts/optimize-tabbar-icons.mjs')
} else {
  const source = readFileSync(tabbarOptimizerPath, 'utf8')
  const requiredOptimizerSnippets = [
    'maxBytes',
    'tabbarFiles',
    'System.Drawing',
    'Get-VisibleBounds',
    'PaddingRatio',
    'sync-weapp-static.mjs',
  ]

  for (const snippet of requiredOptimizerSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`scripts/optimize-tabbar-icons.mjs missing optimizer snippet: ${snippet}`)
    }
  }
}

if (existsSync(mockCustomerApiPath)) {
  const source = readFileSync(mockCustomerApiPath, 'utf8')
  const requiredMockSnippets = [
    "iconKey: 'category1'",
    "iconUrl: '/static/storefront/forest/icons/category-1.png'",
    "iconKey: 'category2'",
    "iconUrl: '/static/storefront/forest/icons/category-2.png'",
    'interface MockProductSkuVO',
    'interface MockSpecVO',
    "status: 'OPEN'",
    "status: 'ACTIVE'",
    "specIds: [1, 2]",
    'skus:',
    'unitPrice: 18',
    'specsText:',
  ]

  for (const snippet of requiredMockSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`src/mock/customer-api.ts missing provided category icon default: ${snippet}`)
    }
  }
}

const appShellConfigPath = resolve(projectRoot, 'src/app.config.ts')
if (existsSync(appShellConfigPath)) {
  const source = readFileSync(appShellConfigPath, 'utf8')
  if (!source.includes("text: '点单'")) {
    failures.push("src/app.config.ts must label the cart tab as 点单")
  }
}

for (const file of requiredSharedThemeFiles) {
  if (!existsSync(resolve(projectRoot, file))) {
    failures.push(`Missing shared customer theme file: ${file}`)
  }
}

for (const file of customerThemePages) {
  const sourcePath = resolve(projectRoot, file)
  if (!existsSync(sourcePath)) {
    failures.push(`Missing customer theme page: ${file}`)
    continue
  }

  const source = readFileSync(sourcePath, 'utf8')
  const requiredPageSnippets = [
    'createCustomerThemeVars',
    ':style="themeVars"',
  ]

  for (const snippet of requiredPageSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`${file} missing shared customer theme snippet: ${snippet}`)
    }
  }
}

if (existsSync(customerIndexVuePath)) {
  const source = readFileSync(customerIndexVuePath, 'utf8')
  const requiredVueSnippets = [
    '<swiper',
    'class="banner-swiper"',
    ':autoplay="bannerSlides.length > 1"',
    'circular',
    'bannerSlides',
    'normalizeBannerConfigs',
    '@change="handleBannerChange"',
    'category.iconKey',
    'mainImageUrl',
    'specIds',
    'skus',
    'selectedProduct',
    'skuGroups',
    'selectedSku',
    'openProductDetail',
    'addSelectedProductToCart',
    'class="product-detail-sheet"',
    "API_ENDPOINTS.STYLE_SPECS",
  ]

  for (const snippet of requiredVueSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`customer/index.vue missing rotating banner snippet: ${snippet}`)
    }
  }
}

if (existsSync(customerIndexScssPath)) {
  const source = readFileSync(customerIndexScssPath, 'utf8')
  const requiredScssSnippets = [
    '--asset-hero-banner-width',
    '--asset-hero-banner-height',
    '--asset-promo-banner-width',
    '--asset-promo-banner-height',
    '--asset-category-icon-size',
    '--asset-bottom-action-icon-size',
    'background-size: contain',
  ]

  for (const snippet of requiredScssSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`customer/index.scss missing forced storefront size token: ${snippet}`)
    }
  }
}

if (failures.length > 0) {
  console.error(failures.join('\n'))
  process.exit(1)
}

console.log('Weapp assets and page styles are present')
