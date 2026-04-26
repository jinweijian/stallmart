# StallMart Agent 工作入口

本文件是后续 Agent 进入 `src/` 仓库后的第一入口。任何修改前先读本文，再按任务范围读对应文档。

## 强制规则

- 不要直接修改业务代码，除非已经阅读对应模块 README 和规范文档。
- 不要提交 `node_modules/`、`dist/`、`target/`、`.env`、`project.private.config.json`。
- 不要引入本机绝对路径。
- 不要把真实密钥、密码、token、微信密钥写入代码或文档。
- 修改 API 时必须同步更新小程序 endpoint、后端 API 文档和测试说明。
- 修改页面结构时必须同步更新小程序模块文档。
- 修改配置、Docker、数据库脚本时必须同步更新 operations 文档。

## 推荐阅读路径

| 任务类型 | 先读 | 再读 |
| --- | --- | --- |
| 不确定从哪开始 | [docs/README.md](docs/README.md) | [README.md](README.md) |
| 小程序页面/状态/API | [docs/mini-program/README.md](docs/mini-program/README.md) | [mini-program/README.md](mini-program/README.md) |
| 后端接口/服务/数据库 | [docs/backend/README.md](docs/backend/README.md) | [backend/README.md](backend/README.md) |
| 代码风格/命名/提交 | [docs/standards/README.md](docs/standards/README.md) | [docs/standards/code-style.md](docs/standards/code-style.md) |
| 配置/部署/Docker | [docs/operations/README.md](docs/operations/README.md) | [docker/README.md](docker/README.md) |
| 测试/质量问题 | [docs/quality/README.md](docs/quality/README.md) | [docs/quality/project-health.md](docs/quality/project-health.md) |

## 当前项目关键风险

- 前端成功码判断与后端成功码不一致。
- 小程序 endpoint 常量存在缺失或命名不一致。
- Docker 引用未落地的 `admin-web/`。
- 两套 SQL 初始化脚本不一致。

详细风险见 [docs/quality/project-health.md](docs/quality/project-health.md)。
