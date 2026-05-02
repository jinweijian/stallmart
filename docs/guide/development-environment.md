# 快速上手

## 前置环境

- Node.js 18+
- npm
- JDK: 当前 `server/build.gradle` 声明 Java 21 toolchain
- Docker 20.10+
- Docker Compose v2+
- 微信开发者工具

## 1. 准备依赖服务

启动本地依赖：

```bash
cd docker
copy .env.example .env
docker compose up -d mysql redis
```

`docker/docker-compose.yml` 也定义了 `api` 和 `admin-web`，管理端源码位于 `web/`。

## 2. 启动后端

```bash
cd server
./gradlew bootRun
```

默认配置读取 `server/src/main/resources/application.yml`：

- 服务端口: `8080`
- MySQL: `localhost:3306/stallmart`
- Redis: `localhost:6379`
- Swagger: `http://localhost:8080/api/v1/swagger-ui/index.html`

本地私有配置建议放入未提交的 `application-local.yml`。

## 3. 启动小程序

```bash
cd app
npm ci
npm run dev:weapp
```

然后用微信开发者工具打开 `app/`，构建产物位于 `app/dist/`。

## 4. 启动管理端

```bash
cd web
npm install
npm run dev
```

默认地址：

- 管理端: `http://localhost:3000`
- 管理端 API 代理: `http://localhost:8091/api/v1`
- 后端本机映射: `http://localhost:8081/api/v1`

管理端浏览器请求必须走同源 `/api/v1` 代理，不允许通过公开运行时配置暴露后端地址。代理目标通过服务端变量 `NUXT_API_PROXY_TARGET` 配置；本机开发默认是 `http://localhost:8081`，Docker Compose 内部是 `http://api:8080`。

## 5. 验证基本连通性

```bash
curl http://localhost:8080/api/v1/actuator/health
```

小程序开发环境 API 地址在 `app/src/app-config/index.ts` 中配置：

```ts
export const API_BASE_URL_DEV = 'http://localhost:8080/api/v1'
```

## 6. 当前提示

- 小程序当前仍启用 mock 数据。
- 服务端当前先完成接口契约和测试闭环，持久化链路后续单独接入。
