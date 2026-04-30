# StallMart Backend

本目录是 StallMart 后端 API 服务，当前代码基于 Spring Boot 3、MyBatis-Plus、MySQL、Redis、JWT 和 SpringDoc OpenAPI。

## 目录职责

```text
backend/
  pom.xml
  docker-compose.yml
  mysql/init.sql
  src/main/java/com/stallmart/
    StallmartApplication.java
    common/       # 通用结果、错误码、异常、上下文
    config/       # Spring Security、JWT、OpenAPI、跨域等配置
    controller/   # HTTP 入口，只做参数接收、上下文读取、响应转换
    model/        # entity、dto、vo、enum
    repository/   # MyBatis-Plus Mapper 和数据访问
    service/      # 业务接口与实现
  src/main/resources/
    application.yml
    mapper/
  src/test/java/
```

## 先读哪些文档

| 工作类型 | 必读文档 |
| --- | --- |
| 修改或新增接口 | [../docs/backend/api-reference.md](../docs/backend/api-reference.md), [../docs/backend/backend-standards.md](../docs/backend/backend-standards.md) |
| 修改登录、token、鉴权 | [../docs/backend/api-reference.md](../docs/backend/api-reference.md), [../docs/standards/security.md](../docs/standards/security.md) |
| 修改数据库或环境变量 | [../docs/operations/configuration.md](../docs/operations/configuration.md), [../docs/overview/system-architecture.md](../docs/overview/system-architecture.md) |
| 提交代码前自查 | [../docs/standards/review-checklist.md](../docs/standards/review-checklist.md), [../docs/quality/testing.md](../docs/quality/testing.md) |

## 本地运行

启动 MySQL 和 Redis：

```bash
docker compose up -d
```

启动 API：

```bash
mvn spring-boot:run
```

默认地址：

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## 测试

```bash
mvn test
```

当前测试位于：

```text
src/test/java/com/stallmart/service/UserServiceTest.java
```

## 配置

主配置文件：

```text
src/main/resources/application.yml
```

重要环境变量：

- `DB_PASSWORD`
- `REDIS_PASSWORD`
- `JWT_SECRET`

本地私有配置建议放在 `src/main/resources/application-local.yml`，该文件不应提交。

## API 约束

- Base URL: `/api/v1`
- 统一响应：`com.stallmart.common.result.Result`
- 当前成功码：`code: 200`
- 认证方式：`Authorization: Bearer <access_token>`
- 路由详情见 [../docs/backend/api-reference.md](../docs/backend/api-reference.md)

## 当前注意事项

- `pom.xml` 当前声明 Java 26，运行前需要确认本地 JDK 版本。
- `mysql/init.sql` 与根级 `docker/mysql/init/01-init.sql` 存在结构差异，需要统一权威脚本。
- 修改接口后必须同步更新 [../docs/backend/api-reference.md](../docs/backend/api-reference.md) 和小程序 `API_ENDPOINTS`。
