# 文档规范

## 目录 README 规则

每个文档目录必须有 `README.md`，并说明：

- 当前文件夹做什么。
- 有哪些文档。
- 做什么事情时应参考哪些文档。
- 与其他目录的关系。

新增文档目录时必须同时新增 README。

## 根 README 规则

根目录 `README.md` 只负责：

- 项目概览。
- 快速启动。
- 各 README 入口。
- 当前关键风险索引。

不要把大量细节堆进根 README。细节放到 `docs/`。

## 文档命名

- 子目录使用 kebab-case 或清晰的单词，例如 `app`、`operations`。
- 文档文件使用 kebab-case，例如 `module-design.md`。
- README 固定大写 `README.md`。

## 写作要求

- 写当前代码能验证的事实。
- 历史规划与当前代码不一致时，明确标注“历史设计”或“当前未落地”。
- 不写模糊词，例如“很快支持”“应该有”。
- 命令必须可复制。
- 路径必须相对当前仓库清晰可找。

## 同步要求

| 改动 | 必须同步 |
| --- | --- |
| 新增页面 | `docs/specification/app-pages.md` |
| 新增小程序模块 | `docs/specification/app-module.md` |
| 新增 API | `docs/api-server/index.md`, `docs/api-app/index.md` |
| 修改配置 | `docs/guide/configuration.md` |
| 修改 Docker | `docs/deploy/index.md`, `docker/README.md` |
| 修改测试命令 | `docs/guide/testing.md` |
| 修改规范 | `docs/standards/README.md` |

## Agent 查阅优化

为了便于 Agent 工作：

- 每个目录 README 都要给出“按任务查阅”。
- 不要让 Agent 需要全文搜索才能知道入口。
- 旧路径迁移后必须更新根 README。
- 发现文档链接失效时，应立即修复。
