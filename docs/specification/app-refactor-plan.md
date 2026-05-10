# 小程序端重构方案

本文面向 `app/` 的渐进式重构。目标是在小程序仍处于 mock 数据模式、真实 API 联调尚未完全稳定的前提下，降低页面复杂度，收敛请求、主题、购物车和 mock 数据边界，提升后续联调和维护效率。

## 当前观察

已检查：

- `app/README.md`
- `docs/specification/app-module.md`
- `docs/specification/app-pages.md`
- `docs/specification/storefront-decoration.md`
- `docs/guide/testing.md`
- `app/src/pages/customer/index/index.vue`
- `app/src/pages/customer/index/index.scss`
- `app/src/pages/customer/my-orders/my-orders.vue`
- `app/src/pages/customer/confirm-order/confirm-order.vue`
- `app/src/pages/vendor/order-manage/order-manage.vue`
- `app/src/utils/request.ts`
- `app/src/app-config/index.ts`
- `app/src/mock/customer-api.ts`

主要现状：

- 顾客首页 `index.vue` 约 880 行，`index.scss` 约 890 行，页面同时负责店铺加载、主题合并、商品规范化、分类、SKU 弹层、购物车本地存储和模板渲染。
- 多个顾客端页面都直接处理主题变量和页面展示逻辑，存在重复的 ViewModel 转换趋势。
- `app-config/index.ts` 同时承载环境配置、endpoint、设计 token、风格包、类型定义，文件约 440 行。
- `request.ts` 同时处理 mock、URL 拼接、header、token refresh、错误映射和 Taro 请求。
- `mock/customer-api.ts` 体量较大，mock DTO 与真实 DTO 需要持续保持一致。
- 部分页面仍有 `any`、console 调试和硬编码 endpoint 字符串。

## 核心问题与风险

### 1. 页面过重，业务规则和 UI 强耦合

问题：

- 首页内部定义大量接口、规范化函数、主题合并函数和购物车操作。
- SKU 可售判断、默认规格选择、购物车合并规则和商品状态文案都在页面内。

风险：

- 小程序首页继续加搜索、活动、会员或推荐后，页面会更难维护。
- SKU 和购物车规则难以复用到确认订单页。
- 样式和逻辑耦合导致视觉调整容易引起行为回归。

### 2. 主题配置、mock 和真实 API 边界混在一起

问题：

- `app-config/index.ts` 同时定义 API、环境、存储 key、颜色 token、风格包大对象和类型。
- 页面本地有 `getMockStore`、`getMockProducts` fallback，同时 `request.ts` 也支持 mock。

风险：

- mock 与真实 API 数据不一致时，页面可能在 mock 下正常、联调失败。
- 风格包契约修改时，影响范围过大。
- 环境配置和业务配置混杂，未来多商户、多 AppID 或插件化扩展成本高。

### 3. 请求模块职责过多

问题：

- `request.ts` 中包含 refresh 队列、mock 分发、错误消息、Taro.request 封装和类型定义。
- `responseInterceptor` 使用 Promise 包裹 async，逻辑分支较多。

风险：

- 401 并发刷新、mock 命中和真实请求失败混在一起，排查困难。
- 后续加入重试、链路追踪或请求取消时会继续膨胀。
- 页面仍可能绕过 endpoint 常量使用字符串 URL。

### 4. 小程序状态边界不够清晰

问题：

- 购物车仍在首页本地维护并写入 `cart_items`，与 `STORAGE_KEYS.CART` 约定不一致。
- 用户态、主题缓存、购物车和订单确认之间缺少清晰 store/composable 分层。

风险：

- tab 间状态不同步。
- 下单后清空购物车、登录失效清理购物车等跨页面行为容易遗漏。
- 后续从本地购物车切换到服务端购物车时迁移成本高。

## 重构总原则

- 小程序页面只保留生命周期、页面状态组合和模板渲染。
- 业务规则优先抽到 composables、store 或 domain 函数。
- mock 数据只在 mock adapter 中出现，页面不再自带大段 fallback 业务数据。
- Endpoint 和 DTO 类型集中维护，页面使用明确的 API 函数。
- 保持 mock 默认开启的项目约束，不在重构中强行切真实 API。

## 方案一：顾客首页拆分为组合式业务模块

目标：

- 降低 `pages/customer/index/index.vue` 的复杂度。
- 将主题、商品、SKU、购物车规则拆成可复用模块。

建议新增：

```text
app/src/types/
  storefront.ts
  product.ts
  cart.ts
app/src/composables/customer/
  useStorefrontData.ts
  useStorefrontTheme.ts
  useProductSkuSelection.ts
  useLocalCart.ts
app/src/domain/customer/
  product-normalizer.ts
  storefront-theme-merger.ts
  sku-availability.ts
```

职责拆分：

| 模块 | 职责 |
| --- | --- |
| `useStorefrontData` | 加载店铺、商品、规格，处理 loading 和 refresh |
| `useStorefrontTheme` | 生成主题变量、banner、分类和文案 |
| `useProductSkuSelection` | 默认规格、可售判断、选中 SKU、数量限制 |
| `useLocalCart` | 购物车读取、保存、合并、数量和金额计算 |
| `product-normalizer` | 后端 DTO 或 mock DTO 转为页面 Product ViewModel |
| `storefront-theme-merger` | 合并默认风格包、服务端 theme、店铺装修覆盖 |
| `sku-availability` | SKU 可选、禁用和库存规则 |

推荐步骤：

1. 先移动类型定义到 `app/src/types/`，保持页面逻辑不变。
2. 提取纯函数：`normalizeProduct`、`normalizeProductStatus`、`mergeStorefrontTheme`、`normalizeBannerConfigs`、`resolveCategories`。
3. 提取 SKU 选择逻辑，并用测试或最小脚本验证有规格、无规格、售罄 SKU。
4. 提取购物车逻辑到 `useLocalCart`，storage key 改为 `STORAGE_KEYS.CART` 时需要兼容旧 `cart_items` 一次迁移。
5. 首页只保留生命周期调用、组合状态和模板。

验收：

- `index.vue` 不再包含大段 mock 数据和主题合并细节。
- SKU 规则可被商品详情弹层、购物车和确认订单复用。
- 首页仍能在 mock 模式下展示同样数据和交互。

## 方案二：配置、主题和 mock 数据分层

目标：

- 拆解 `app-config/index.ts` 和 `mock/customer-api.ts`，让环境配置、endpoint、主题契约和 mock adapter 各自独立。

建议调整：

```text
app/src/app-config/
  env.ts
  endpoints.ts
  storage-keys.ts
  index.ts
app/src/theme/
  storefront-types.ts
  storefront-packages.ts
  storefront-defaults.ts
app/src/mock/
  customer-api.ts
  fixtures/
    store.ts
    products.ts
    specs.ts
    orders.ts
```

分工：

| 文件 | 职责 |
| --- | --- |
| `env.ts` | `APP_ENV`、`API_BASE_URL`、`ENABLE_API_MOCK` |
| `endpoints.ts` | 所有 REST endpoint |
| `storage-keys.ts` | token、用户、购物车、主题缓存 key |
| `storefront-types.ts` | 主题、分类、banner、尺寸类型 |
| `storefront-packages.ts` | 内置风格包定义 |
| `storefront-defaults.ts` | 默认主题与默认值 |
| `mock/fixtures/*` | mock 数据，不写请求逻辑 |
| `mock/customer-api.ts` | 根据 `RequestOptions` 分发 fixture |

推荐步骤：

1. 先从 `app-config/index.ts` re-export 新文件，保持旧 import 路径兼容。
2. 拆 endpoint 和 storage key，所有页面继续从 `@/config` 或 `@/app-config` 读取。
3. 拆主题类型与内置风格包，减少页面引入配置大对象的副作用。
4. 拆 mock fixtures，并增加脚本校验 mock 关键字段与 API 文档一致。
5. 清理页面内 fallback mock 数据，统一由 mock adapter 或默认空态处理。

验收：

- 新增 endpoint 只需要改 `endpoints.ts` 和 API 文档。
- 主题契约改动集中在 `theme/`。
- mock fixture 可以单独阅读和维护。

## 方案三：请求层和页面 API 封装收敛

目标：

- 将 `request.ts` 拆成小而清晰的请求管线。
- 页面不直接拼接业务 URL，统一调用 API 函数。

建议新增：

```text
app/src/utils/request/
  index.ts
  client.ts
  auth-refresh.ts
  error-mapper.ts
  url-builder.ts
  mock-adapter.ts
app/src/api/
  store-api.ts
  product-api.ts
  order-api.ts
  auth-api.ts
  vendor-api.ts
```

分工：

| 模块 | 职责 |
| --- | --- |
| `client.ts` | Taro.request 包装、header 注入、timeout |
| `auth-refresh.ts` | 401 refresh 队列、失败 logout |
| `error-mapper.ts` | HTTP 和业务错误转统一错误对象 |
| `url-builder.ts` | baseURL、query 参数拼接 |
| `mock-adapter.ts` | mock 开关和 mock response 命中 |
| `store-api.ts` | 店铺、bootstrap、装修相关请求 |
| `product-api.ts` | 商品、规格相关请求 |
| `order-api.ts` | 下单、列表、详情、状态动作 |
| `auth-api.ts` | 微信登录、刷新、手机号绑定 |
| `vendor-api.ts` | 摊主端订单和店铺设置 |

推荐步骤：

1. 先保持 `request<T>`、`get<T>`、`post<T>` 等公开函数签名不变，内部拆文件。
2. 提取 `buildUrl` 和 `mapError`，减少 `responseInterceptor` 分支长度。
3. 提取 refresh 队列，保证并发 401 只触发一次刷新。
4. 增加业务 API 函数，让页面从 `@/api/*` 获取数据。
5. 清理页面中的硬编码 URL，例如摊主端 `'/vendor/orders'`、`'/stores/' + storeId`。

验收：

- 页面不直接调用 `Taro.request`，也不直接拼业务 URL。
- request 管线每个文件职责单一。
- mock 和真实请求使用同一 API 函数入口。

## 推荐执行顺序

1. 先做方案二的配置拆分，风险低，能为后续 import 边界打基础。
2. 再做方案三的请求层拆分和 API 函数封装，减少页面对底层请求的依赖。
3. 最后做方案一的首页拆分，因为它涉及页面行为最多，需要更完整的回归。

## 测试与回归清单

当前小程序基线：

```bash
cd app
npm run lint
npm run test:weapp-assets
```

建议补充：

- `normalizeProduct`：真实 DTO、mock DTO、无 SKU、售罄 SKU。
- `mergeStorefrontTheme`：默认主题、服务端 theme、店铺 decoration 覆盖。
- `sku-availability`：必选规格、无库存、部分组合不可售。
- `useLocalCart`：新增、同 SKU 合并、不同 SKU 分行、旧 key 迁移。
- `url-builder`：无 query、多 query、特殊字符。
- `auth-refresh`：并发 401、刷新成功重试、刷新失败 logout。

人工回归：

- 顾客首页扫码或默认店铺进入。
- 商品详情弹层规格选择和售罄禁用。
- 加入购物车、购物车 tab、确认订单。
- 我的订单下拉刷新。
- 摊主订单管理状态动作。

## 不建议现在做的事

- 不建议在重构中关闭 mock 默认策略。
- 不建议一次性替换所有页面 UI。
- 不建议把所有小程序状态都放入 Pinia，页面局部状态仍应留在 composable。
- 不建议为所有接口提前生成复杂 SDK，先用轻量 API 函数保持可读性。
