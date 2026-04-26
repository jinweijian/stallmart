# 项目规范

本文是 StallMart 当前仓库的项目级约束，覆盖目录、编码、命名、忽略、安全、API 和文档维护。它由外层 `review/code-style.md` 拆解补充，并结合当前代码落地。

## 仓库规范

- 根目录只放跨模块文件，例如 `README.md`、`AGENTS.md`、`.gitignore`、`.editorconfig`、`.gitattributes`、`.prettierrc`、`docs/`。
- 模块内部 README 只描述模块自身，不重复维护整站说明。
- 生成产物、依赖目录和本地私有配置不进入 Git。
- 对项目事实有疑问时，先读根目录 [../../README.md](../../README.md)，再读对应模块目录的 README。

## 编码规范

- 所有文本文件使用 UTF-8。
- 默认使用 LF 换行。
- TypeScript、Vue、JSON、YAML、Markdown 使用 2 空格缩进。
- Java 使用 4 空格缩进。
- Markdown 文件允许为必要换行语义保留行尾两个空格，其余文件不保留行尾空格。
- 默认使用单引号、无分号、100 字符软换行，除非已有文件明确采用其他风格。

## 命名规范

- 小程序页面目录使用 kebab-case，例如 `confirm-order`。
- 小程序 composable、工具函数和普通变量使用 camelCase。
- 小程序类型、接口和组件名使用 PascalCase。
- 后端 Java 类遵循标准 PascalCase。
- 后端包名使用全小写。
- API 常量使用 UPPER_SNAKE_CASE。
- 文档文件使用清晰的 kebab-case；目录 README 固定命名为 `README.md`。

## Git 忽略策略

必须忽略：

- `node_modules/`
- `dist/`
- `target/`
- `.env`
- `application-local.yml`
- `project.private.config.json`
- IDE 和系统文件

必须保留：

- `package-lock.json`
- `.env.example`
- `docker/mysql/init/*.sql`
- `backend/src/main/resources/application.yml`

## 安全规范

- `JWT_SECRET`、数据库密码、Redis 密码、微信密钥只放在本地 `.env` 或安全的部署环境变量中。
- 不在文档中写真实生产密钥。
- 本地 Docker 端口优先绑定 `127.0.0.1`。
- 对外部署前必须确认 CORS、TLS、数据库账号权限和日志脱敏。
- 手机号、微信 openId、unionId、token 不得以明文形式进入普通业务日志。

## API 规范

- 后端成功响应当前为 `code: 200`。
- 前端请求封装必须和后端成功码保持一致。
- 需要登录的接口统一使用 `Authorization: Bearer <token>`。
- 新增 endpoint 必须同步更新小程序 `API_ENDPOINTS` 和 [../backend/api-reference.md](../backend/api-reference.md)。
- API 字段命名以小程序实际消费模型和后端 DTO/VO 为准，禁止同一概念多套字段名并存。

## 模块边界规范

- 小程序模块划分以 [../mini-program/module-design.md](../mini-program/module-design.md) 为准。
- 后端分层规范以 [../backend/backend-standards.md](../backend/backend-standards.md) 为准。
- 任何跨模块变更必须同步检查 [../overview/system-architecture.md](../overview/system-architecture.md)。

## 文档维护规范

当以下内容变化时必须更新文档：

- 依赖版本或运行环境。
- API 路径、请求体、响应体。
- 页面路由、分包、tabBar。
- 环境变量。
- Docker 服务、端口、镜像。
- 数据库初始化脚本。
- 启动、测试、构建命令。

新增文档时应放入合适的 `docs/` 子目录，并更新该目录 `README.md` 与根 [../../README.md](../../README.md) 的索引。
