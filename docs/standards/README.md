# standards 文档目录

本目录负责项目级规范。这里的文档是后续代码质量和风格统一的依据。

## 文档列表

| 文档 | 作用 |
| --- | --- |
| [project-standards.md](project-standards.md) | 项目结构、命名、安全、文档维护等总规范。 |
| [development.md](development.md) | 日常开发规范。 |
| [code-style.md](code-style.md) | 从 `review/code-style.md` 拆解后的代码风格强制规则。 |
| [git-workflow.md](git-workflow.md) | 分支、提交、提交前检查规则。 |
| [review-checklist.md](review-checklist.md) | Code Review 检查清单。 |
| [security.md](security.md) | 密钥、认证、日志、输入安全规范。 |
| [documentation.md](documentation.md) | 文档写作、目录 README、同步规则。 |

## 做什么事应参考这里

- 修改代码风格、格式化配置、lint 规则。
- 准备提交或合并。
- 做 Code Review。
- 新增目录或模块。
- 修改文档结构。
- 处理认证、敏感信息、日志和外部服务。

## 规范来源

本目录吸收并落地了外层 `review/code-style.md` 中的内容：

- 后端 Java/Spring Boot 分层与命名。
- API 统一响应、错误码、分页、REST 约束。
- 小程序 Vue3/Taro/Pinia/Tailwind/rpx 约束。
- Git 提交和分支规范。
- Review 严重程度和检查项。
- 日期格式、日志、安全、注释规范。

## 强制执行点

- `.editorconfig` 负责基础缩进和换行。
- `.gitattributes` 负责文本换行一致性。
- `.gitignore` 负责排除依赖、构建产物和本地私有配置。
- `.prettierrc` 负责前端/文档格式化基线。
- `AGENTS.md` 负责 Agent 阅读入口。
