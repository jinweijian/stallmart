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
const maxTabbarAssetBytes = 20 * 1024
const maxTabbarIconPixels = 96

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
    failures.push(`Tabbar icon is too large for stable WeChat DevTools loading: ${file}`)
  }
}

const builtAppJsonPath = resolve(projectRoot, 'dist/app.json')
if (existsSync(builtAppJsonPath)) {
  const appJson = JSON.parse(readFileSync(builtAppJsonPath, 'utf8'))
  const tabBarItems = appJson.tabBar?.list ?? []

  tabBarItems.forEach((item, index) => {
    for (const key of ['iconPath', 'selectedIconPath']) {
      const iconPath = item[key]
      if (!iconPath) {
        failures.push(`dist/app.json tabBar item ${index} missing ${key}`)
        continue
      }

      const builtIconPath = resolve(projectRoot, 'dist', iconPath)
      if (!existsSync(builtIconPath)) {
        failures.push(`dist/app.json tabBar item ${index} ${key} points to missing file: ${iconPath}`)
        continue
      }

      const iconBytes = statSync(builtIconPath).size
      if (iconBytes > maxTabbarAssetBytes) {
        failures.push(`dist/app.json tabBar item ${index} ${key} is too large: ${iconPath}`)
      }

      const { width, height } = readPngSize(builtIconPath)
      if (width > maxTabbarIconPixels || height > maxTabbarIconPixels) {
        failures.push(`dist/app.json tabBar item ${index} ${key} exceeds ${maxTabbarIconPixels}px: ${iconPath}`)
      }
    }
  })
} else {
  failures.push('Missing build output: dist/app.json')
}

function readPngSize(filePath) {
  const header = readFileSync(filePath)
  const signature = header.subarray(0, 8).toString('hex')
  if (signature !== '89504e470d0a1a0a') {
    throw new Error(`${filePath} is not a PNG file`)
  }

  return {
    width: header.readUInt32BE(16),
    height: header.readUInt32BE(20),
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
const appConfigEndpointPath = resolve(projectRoot, 'src/app-config/endpoints.ts')
const packageJsonPath = resolve(projectRoot, 'package.json')
const tabbarOptimizerPath = resolve(projectRoot, 'scripts/optimize-tabbar-icons.mjs')
const requestUtilPath = resolve(projectRoot, 'src/utils/request.ts')
const requestMockAdapterPath = resolve(projectRoot, 'src/utils/request/mock-adapter.ts')
const appEnvPath = resolve(projectRoot, 'src/app-config/env.ts')
const mockDirectoryPath = resolve(projectRoot, 'src/mock')
const customerIndexVuePath = resolve(projectRoot, 'src/pages/customer/index/index.vue')
const customerIndexScssPath = resolve(projectRoot, 'src/pages/customer/index/index.scss')
const customerMyOrdersScssPath = resolve(projectRoot, 'src/pages/customer/my-orders/my-orders.scss')
const customerThemeUtilPath = resolve(projectRoot, 'src/utils/customer-theme.ts')
const customerDomainPaths = [
  resolve(projectRoot, 'src/domain/customer/storefront-theme.ts'),
  resolve(projectRoot, 'src/domain/customer/product-catalog.ts'),
]
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
  const source = [
    appConfigPath,
    appConfigEndpointPath,
  ]
    .filter(path => existsSync(path))
    .map(path => readFileSync(path, 'utf8'))
    .join('\n')
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

const forbiddenMockFiles = [
  requestMockAdapterPath,
  mockDirectoryPath,
]

for (const file of forbiddenMockFiles) {
  if (existsSync(file)) {
    failures.push(`App mock artifact must be removed: ${file.replace(`${projectRoot}/`, '')}`)
  }
}

const mockFreeSourceChecks = [
  {
    path: requestUtilPath,
    snippets: ['resolveMockResponse', 'mock-adapter'],
  },
  {
    path: appEnvPath,
    snippets: ['ENABLE_API_MOCK', 'TARO_APP_ENABLE_API_MOCK'],
  },
  {
    path: customerIndexVuePath,
    snippets: ['createMockStore', 'getMockProducts'],
  },
]

for (const check of mockFreeSourceChecks) {
  if (!existsSync(check.path)) continue
  const source = readFileSync(check.path, 'utf8')
  for (const snippet of check.snippets) {
    if (source.includes(snippet)) {
      failures.push(`${check.path.replace(`${projectRoot}/`, '')} still contains mock hook: ${snippet}`)
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
  const source = [
    customerIndexVuePath,
    ...customerDomainPaths,
  ]
    .filter(path => existsSync(path))
    .map(path => readFileSync(path, 'utf8'))
    .join('\n')
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
    'bottom: calc(var(--asset-tab-bar-reserve, 132rpx) + var(--asset-cart-bar-bottom-offset, 60rpx) + env(safe-area-inset-bottom));',
    'background-size: contain',
  ]

  for (const snippet of requiredScssSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`customer/index.scss missing forced storefront size token: ${snippet}`)
    }
  }
}

if (existsSync(customerMyOrdersScssPath)) {
  const source = readFileSync(customerMyOrdersScssPath, 'utf8')
  const requiredScssSnippets = [
    '--asset-promo-banner-width',
    '--asset-promo-banner-height',
  ]

  for (const snippet of requiredScssSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`customer/my-orders.scss missing order banner size token: ${snippet}`)
    }
  }
}

if (existsSync(customerThemeUtilPath)) {
  const source = readFileSync(customerThemeUtilPath, 'utf8')
  const requiredRuntimeUnitSnippets = [
    'Taro.getEnv() === Taro.ENV_TYPE.WEB',
    'return size',
    'return `${Number(rem.toFixed(4))}rem`',
  ]

  for (const snippet of requiredRuntimeUnitSnippets) {
    if (!source.includes(snippet)) {
      failures.push(`customer-theme.ts missing runtime unit guard for weapp sizing: ${snippet}`)
    }
  }
}

if (failures.length > 0) {
  console.error(failures.join('\n'))
  process.exit(1)
}

console.log('Weapp assets and page styles are present')
