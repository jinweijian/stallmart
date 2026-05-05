# 顾客端森系水果茶风格包 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将森系水果茶风格包从首页扩展到首页、点单、订单、我的四个顾客端 tab，并强制统一 icon、图片和底部栏展示尺寸。

**Architecture:** `src/app-config/index.ts` 定义完整风格包契约；`src/utils/customer-theme.ts` 负责主题持久化和 CSS 变量生成；各页面只消费共享变量和页面配置。静态脚本 `scripts/verify-weapp-assets-and-styles.mjs` 作为契约测试，防止尺寸和风格字段回退。

**Tech Stack:** Taro 4、Vue 3、TypeScript、SCSS、微信小程序 `swiper` 和本地静态 PNG。

---

### Task 1: 契约测试

**Files:**
- Modify: `app/scripts/verify-weapp-assets-and-styles.mjs`

- [ ] **Step 1: Write the failing test**

添加断言：

```js
const requiredConfigSnippets = [
  'interface StoreThemePagePackages',
  'pageThemes?: StoreThemePagePackages',
  "cartProductImage: '148rpx'",
  "orderProductImage: '112rpx'",
  "profileAvatar: '132rpx'",
  "menuIcon: '44rpx'",
  "qtyStepper: '44rpx'",
  "progressIcon: '44rpx'",
  "tabBarReserve: '132rpx'",
]
```

并检查：

```js
const requiredSharedThemeFiles = ['src/utils/customer-theme.ts']
const customerThemePages = [
  'src/pages/customer/index/index.vue',
  'src/pages/customer/cart/cart.vue',
  'src/pages/customer/my-orders/my-orders.vue',
  'src/pages/customer/my/my.vue',
]
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd app; npm.cmd run test:weapp-assets`

Expected: FAIL，提示缺少 `StoreThemePagePackages` 或 `src/utils/customer-theme.ts`。

### Task 2: 共享主题契约

**Files:**
- Modify: `app/src/app-config/index.ts`
- Create: `app/src/utils/customer-theme.ts`

- [ ] **Step 1: Implement config contract**

扩展 `StorefrontAssetSizes` 和 `StoreThemePackage`，新增 `StoreThemePagePackages`。

- [ ] **Step 2: Implement theme utility**

`customer-theme.ts` 导出：

```ts
export const CUSTOMER_THEME_STORAGE_KEY = 'customer_theme_package'
export function persistCustomerTheme(theme: StoreThemePackage): void
export function getCurrentCustomerTheme(): StoreThemePackage
export function createCustomerThemeVars(theme: StoreThemePackage): Record<string, string>
```

- [ ] **Step 3: Run contract test**

Run: `cd app; npm.cmd run test:weapp-assets`

Expected: PASS。

### Task 3: 四个 tab 页面接入共享主题

**Files:**
- Modify: `app/src/pages/customer/index/index.vue`
- Modify: `app/src/pages/customer/cart/cart.vue`
- Modify: `app/src/pages/customer/my-orders/my-orders.vue`
- Modify: `app/src/pages/customer/my/my.vue`
- Modify: `app/src/pages/customer/index/index.scss`
- Modify: `app/src/pages/customer/cart/cart.scss`
- Modify: `app/src/pages/customer/my-orders/my-orders.scss`
- Modify: `app/src/pages/customer/my/my.scss`

- [ ] **Step 1: Home persists merged theme**

首页使用 `createCustomerThemeVars()`，加载店铺后调用 `persistCustomerTheme(currentTheme.value)`。

- [ ] **Step 2: Cart uses cached theme**

购物车根节点绑定 `themeVars`，顶部使用 `pageThemes.cart.headerBanner`，商品图和数量按钮尺寸走 CSS 变量。

- [ ] **Step 3: Orders uses cached theme**

订单页根节点绑定 `themeVars`，状态色和展开详情卡片走森系 token。

- [ ] **Step 4: My uses cached theme**

我的页根节点绑定 `themeVars`，头像、菜单 icon、邀请 banner 使用统一尺寸；不新增会员、优惠券、卡包、礼品、成长值入口。

- [ ] **Step 5: Run checks**

Run: `cd app; npm.cmd run test:weapp-assets`

Expected: PASS。

### Task 4: 文档同步与构建验证

**Files:**
- Modify: `docs/specification/storefront-decoration.md`
- Modify: `docs/specification/app-style.md`
- Modify: `docs/specification/app-pages.md`
- Modify: `docs/specification/app-module.md`
- Modify: `docs/guide/testing.md`

- [ ] **Step 1: Update docs**

补充跨页风格包、统一 icon 尺寸、底部 tabBar 预留和首页轮播规则。

- [ ] **Step 2: Verify**

Run:

```bash
cd app
npm.cmd run test:weapp-assets
npm.cmd run lint
npm.cmd run build:weapp
git diff --check
```

Expected: 所有命令通过；lint/build 若只有既有 warning，需要在最终说明中标明。

### Task 5: 分类 icon 库改造

**Files:**
- Modify: `app/src/app-config/index.ts`
- Modify: `app/src/pages/customer/index/index.vue`
- Modify: `app/src/mock/customer-api.ts`
- Modify: `server/src/main/java/com/stallmart/product/dto/CategoryDTO.java`
- Modify: `server/src/main/java/com/stallmart/product/dto/CategoryUpsertParams.java`
- Modify: `server/src/main/java/com/stallmart/style/dto/StorefrontThemeDTO.java`
- Modify: `server/src/main/java/com/stallmart/store/dto/StoreDecorationDTO.java`
- Modify: `server/src/main/java/com/stallmart/store/internal/service/StoreServiceImpl.java`
- Modify: `web/app/types/admin.ts`
- Modify: `web/app/pages/vendor/categories.vue`
- Modify: `web/app/pages/vendor/decoration.vue`

- [ ] **Step 1: Write failing contract checks**

`app/scripts/verify-weapp-assets-and-styles.mjs` 必须检查 `categoryIconLibrary`、`iconKey`，并检查风格包不再声明 `categories?: ReadonlyArray<StorefrontCategoryConfig>`。

- [ ] **Step 2: Run checks and verify RED**

Run: `cd app; npm.cmd run test:weapp-assets`

Expected: FAIL，提示缺少 `categoryIconLibrary` 和 `iconKey`。

- [ ] **Step 3: Implement**

风格包只提供 icon 库，服务端分类保存 `iconKey`，装修 DTO 返回 icon 库和实际分类列表，小程序首页只渲染分类数据。

- [ ] **Step 4: Verify**

Run:

```bash
cd app
npm.cmd run test:weapp-assets
cd ../server
./gradlew test
cd ../web
npm.cmd run build
```
