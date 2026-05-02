# StallMart Docker 本地环境

本目录用于本地开发环境编排，包含 MySQL、Redis、API 和 Nuxt 管理端服务定义。

## 文件结构

```text
docker/
  docker-compose.yml
  .env.example
  Dockerfile.api
  Dockerfile.admin-web
  mysql/init/01-init.sql
```

## 先读哪些文档

| 工作类型 | 必读文档 |
| --- | --- |
| 修改环境变量或端口 | [../docs/guide/configuration.md](../docs/guide/configuration.md) |
| 修改部署方式 | [../docs/deploy/index.md](../docs/deploy/index.md) |
| 修改数据库初始化脚本 | [../docs/specification/architecture.md](../docs/specification/architecture.md), [../docs/specification/project-health.md](../docs/specification/project-health.md) |
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
| `redis` | 缓存和 token 相关存储 | `127.0.0.1:6379` |
| `api` | Spring Boot API | `127.0.0.1:8081` |
| `admin-web` | Nuxt 管理端前端，源码位于 `../web` | `127.0.0.1:8091` |

## 环境变量

核心变量位于 `.env.example`：

- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`
- `REDIS_VERSION`
- `REDIS_PASSWORD`
- `JWT_SECRET`
- `WECHAT_APP_ID`
- `WECHAT_APP_SECRET`

`.env` 是本地私有文件，不应提交。

## 当前注意事项

- `admin-web` 服务引用 `../web`，浏览器固定请求同源 `/api/v1`，容器内通过 `NUXT_API_PROXY_TARGET=http://api:8080` 代理到后端。
- `api` 的 healthcheck 使用 `/api/v1/actuator/health`，后端已接入 Actuator。
- `api` 使用 `server/gradlew` 构建。
- 所有端口默认绑定到 `127.0.0.1`，本地开发环境不要暴露到公网。
