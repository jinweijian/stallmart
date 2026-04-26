# 部署说明

## 当前部署状态

当前仓库具备本地开发部署配置，但还不是完整生产部署方案。

已有内容：

- `docker/docker-compose.yml`
- `docker/Dockerfile.api`
- `docker/Dockerfile.admin-web`
- `docker/.env.example`
- `docker/mysql/init/01-init.sql`

待确认内容：

- `admin-web/` 目录当前不存在，但 Docker Compose 和 Dockerfile 已引用。
- 后端 Dockerfile 使用 `eclipse-temurin:26-*` 镜像，需确认 Java 26 是否为团队目标版本。
- Docker healthcheck 使用 `/actuator/health`，但 `backend/pom.xml` 当前未看到 `spring-boot-starter-actuator` 依赖。

## 本地 Docker

```bash
cd docker
copy .env.example .env
docker compose up -d mysql redis
```

如果只启动依赖服务，这通常足够支撑本地后端运行。

不要直接启动全部服务，除非已经补齐 `admin-web/`：

```bash
docker compose up -d
```

## 后端容器

`docker/Dockerfile.api` 以 `backend/` 作为 build context：

```yaml
api:
  build:
    context: ../backend
    dockerfile: ../docker/Dockerfile.api
```

启动前需要：

- 有可用 JDK/JRE 26 镜像。
- `backend/pom.xml` 能完成 Maven 构建。
- MySQL 和 Redis healthcheck 通过。
- `.env` 中设置 `JWT_SECRET`、数据库和 Redis 密码。

## 小程序发布

小程序构建：

```bash
cd mini-program
npm ci
npm run build:weapp
```

构建产物位于：

```text
mini-program/dist/
```

用微信开发者工具上传前确认：

- `project.config.json` 中 AppID 正确。
- 生产 API 地址 `API_BASE_URL_PROD` 正确。
- 生产域名已在微信公众平台配置 request 合法域名。

## 生产前检查清单

- 统一 Java 版本。
- 修复前后端成功码不一致问题。
- 修复缺失 endpoint 常量。
- 复核 Taro alias 不含本机绝对路径。
- 合并数据库初始化脚本。
- 增加生产环境 Spring profile。
- 使用强随机 `JWT_SECRET`。
- 使用最小权限数据库账号。
- API 只通过 HTTPS 暴露。
- Docker 端口、CORS、日志脱敏完成复核。
