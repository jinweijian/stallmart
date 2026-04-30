# 项目健康检查

本文记录规范化过程中发现的项目问题，只描述已从代码、配置或既有文档中确认的事实。

## 已处理事项

### 前后端成功码不一致

处理结果：

- 后端 `backend/src/main/java/com/stallmart/common/result/Result.java` 成功响应返回 `code = 200`。
- 小程序 `mini-program/src/utils/request.ts` 已兼容 `data?.code === 200` 和历史 `0`。
- API 文档和小程序状态/API 文档已同步当前契约。

### Taro alias 本机绝对路径

处理结果：

- `mini-program/taro.config.ts` 已改为使用 `path.resolve(__dirname, 'src')` 和 `path.resolve(__dirname, 'src', 'app-config')`。
- 后续不得在配置中写入 Windows 用户目录这类本机绝对路径。

### 文档目录初步规范化

处理结果：

- `docs/` 已拆分为 `overview`、`mini-program`、`backend`、`standards`、`operations`、`quality`。
- 每个一级文档目录都有 `README.md`。
- 根 `README.md` 和 `AGENTS.md` 已挂载主要 README 入口。

## 高优先级

### 小程序引用未定义 API endpoint

证据：

- `mini-program/src/utils/auth.ts` 使用 `API_ENDPOINTS.USER_BIND_PHONE`。
- `mini-program/src/pages/vendor/my-stall/my-stall.vue` 使用 `API_ENDPOINTS.STORE_INFO`。
- `mini-program/src/app-config/index.ts` 当前没有这两个 key。

影响：

- TypeScript 编译或运行时可能失败。

建议：

- 补齐 endpoint 常量，或改用已存在的 `AUTH_BIND_PHONE`、`STORE_DETAIL` 等常量。

## 中优先级

### Java 版本需要确认

证据：

- `backend/pom.xml` 中 `<java.version>26</java.version>`。
- `docker/Dockerfile.api` 使用 `eclipse-temurin:26-jdk-alpine` 和 `eclipse-temurin:26-jre-alpine`。

影响：

- 团队本地 JDK 或镜像仓库不支持 Java 26 时无法构建。

建议：

- 明确目标版本。若目标是 Java 21，需要同步修改 Maven、Dockerfile 和文档。

### Docker Compose 引用缺失模块

证据：

- `docker/docker-compose.yml` 中 `admin-web` build context 为 `../admin-web`。
- 当前仓库未看到 `admin-web/` 目录。

影响：

- `docker compose up -d` 启动全部服务会失败。

建议：

- 先只启动 `mysql redis api`，或补齐 `admin-web/` 后再启用完整编排。

### 数据库初始化脚本不一致

证据：

- `backend/mysql/init.sql`
- `docker/mysql/init/01-init.sql`

两者表结构、字段命名、状态值存在差异。

影响：

- 使用不同启动方式时数据库结构可能不一致。

建议：

- 选定一个权威脚本，另一个删除或改成引用同一来源。

## 低优先级

### 小程序测试体系未落地

证据：

- `mini-program/package.json` 有 lint 和 format 脚本，但没有 test 脚本。
- `npm run lint` 当前可运行通过，但仍输出历史 warning。

影响：

- 请求封装、状态管理、页面逻辑缺少自动化回归。

建议：

- 增加前端测试框架，至少覆盖 `utils/request.ts`、`utils/auth.ts`、store 和核心页面流程。

### 后端测试覆盖范围有限

证据：

- 当前仅看到 `backend/src/test/java/com/stallmart/service/UserServiceTest.java`。

影响：

- Controller、认证链路、订单状态流转、数据库脚本一致性缺少自动化保障。

建议：

- 逐步补充 Controller 集成测试、认证测试、订单状态流转测试和数据库初始化验证。
