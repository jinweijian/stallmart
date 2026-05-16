# StallMart Docker 本地环境

本目录用于本地开发环境编排，包含 MySQL、Redis、API、Nuxt 管理端和 Taro H5 调试服务定义。

## 文件结构

```text
docker/
  docker-compose.yml
  .env.example
  Dockerfile.api
  Dockerfile.admin-web
  Dockerfile.app-h5
  mysql/init/01-init.sql
```

## 先读哪些文档

| 工作类型 | 必读文档 |
| --- | --- |
| 修改环境变量或端口 | [../docs/guide/configuration.md](../docs/guide/configuration.md) |
| 修改部署方式 | [../docs/deploy/index.md](../docs/deploy/index.md) |
| 修改数据库初始化脚本或 Flyway migration | [../docs/specification/architecture.md](../docs/specification/architecture.md), [../docs/specification/project-health.md](../docs/specification/project-health.md) |
| 提交前安全检查 | [../docs/standards/security.md](../docs/standards/security.md) |

## 快速启动

复制环境变量模板：

```bash
copy .env.example .env
```

只启动后端依赖服务：

```bash
docker compose up -d mysql redis
```

查看状态：

```bash
docker compose ps
```

查看日志：

```bash
docker compose logs -f mysql
docker compose logs -f redis
```

启动 App H5 联调容器：

```bash
docker compose up -d app-h5
```

浏览器打开 `http://localhost:10086/`。该服务通过 `TARO_APP_ID=wx-stallmart-demo` 调用 `/app/bootstrap`，并通过 `TARO_APP_API_BASE_URL=http://localhost:8081/api/v1` 访问 Docker 后端。

停止服务：

```bash
docker compose down
```

清除数据卷：

```bash
docker compose down -v
```

## 服务

| 服务 | 说明 | 默认端口 |
| --- | --- | --- |
| `mysql` | 主数据库 | `127.0.0.1:3307` |
| `redis` | 缓存和 token 相关存储 | `127.0.0.1:6380` |
| `api` | Spring Boot API | `127.0.0.1:8081` |
| `admin-web` | Nuxt 管理端前端，源码位于 `../web` | `127.0.0.1:8091` |
| `app-h5` | Taro H5 调试端，源码位于 `../app` | `127.0.0.1:10086` |

## 环境变量

核心变量位于 `.env.example`：

- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `MYSQL_PORT`
- `REDIS_VERSION`
- `REDIS_PASSWORD`
- `REDIS_PORT`
- `JWT_SECRET`
- `STALLMART_CORS_ALLOWED_ORIGINS`
- `WECHAT_APP_ID`
- `WECHAT_APP_SECRET`
- `TARO_APP_ID`
- `TARO_APP_API_BASE_URL`
- `TARO_APP_H5_PORT`

`.env` 是本地私有文件，不应提交。

## 当前注意事项

- `admin-web` 服务引用 `../web`，浏览器固定请求同源 `/api/v1`，容器内通过 `NUXT_API_PROXY_TARGET=http://api:8080` 代理到后端。
- `app-h5` 服务引用 `../app`，用于小程序域名申请前的 H5 优先联调；浏览器端通过 `TARO_APP_API_BASE_URL` 访问本机映射出的 API 地址，dev server 端口由 `TARO_APP_H5_PORT` 控制。
- `api` 的 healthcheck 使用 `/api/v1/actuator/health`，后端已接入 Actuator。
- `api` 默认允许 `http://localhost:10086` 和 `http://127.0.0.1:10086` 跨域访问，供 `app-h5` 调试真实 API。
- `api` 使用 `server/gradlew` 构建。
- `mysql/init/01-init.sql` 不创建业务表；业务 schema 和 seed 由 API 启动时的 Flyway migration 管理。
- 所有端口默认绑定到 `127.0.0.1`，本地开发环境不要暴露到公网。
