# StallMart

StallMart 是一个面向摊位、小店和临时经营场景的小程序项目。当前 `src/` 仓库包含微信小程序前端、Spring Boot 后端 API、本地 Docker 开发环境，以及已经结构化的项目文档。

本文是后续开发者和 Agent 的入口。进入项目后优先按本文索引查阅，不要在没有阅读规范的情况下直接修改业务代码。

## 快速入口

| 入口 | 作用 | 适合什么时候读 |
| --- | --- | --- |
| [AGENTS.md](AGENTS.md) | Agent 工作入口和强制查阅顺序 | 任何 Agent 开始任务前 |
| [docs/README.md](docs/README.md) | 文档总索引 | 不确定该读哪份文档时 |
| [mini-program/README.md](mini-program/README.md) | 小程序模块入口 | 修改小程序代码前 |
| [backend/README.md](backend/README.md) | 后端模块入口 | 修改后端接口、服务、数据库前 |
| [docker/README.md](docker/README.md) | 本地 Docker 环境 | 启动依赖服务或修改编排前 |
| [CONTRIBUTING.md](CONTRIBUTING.md) | 协作和提交要求 | 准备提交或交付前 |

## 文档目录

每个文档目录都有自己的 README，说明该目录负责什么、包含哪些文档、做什么事情时应该参考哪些文件。

| 文档目录 | README | 内容 |
| --- | --- | --- |
| `docs/` | [docs/README.md](docs/README.md) | 全部文档导航和推荐阅读路径。 |
| `docs/overview/` | [docs/overview/README.md](docs/overview/README.md) | 项目概览、快速上手、系统架构。 |
| `docs/mini-program/` | [docs/mini-program/README.md](docs/mini-program/README.md) | 小程序模块划分、页面设计、状态/API、交互风格。 |
| `docs/backend/` | [docs/backend/README.md](docs/backend/README.md) | 后端 API、分层约束和接口一致性。 |
| `docs/standards/` | [docs/standards/README.md](docs/standards/README.md) | 代码风格、项目规范、Git、Review、安全、文档规范。 |
| `docs/operations/` | [docs/operations/README.md](docs/operations/README.md) | 配置、环境变量、本地/部署运行方式。 |
| `docs/quality/` | [docs/quality/README.md](docs/quality/README.md) | 测试策略、健康检查、质量风险。 |

## 项目结构

| 路径 | 说明 |
| --- | --- |
| `mini-program/` | Taro 4 + Vue 3 微信小程序，包含顾客端页面、摊主端分包、Pinia 状态和 API 请求封装。 |
| `backend/` | Spring Boot 3.2.3 后端 API，使用 MyBatis-Plus、MySQL、Redis、JWT 和 SpringDoc OpenAPI。 |
| `docker/` | 本地开发 Docker Compose 与镜像构建文件。当前引用了 `admin-web/`，但仓库内尚未提供该目录。 |
| `docs/` | 结构化项目文档和开发规范。 |
| `.planning/` | 本地规划/代码库分析资料，不是运行时依赖。 |

## 快速开始

启动后端依赖服务：

```bash
cd backend
docker compose up -d
```

启动后端 API：

```bash
cd backend
mvn spring-boot:run
```

启动小程序开发构建：

```bash
cd mini-program
npm ci
npm run dev:weapp
```

用微信开发者工具打开 `mini-program/`。构建产物位于 `mini-program/dist/`。

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
| Docker 配置检查 | `cd docker && docker compose config` |

## 当前必须知道的项目事实

- `backend/pom.xml` 当前声明 `java.version` 为 `26`，需要本地 JDK 与 Docker 镜像一致。
- 小程序请求封装当前按 `code === 0` 判断成功，后端 `Result.success` 当前返回 `code === 200`，这是联调阻断项。
- `docker/docker-compose.yml` 引用 `admin-web/`，当前仓库未提供该目录。
- `backend/mysql/init.sql` 与 `docker/mysql/init/01-init.sql` 不一致，后续需要统一权威脚本。

这些问题集中记录在 [docs/quality/project-health.md](docs/quality/project-health.md)。
