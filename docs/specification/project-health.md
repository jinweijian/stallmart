# 项目健康检查

本文记录当前已确认事实和后续风险。

## 已处理

### 根目录规范化

- `backend/` 已规范为 `server/`。
- `mini-program/` 已规范为 `app/`。
- `docs/` 已按 `guide`、`api-app`、`api-server`、`specification`、`deploy` 重组。
- 服务端包结构已按领域模块重排为 `{domain}/dto/internal`。

### Java 版本

- `server/build.gradle` 使用 Java 25 toolchain。
- `docker/Dockerfile.api` 使用 `eclipse-temurin:25-*`。

### 后端测试入口

- 已补充 `ResultTest`、`OrderServiceTest`、`ApiControllerTest`。

### 管理端初始落地

- 已新增 `web/`，使用 Nuxt + Vue 3。
- 已补充平台管理、商家 H5 管理、商品、订单、店铺、装修、用户、购物车页面。
- 已补充 `/admin/platform/*` 和 `/admin/vendor/me/*` 后端接口，管理端写入商品后小程序商品接口可读取同一份数据。

### 旧 backend 目录归并

- 旧 `backend/` 中的 JWT 生成与解析能力已迁入 `server/src/main/java/com/stallmart/support/security/JwtService.java`。
- 旧 `backend/` 中的 OpenAPI 配置已迁入 `server/src/main/java/com/stallmart/support/config/OpenApiConfig.java`。
- 旧 `backend/` 目录已移除，后端唯一入口为 `server/`。

## 高优先级

### 小程序已切换真实 API 链路

当前小程序请求层已移除本地模拟数据分支，页面数据来自 `/api/v1` 真实接口。

影响：

- 本地调试前需要启动服务端或配置 `TARO_APP_API_BASE_URL`。
- 服务端新增接口仍需要通过服务端测试和小程序构建验证。

### 小程序 endpoint 常量已收敛

小程序代码已使用现有 endpoint key：手机号绑定走 `API_ENDPOINTS.AUTH_BIND_PHONE`，店铺详情和更新走 `API_ENDPOINTS.STORE_DETAIL`。

## 中优先级

### 管理端样式和交互仍是初始化版本

当前 `web/` 已有可运行页面，但还没有接入登录、权限菜单、分页筛选、批量操作和生产级视觉细节。

### 数据库持久化尚未接入服务端

当前服务端是初始化接口契约和测试闭环，尚未接入 MySQL/Redis/JWT 真实链路。数据库脚本当前以 `docker/mysql/init/01-init.sql` 为准。
