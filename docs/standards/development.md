# 开发规范

本文定义日常开发流程和跨模块协作规则。更细的代码风格见 [code-style.md](code-style.md)，安全要求见 [security.md](security.md)。

## 分支与提交

- 每个功能或修复使用独立分支。
- 提交保持小步、可回滚，避免把业务改动和纯格式化改动混在一起。
- 提交前至少运行与改动范围相关的检查命令。

建议提交信息格式：

```text
type(scope): summary
```

示例：

```text
docs(root): add project documentation
fix(mini-program): align auth endpoint constants
```

## 目录职责

- 小程序业务页面只放在 `mini-program/src/pages/`。
- 小程序公共状态放在 `mini-program/src/store/`。
- 小程序网络、认证、缓存等工具放在 `mini-program/src/utils/`。
- 小程序 API 常量放在 `mini-program/src/app-config/index.ts`。
- 后端 Controller 只做请求入参、权限上下文和响应转换，不承载复杂业务。
- 后端业务规则放在 Service Impl。
- 数据库访问集中在 Repository/Mapper。
- 项目级说明放根 README，深入文档放 `docs/` 对应子目录。

## TypeScript / 小程序规范

- 使用 `@/*` 从 `mini-program/src` 引用业务代码。
- 不在代码中写本机绝对路径。
- 页面不得直接调用 `Taro.request`，请求统一走 `mini-program/src/utils/request.ts`。
- 用户态统一走 Pinia store，不在多个页面重复维护登录状态。
- 登录、手机号绑定、token refresh、logout 统一走 `mini-program/src/utils/auth.ts`。

## Java / 后端规范

- API 路径统一使用 `/api/v1` 前缀。
- 响应统一使用 `Result<T>`。
- DTO 用于请求体，VO 用于响应体，Entity 不直接暴露给前端。
- 新增接口时同步更新 Swagger 注解和 [../backend/api-reference.md](../backend/api-reference.md)。
- 需要登录的接口应明确校验 `userId` 来源，避免公开路径误放行。

## 配置规范

- 可提交模板文件：`.env.example`、示例配置、非敏感默认值。
- 不提交真实 `.env`、本地密码、微信密钥、JWT secret。
- 本地覆盖配置使用 `application-local.yml` 或未提交的 `.env`。
- 涉及端口、数据库名、外部服务地址的变更需要同步 [../operations/configuration.md](../operations/configuration.md)。

## 文档规范

- 根目录 `README.md` 只保留项目入口、快速开始和文档索引。
- 深入内容写入 `docs/`。
- 文档中只写能从代码、配置或已确认产品文档验证的事实。
- 未落地能力要明确写成“计划中”或“当前未提供”，不要写成已完成。
- 每个 `docs/` 一级目录必须有 `README.md`，说明目录职责、包含文档和任务阅读路径。

## 代码评审重点

- API 成功码、错误码和前端请求封装是否一致。
- DTO/VO 字段名是否和小程序调用一致。
- 是否新增了未忽略的构建产物、依赖目录或本地配置。
- 是否引入了本机绝对路径。
- 是否把真实密钥、账号或 `.env` 提交进仓库。
