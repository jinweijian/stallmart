# 后台认证与审计日志加固设计

## 背景

StallMart 管理端准备上线，后台登录和身份校验需要从本地演示账号升级为可部署的安全基线。当前服务端已经使用 BCrypt 比对 `admin_account.password_hash`，但账号密码仍是旧 dev seed，数据库没有显式账户盐，登录失败后也没有验证码门槛。管理端同时缺少可追踪的后台操作日志，平台和商户上线后无法排查谁在什么时候改了商品、装修、规格、风格包或订单状态。

本设计覆盖两条工作：

- 管理端登录加固：新账号密码、显式账户盐、失败 3 次后验证码、防爆破状态。
- 后台审计日志：商户看自己店铺日志，平台看平台日志，也能按商户查看该商户日志。

## 目标

- 线上平台账号使用 `platform`，密码由部署负责人单独分发，不写入仓库。
- 线上商家账号使用 `vendor`，密码由部署负责人单独分发，不写入仓库。
- `admin_account` 增加 `password_salt`，每个后台账号拥有独立账户盐。
- 登录校验使用成熟算法：`BCrypt(password + passwordSalt)`，数据库保存 `password_salt` 和 `password_hash`，不保存明文密码。
- 管理端登录同一 `account + IP` 连续失败 3 次后，后续登录必须提交验证码。
- 验证码由服务端使用现成图形验证码库生成，返回 `captchaId` 和图片 Base64，答案只保存在服务端内存状态中并设置短 TTL。
- 管理端写操作记录审计日志，不记录密码、token、验证码答案、完整手机号等敏感信息。
- 商户管理端能查看自己店铺的操作日志。
- 平台管理端能查看平台操作日志，也能查看某个商户的操作日志。
- API、web 类型、页面、测试文档同步更新。

## 登录加固设计

新增 `AdminLoginSecurityService` 负责登录失败状态和验证码：

- 状态 key：`normalizedAccount + clientIp`。
- 失败计数 TTL：15 分钟。
- 第 3 次失败后，`captchaRequired = true`。
- 成功登录后清理该 key 的失败状态。
- 验证码 TTL：5 分钟，一次性使用。
- 验证码题目只做简单整数加法，例如 `7 + 4 = ?`。

接口变化：

- `GET /admin/auth/captcha` 返回 `{ captchaId, imageBase64 }`。
- `POST /admin/auth/login` 请求体新增可选字段 `captchaId`、`captchaAnswer`。
- 登录失败但未到验证码门槛时仍返回 `401 invalid_credentials`。
- 需要验证码但未提供或验证码错误时返回 `401 captcha_required` 或 `401 captcha_invalid`，并在响应中提示 `captchaRequired = true`。

前端体验：

- 登录页初始不显示验证码。
- 捕获 `captcha_required` 或本地连续失败 3 次后显示验证码。
- 验证码错误或账号密码错误后刷新图片。
- 登录成功后清理本地失败提示状态。

## 审计日志设计

新增 `admin_operation_log` 表：

| 字段 | 说明 |
| --- | --- |
| `id` | 主键 |
| `scope` | `PLATFORM` 或 `VENDOR` |
| `store_id` | 商户日志所属店铺；平台日志为空 |
| `actor_user_id` | 操作者用户 ID |
| `actor_account` | 后台账号 |
| `actor_role` | `ADMIN` 或 `VENDOR` |
| `action` | 操作类型，如 `PRODUCT_CREATE`、`ORDER_TRANSITION` |
| `resource_type` | 资源类型，如 `PRODUCT`、`STYLE`、`ORDER` |
| `resource_id` | 资源 ID，无法确定时为空 |
| `description` | 面向后台展示的短描述 |
| `result` | `SUCCESS` 或 `FAILURE` |
| `ip_address` | 来源 IP |
| `user_agent` | 来源 User-Agent，截断到安全长度 |
| `created_at` | 创建时间 |

记录范围：

- 认证安全事件：后台登录成功、登录失败、验证码校验失败。
- 平台写操作：风格包新增、编辑、上架、下架、删除。
- 商户写操作：店铺资料修改、分类新增/编辑、图片上传、商品新增/编辑/上下架/售罄、订单状态流转、装修修改、规格新增/编辑/删除。

第一版不记录普通查询，避免噪音；平台“查看某商户日志”通过单独接口读取，不把平台查看动作本身写入日志。

接口：

- `GET /admin/platform/operation-logs`：平台日志。
- `GET /admin/platform/vendors/{storeId}/operation-logs`：指定商户日志。
- `GET /admin/vendor/me/operation-logs`：当前商户日志。

响应统一为 `OperationLogDTO[]`，前端先做列表展示，分页和筛选可后续扩展。

## 权限边界

- `/admin/vendor/me/operation-logs` 必须通过当前商家 token 解析店铺，只返回自己的 `storeId`。
- `/admin/platform/operation-logs` 和 `/admin/platform/vendors/{storeId}/operation-logs` 只能由 `ADMIN` 调用。
- 审计日志接口不得允许前端提交任意 `storeId` 绕过服务端权限判断。
- 登录日志中的失败账号只保存账号字符串，不保存提交的密码。

## 测试与验收

- 服务端测试覆盖新密码登录、旧密码拒绝、密码盐存在且每账号不同。
- 服务端测试覆盖失败 3 次后需要验证码、错误验证码拒绝、正确验证码后可登录。
- 服务端测试覆盖商户写操作生成商户日志，商户只能读取自己的日志。
- 服务端测试覆盖平台写操作生成平台日志，平台可读取平台日志和某商户日志。
- web 构建通过，登录页类型和验证码请求编译通过。
- 文档同步 API、测试说明、web 本地账号、安全规范。
