# 测试说明

## 当前测试入口

后端已有 JUnit/Spring Boot 测试：

```text
backend/src/test/java/com/stallmart/service/UserServiceTest.java
```

运行命令：

```bash
cd backend
mvn test
```

小程序当前 `package.json` 没有单元测试脚本，只有 lint 和格式化：

```bash
cd mini-program
npm run lint
npm run format
```

## 建议的提交前检查

仅改文档：

```bash
git diff --check
```

改小程序代码：

```bash
cd mini-program
npm run lint
npm run build:weapp
```

改后端代码：

```bash
cd backend
mvn test
```

改 Docker 配置：

```bash
cd docker
docker compose config
```

## 当前测试风险

- 后端测试依赖 Spring 上下文和数据库，运行前需要可用 MySQL/Redis 或测试专用配置。
- `backend/pom.xml` 使用 Java 26，测试环境 JDK 版本不匹配时会直接失败。
- 小程序未配置明确的单元测试框架。
- 小程序 lint 脚本存在，但当前仓库未看到独立 ESLint 配置文件，首次运行可能需要补齐配置。

## 建议补充测试

- 后端 Controller 层 WebMvc 测试，覆盖认证、成功响应和错误响应。
- Service 层测试使用测试数据库或 Testcontainers。
- 小程序请求封装测试，覆盖成功码、401 refresh、logout。
- API endpoint 常量和后端路由的一致性检查。
