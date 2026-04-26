# 系统架构说明

## 总览

StallMart 当前由三部分组成：

- `mini-program/`: 面向顾客和摊主的小程序端，使用 Taro 4、Vue 3、Pinia、TypeScript。
- `backend/`: Spring Boot API 服务，提供认证、用户、店铺、风格、规格、商品和订单接口。
- `docker/`: 本地 MySQL、Redis、API 和管理端容器编排。当前管理端目录 `admin-web/` 尚未在仓库中落地。

典型请求链路：

```text
微信小程序 -> Taro request 封装 -> /api/v1 后端接口 -> Service -> Mapper/MyBatis-Plus -> MySQL
                                            |
                                            +-> Redis / JWT
```

## 小程序端

入口文件：

- `mini-program/src/app.ts`
- `mini-program/src/app.vue`
- `mini-program/src/app.config.ts`

页面结构：

- 顾客端页面位于 `mini-program/src/pages/customer/`
- 摊主端分包位于 `mini-program/src/pages/vendor/`
- 全局状态位于 `mini-program/src/store/`
- API 与认证封装位于 `mini-program/src/utils/`
- 全局配置位于 `mini-program/src/app-config/index.ts`

小程序端通过 `API_BASE_URL` 访问后端，开发环境默认值为 `http://localhost:8080/api/v1`。

## 后端

后端采用分层结构：

| 层级 | 路径 | 职责 |
| --- | --- | --- |
| Controller | `backend/src/main/java/com/stallmart/controller/` | 暴露 REST API。 |
| Service | `backend/src/main/java/com/stallmart/service/` | 业务接口。 |
| Service Impl | `backend/src/main/java/com/stallmart/service/impl/` | 业务实现。 |
| Repository | `backend/src/main/java/com/stallmart/repository/` | MyBatis-Plus Mapper。 |
| Model | `backend/src/main/java/com/stallmart/model/` | Entity、DTO、VO。 |
| Common | `backend/src/main/java/com/stallmart/common/` | 统一响应、异常、安全上下文和过滤器。 |
| Config | `backend/src/main/java/com/stallmart/config/` | Web、Redis、JWT、Swagger 配置。 |

统一响应对象为 `Result<T>`：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1743443200
}
```

## 认证

后端通过 `JwtAuthFilter` 处理 `/api/*` 请求。当前公开路径包括：

- `/api/v1/stores/qr/**`
- `/api/v1/auth/**`
- `/api/v1/styles/**`
- `/swagger-ui/**`
- `/api-docs/**`
- `/v3/api-docs/**`

非公开接口需要 `Authorization: Bearer <token>` 请求头。过滤器解析 token 后将 `userId` 写入 request attribute。

## 数据存储

主要数据表来自 `backend/mysql/init.sql` 和 `docker/mysql/init/01-init.sql`：

- `user`
- `store`
- `store_style`
- `product_spec`
- `product`
- `order`
- `order_item`

两个 SQL 初始化文件之间存在字段命名和结构差异，后续应选择一个作为唯一权威脚本。

## 基础设施

| 服务 | 来源 | 默认端口 |
| --- | --- | --- |
| MySQL | `backend/docker-compose.yml` 或 `docker/docker-compose.yml` | `3306` |
| Redis | `backend/docker-compose.yml` 或 `docker/docker-compose.yml` | `6379` |
| API | `docker/Dockerfile.api` | `8080` |
| Admin Web | `docker/Dockerfile.admin-web` | `3000` |

`docker/docker-compose.yml` 将服务绑定到 `127.0.0.1`，更适合作为本地开发入口。`backend/docker-compose.yml` 只提供 MySQL 和 Redis。

## 当前架构风险

- `docker/docker-compose.yml` 引用 `../admin-web`，但当前仓库没有该目录。
- 小程序请求封装期望后端成功响应 `code === 0`，但后端 `Result.success` 返回 `code === 200`。
- 前端代码引用了未定义的 `API_ENDPOINTS.USER_BIND_PHONE` 和 `API_ENDPOINTS.STORE_INFO`。
- 两套数据库初始化脚本不一致，容易导致本地和容器环境行为不同。
