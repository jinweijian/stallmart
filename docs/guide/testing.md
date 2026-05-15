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
- Java 25 下测试任务通过 Gradle 显式加载 Mockito agent，避免 Mockito inline mock maker 运行时自挂载 agent；测试 JVM 关闭 class data sharing，避免 javaagent 追加 bootstrap classpath 时输出 CDS 警告。
- Spring 集成测试使用 `@SpringBootTest` 和 `@ActiveProfiles("test")`。
- 测试方法命名使用 `should<Outcome>_when<Condition>`。
- 管理端装修权限回归由 `ApiControllerTest` 覆盖：商家可更新 Logo、封面、Banner、展示描述和已上架风格包选择，但提交 `colors/iconUrls/categoryIconUrls/imageUrls/copywriting` 必须返回 HTTP `400`。
- 管理端认证加固由 `PersistenceMigrationTest` 和 `ApiControllerTest` 覆盖：后台账号必须有独立 `password_salt`，新密码可登录，旧密码拒绝，连续失败 3 次后必须验证码。
- 管理端操作日志由 `ApiControllerTest` 覆盖：商家写操作生成商家日志，商家只能读自己的日志；平台写操作生成平台日志，平台可读平台日志和指定商家日志。

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

小程序域名未配置前，优先用 H5 验证真实数据链路。开发调试：

```bash
cd app
TARO_APP_ENABLE_API_MOCK=false TARO_APP_ID=wx-stallmart-demo npm run dev:h5
```

H5 构建验收：

```bash
cd app
TARO_APP_ENABLE_API_MOCK=false TARO_APP_ID=wx-stallmart-demo npm run build:h5
```

Windows CMD 可使用：

```cmd
cd app
set TARO_APP_ENABLE_API_MOCK=false
set TARO_APP_ID=wx-stallmart-demo
npm run dev:h5
```

启动后 app shell 会调用 `/app/bootstrap` 写入主题缓存，页面继续读取真实 `/stores`、`/products`、`/styles`、`/orders` 等接口。

Docker H5 联调验证：

```bash
cd docker
docker compose up -d app-h5
curl -I http://localhost:10086/
curl -s http://localhost:10086/ | grep -E "taro|app"
curl -s -D - -o /tmp/stallmart-h5-cart.png http://localhost:10086/static/storefront/forest/icons/cart.png
file /tmp/stallmart-h5-cart.png
```

预期 H5 返回 `HTTP/1.1 200 OK`，且正文不是目录索引；静态图片必须返回真实图片类型，不得回退为 H5 `index.html`。该容器默认关闭 mock，并通过 `TARO_APP_API_BASE_URL=http://localhost:8081/api/v1` 访问 Docker 后端。

小程序 request 合法域名申请完成前，weapp 构建需要保持通过，但真实 API 联调先不作为阻塞项：

```bash
cd app
npm run build:weapp
```

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
