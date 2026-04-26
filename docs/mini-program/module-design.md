# 小程序模块设计

本文根据外层 PRD、架构文档、设计系统和当前代码结构整理。它定义小程序端模块边界，后续新增功能必须先确认属于哪个模块。

## 端定位

| 端 | 入口 | 核心价值 |
| --- | --- | --- |
| 顾客端 | 微信小程序 tabBar 和扫码进入 | 扫码进店、浏览商品、购物车、下单、查看订单。 |
| 摊主端 | 同一小程序的 `pages/vendor` 分包 | 查看摊位、接单、拒单、备餐、出餐、设置摊位信息。 |

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
| Static | `src/static/` | 图片、tabBar 图标、默认头像 | 放运行时生成文件 |

## 顾客端业务模块

### 店铺浏览模块

当前页面：

- `pages/customer/index/index`

职责：

- 扫码进入店铺后的首页。
- 展示店铺信息、商品列表、分类、风格化页面。
- 接收扫码参数并加载对应店铺。

依赖：

- `API_ENDPOINTS.STORE_GET_BY_QR`
- `API_ENDPOINTS.STORE_DETAIL`
- `API_ENDPOINTS.PRODUCTS`

约束：

- 店铺信息和商品列表不能硬编码到页面中。
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

### 我的订单模块

当前页面：

- `pages/customer/my-orders/my-orders`

职责：

- 展示当前用户订单列表。
- 支持下拉刷新订单状态。

依赖：

- `API_ENDPOINTS.ORDERS`
- `API_ENDPOINTS.ORDER_DETAIL`

约束：

- 不在顾客端暴露摊主操作按钮。
- 订单状态文案必须与后端状态枚举映射统一。

### 我的模块

当前页面：

- `pages/customer/my/my`

职责：

- 展示用户信息。
- 展示摊主入口。
- 登录、登出、个人状态入口。

依赖：

- `useUserStore`
- `utils/auth.ts`

约束：

- 默认头像使用 `/static/default-avatar.png`。
- 不直接操作 token，必须通过 auth/storage 工具。

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
4. 如涉及 API，在 [state-and-api.md](state-and-api.md) 和 [../backend/api-reference.md](../backend/api-reference.md) 更新 endpoint。
5. 如涉及视觉规则，在 [style-and-interaction.md](style-and-interaction.md) 更新组件或状态。
