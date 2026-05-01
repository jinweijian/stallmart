# 服务端 API

## 基础信息

- Base URL: `/api/v1`
- 本地开发地址: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/api/v1/v3/api-docs`
- Healthcheck: `http://localhost:8080/api/v1/actuator/health`

## 统一响应

成功响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1743443200
}
```

错误响应：

```json
{
  "code": 10004,
  "message": "not_found",
  "data": null,
  "timestamp": 1743443200
}
```

## 当前认证状态

当前初始化服务端以接口契约和测试闭环为主，管理端已经使用 JWT accessToken。需要当前用户的接口支持：

- `X-User-Id: <userId>`
- 或 `Authorization: Bearer <JWT access token>`
- 兼容旧测试 token: `Authorization: Bearer access-<userId>-...`

未传时默认使用测试用户 `1`，仅用于小程序契约测试。管理端不得依赖默认用户，必须携带 `Authorization`。

管理端商家接口当前使用开发种子摊主：

- `Authorization: Bearer <vendor accessToken>`
- 对应店铺：`storeId = 1`

## 管理端账号

当前为本地初始化账号，后续接入数据库后需要迁移为账号表和密码哈希：

| 角色 | 账号 | 密码 | 登录后入口 |
| --- | --- | --- | --- |
| 平台管理员 | `platform` | `platform123` | `/platform/vendors` |
| 商家管理员 | `vendor` | `vendor123` | `/vendor` |

权限边界：

- `ADMIN` 可访问 `/admin/platform/*`，用于查看全部商家并进入单个商家模块。
- `VENDOR` 只能访问 `/admin/vendor/me/*`，服务端按 accessToken 解析当前商家并绑定自己的店铺。
- 商家订单、商品、规格、装修操作都会校验资源归属。
- 管理端鉴权失败返回 HTTP `401`，越权返回 HTTP `403`，业务码分别沿用 `10001/10002/10003/10006` 与 `10005`。

## Endpoint 列表

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/auth/wechat/login` | 微信登录 |
| `POST` | `/auth/phone/bind` | 绑定手机号 |
| `POST` | `/auth/refresh` | 刷新 token |
| `POST` | `/auth/logout` | 登出 |
| `POST` | `/admin/auth/login` | 管理端账号密码登录 |
| `POST` | `/admin/auth/refresh` | 管理端刷新 accessToken |
| `GET` | `/admin/auth/me` | 获取当前管理端登录态 |
| `GET` | `/user/profile` | 获取用户资料 |
| `PUT` | `/user/profile` | 更新用户资料 |
| `GET` | `/stores/{id}` | 获取店铺信息 |
| `PUT` | `/stores/{id}` | 更新店铺信息 |
| `GET` | `/stores/qr/{qrCode}` | 扫码进店 |
| `GET` | `/stores/{storeId}/products` | 获取店铺商品列表 |
| `GET` | `/products/{id}` | 获取商品详情 |
| `GET` | `/styles` | 获取风格列表 |
| `GET` | `/styles/{id}` | 获取风格详情 |
| `GET` | `/styles/{styleId}/specs` | 获取风格规格列表 |
| `POST` | `/orders` | 创建订单 |
| `GET` | `/orders/{id}` | 获取订单详情 |
| `GET` | `/orders` | 获取我的订单 |
| `GET` | `/orders/counts` | 获取我的订单统计 |
| `PUT` | `/orders/{id}/accept` | 摊主接单 |
| `PUT` | `/orders/{id}/reject` | 摊主拒单 |
| `PUT` | `/orders/{id}/prepare` | 订单备餐中 |
| `PUT` | `/orders/{id}/ready` | 订单待取餐 |
| `PUT` | `/orders/{id}/complete` | 完成订单 |
| `GET` | `/cart` | 获取当前用户购物车 |
| `POST` | `/cart/items` | 加入购物车 |
| `DELETE` | `/cart/stores/{storeId}` | 清空当前用户在指定店铺的购物车 |
| `GET` | `/admin/platform/summary` | 平台总览统计 |
| `GET` | `/admin/platform/vendors` | 平台商家列表 |
| `GET` | `/admin/platform/vendors/{storeId}/summary` | 平台进入商家模块 |
| `GET` | `/admin/platform/vendors/{storeId}/products` | 平台查看商家商品 |
| `GET` | `/admin/platform/vendors/{storeId}/orders` | 平台查看商家订单 |
| `GET` | `/admin/platform/vendors/{storeId}/carts` | 平台查看商家购物车 |
| `GET` | `/admin/platform/vendors/{storeId}/users` | 平台查看商家关联用户 |
| `GET` | `/admin/vendor/me/summary` | 商家 H5 工作台 |
| `GET` | `/admin/vendor/me/store` | 商家店铺信息 |
| `PUT` | `/admin/vendor/me/store` | 更新商家店铺 |
| `GET` | `/admin/vendor/me/categories?module=PRODUCT` | 商家模块分类列表 |
| `POST` | `/admin/vendor/me/categories` | 新增商家模块分类 |
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
| `PUT` | `/admin/vendor/me/orders/{orderId}/{action}` | 商家订单流转，`action` 为 `accept/reject/prepare/ready/complete` |
| `GET` | `/admin/vendor/me/carts` | 商家购物车列表 |
| `GET` | `/admin/vendor/me/users` | 商家用户列表 |
| `GET` | `/admin/vendor/me/users/{userId}/orders` | 商家查看顾客订单记录 |
| `GET` | `/admin/vendor/me/decoration` | 商家装修设置 |
| `PUT` | `/admin/vendor/me/decoration` | 更新商家装修设置 |
| `GET` | `/admin/vendor/me/specs` | 商家商品规格列表 |
| `POST` | `/admin/vendor/me/specs` | 新增商品规格 |
| `PUT` | `/admin/vendor/me/specs/{specId}` | 更新商品规格 |
| `DELETE` | `/admin/vendor/me/specs/{specId}` | 删除商品规格 |

## 小程序契约

小程序当前仍启用 mock 数据，服务端字段必须保持这些关键字段：

| 页面 | 方法 | 路径 | 关键响应字段 |
| --- | --- | --- | --- |
| 首页 | `GET` | `/stores/{id}` | `id`, `name`, `description`, `avatarUrl`, `styleId`, `styleCode`, `status`, `decoration` |
| 首页 | `GET` | `/stores/{storeId}/products` | `id`, `storeId`, `name`, `description`, `price`, `imageUrl`, `status`, `sortOrder` |
| 确认订单 | `POST` | `/orders` | `id`, `orderNo`, `status`, `confirmCode`, `totalAmount`, `items` |
| 我的订单 | `GET` | `/orders` | `orderNo`, `storeId`, `status`, `confirmCode`, `totalAmount`, `createdAt`, `items` |
| 我的 | `GET` | `/user/profile` | `id`, `nickname`, `avatarUrl`, `phone`, `hasPhone`, `role` |
| 购物车 | `GET` | `/cart` | `id`, `storeId`, `items`, `updatedAt` |

## 管理端共享数据约束

- 管理端新增或更新商品走 `StoreService`，小程序 `/stores/{storeId}/products` 同步读取同一份商品数据。
- 商品必须绑定 `categoryId`、主图、至少一个 `specId` 和至少一个 SKU；商品列表 `price` 为 SKU 最低价，单独商品价格不作为售卖价格。
- 商品主图通过 `/admin/vendor/me/assets/product-image` 上传，返回 `url` 后写入商品 `mainImageUrl`，单图大小上限为 10MB。
- 分类按模块维护，商品分类使用 `PRODUCT` 模块；商品创建和编辑必须选择当前店铺的商品分类。
- 管理端店铺和装修写入走 `StoreService`，小程序 `/stores/{id}` 同步读取同一份店铺数据和 `decoration` 装修配置。
- 订单和购物车管理读取 `OrderService`、`CartService`，后续接入数据库时保持服务接口不变。

## 店铺装修契约

`GET /stores/{id}` 与 `GET /admin/vendor/me/decoration` 都会返回小程序可消费的装修字段：

| 字段 | 说明 |
| --- | --- |
| `layoutVersion` | 小程序首页布局版本，当前为 `customer-storefront-v1`。 |
| `colors` | 主色、副色、背景、文字、边框、价格等 token。 |
| `iconNames/iconUrls` | 语义 icon 名称和文件地址。 |
| `imageUrls` | 首页头图、吉祥物、商品占位图、活动插画等文件地址。 |
| `copywriting` | 首页可切换文案。 |
| `categories` | 左侧分类入口，含 icon 名称、地址和文字兜底。 |
| `banners` | 店铺 banner 列表。 |

详细标准见 [../specification/storefront-decoration.md](../specification/storefront-decoration.md)。

管理端装修页必须以可视化配置为主：颜色用色板/取色器，Logo、封面、Banner、图标和主题图片通过上传按钮替换并展示预览，文案按语义字段编辑。仅在必要兜底时展示文件地址。
