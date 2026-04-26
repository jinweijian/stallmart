# Git 与提交规范

## 分支规范

| 分支类型 | 命名 | 用途 |
| --- | --- | --- |
| 主分支 | `main` | 稳定版本。 |
| 开发分支 | `develop` | 集成开发。 |
| 功能分支 | `feature/<scope>` | 新功能。 |
| 修复分支 | `fix/<scope>` | Bug 修复。 |
| 文档分支 | `docs/<scope>` | 文档整理。 |
| 发布分支 | `release/vX.Y.Z` | 发布准备。 |

当前仓库处于初始未跟踪状态时，先完成基础提交，再开展功能分支管理。

## 提交信息

格式：

```text
<type>(<scope>): <subject>
```

可选关联任务：

```text
feat(order): add vendor accept flow #T-v1.0.0-007
```

type 取值：

| type | 用途 |
| --- | --- |
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档 |
| `style` | 纯格式，不影响行为 |
| `refactor` | 重构，不改变行为 |
| `test` | 测试 |
| `chore` | 工具、依赖、构建 |

## 提交前检查

通用：

```bash
git diff --check
```

小程序：

```bash
cd mini-program
npm run lint
npm run build:weapp
```

后端：

```bash
cd backend
mvn test
```

Docker：

```bash
cd docker
docker compose config
```

文档：

```bash
git diff --check
```

## 不允许混在一个提交里的改动

- 业务功能 + 大规模格式化。
- 前端 API 调整 + 后端无关重构。
- 文档迁移 + 业务修复。
- 密钥清理 + 功能开发。

## 提交内容检查

提交前确认：

- 没有 `node_modules/`。
- 没有 `dist/`。
- 没有 `target/`。
- 没有 `.env`。
- 没有 `project.private.config.json`。
- 没有本机绝对路径。
- 文档链接没有指向旧路径。
