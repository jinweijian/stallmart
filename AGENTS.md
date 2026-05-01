# StallMart Agent 工作入口

请用中文与用户沟通。任何修改前先读本文，再按任务范围读取对应文档。

## 强制规则

- 不要直接修改业务代码，除非已经阅读对应模块 README 和规范文档。
- 不要提交 `node_modules/`、`dist/`、`target/`、`.env`、`project.private.config.json`。
- 不要引入本机绝对路径。
- 不要把真实密钥、密码、token、微信密钥写入代码或文档。
- 修改 API 时必须同步 `docs/api-server/index.md`、`docs/api-app/index.md` 和 `docs/guide/testing.md`。
- 修改页面结构时必须同步 `docs/specification/app-pages.md` 和 `docs/specification/app-module.md`。
- 修改配置、Docker、数据库脚本时必须同步 `docs/guide/configuration.md`、`docs/deploy/index.md` 或对应规范。
- 小程序特殊规则保留在 `app/` 内；除小程序平台限制外，项目目录、命名和测试组织按 `edusoho-lms` 风格执行。

## 推荐阅读路径

| 任务类型 | 先读 | 再读 |
| --- | --- | --- |
| 不确定从哪开始 | [docs/README.md](docs/README.md) | [README.md](README.md) |
| 服务端接口/服务/数据库 | [server/README.md](server/README.md) | [docs/specification/server.md](docs/specification/server.md) |
| 小程序页面/状态/API | [app/README.md](app/README.md) | [docs/specification/app-module.md](docs/specification/app-module.md) |
| API 契约 | [docs/api-server/index.md](docs/api-server/index.md) | [docs/api-app/index.md](docs/api-app/index.md) |
| 配置/部署/Docker | [docs/guide/configuration.md](docs/guide/configuration.md) | [docs/deploy/index.md](docs/deploy/index.md) |
| 测试/质量问题 | [docs/guide/testing.md](docs/guide/testing.md) | [docs/specification/project-health.md](docs/specification/project-health.md) |

## 当前项目关键风险

- 小程序仍处于 mock 数据模式，联调前不得误判为真实 API 数据。
- Docker 仍引用未落地的 `admin-web/`。
- 数据库初始化当前以 `docker/mysql/init/01-init.sql` 为准，服务端持久化接入后需要补迁移策略。
