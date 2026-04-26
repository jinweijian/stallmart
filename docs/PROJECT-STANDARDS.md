# 项目规范

## 仓库规范

- 根目录只放跨模块文件，如 `README.md`、`.gitignore`、`.editorconfig`、`.gitattributes`、`docs/`。
- 模块内部 README 只描述模块自身，不重复维护整站说明。
- 生成产物、依赖目录和本地私有配置不进入 Git。

## 编码规范

- 所有文本文件使用 UTF-8。
- 默认使用 LF 换行。
- TypeScript、Vue、JSON、YAML、Markdown 使用 2 空格缩进。
- Java 使用 4 空格缩进。
- Markdown 文件保留行尾空格，用于必要的换行语义。

## 命名规范

- 前端页面目录使用 kebab-case，例如 `confirm-order`。
- 后端 Java 类遵循标准 PascalCase。
- API 常量使用 UPPER_SNAKE_CASE。
- 文档文件使用大写主题名或清晰的 kebab-case，保持 `docs/` 内一致。

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

## API 规范

- 后端成功响应当前为 `code: 200`。
- 前端请求封装必须和后端成功码保持一致。
- 需要登录的接口统一使用 `Authorization: Bearer <token>`。
- 新增 endpoint 必须同步更新小程序 `API_ENDPOINTS` 和 `docs/API.md`。

## 文档维护规范

当以下内容变化时必须更新文档：

- 依赖版本或运行环境。
- API 路径、请求体、响应体。
- 环境变量。
- Docker 服务、端口、镜像。
- 数据库初始化脚本。
- 启动、测试、构建命令。
