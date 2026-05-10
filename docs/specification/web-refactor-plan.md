# 管理端重构方案

本文面向 `web/` 的渐进式重构。目标是在保持现有 Nuxt 路由、API client 和页面行为稳定的前提下，降低页面复杂度，提升类型约束、复用能力和长期维护效率。

## 当前观察

已检查：

- `web/README.md`
- `docs/specification/web.md`
- `docs/guide/testing.md`
- `web/app/api/stallmart-api.ts`
- `web/app/types/admin.ts`
- `web/app/pages/vendor/decoration.vue`
- `web/app/pages/vendor/products.vue`
- `web/app/pages/platform/styles.vue`

主要现状：

- `web/app/pages/vendor/decoration.vue` 约 390 行，页面同时处理数据加载、上传、表单状态、主题预览、保存和模板渲染。
- `web/app/pages/vendor/products.vue` 约 300 行，新增商品表单、SKU 输入解析、图片上传、状态切换和列表展示集中在一个页面。
- `web/app/pages/platform/styles.vue` 约 300 行，风格包 fallback 主题构造、编辑表单和发布/删除动作集中在页面。
- `web/app/api/stallmart-api.ts` 是单文件 API client，包含平台、商家、上传、订单、规格等全部端点。
- `web/app/types/admin.ts` 是单文件类型入口，所有业务类型混在一起。
- 页面里存在较多 `any` 异常处理和动态 `:style`，装修预览与主题契约还没有形成组件边界。

## 核心问题与风险

### 1. 页面组件承担过多业务逻辑

问题：

- 页面直接维护复杂表单、状态切换、上传逻辑和保存后的刷新策略。
- SKU 文本解析、主题 fallback 构造等业务规则写在页面内部。

风险：

- 页面继续增长后，任何视觉调整都可能碰到业务逻辑。
- 单元测试无法直接覆盖表单构造和校验，只能靠人工页面验证。
- 新增编辑页或复用表单时会复制逻辑。

### 2. API client 和类型文件过大

问题：

- `useStallmartApi` 聚合所有接口，调用方无法从命名空间上区分平台、商家、商品、订单、装修。
- `admin.ts` 同时定义会话、店铺、商品、订单、风格包、购物车、用户。

风险：

- 类型变更容易产生跨模块影响。
- API 路径、请求体和响应体没有按业务边界组织。
- 后续插件化或 SaaS 多租户能力扩展时，API 客户端会越来越难维护。

### 3. 主题预览和动态样式边界不清晰

问题：

- 装修页在模板中直接组合颜色、图片、文案和分类预览。
- `:style="{ backgroundColor: ... }"` 分散在页面结构中。

风险：

- 风格包契约调整时，页面和类型要同时大改。
- 预览逻辑不能复用于平台风格包管理。
- 动态颜色可能绕过统一 Tailwind 组件类，造成视觉不一致。

## 重构总原则

- 页面只负责路由级编排和组合组件。
- composable 承载页面状态、表单提交和错误处理。
- API 按业务域拆分，但保留 `useStallmartApi` 兼容导出，降低迁移成本。
- 类型按域拆分，避免一个类型文件成为隐性全局依赖。
- 组件优先表达业务语义，不为了拆而拆。

## 方案一：页面逻辑下沉到 composables

目标：

- 把页面中的表单状态、payload 构造、上传、保存、状态动作抽离为可测试、可复用的 composable。

建议新增：

```text
web/app/composables/vendor/
  useVendorDecorationForm.ts
  useVendorProductCreateForm.ts
  useVendorProductActions.ts
web/app/composables/platform/
  usePlatformStyleForm.ts
```

建议迁移：

| 当前位置 | 迁移目标 | 说明 |
| --- | --- | --- |
| `decoration.vue` 的 `form`、`uploadTarget`、`uploadDecorationImage`、`save` | `useVendorDecorationForm` | 页面只消费状态和动作 |
| `products.vue` 的 `skuRows`、`buildPayload`、`save` | `useVendorProductCreateForm` | SKU 文本解析和校验集中 |
| `products.vue` 的 `setProductStatus` | `useVendorProductActions` | 状态切换统一处理 |
| `styles.vue` 的 `fallbackTheme`、`buildPayload`、`saveStyle` | `usePlatformStyleForm` | 风格包构造规则集中 |

推荐步骤：

1. 先提取纯函数，例如 SKU 文本解析、最低价计算、风格包 payload 构造。
2. 为纯函数增加 Vitest 测试；如当前项目未配置测试框架，可先落文档计划，实际执行时单独引入最小 Vitest 配置。
3. 再把 reactive state 和 action 移入 composable。
4. 最后瘦身页面模板，只保留组件组合和少量页面级数据加载。

验收：

- `vendor/products.vue` 中不再出现 SKU payload 构造细节。
- `vendor/decoration.vue` 中不再直接管理上传目标细节。
- `platform/styles.vue` 中不再内联完整 fallback 主题构造。

## 方案二：组件化商品、装修和风格包工作台

目标：

- 将重复 UI 和业务语义组件化，降低页面模板长度和认知成本。

建议新增：

```text
web/app/components/vendor/
  VendorProductForm.vue
  VendorProductTable.vue
  VendorSkuRowsEditor.vue
  VendorDecorationBasicForm.vue
  VendorDecorationStylePicker.vue
  VendorStorefrontPreview.vue
web/app/components/platform/
  PlatformStyleForm.vue
  PlatformStyleCardGrid.vue
```

组件边界：

| 组件 | 职责 | 输入 | 输出 |
| --- | --- | --- | --- |
| `VendorProductForm` | 新增或编辑商品表单容器 | 分类、规格、初始值、保存中状态 | submit payload |
| `VendorSkuRowsEditor` | SKU 行编辑和基础校验展示 | SKU 行数组、规格列表 | update rows |
| `VendorProductTable` | 商品列表、状态标签、操作按钮 | 商品列表、忙碌状态 | status change |
| `VendorDecorationStylePicker` | 风格包选择 | 风格包列表、当前 styleId | select style |
| `VendorDecorationBasicForm` | Logo、封面、Banner、描述 | 表单值、上传状态 | upload、update |
| `VendorStorefrontPreview` | 小程序装修预览 | 主题、banner、分类、店铺信息 | 无业务写入 |
| `PlatformStyleForm` | 平台风格包新增/编辑表单 | 当前 style、保存状态 | submit、cancel |

注意事项：

- 不要把组件拆成只有一层 DOM 的碎片。
- 组件事件使用业务语义，例如 `@save-product`、`@upload-logo`，不要暴露页面内部字段名。
- Tailwind class 仍在组件内组织，跨页面语义样式继续放在 `app/assets/css/main.css`。

验收：

- 页面文件主要是 `useAsyncData`、composable 调用和组件组合。
- 表单组件能被新增和编辑场景复用。
- 装修预览能被商家装修页和平台风格包页共同复用。

## 方案三：API 与类型按业务域拆分

目标：

- 提升 API client 和类型定义的模块边界，为后续 SaaS、插件化和更多后台模块做准备。

建议新增：

```text
web/app/api/
  client.ts
  auth-api.ts
  platform-api.ts
  vendor-api.ts
  product-api.ts
  order-api.ts
  decoration-api.ts
web/app/types/
  auth.ts
  store.ts
  product.ts
  order.ts
  style.ts
  user.ts
  cart.ts
  admin.ts
```

拆分方式：

- `client.ts` 只负责 baseURL、token、`X-Store-Id`、Result 解包、401 refresh。
- `auth-api.ts` 暴露登录、刷新、当前会话。
- `platform-api.ts` 暴露平台总览、商家、平台风格包。
- `vendor-api.ts` 暴露商家工作台、店铺、用户、购物车。
- `product-api.ts` 暴露商品、分类、规格、上传商品图。
- `order-api.ts` 暴露订单列表、详情、状态动作。
- `decoration-api.ts` 暴露装修读取、保存、装修图片上传。
- `admin.ts` 保留兼容导出，统一 re-export 其他类型。

推荐步骤：

1. 先提取 `createApiRequest`，保持 `useStallmartApi` 返回对象不变。
2. 将 endpoint 按域移动到独立文件，`useStallmartApi` 组合这些域 API。
3. 类型文件按域拆分，先让 `admin.ts` re-export，页面 import 暂时不变。
4. 页面或 composable 再逐步改为从具体域类型导入。

验收：

- 新增一个业务接口时可以明确放到某个 `*-api.ts`。
- `stallmart-api.ts` 变成兼容聚合层，而不是全部接口实现。
- 类型变更影响范围更容易从文件名判断。

## 推荐执行顺序

1. 先做方案三的 API request core 提取，因为它能为后续 composable 降低依赖。
2. 再做方案一，把复杂页面逻辑下沉。
3. 最后做方案二，组件化模板并复用装修预览。

## 测试与回归清单

当前管理端基线：

```bash
cd web
npm run build
```

建议补充：

- `parseSkuRows`：正常输入、中文逗号、空行、无规格值。
- `buildProductPayload`：缺分类、缺主图、缺规格、缺 SKU。
- `buildStylePayload`：新增风格包、编辑风格包、沿用现有主题。
- `createApiRequest`：Result 解包、业务错误、401 refresh 后重试。

人工回归：

- `/auth/login`
- `/vendor/products`
- `/vendor/decoration`
- `/platform/styles`
- `/vendor/orders`

## 不建议现在做的事

- 不建议把所有页面一次性换成全新设计系统。
- 不建议引入全局状态管理承载所有表单状态，页面级表单用 composable 更合适。
- 不建议让管理端维护独立 mock 数据，继续以服务端 dev seed 和 API 为准。
- 不建议为了减少 class 数量把所有样式塞回裸 CSS，跨页面样式只抽稳定语义类。
