# 小程序模块设计

本文根据外层 PRD、架构文档、设计系统和当前代码结构整理。它定义小程序端模块边界，后续新增功能必须先确认属于哪个模块。

## 端定位

| 端 | 入口 | 核心价值 |
| --- | --- | --- |
| 顾客端 | 微信小程序 tabBar 和扫码进入 | 扫码进店、浏览商品、购物车、下单、查看订单。 |
| 摊主端 | 同一小程序的商家视角和 `pages/vendor` 分包 | 商家视角可在 tabBar 内查看商品详情和接单流转；分包用于摊位总览、订单管理、摊位设置。 |

当前未落地但被 Docker/历史文档引用的管理端，不属于当前小程序模块。

## 当前模块边界

| 模块 | 目录 | 职责 | 不应做什么 |
| --- | --- | --- | --- |
| App Shell | `src/app.*` | 生命周期、页面注册、tabBar、扫码入口事件 | 写具体业务请求 |
| Config | `src/app-config/` | API base URL、endpoint、存储 key、设计 token | 写页面状态 |
| Customer Pages | `src/pages/customer/` | 顾客业务页面 | 处理摊主后台流程 |
| Vendor Pages | `src/pages/vendor/` | 摊主业务页面 | 处理顾客购物车流程 |
| Store | `src/store/` | 跨页面共享状态 | 直接调用页面跳转作为主要业务流 |
| Utils | `src/utils/` | request、auth、storage 等基础能力 | 写具体页面 UI 逻辑 |
| Static | `src/static/` | 图片、tabBar 图标、默认头像 | 放运行时生成文件；H5 构建必须 copy 到 `dist/static/` 并以图片 MIME 返回 |

## 顾客端业务模块

### 店铺浏览模块

当前页面：

- `pages/customer/index/index`

职责：

- 扫码进入店铺后的首页。
- 展示店铺信息、商品列表、分类、商品详情弹层和风格化页面。
- 根据当前身份展示用户购物能力或商家商品查看能力。
- 接收扫码参数并加载对应店铺。

依赖：

- `API_ENDPOINTS.STORE_GET_BY_QR`
- `API_ENDPOINTS.STORE_DETAIL`
- `API_ENDPOINTS.PRODUCTS`

约束：

- 店铺信息和商品列表不能硬编码到页面中。
- 店铺装修必须消费服务端 `decoration` 和本地 `STORE_THEME_PACKAGES` 合并后的配置，颜色、文案、icon、banner、分类 icon 库和展示尺寸都不得散落硬编码。首页轮播 banner 必须由合并后的 `banners` 驱动，头图和轮播默认只展示图片本身，不在图片上追加演示文案或符号层；分类入口必须来自服务端 `decoration.categories`，分类 icon 由分类的 `iconKey` 解析得到，icon 与图片展示大小必须由 `assetSizes` 统一约束。首页需要将合并后的主题缓存给点单、订单、我的三个 tab 使用。完整规则见 [storefront-decoration.md](storefront-decoration.md)。
- 小程序启动时 App Shell 可调用 `API_ENDPOINTS.APP_BOOTSTRAP`，按 AppID 获取当前商家基础配置和主题，成功后写入 `src/utils/customer-theme.ts` 缓存；失败时继续使用缓存或默认主题。
- 商品详情弹层必须从商品 `specIds/skus` 和风格规格接口 `/styles/{styleId}/specs` 生成 SKU 选项；不可售 SKU 不得允许选择。加入购物车时保存商品快照、选中 SKU id、规格文案和最终价格。
- 商家视角下首页不展示购物车、加购、结算和购买数量控件；商品详情仍可查看规格、状态、库存、销量。
- 搜索、分类筛选只处理当前页面视图状态，持久化规则另行设计。

### 购物车模块

当前页面：

- `pages/customer/cart/cart`

职责：

- 展示购物车商品。
- 修改数量。
- 进入确认订单。

建议状态归属：

- 购物车应独立为 `store/cart.ts`。
- Storage key 使用 `STORAGE_KEYS.CART`。

约束：

- 购物车项必须包含商品快照，避免商品后续变更影响已选内容。
- 规格选择只存用户选择的规格快照，不存完整商品定义。
- 顾客端点单页展示风格必须读取 `src/utils/customer-theme.ts` 生成的 CSS 变量，商品图、数量步进器、底部结算栏和原生 tabBar 预留高度必须使用风格包 `assetSizes`。

### 确认订单模块

当前页面：

- `pages/customer/confirm-order/confirm-order`

职责：

- 展示订单确认信息。
- 提交订单。
- 显示确认码或跳转订单列表。

依赖：

- `API_ENDPOINTS.ORDER_CREATE`
- 用户登录态和手机号绑定状态。

约束：

- 下单前必须确认用户已登录。
- 需要手机号时触发手机号绑定流程。
- 不处理真实支付，v1 为线下支付。
- 顾客端确认订单页必须读取 `src/utils/customer-theme.ts` 生成的 CSS 变量，复用 `pageThemes.cart.headerBanner` 作为顶部风格 banner，商品图和底部提交栏尺寸由风格包 `assetSizes` 驱动。

### 我的订单模块

当前页面：

- `pages/customer/my-orders/my-orders`

职责：

- 展示当前用户订单列表。
- 支持下拉刷新订单状态。
- 商家视角下同一页面切换为接单管理页。

依赖：

- `API_ENDPOINTS.ORDERS`
- `API_ENDPOINTS.ORDER_DETAIL`

约束：

- 不在顾客端暴露摊主操作按钮。
- 只有当前身份为商家视角时才展示接单、拒单、备餐、待取餐、完成等摊主操作。
- 订单状态文案必须与后端状态枚举映射统一。
- 顾客端订单页展示风格必须读取 `src/utils/customer-theme.ts` 生成的 CSS 变量，顶部 banner、状态色、进度 icon 和底部 tabBar 预留由风格包 `pageThemes.orders` 与 `assetSizes` 驱动。
- 订单卡必须常显取餐码，展开状态只用于完整明细和操作按钮；列表底部必须保留足够空间，避免最后一张订单被原生 tabBar 遮挡。
- 商家视角订单列表使用 `API_ENDPOINTS.VENDOR_ORDERS` 或等价 `/vendor/orders` 数据源，状态流转必须遵循 `NEW -> ACCEPTED -> PREPARING -> READY -> COMPLETED`，拒单只能从新订单或指定可拒绝状态进入。

### 我的模块

当前页面：

- `pages/customer/my/my`

职责：

- 展示用户信息。
- 展示商家/用户视角切换和摊主入口。
- 登录、登出、个人状态入口。

依赖：

- `useUserStore`
- `utils/auth.ts`

约束：

- 默认头像使用 `/static/default-avatar.png`。
- 不直接操作 token，必须通过 auth/storage 工具。
- 我的页展示风格必须读取 `src/utils/customer-theme.ts` 生成的 CSS 变量。当前只展示现有功能入口，不新增会员、优惠券、卡包、礼品、成长值。
- 当前身份视角必须本地持久化；切回用户视角后购物车、下单、我的订单与普通用户一致。

## 摊主端业务模块

### 我的摊位模块

当前页面：

- `pages/vendor/my-stall/my-stall`

职责：

- 展示摊位基本信息和今日数据。
- 进入订单管理和摊位设置。

依赖：

- 店铺详情 API。
- 订单列表 API。

约束：

- 店铺更新 endpoint 必须在 `API_ENDPOINTS` 中明确声明。
- 摊主权限必须由后端校验，前端只做入口展示。

### 订单管理模块

当前页面：

- `pages/vendor/order-manage/order-manage`

职责：

- 查看新订单、处理中订单、待取餐订单。
- 执行接单、拒单、备餐、待取餐、完成。

依赖：

- `API_ENDPOINTS.ORDER_ACCEPT`
- `API_ENDPOINTS.ORDER_REJECT`
- `API_ENDPOINTS.ORDER_PREPARE`
- `API_ENDPOINTS.ORDER_READY`
- `API_ENDPOINTS.ORDER_COMPLETE`

约束：

- 状态流转必须遵循 `NEW -> ACCEPTED -> PREPARING -> READY -> COMPLETED`，拒单只能从新订单或指定可拒绝状态进入。
- 前端按钮禁用态必须与状态机一致。
- 后端必须做最终权限和状态校验。

### 摊位设置模块

当前页面：

- `pages/vendor/stall-settings/stall-settings`

职责：

- 编辑店铺名称、头像、简介等基础信息。

当前状态：

- 历史 Review 记录该页面可能仍是占位或未完整实现，修改前先确认当前代码。

约束：

- 表单字段、校验、保存状态要与 `style-and-interaction.md` 一致。
- 修改店铺信息后要刷新 `my-stall` 展示。

## 共享基础模块

### 请求模块

文件：

- `src/utils/request.ts`

职责：

- 拼接 base URL。
- 注入请求头。
- 处理统一响应。
- 处理 401 token refresh。

强制约束：

- 成功码必须与后端 `Result.success` 保持一致。
- 不允许页面直接调用 `Taro.request` 发业务请求。

### 认证模块

文件：

- `src/utils/auth.ts`
- `src/utils/storage.ts`
- `src/store/user.ts`

职责：

- 微信登录。
- token 存储和刷新。
- 手机号绑定。
- 登出清理。
- 用户态持久化。

强制约束：

- Access Token 和 Refresh Token 的 key 必须来自 `STORAGE_KEYS`。
- 登出必须清理用户信息、token 和购物车。
- 401 刷新失败必须触发统一 logout 流程。

## 新模块新增流程

新增小程序模块时必须：

1. 在 `app.config.ts` 注册页面或分包。
2. 在本文件中新增模块说明。
3. 在 [page-map.md](page-map.md) 中新增页面职责和跳转关系。
4. 如涉及 API，在 [state-and-api.md](state-and-api.md) 和 [../api-server/index.md](../api-server/index.md) 更新 endpoint。
5. 如涉及视觉规则，在 [style-and-interaction.md](style-and-interaction.md) 更新组件或状态。
