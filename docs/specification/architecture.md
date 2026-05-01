# 架构说明

## 总览

```text
app/      微信小程序端，Taro 4 + Vue 3
web/      后台管理端，Nuxt + Vue 3
server/   服务端，Spring Boot
docker/   本地开发依赖和镜像构建
docs/     开发文档
```

典型请求链路：

```text
微信小程序 -> app/src/utils/request.ts -> /api/v1 -> server 领域服务
后台管理 -> web/app/api/stallmart-api.ts -> /api/v1/admin/* -> server 领域服务
```

当前小程序仍启用 mock 数据；联调前不要把页面数据视为真实后端数据。

## 服务端结构

服务端按领域组织，参考 `edusoho-lms/server`：

```text
server/src/main/java/com/stallmart/
  auth/
  order/
  product/
  store/
  style/
  user/
  cart/
  management/
  support/
```

领域模块结构：

```text
order/
  OrderService.java
  dto/
  internal/
    api/
    service/
```

## 小程序结构

```text
app/src/
  app-config/
  pages/customer/
  pages/vendor/
  static/
  store/
  utils/
```

小程序特殊约束保留在 `app/` 内；除小程序平台限制外，新增模块和文档组织按本项目统一体系执行。

## 管理端结构

```text
web/app/
  api/
  assets/css/
  components/
  pages/platform/
  pages/vendor/
  types/
```

平台管理进入 `pages/platform/`，商家手机 H5 管理进入 `pages/vendor/`。

## 基础设施

- Docker Compose 位于 `docker/docker-compose.yml`。
- MySQL 初始化脚本当前以 `docker/mysql/init/01-init.sql` 为准。
- 管理端 Docker build context 为 `web/`。
