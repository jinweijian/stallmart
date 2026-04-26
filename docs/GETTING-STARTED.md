# 快速上手

## 前置环境

- Node.js 18+
- npm
- Maven
- JDK: 当前 `backend/pom.xml` 声明 Java 26
- Docker 20.10+
- Docker Compose v2+
- 微信开发者工具

## 1. 准备依赖服务

如果只需要后端依赖：

```bash
cd backend
docker compose up -d
```

如果要使用根级 Docker 编排：

```bash
cd docker
copy .env.example .env
docker compose up -d mysql redis
```

`docker/docker-compose.yml` 也定义了 `api` 和 `admin-web`，但 `admin-web/` 目录当前不存在，直接启动全部服务会失败。

## 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认配置读取 `backend/src/main/resources/application.yml`：

- 服务端口: `8080`
- MySQL: `localhost:3306/stallmart`
- Redis: `localhost:6379`
- Swagger: `http://localhost:8080/swagger-ui.html`

本地私有配置建议放入未提交的 `application-local.yml`。

## 3. 启动小程序

```bash
cd mini-program
npm ci
npm run dev:weapp
```

然后用微信开发者工具打开 `mini-program/`，构建产物位于 `mini-program/dist/`。

## 4. 验证基本连通性

```bash
curl http://localhost:8080/api-docs
```

小程序开发环境 API 地址在 `mini-program/src/app-config/index.ts` 中配置：

```ts
export const API_BASE_URL_DEV = 'http://localhost:8080/api/v1'
```

## 5. 推荐首次整理动作

首次进入项目后，建议先处理这些基础问题：

- 确认团队实际使用 Java 版本，并同步 `backend/pom.xml`、Dockerfile 和文档。
- 修正 `mini-program/taro.config.ts` 的本机绝对路径 alias。
- 统一后端 `Result.code` 与小程序请求封装的成功码判断。
- 在两套 SQL 初始化脚本中选择一个作为权威版本。
