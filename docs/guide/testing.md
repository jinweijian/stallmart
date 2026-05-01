# 测试说明

## 服务端

测试入口：

```text
server/src/test/java/com/stallmart/support/web/ResultTest.java
server/src/test/java/com/stallmart/support/security/JwtServiceTest.java
server/src/test/java/com/stallmart/order/OrderServiceTest.java
server/src/test/java/com/stallmart/web/ApiControllerTest.java
```

运行：

```bash
cd server
./gradlew test
```

服务端测试约定：

- 使用 JUnit 5。
- 断言优先使用 AssertJ。
- Spring 集成测试使用 `@SpringBootTest` 和 `@ActiveProfiles("test")`。
- 测试方法命名使用 `should<Outcome>_when<Condition>`。

## 小程序

当前小程序没有单元测试框架，保持现有 lint 基线：

```bash
cd app
npm run lint
```

前端仍使用 mock 数据，本次初始化不修改 mock 数据和 mock 策略。

## 管理端

管理端使用 Nuxt：

```bash
cd web
npm install
npm run build
```

管理端不维护独立 mock 数据，页面通过后端 `/admin/*` 接口读取初始化内存数据。

## 提交前检查

```bash
git diff --check
```

```bash
cd server
./gradlew test
```

```bash
cd app
npm run lint
```

```bash
cd web
npm run build
```

```bash
cd docker
docker compose config
```
