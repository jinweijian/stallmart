# 测试说明

## 服务端

测试入口：

```text
server/src/test/java/com/stallmart/support/web/ResultTest.java
server/src/test/java/com/stallmart/support/security/JwtServiceTest.java
server/src/test/java/com/stallmart/order/OrderServiceTest.java
server/src/test/java/com/stallmart/persistence/PersistenceMigrationTest.java
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

小程序静态资源、页面样式和顾客端装修约束校验：

```bash
cd app
npm run test:weapp-assets
```

该脚本会检查森系风格包 banner、`assetSizes` 展示尺寸 token、`pageThemes` 跨页配置、`categoryIconLibrary` 分类 icon 库、首页自动轮播结构、四个顾客端 tab 是否接入 `src/utils/customer-theme.ts`、关键静态资源和微信端样式产物，并校验顾客端 mock 数据保留后端 DTO 关键字段，例如商品 `mainImageUrl/specIds/skus`、规格 `/styles/{styleId}/specs`、订单 item `unitPrice/specsText` 和订单统计 `total/pending/preparing/completed`。

前端仍使用 mock 数据时，`src/mock/customer-api.ts` 必须返回与真实接口一致的数据形状；页面内部可以再转换为 ViewModel，但不得依赖只存在于 mock 的字段。

替换小程序原生 tabBar 图标时，先将 8 个源图放入临时素材目录，再运行：

```bash
cd app
npm run optimize:tabbar -- ../tmp/image2
```

该脚本会先按 alpha 通道裁掉透明留白，再在微信单图 `40KB` 限制内寻找最大可用 PNG 尺寸，写入 `src/static/tabbar/`，同步 `dist/static/tabbar/`，并输出每张图的尺寸和体积报告。

## 管理端

管理端使用 Nuxt：

```bash
cd web
npm install
npm run build
```

管理端不维护独立 mock 数据，页面通过后端 `/admin/*` 接口读取初始化内存数据。
管理端不维护独立 mock 数据，页面通过后端 `/admin/*` 接口读取 Flyway dev seed 或真实数据库数据。

## H5 真实 API 调试

小程序域名未配置前，优先用 H5 验证真实数据链路：

```bash
cd app
set TARO_APP_ENABLE_API_MOCK=false
set TARO_APP_ID=wx-stallmart-demo
npm run dev:h5
```

启动后 app shell 会调用 `/app/bootstrap` 写入主题缓存，页面继续读取真实 `/stores`、`/products`、`/styles`、`/orders` 等接口。

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
cd app
npm run test:weapp-assets
```

```bash
cd web
npm run build
```

```bash
cd docker
docker compose config
```
