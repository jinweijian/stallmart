# 项目健康检查

本文记录当前已确认事实和后续风险。

## 已处理

### 根目录规范化

- `backend/` 已规范为 `server/`。
- `mini-program/` 已规范为 `app/`。
- `docs/` 已按 `guide`、`api-app`、`api-server`、`specification`、`deploy` 重组。
- 服务端包结构已按领域模块重排为 `{domain}/dto/internal`。

### Java 版本

- `server/pom.xml` 使用 Java 21。
- `docker/Dockerfile.api` 使用 `eclipse-temurin:21-*`。

### 后端测试入口

- 已补充 `ResultTest`、`OrderServiceTest`、`ApiControllerTest`。

### 管理端初始落地

- 已新增 `web/`，使用 Nuxt + Vue 3。
- 已补充平台管理、商家 H5 管理、商品、订单、店铺、装修、用户、购物车页面。
- 已补充 `/admin/platform/*` 和 `/admin/vendor/me/*` 后端接口，管理端写入商品后小程序商品接口可读取同一份数据。

## 高优先级

### 小程序 mock 数据仍未进入真实联调

当前 `app/src/app-config/index.ts` 中 `ENABLE_API_MOCK = true`。本次初始化不修改前端 mock 策略。

影响：

- 页面行为不能代表真实 API 联调结果。
- 服务端新增接口需要通过服务端测试验证。

### 小程序 endpoint 缺失

当前代码仍引用：

- `API_ENDPOINTS.USER_BIND_PHONE`
- `API_ENDPOINTS.STORE_INFO`

本次按要求不改小程序 mock 和前端业务代码，后续联调前需要单独修复。

## 中优先级

### 管理端样式和交互仍是初始化版本

当前 `web/` 已有可运行页面，但还没有接入登录、权限菜单、分页筛选、批量操作和生产级视觉细节。

### 数据库持久化尚未接入服务端

当前服务端是初始化接口契约和测试闭环，尚未接入 MySQL/Redis/JWT 真实链路。数据库脚本当前以 `docker/mysql/init/01-init.sql` 为准。
