# 快速上手

## 前置环境

- Node.js 18+
- npm
- Maven
- JDK: 当前 `server/pom.xml` 声明 Java 21
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

`docker/docker-compose.yml` 也定义了 `api` 和 `admin-web`，但 `admin-web/` 目录当前不存在，直接启动全部服务会失败。

## 2. 启动后端

```bash
cd server
mvn spring-boot:run
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

## 4. 验证基本连通性

```bash
curl http://localhost:8080/api/v1/actuator/health
```

小程序开发环境 API 地址在 `app/src/app-config/index.ts` 中配置：

```ts
export const API_BASE_URL_DEV = 'http://localhost:8080/api/v1'
```

## 5. 当前提示

- 小程序当前仍启用 mock 数据。
- `admin-web/` 尚未落地，不要直接启动完整 Docker Compose。
- 服务端当前先完成接口契约和测试闭环，持久化链路后续单独接入。
