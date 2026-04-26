# API 说明

## 基础信息

- Base URL: `/api/v1`
- 本地开发地址: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## 统一响应

后端 `Result<T>` 当前成功响应为：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1743443200
}
```

错误响应为：

```json
{
  "code": 401,
  "message": "token_missing",
  "data": null,
  "timestamp": 1743443200
}
```

## 认证

非公开接口需要：

```http
Authorization: Bearer <access_token>
```

当前 `JwtAuthFilter` 放行：

- `/api/v1/stores/qr/**`
- `/api/v1/auth/**`
- `/api/v1/styles/**`
- `/swagger-ui/**`
- `/api-docs/**`
- `/v3/api-docs/**`

## Endpoint 列表

| 方法 | 路径 | 说明 | 认证 |
| --- | --- | --- | --- |
| `POST` | `/auth/wechat/login` | 微信登录 | 当前公开 |
| `POST` | `/auth/phone/bind` | 绑定手机号 | 当前公开路径下，但方法读取 `userId`，需要复核 |
| `POST` | `/auth/refresh` | 刷新 token | 当前公开 |
| `POST` | `/auth/logout` | 登出 | 当前公开路径下，但方法读取 `userId`，需要复核 |
| `GET` | `/user/profile` | 获取用户资料 | 需要 token |
| `PUT` | `/user/profile` | 更新用户资料 | 需要 token |
| `GET` | `/stores/{id}` | 获取店铺信息 | 需要 token |
| `GET` | `/stores/qr/{qrCode}` | 扫码进店 | 当前公开 |
| `GET` | `/stores/{storeId}/products` | 获取店铺商品列表 | 需要 token |
| `GET` | `/styles` | 获取风格列表 | 当前公开 |
| `GET` | `/styles/{id}` | 获取风格详情 | 当前公开 |
| `GET` | `/styles/{styleId}/specs` | 获取风格规格列表 | 当前公开 |
| `POST` | `/orders` | 创建订单 | 需要 token |
| `GET` | `/orders/{id}` | 获取订单详情 | 需要 token |
| `GET` | `/orders` | 获取我的订单 | 需要 token |
| `PUT` | `/orders/{id}/accept` | 摊主接单 | 需要 token |
| `PUT` | `/orders/{id}/reject` | 摊主拒单 | 需要 token |
| `PUT` | `/orders/{id}/prepare` | 订单备餐中 | 需要 token |
| `PUT` | `/orders/{id}/ready` | 订单待取餐 | 需要 token |
| `PUT` | `/orders/{id}/complete` | 完成订单 | 需要 token |

## 前后端一致性问题

当前小程序请求封装在 `mini-program/src/utils/request.ts` 中判断 `data.code === 0` 才算成功，而后端 `Result.success` 返回 `code === 200`。两边必须统一，否则所有成功请求都会被前端当作失败处理。

当前小程序还引用了未定义 endpoint：

- `API_ENDPOINTS.USER_BIND_PHONE`
- `API_ENDPOINTS.STORE_INFO`

这些问题需要在联调前处理。
