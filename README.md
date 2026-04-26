# StallMart

StallMart 是一个面向摊位/小店场景的小程序项目，当前仓库包含微信小程序前端、Spring Boot 后端 API、以及本地 Docker 开发环境配置。

## 项目结构

| 路径 | 说明 |
| --- | --- |
| `mini-program/` | Taro 4 + Vue 3 微信小程序，包含顾客端页面、摊主端分包、Pinia 状态和 API 请求封装。 |
| `backend/` | Spring Boot 3.2.3 后端 API，使用 MyBatis-Plus、MySQL、Redis、JWT 和 SpringDoc OpenAPI。 |
| `docker/` | 本地开发 Docker Compose 与镜像构建文件。当前引用了 `admin-web/`，但仓库内尚未提供该目录。 |
| `docs/` | 项目规范、架构、开发、配置、测试、API 和部署文档。 |
| `.planning/` | 本地规划/代码库分析资料，不是运行时依赖。 |

## 快速开始

### 后端依赖服务

仅启动 MySQL 和 Redis：

```bash
cd backend
docker compose up -d
```

或使用根级 Docker 配置：

```bash
cd docker
copy .env.example .env
docker compose up -d mysql redis
```

### 后端 API

`backend/pom.xml` 当前声明 `java.version` 为 `26`。本地运行前需要安装匹配 JDK，或先把版本统一到团队实际使用的 Java 版本。

```bash
cd backend
mvn spring-boot:run
```

默认服务地址：

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### 小程序

```bash
cd mini-program
npm ci
npm run dev:weapp
```

构建产物输出到 `mini-program/dist/`，然后用微信开发者工具打开 `mini-program/`。

## 常用命令

| 任务 | 命令 |
| --- | --- |
| 小程序开发构建 | `cd mini-program && npm run dev:weapp` |
| 小程序生产构建 | `cd mini-program && npm run build:weapp` |
| 小程序 lint | `cd mini-program && npm run lint` |
| 小程序格式化 | `cd mini-program && npm run format` |
| 后端运行 | `cd backend && mvn spring-boot:run` |
| 后端测试 | `cd backend && mvn test` |
| 本地依赖服务 | `cd backend && docker compose up -d` |

## 文档索引

- [架构说明](docs/ARCHITECTURE.md)
- [快速上手](docs/GETTING-STARTED.md)
- [开发规范](docs/DEVELOPMENT.md)
- [项目规范](docs/PROJECT-STANDARDS.md)
- [配置说明](docs/CONFIGURATION.md)
- [测试说明](docs/TESTING.md)
- [API 说明](docs/API.md)
- [部署说明](docs/DEPLOYMENT.md)
- [项目健康检查](docs/PROJECT-HEALTH.md)

## 当前注意事项

- `mini-program/node_modules/` 和 `mini-program/dist/` 已通过 `.gitignore` 排除，不应提交。
- `mini-program/project.private.config.json` 属于微信开发者工具个人配置，已排除。
- 当前代码里存在若干待修复一致性问题，集中记录在 [项目健康检查](docs/PROJECT-HEALTH.md)。
- `docker/.env` 不应提交，提交模板只保留 `docker/.env.example`。
