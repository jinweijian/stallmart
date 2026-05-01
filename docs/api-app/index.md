# 小程序状态与 API 规范

本文约束小程序状态、Storage、请求封装、认证刷新和 endpoint 管理。

## 配置入口

| 文件 | 职责 |
| --- | --- |
| `src/app-config/index.ts` | API base URL、endpoint、Storage key、颜色、分页、购物车配置。 |
| `src/utils/request.ts` | Taro request 统一封装。 |
| `src/utils/auth.ts` | 微信登录、手机号绑定、token refresh、logout。 |
| `src/utils/storage.ts` | Storage 类型安全封装和 token 快捷方法。 |
| `src/store/user.ts` | 用户状态和登录态。 |

## API base URL

当前配置：

| 环境 | 地址 |
| --- | --- |
| development | `http://localhost:8080/api/v1` |
| production | `https://api.stallmart.com/api/v1` |

约束：

- 页面中禁止硬编码完整 API 地址。
- API base URL 只允许从 `API_BASE_URL` 获取。
- 切换环境必须通过配置，不在页面里判断环境。
- 微信小程序 request 合法域名未配置前，`ENABLE_API_MOCK` 保持开启，业务请求由 `src/mock/customer-api.ts` 返回后端契约形状的 mock 数据，不触发真实网络请求。

## endpoint 命名规则

简单字符串 endpoint：

```ts
AUTH_WECHAT_LOGIN: '/auth/wechat/login'
ORDERS: '/orders'
```

带参数 endpoint：

```ts
STORE_DETAIL: (id: string) => `/stores/${id}`
ORDER_DETAIL: (id: string) => `/orders/${id}`
```

约束：

- endpoint key 使用 UPPER_SNAKE_CASE。
- 不允许同名 key 既是字符串又是函数。
- 函数式 endpoint 的参数名称必须与后端路径变量一致。
- endpoint 新增或修改必须同步 [../api-server/index.md](../api-server/index.md)。

当前已知问题：

- `USER_BIND_PHONE` 被代码引用但未定义，应改用或补齐 `AUTH_BIND_PHONE`。
- `STORE_INFO` 被代码引用但未定义，应改用或补齐 `STORE_DETAIL`。

## 请求封装规则

所有业务请求必须通过：

```ts
request()
get()
post()
put()
del()
patch()
```

禁止页面直接调用 `Taro.request` 发业务请求，除非是在 `utils/request.ts` 或底层基础设施中。

统一请求头：

- `Content-Type: application/json`
- `X-Client-Version`
- `X-Platform: weapp`
- `Authorization: Bearer <token>`，除非 `skipAuth: true`

## 成功码一致性

当前冲突：

- 小程序 `request.ts` 已兼容 `data?.code === 200` 与历史 `0`。
- 后端 `Result.success` 返回 `code === 200`。
- 外层历史 PRD/API 设计倾向 `code: 0`，当前代码事实是 `code: 200`。

强制要求：

- 后续新增接口按后端当前事实优先返回 `code: 200`。
- 修复时同步更新 [../api-server/index.md](../api-server/index.md) 和 [../specification/project-health.md](../specification/project-health.md)。
- 修复后增加请求封装测试或最小联调验证。

## 当前顾客端 mock 契约

在 `ENABLE_API_MOCK = true` 时，以下接口由 `src/mock/customer-api.ts` 直接返回，不经过 `Taro.request`：

| 方法 | 路径 | 页面 |
| --- | --- | --- |
| `GET` | `/stores/{id}` | 首页店铺信息和风格包。 |
| `GET` | `/stores/{storeId}/products` | 首页商品列表和加购。 |
| `GET` | `/orders` | 我的订单。 |
| `POST` | `/orders` | 确认订单提交。 |
| `PUT` | `/orders/{id}/reject` | 我的订单取消。 |
| `GET` | `/orders/counts` | 我的页订单统计。 |
| `GET` | `/user/profile` | 我的页用户资料。 |
| `GET` | `/cart` | 购物车列表，后端已提供接口；当前小程序仍使用本地 mock/Storage。 |
| `POST` | `/cart/items` | 加入购物车，后端已提供接口；当前小程序仍使用本地 mock/Storage。 |
| `DELETE` | `/cart/stores/{storeId}` | 清空店铺购物车，后端已提供接口；当前小程序仍使用本地 mock/Storage。 |

页面内使用 ViewModel；后端字段进入页面前必须做适配，例如：

- 商品 `price/imageUrl/status(active/off_sale/sold_out)` 转为 `basePrice/image/status(ACTIVE/INACTIVE/SOLD_OUT)`。
- 订单 `pending/accepted/preparing/ready/completed/rejected` 转为页面状态 `NEW/ACCEPTED/PREPARING/READY/COMPLETED/REJECTED`。
- 店铺 `avatarUrl/styleId/status` 转为首页展示所需的 `logo/styleCode/isOpen`。

## Storage 规范

Storage key 必须来自 `STORAGE_KEYS`：

| Key | 用途 |
| --- | --- |
| `ACCESS_TOKEN` | Access Token。 |
| `REFRESH_TOKEN` | Refresh Token。 |
| `USER_INFO` | 用户信息。 |
| `CART` | 购物车。 |

约束：

- 不在页面里手写 token key。
- 登出必须清理 token、用户信息、购物车。
- Storage 中的对象必须序列化/反序列化清晰，不依赖隐式 any。

## Pinia 规范

当前已有：

- `useUserStore`

建议新增：

- `useCartStore`
- `useOrderStore`，如果订单状态需要跨页面共享。

约束：

- Store 只维护跨页面状态。
- 页面局部状态留在页面组件内。
- Store action 可以调用 service/request，但不能包含复杂 UI 交互。
- Store 中不直接调用 `Taro.showToast`，除非作为明确的全局 UX 策略。

## 认证生命周期

推荐流程：

```text
wx.login
  -> POST /auth/wechat/login
  -> 保存 accessToken / refreshToken / userInfo
  -> 请求时携带 Authorization
  -> 401 时 refresh
  -> refresh 失败时 logout
```

约束：

- refresh 并发必须合并，避免多个请求同时刷新。
- refresh 失败必须清理本地认证态。
- 手机号绑定成功后必须刷新用户信息和 token。
- 登出请求失败也必须清理本地状态。

## API 与页面职责边界

- 页面负责触发行为、展示 loading/error/empty 状态。
- `utils/request.ts` 负责协议级错误和 token。
- `utils/auth.ts` 负责认证业务流程。
- `store/` 负责跨页面状态。
- 后端负责最终权限与状态校验，前端不能作为安全边界。
