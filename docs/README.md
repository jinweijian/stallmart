# 文档总览

本目录是 `src/` 仓库的正式文档入口。后续开发者和 Agent 应优先阅读本目录，而不是直接依赖外层 `specs/`、`review/`、`tasks/` 中的历史材料。

外层材料已经被整理进本目录：

- 外层 `specs/PRD_v1.0_StallMart.md` -> 产品范围、端定位、订单流程、小程序模块。
- 外层 `specs/architecture.md` -> 技术架构、数据模型、API 和 ADR。
- 外层 `specs/auth.md` -> JWT 生命周期、刷新、登出、手机号绑定。
- 外层 `specs/design-system.md` -> 色彩、字体、间距、组件、风格包。
- 外层 `review/code-style.md` -> 代码风格、命名、Git、Review、安全、日志、注释。
- 外层 `test/test-cases_v1.0.md` -> API 测试和安全测试方向。

## 文档目录职责

| 目录 | 作用 | README |
| --- | --- | --- |
| `overview/` | 项目概览、快速开始、系统架构。 | [overview/README.md](overview/README.md) |
| `mini-program/` | 小程序模块划分、页面边界、状态/API、交互规范。 | [mini-program/README.md](mini-program/README.md) |
| `backend/` | 后端 API、接口契约、分层约束。 | [backend/README.md](backend/README.md) |
| `standards/` | 项目规范、代码风格、Git、Review、安全、文档规范。 | [standards/README.md](standards/README.md) |
| `operations/` | 配置、部署、本地 Docker、环境变量。 | [operations/README.md](operations/README.md) |
| `quality/` | 测试策略、健康检查、已知风险。 | [quality/README.md](quality/README.md) |

## 按任务查阅

| 要做的事 | 必读文档 |
| --- | --- |
| 新 Agent 接手项目 | [../AGENTS.md](../AGENTS.md), [overview/getting-started.md](overview/getting-started.md) |
| 修改小程序页面 | [mini-program/README.md](mini-program/README.md), [mini-program/module-design.md](mini-program/module-design.md), [mini-program/page-map.md](mini-program/page-map.md) |
| 修改小程序请求/登录 | [mini-program/state-and-api.md](mini-program/state-and-api.md), [backend/api-reference.md](backend/api-reference.md) |
| 修改后端接口 | [backend/README.md](backend/README.md), [backend/api-reference.md](backend/api-reference.md), [standards/code-style.md](standards/code-style.md) |
| 修改数据库脚本 | [overview/system-architecture.md](overview/system-architecture.md), [operations/configuration.md](operations/configuration.md), [quality/project-health.md](quality/project-health.md) |
| 修改 Docker 或环境变量 | [operations/README.md](operations/README.md), [operations/configuration.md](operations/configuration.md), [operations/deployment.md](operations/deployment.md) |
| 准备提交 | [standards/git-workflow.md](standards/git-workflow.md), [quality/testing.md](quality/testing.md), [../CONTRIBUTING.md](../CONTRIBUTING.md) |
| 做 Code Review | [standards/review-checklist.md](standards/review-checklist.md), [quality/project-health.md](quality/project-health.md) |

## 文档维护规则

- 新增文档必须放入合适子目录，并更新该目录的 `README.md`。
- 新增文档目录必须包含 `README.md`。
- 根目录 `README.md` 只做项目入口，不承载深层细节。
- 文档中不要写无法从代码、配置或上游规格确认的事实。
- 如果实现与上游规格冲突，以当前代码事实为准，并在 [quality/project-health.md](quality/project-health.md) 记录差异。
