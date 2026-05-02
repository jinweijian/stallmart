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

- 管理端源码位于 `web/`，Docker Compose 的 `admin-web` build context 指向 `../web`。
- 后端 Dockerfile 使用 `eclipse-temurin:21-*` 镜像，与 `server/build.gradle` 的 Java 21 toolchain 保持一致。
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

## 后端容器

`docker/Dockerfile.api` 以 `server/` 作为 build context：

```yaml
api:
  build:
    context: ../server
    dockerfile: ../docker/Dockerfile.api
```

启动前需要：

- 有可用 JDK/JRE 21 镜像。
- `server/gradlew` 能完成 Gradle 构建。
- MySQL 和 Redis healthcheck 通过。
- `.env` 中设置 `JWT_SECRET`、数据库和 Redis 密码。

## 管理端容器

管理端浏览器端固定请求同源 `/api/v1`，不得在公开运行时配置中暴露后端真实地址。容器内通过 `NUXT_API_PROXY_TARGET=http://api:8080` 代理到后端服务；如后端服务名或端口变化，只调整该服务端变量。

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
- 合并数据库初始化脚本。
- 增加生产环境 Spring profile。
- 使用强随机 `JWT_SECRET`。
- 使用最小权限数据库账号。
- API 只通过 HTTPS 暴露。
- Docker 端口、CORS、日志脱敏完成复核。
