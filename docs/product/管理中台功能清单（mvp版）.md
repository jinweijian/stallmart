# 管理中台功能清单（MVP 第一版本）

本文是 StallMart 第一版管理中台 MVP 的产品范围。产品功能以本文为准；接口、路由、权限和工程实现必须遵循现有系统规范：

- 管理端前端位于 `web/`，使用 Nuxt + Vue 3 + Tailwind CSS。
- 服务端 Base URL 为 `/api/v1`，管理端本地开发通过 Nuxt 同源代理请求 `/api/v1`。
- API 统一返回 `Result<T>`，成功码为 `200`。
- 管理端统一使用 `Authorization: Bearer <accessToken>` 鉴权。
- 平台端使用 `ADMIN` 角色，商家端使用 `VENDOR` 角色。
- 小程序端当前仍启用 mock 数据，但管理端写入的商品、店铺和装修数据必须与小程序公开接口保持同一份契约。

相关规范：

- [服务端 API](../api-server/index.md)
- [管理端规范](../specification/web.md)
- [小程序店铺装修规范](../specification/storefront-decoration.md)

## 一、系统概述

管理中台为“平台 + 商家”一体化系统：

| 角色 | 说明 | 默认入口 |
| --- | --- | --- |
| 平台管理员 `ADMIN` | 管理平台整体数据，查看所有商家，并进入单个商家的管理模块 | `/platform/vendors` |
| 商家管理员 `VENDOR` | 只管理自己绑定的店铺、商品、订单、用户和装修 | `/vendor` |

第一版本不做细粒度 RBAC，只做角色隔离和商家归属隔离。权限边界必须由服务端兜底，前端只负责入口展示和交互限制。

## 二、登录与账号体系

### 1. 登录功能

- 账号密码登录。
- 登录成功返回 `accessToken`、`refreshToken`、用户信息、角色和默认入口。
- 前端保存登录态，并在后续请求中携带 `Authorization: Bearer <accessToken>`。
- 登录失败展示明确错误提示。
- token 过期后使用 refresh 接口续期；续期失败返回登录页。

### 2. 角色跳转

| 角色 | 登录后跳转 |
| --- | --- |
| `ADMIN` | `/platform/vendors` |
| `VENDOR` | `/vendor` |

### 3. 账号字段

| 字段 | 说明 |
| --- | --- |
| `id` | 用户 ID |
| `account` | 登录账号，后续接入数据库 |
| `passwordHash` | 加密后的密码，不保存明文 |
| `role` | `ADMIN` 或 `VENDOR` |
| `storeId` | 商家管理员绑定的店铺 ID，仅 `VENDOR` 必填 |
| `status` | `ENABLED / DISABLED` |

本地初始化账号见 [服务端 API - 管理端账号](../api-server/index.md#管理端账号)。

## 三、路由结构

### 平台端路由

| 路由 | 页面 |
| --- | --- |
| `/` | 平台总览 |
| `/platform/vendors` | 商家列表 |
| `/platform/vendors/:id` | 平台进入单个商家模块 |

平台端 MVP 不单独新增全局订单列表和全局商品列表页面；平台查看某个商家的订单、商品、用户、购物车和装修时，先从商家列表进入该商家模块。

### 商家端路由

| 路由 | 页面 |
| --- | --- |
| `/vendor` | 商家工作台 |
| `/vendor/orders` | 订单列表 |
| `/vendor/orders/:id` | 订单详情和备餐操作 |
| `/vendor/products` | 商品列表和创建 |
| `/vendor/products/:id` | 商品详情、编辑、上下架 |
| `/vendor/categories` | 分类管理 |
| `/vendor/specs` | 商品规格管理 |
| `/vendor/store` | 店铺基础信息 |
| `/vendor/decoration` | 店铺装修 |
| `/vendor/users` | 商家关联用户 |
| `/vendor/carts` | 商家购物车 |

商家页面移动端优先，同时必须适配 PC 端运营使用，不能只做窄屏手机壳。

## 四、平台管理端功能

### 1. 平台总览

展示：

- 商家总数。
- 用户总数。
- 订单总数。
- 销售额。
- 商家运营入口。

### 2. 商家管理

商家列表字段：

- 店铺 ID。
- 店铺名称。
- 分类。
- 风格编码。
- 状态。
- 地址。
- 简介。

功能：

- 查看商家列表。
- 进入某个商家模块。
- 平台进入商家模块后，可查看该商家的商品、订单、用户、购物车和装修信息。

第一版本暂不做商家新增、删除、禁用、资质审核；这些进入后续版本。

### 3. 平台视角商家模块

进入 `/platform/vendors/:id` 后展示：

- 商品数量。
- 订单数量。
- 购物车数量。
- 销售额。
- 当前装修风格。
- 关联用户数量。
- 最近订单列表。

平台管理员在该模块中以查看和运营分析为主。涉及会改变商家业务数据的操作，第一版本优先由商家端完成。

## 五、商家管理端功能

### 1. 商家工作台

展示：

- 当前店铺名称。
- 订单数量。
- 商品数量。
- 销售额。
- 最近订单。
- 快捷入口：订单、商品、店铺、装修、用户、购物车。

### 2. 订单管理

订单列表字段：

- 订单号。
- 取餐码。
- 用户 ID。
- 商品摘要。
- 金额。
- 备注。
- 订单状态。
- 下单时间。

功能：

- 查看订单列表。
- 查看订单详情。
- 接单。
- 拒单。
- 开始备餐。
- 待取餐。
- 完成订单。

订单状态流转：

```text
NEW -> ACCEPTED -> PREPARING -> READY -> COMPLETED
NEW -> REJECTED
```

服务端必须校验订单归属和状态流转合法性。

### 3. 商品管理

商品列表字段：

- 商品 ID。
- 商品名称。
- 分类。
- 主图。
- 最低规格价。
- 关联规格数量。
- SKU 数量。
- 描述。
- 状态。
- 排序。

功能：

- 创建商品。
- 编辑商品。
- 查看商品详情。
- 上架。
- 下架。

第一版本商品字段以现有 `ProductDTO` 为准。商品本身不直接售卖，必须绑定分类、主图、规格和至少一个 SKU；列表价格展示 SKU 最低价。销量统计和删除商品进入后续版本。

### 4. 商品创建 / 编辑

基础信息：

- 商品名称。
- 分类。
- 商品主图，管理端通过上传按钮上传，保存 `mainImageUrl`，单图大小上限 10MB。
- 描述。
- 排序。
- 关联规格，至少选择一个规格。
- SKU 价格行，每行包含规格值、价格、库存和状态。

价格状态：

- 商品列表价格由 SKU 最低价计算。
- 状态：`ACTIVE / INACTIVE / SOLD_OUT`。

### 5. 分类管理

第一版本分类按模块维护，商品模块使用 `PRODUCT`。

字段：

- 模块：`PRODUCT / BANNER / DECORATION`。
- 分类名称。
- 排序。
- 状态：`ACTIVE / INACTIVE`。

功能：

- 查看分类列表。
- 点击按钮新增分类。
- 商品创建和编辑时必须从商品模块分类中选择一个分类。

### 6. 商品规格管理

第一版本规格绑定到店铺当前风格包。

字段：

- 规格名称。
- 规格类型：`SIZE / SWEET / SPICE / OTHER`。
- 是否必选。
- 选项列表。

功能：

- 新增规格。
- 编辑规格。
- 删除规格。
- 查看规格列表。

第一版本不自动生成笛卡尔积 SKU，但商品必须手动维护至少一个 SKU。SKU 独立价格和库存已纳入第一版，后续再补批量生成和批量改价。

### 7. 用户管理

用户列表字段：

- 用户 ID。
- 昵称。
- 头像。
- 手机号。
- 手机号授权状态。
- 角色。

功能：

- 查看与当前商家产生订单或购物车关系的用户。

第一版本不做用户详情页、用户标签、会员等级和营销操作。

### 8. 购物车管理

购物车列表字段：

- 购物车 ID。
- 用户 ID。
- 状态。
- 商品摘要。
- 更新时间。

功能：

- 查看当前商家下用户未结算或历史保留的购物车数据。

第一版本不做后台代客改购物车。

### 9. 店铺基础信息

字段：

- 店铺名称。
- Logo URL。
- 封面 URL。
- 简介。
- 状态。

功能：

- 查看店铺信息。
- 更新店铺信息。

### 10. 店铺装修

店铺装修必须配合小程序首页设计，遵循 [小程序店铺装修规范](../specification/storefront-decoration.md)。

第一版本风格：

- `forestFruitTeaCrayon`：森系水果茶。

可配置内容：

- 风格包。
- 颜色主题 `colors`，管理端必须提供取色器和实时预览。
- Logo。
- 封面。
- Banner 列表，管理端必须提供上传、预览、排序入口。
- 首页文案 `copywriting`，按语义字段编辑，不以 key=value 文本作为主交互。
- Icon 文件地址 `iconUrls`，按图标语义展示预览和替换入口。
- 图片资源地址 `imageUrls`，按图片语义展示预览和替换入口。
- 店铺简介。

小程序展示相关内容都必须通过装修配置切换，不允许在小程序页面里按店铺名或风格名硬编码。

## 六、数据隔离规则

### 商家管理员

- 只能访问 `/admin/vendor/me/*`。
- 服务端从 accessToken 解析当前用户。
- 当前用户必须绑定店铺。
- 商品、订单、规格、用户、购物车、装修操作都必须校验店铺归属。

### 平台管理员

- 可访问 `/admin/platform/*`。
- 可查看全部商家。
- 可进入单个商家模块查看该商家运营数据。
- 不通过伪造 `storeId` 获得商家管理员权限。

## 七、通用功能

### 1. 列表页

MVP 第一版列表页优先满足可查看和可操作：

- 表格在 PC 端适配宽屏。
- 移动端允许横向滚动表格。
- 空状态明确展示。

分页、复杂搜索和多条件筛选可后续补充。

### 2. 表单

- 必填字段校验。
- 提交中禁用重复提交。
- 成功后刷新列表或详情。
- 失败展示错误提示。

### 3. 状态管理

- 商品状态：`ACTIVE / INACTIVE / SOLD_OUT`。
- 订单状态：`NEW / ACCEPTED / PREPARING / READY / COMPLETED / REJECTED`。
- 店铺状态：`OPEN / CLOSED / DISABLED`。

## 八、接口设计修订

产品文档中的接口设计必须按当前系统规范执行，不另起一套 `/merchant/*` 或非统一响应协议。

### 1. 基础协议

| 项 | 规范 |
| --- | --- |
| Base URL | `/api/v1` |
| 管理端本地请求 | 浏览器请求 `/api/v1`，由 Nuxt 代理到后端 |
| 认证 | `Authorization: Bearer <accessToken>` |
| 成功码 | `code = 200` |
| 错误码 | 使用 `ErrorCode`，如 `10001/10005/10006` |
| 响应结构 | `Result<T>` |

成功响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1743443200
}
```

### 2. 管理端认证接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/admin/auth/login` | 管理端账号密码登录 |
| `POST` | `/admin/auth/refresh` | 刷新管理端 accessToken |
| `GET` | `/admin/auth/me` | 获取当前管理端登录态 |

### 3. 平台端接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/admin/platform/summary` | 平台总览统计 |
| `GET` | `/admin/platform/vendors` | 商家列表 |
| `GET` | `/admin/platform/vendors/{storeId}/summary` | 平台进入商家模块 |
| `GET` | `/admin/platform/vendors/{storeId}/products` | 平台查看商家商品 |
| `GET` | `/admin/platform/vendors/{storeId}/orders` | 平台查看商家订单 |
| `GET` | `/admin/platform/vendors/{storeId}/carts` | 平台查看商家购物车 |
| `GET` | `/admin/platform/vendors/{storeId}/users` | 平台查看商家关联用户 |

### 4. 商家端接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/admin/vendor/me/summary` | 商家工作台 |
| `GET` | `/admin/vendor/me/store` | 商家店铺信息 |
| `PUT` | `/admin/vendor/me/store` | 更新商家店铺 |
| `GET` | `/admin/vendor/me/categories?module=PRODUCT` | 分类列表 |
| `POST` | `/admin/vendor/me/categories` | 新增分类 |
| `POST` | `/admin/vendor/me/assets/product-image` | 上传商品主图 |
| `POST` | `/admin/vendor/me/assets/decoration-image` | 上传装修图片、Banner 和图标 |
| `GET` | `/admin/vendor/me/products` | 商家商品列表 |
| `POST` | `/admin/vendor/me/products` | 新增商家商品 |
| `GET` | `/admin/vendor/me/products/{productId}` | 商家商品详情 |
| `PUT` | `/admin/vendor/me/products/{productId}` | 更新商家商品 |
| `PUT` | `/admin/vendor/me/products/{productId}/on-sale` | 商品上架 |
| `PUT` | `/admin/vendor/me/products/{productId}/off-sale` | 商品下架 |
| `PUT` | `/admin/vendor/me/products/{productId}/sold-out` | 商品售罄 |
| `GET` | `/admin/vendor/me/orders` | 商家订单列表 |
| `GET` | `/admin/vendor/me/orders/{orderId}` | 商家订单详情 |
| `PUT` | `/admin/vendor/me/orders/{orderId}/{action}` | 商家订单流转 |
| `GET` | `/admin/vendor/me/users` | 商家关联用户 |
| `GET` | `/admin/vendor/me/users/{userId}/orders` | 顾客订单记录 |
| `GET` | `/admin/vendor/me/carts` | 商家购物车 |
| `GET` | `/admin/vendor/me/decoration` | 商家装修设置 |
| `PUT` | `/admin/vendor/me/decoration` | 更新商家装修 |
| `GET` | `/admin/vendor/me/specs` | 商品规格列表 |
| `POST` | `/admin/vendor/me/specs` | 新增商品规格 |
| `PUT` | `/admin/vendor/me/specs/{specId}` | 更新商品规格 |
| `DELETE` | `/admin/vendor/me/specs/{specId}` | 删除商品规格 |

### 5. 小程序共享接口

管理端写入商品、店铺、装修后，小程序公开接口必须读取同一份数据：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/stores/{id}` | 店铺信息和装修配置 |
| `GET` | `/stores/{storeId}/products` | 店铺商品列表 |
| `GET` | `/products/{id}` | 商品详情 |
| `GET` | `/styles` | 风格包列表 |
| `GET` | `/styles/{styleId}/specs` | 风格包规格列表 |

## 九、MVP 不做范围

- 细粒度 RBAC。
- 商家新增、审核、禁用流程。
- 商品销量统计和删除。
- SKU 批量生成和批量改价。
- 平台全局订单页、平台全局商品页。
- 用户详情、会员等级和营销。
- 真实支付和配送系统。
- 小程序关闭 mock 并全面接真实接口。
