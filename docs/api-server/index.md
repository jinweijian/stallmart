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

当前初始化服务端以接口契约和测试闭环为主，尚未接入真实 JWT Filter。需要当前用户的接口支持：

- `X-User-Id: <userId>`
- 或 `Authorization: Bearer access-<userId>-...`

未传时默认使用测试用户 `1`。接入真实认证时必须同步本文和 `docs/api-app/index.md`。

管理端商家接口当前使用开发种子摊主：

- `X-User-Id: 2`
- 对应店铺：`storeId = 1`

## Endpoint 列表

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/auth/wechat/login` | 微信登录 |
| `POST` | `/auth/phone/bind` | 绑定手机号 |
| `POST` | `/auth/refresh` | 刷新 token |
| `POST` | `/auth/logout` | 登出 |
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
| `GET` | `/admin/vendor/me/products` | 商家商品列表 |
| `POST` | `/admin/vendor/me/products` | 新增商家商品 |
| `PUT` | `/admin/vendor/me/products/{productId}` | 更新商家商品 |
| `GET` | `/admin/vendor/me/orders` | 商家订单列表 |
| `PUT` | `/admin/vendor/me/orders/{orderId}/{action}` | 商家订单流转，`action` 为 `accept/reject/prepare/ready/complete` |
| `GET` | `/admin/vendor/me/carts` | 商家购物车列表 |
| `GET` | `/admin/vendor/me/users` | 商家用户列表 |
| `GET` | `/admin/vendor/me/decoration` | 商家装修设置 |
| `PUT` | `/admin/vendor/me/decoration` | 更新商家装修设置 |

## 小程序契约

小程序当前仍启用 mock 数据，服务端字段必须保持这些关键字段：

| 页面 | 方法 | 路径 | 关键响应字段 |
| --- | --- | --- | --- |
| 首页 | `GET` | `/stores/{id}` | `id`, `name`, `description`, `avatarUrl`, `styleId`, `styleCode`, `status` |
| 首页 | `GET` | `/stores/{storeId}/products` | `id`, `storeId`, `name`, `description`, `price`, `imageUrl`, `status`, `sortOrder` |
| 确认订单 | `POST` | `/orders` | `id`, `orderNo`, `status`, `confirmCode`, `totalAmount`, `items` |
| 我的订单 | `GET` | `/orders` | `orderNo`, `storeId`, `status`, `confirmCode`, `totalAmount`, `createdAt`, `items` |
| 我的 | `GET` | `/user/profile` | `id`, `nickname`, `avatarUrl`, `phone`, `hasPhone`, `role` |
| 购物车 | `GET` | `/cart` | `id`, `storeId`, `items`, `updatedAt` |

## 管理端共享数据约束

- 管理端新增或更新商品走 `StoreService`，小程序 `/stores/{storeId}/products` 同步读取同一份商品数据。
- 管理端店铺和装修写入走 `StoreService`，小程序 `/stores/{id}` 同步读取同一份店铺数据。
- 订单和购物车管理读取 `OrderService`、`CartService`，后续接入数据库时保持服务接口不变。
