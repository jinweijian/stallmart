# 部署说明

## 当前部署状态

当前仓库具备本地开发部署配置，但还不是完整生产部署方案。

已有内容：

- `docker/docker-compose.yml`
- `docker/Dockerfile.api`
- `docker/Dockerfile.admin-web`
- `docker/Dockerfile.app-h5`
- `docker/.env.example`
- `docker/mysql/init/01-init.sql`
- `server/src/main/resources/db/migration/V1__init_schema.sql`
- `server/src/main/resources/db/migration/V2__seed_dev_data.sql`

待确认内容：

- 管理端源码位于 `web/`，Docker Compose 的 `admin-web` build context 指向 `../web`。
- App H5 调试源码位于 `app/`，Docker Compose 的 `app-h5` build context 指向 `../app`。
- 后端 Dockerfile 使用 `eclipse-temurin:25-*` 镜像，与 `server/build.gradle` 的 Java 25 toolchain 保持一致。
- Docker healthcheck 使用 `/api/v1/actuator/health`，服务端已接入 Actuator。

## 本地 Docker

```bash
cd docker
copy .env.example .env
docker compose up -d mysql redis
```

如果只启动依赖服务，这通常足够支撑本地后端运行。

管理端依赖 API，启动全部服务前确认 `.env` 已配置：

```bash
docker compose up -d
```

只启动 H5 联调链路：

```bash
docker compose up -d app-h5
```

浏览器打开 `http://localhost:10086/`。`app-h5` 默认关闭 mock，使用 `TARO_APP_ID=wx-stallmart-demo` 触发 `/app/bootstrap`，通过 `TARO_APP_API_BASE_URL=http://localhost:8081/api/v1` 访问 Docker 暴露的后端 API，并通过 `TARO_APP_H5_PORT=10086` 固定 Taro H5 dev server 端口。API 容器默认通过 `STALLMART_CORS_ALLOWED_ORIGINS` 允许该 H5 调试源跨域访问。

本地 Redis 默认映射到 `127.0.0.1:6380`，避免和本机已有 Redis 的 `6379` 冲突；容器内部仍通过 `redis:6379` 通信。
`docker/mysql/init/01-init.sql` 不再创建业务表，业务 schema 和 seed 由 API 启动时的 Flyway migration 管理。

## 后端容器

`docker/Dockerfile.api` 以 `server/` 作为 build context：

```yaml
api:
  build:
    context: ../server
    dockerfile: ../docker/Dockerfile.api
```

启动前需要：

- 有可用 JDK/JRE 25 镜像。
- `server/gradlew` 能完成 Gradle 构建。
- MySQL 和 Redis healthcheck 通过。
- `.env` 中设置 `JWT_SECRET`、数据库和 Redis 密码。

## 管理端容器

管理端浏览器端固定请求同源 `/api/v1`，不得在公开运行时配置中暴露后端真实地址。容器内通过 `NUXT_API_PROXY_TARGET=http://api:8080` 代理到后端服务；如后端服务名或端口变化，只调整该服务端变量。

## App H5 容器

`docker/Dockerfile.app-h5` 以 `app/` 作为 build context：

```yaml
app-h5:
  build:
    context: ../app
    dockerfile: ../docker/Dockerfile.app-h5
```

该服务用于域名申请完成前的 H5 优先开发调试，不作为生产发布物。源码以 volume 方式挂载，容器保留独立 `node_modules_app` volume。

## 小程序发布

小程序构建：

```bash
cd app
npm ci
npm run build:weapp
```

构建产物位于：

```text
app/dist/
```

用微信开发者工具上传前确认：

- `project.config.json` 中 AppID 正确。
- 生产 API 地址 `API_BASE_URL_PROD` 正确。
- 生产域名已在微信公众平台配置 request 合法域名。

## 生产前检查清单

- 保持 Java 版本一致。
- 联调前复核成功码和 endpoint 常量。
- 复核 Taro alias 不含本机绝对路径。
- Flyway migration 已随 API 服务启动执行，生产环境禁止手工改业务表结构。
- 增加生产环境 Spring profile。
- 使用强随机 `JWT_SECRET`。
- 使用最小权限数据库账号。
- API 只通过 HTTPS 暴露。
- Docker 端口、CORS、日志脱敏完成复核。
