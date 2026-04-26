# StallMart Docker 本地环境

本目录用于本地开发环境编排，包含 MySQL、Redis、API 和管理端服务定义。

## 文件结构

```text
docker/
  docker-compose.yml
  .env.example
  Dockerfile.api
  Dockerfile.admin-web
  mysql/init/01-init.sql
```

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
| `mysql` | 主数据库 | `127.0.0.1:3306` |
| `redis` | 缓存和 token 相关存储 | `127.0.0.1:6379` |
| `api` | Spring Boot API | `127.0.0.1:8080` |
| `admin-web` | 管理端前端 | `127.0.0.1:3000` |

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

- `admin-web` 服务引用 `../admin-web`，但当前仓库尚未提供该目录。没有补齐前不要直接启动全部服务。
- `api` 的 healthcheck 使用 `/actuator/health`，但后端当前未看到 Actuator 依赖，需要补齐或调整 healthcheck。
- 所有端口默认绑定到 `127.0.0.1`，不要在本地开发环境暴露到公网。
