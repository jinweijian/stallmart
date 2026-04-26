# 项目健康检查

本文件记录当前规范化过程中发现的项目问题，只描述已从代码或配置中确认的事实。

## 高优先级

### 前后端成功码不一致

证据：

- 后端 `backend/src/main/java/com/stallmart/common/result/Result.java` 成功响应返回 `code = 200`。
- 小程序 `mini-program/src/utils/request.ts` 只有 `data?.code === 0` 才 resolve。

影响：

- 后端成功响应会被小程序当作业务失败。

建议：

- 统一为 `200` 或 `0`，并同步 API 文档、前端请求封装和测试。

### 小程序引用未定义 API endpoint

证据：

- `mini-program/src/utils/auth.ts` 使用 `API_ENDPOINTS.USER_BIND_PHONE`。
- `mini-program/src/pages/vendor/my-stall/my-stall.vue` 使用 `API_ENDPOINTS.STORE_INFO`。
- `mini-program/src/app-config/index.ts` 当前没有这两个 key。

影响：

- TypeScript 编译或运行时会失败。

建议：

- 补齐 endpoint 常量，或改用已存在的 `AUTH_BIND_PHONE`、`STORE_DETAIL` 等常量。

### Taro alias 使用本机绝对路径

证据：

- `mini-program/taro.config.ts` 中 alias 指向 `C:/Users/myclaw/...`。

影响：

- 换机器或 CI 环境后路径失效。

建议：

- 使用 `path.resolve(__dirname, 'src')` 和 `path.resolve(__dirname, 'src/app-config')`。

## 中优先级

### Java 版本需要确认

证据：

- `backend/pom.xml` 中 `<java.version>26</java.version>`。
- `docker/Dockerfile.api` 使用 `eclipse-temurin:26-jdk-alpine` 和 `eclipse-temurin:26-jre-alpine`。

影响：

- 团队本地 JDK 或镜像仓库不支持 Java 26 时无法构建。

建议：

- 明确目标版本。如果目标是 Java 21，需要同步修改 Maven、Dockerfile 和文档。

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

### 模块 README 存在编码可读性问题

证据：

- `backend/README.md` 和 `docker/README.md` 中大量中文显示为乱码。

影响：

- 新成员阅读成本高。

建议：

- 用 UTF-8 重写模块 README，或让根目录文档成为唯一入口。

### 小程序测试体系未落地

证据：

- `mini-program/package.json` 有 lint 和 format 脚本，但没有 test 脚本。

影响：

- 请求封装、状态管理、页面逻辑缺少自动化回归。

建议：

- 增加前端测试框架，至少覆盖 `utils/request.ts`、`utils/auth.ts`、store 和核心页面流程。
