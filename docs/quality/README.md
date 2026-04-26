# quality 文档目录

本目录负责测试、质量风险、健康检查和验收前检查。

## 文档列表

| 文档 | 作用 |
| --- | --- |
| [testing.md](testing.md) | 当前测试入口、推荐检查命令、测试风险。 |
| [project-health.md](project-health.md) | 当前项目已知问题和优先级。 |

## 做什么事应参考这里

- 准备提交前跑检查。
- 做 Code Review 前查当前风险。
- 修复联调问题。
- 判断某个历史设计是否已经落地。
- 为后续测试补用例。

## 当前高优先级质量问题

- 前后端成功码不一致。
- 小程序 endpoint 常量缺失。
- Docker 引用缺失的 `admin-web/`。
- 两套 SQL 初始化脚本不一致。

详细说明见 [project-health.md](project-health.md)。
