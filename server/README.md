# StallMart 服务端

本目录是 StallMart 服务端，基于 Spring Boot。目录和命名参考 `edusoho-lms/server`：服务按领域拆包，领域公开接口放模块根目录，DTO/Params 放 `dto/`，实现细节放 `internal/`。

## 目录结构

```text
server/
  src/main/java/com/stallmart/
    auth/                 认证模块
    order/                订单模块
    product/              商品模块
    store/                店铺模块
    style/                风格与规格模块
    user/                 用户模块
    cart/                 购物车模块
    management/           平台和商家管理 API
    support/              通用响应、异常、安全上下文等支撑代码
```

领域模块约定：

```text
{domain}/
  {Domain}Service.java
  dto/
    XxxDTO.java
    XxxParams.java
  internal/
    api/
      XxxController.java
    service/
      XxxServiceImpl.java
```

## 本地命令

```bash
./gradlew test
./gradlew bootRun
```

接口基础路径为 `/api/v1`。

## 单元测试

### 运行测试

由于项目运行在 Docker 中，建议使用 Docker 执行单元测试：

```bash
# 在 Docker 中运行测试并生成覆盖率报告
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:25-jdk-alpine ./gradlew test jacocoTestReport --no-daemon
```

或者使用本地 Java 环境：

```bash
./gradlew test jacocoTestReport
```

### 测试覆盖率

项目使用 JaCoCo 生成测试覆盖率报告。运行测试后，覆盖率报告位于：

- **HTML 报告**: `build/reports/jacoco/test/html/index.html`
- **XML 报告**: `build/reports/jacoco/test/jacocoTestReport.xml`

当前覆盖率统计：
- **指令覆盖率**: 71%
- **分支覆盖率**: 45%

### 测试规范

- 使用 JUnit 5 作为测试框架
- 断言优先使用 AssertJ
- Spring 集成测试使用 `@SpringBootTest` + `@ActiveProfiles("test")`
- 测试方法命名规范: `should<Outcome>_when<Condition>`
- 测试数据库使用 H2 内存数据库（MySQL 兼容模式）
- 测试配置文件: `src/test/resources/application-test.yml`

### 测试文件位置

测试文件位于 `src/test/java/com/stallmart/` 目录下，与主代码结构对应。

## 必读文档

- [服务端规范](../docs/specification/server.md)
- [服务端 API](../docs/api-server/index.md)
- [测试说明](../docs/guide/testing.md)
