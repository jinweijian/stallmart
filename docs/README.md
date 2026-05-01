# 开发文档

文档目录参考 `edusoho-lms` 的组织方式，按用途分区，而不是按历史问题分区。

## 文档目录

| 目录 | 内容 |
| --- | --- |
| [guide/](guide/index.md) | 开发环境、配置、测试等日常指引。 |
| [api-server/](api-server/index.md) | 服务端 API 契约。 |
| [api-app/](api-app/index.md) | 小程序端 API、状态和 mock 约束。 |
| [specification/](specification/index.md) | 架构、服务端、小程序、文档和质量规范。 |
| [deploy/](deploy/index.md) | Docker、本地部署和发布检查。 |
| [standards/](standards/README.md) | 通用代码风格、安全、Git 和 Review 规则。 |

## 推荐阅读

| 任务 | 阅读 |
| --- | --- |
| 初始化环境 | [guide/development-environment.md](guide/development-environment.md) |
| 修改服务端 | [../server/README.md](../server/README.md), [specification/server.md](specification/server.md) |
| 修改小程序 | [../app/README.md](../app/README.md), [specification/app-module.md](specification/app-module.md) |
| 修改管理端 | [../web/README.md](../web/README.md), [specification/web.md](specification/web.md) |
| 修改 API | [api-server/index.md](api-server/index.md), [api-app/index.md](api-app/index.md) |
| 修改 Docker 或配置 | [guide/configuration.md](guide/configuration.md), [deploy/index.md](deploy/index.md) |
| 准备提交 | [guide/testing.md](guide/testing.md), [standards/review-checklist.md](standards/review-checklist.md) |
