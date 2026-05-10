# 服务端重构方案

本文面向 `server/` 的渐进式重构。目标不是重写系统，而是在保持现有 API 行为、测试入口和数据库结构稳定的前提下，逐步提高服务端可读性、模块边界、事务一致性和长期维护能力。

## 当前观察

已检查：

- `server/README.md`
- `docs/specification/server.md`
- `docs/specification/architecture.md`
- `docs/guide/testing.md`
- `server/src/main/java/com/stallmart/store/internal/service/StoreServiceImpl.java`
- `server/src/main/java/com/stallmart/management/internal/api/VendorAdminController.java`
- `server/src/main/java/com/stallmart/order/internal/service/OrderServiceImpl.java`
- `server/src/main/java/com/stallmart/cart/internal/service/CartServiceImpl.java`

主要现状：

- `StoreServiceImpl` 约 650 行，同时负责店铺、商品、分类、风格包、规格、装修合并、DTO 转换和校验。
- `VendorAdminController` 约 330 行，同时负责 HTTP 接口、商家上下文解析、工作台聚合、用户归属筛选、文件上传和订单动作分发。
- `OrderServiceImpl` 和 `CartServiceImpl` 在 DTO 转换时逐单查询明细，后续数据量增加后容易形成 N+1 查询。
- 订单状态、商品状态、店铺状态、规格类型等仍以字符串散落在服务实现和 Controller 中。
- 当前模块已有 `{domain}/dto/internal` 目录约定，但实际业务能力仍集中在少数 Service 和 Controller 中。

## 核心问题与风险

### 1. 店铺模块承担过多职责

问题：

- `StoreServiceImpl` 已经成为商品目录、风格包、装修、分类和规格的混合服务。
- 私有方法之间隐式依赖较多，例如商品 DTO 转换依赖分类、SKU、规格；装修 DTO 合并依赖风格包、分类 icon 和 JSON 字段。

风险：

- 新增商品或装修规则时，很容易改到无关流程。
- 单元测试难以聚焦，只能通过较重的集成测试覆盖。
- 未来接入 Spring Modulith 后，模块公开 API 和内部实现边界不清晰。

### 2. 管理端 Controller 写入了应用层逻辑

问题：

- `VendorAdminController` 中包含 `buildWorkspace`、`usersForStore`、`salesAmount`、`uploadAsset`、`extensionOf` 等非 HTTP 编排逻辑。
- `/orders/{orderId}/{action}` 使用字符串 action 做运行时分发。
- Controller 直接处理文件保存路径和上传限制。

风险：

- Controller 变成应用服务，违背“Controller 只负责参数接收与响应”的约定。
- 文件上传策略、工作台聚合和订单动作无法独立测试。
- 路由层改动容易引入业务回归。

### 3. 状态流转和业务规则缺少显式模型

问题：

- 订单状态流转通过 `transition(id, nextStatus, requiredCurrentStatus)` 和字符串参数表达。
- 商品状态、风格包状态、分类状态分散在不同方法中。
- `buildPendingOrderNo`、`confirmCode` 生成规则直接写在订单服务里。

风险：

- 状态机扩展时容易漏改前后端一致性。
- 错误状态可能直到运行期才暴露。
- 业务规则难以形成可读、可测试的单元。

### 4. 查询与 DTO 映射存在扩展性隐患

问题：

- `OrderServiceImpl.toDTO` 每个订单查询一次订单明细。
- `CartServiceImpl.toDTO` 每个购物车查询一次购物车明细。
- `StoreServiceImpl.toProductDTO` 每个商品查询分类和 SKU。

风险：

- 管理端列表、工作台聚合和小程序首页数据量上来后，查询次数随行数线性放大。
- 后续引入 MySQL 慢查询排查时，问题会分散在多个 DTO mapper 中。

## 重构总原则

- 保持外部 API 契约不变，先移动代码再优化行为。
- 优先拆应用服务和领域规则，不提前引入复杂框架。
- 事务边界放在 Application Service，Repository 只做数据访问。
- 每次改动都以可验证的测试闭环结束。
- 先保护高风险行为，再拆大类。

## 方案一：应用层拆分与 Controller 减负

目标：

- 让 Controller 回到路由、参数、响应的职责。
- 将商家工作台、文件上传、订单动作分发拆入明确的应用服务。

建议新增或调整：

```text
server/src/main/java/com/stallmart/management/
  VendorWorkspaceService.java
  VendorAssetService.java
  VendorOrderCommandService.java
  internal/service/
    VendorWorkspaceServiceImpl.java
    LocalVendorAssetService.java
    VendorOrderCommandServiceImpl.java
```

分工：

| 类 | 职责 | 不应做什么 |
| --- | --- | --- |
| `VendorAdminController` | 读取请求、调用应用服务、返回 `Result<T>` | 聚合工作台、筛选用户、保存文件、判断订单动作 |
| `VendorWorkspaceService` | 聚合店铺、装修、商品、订单、购物车、用户和销售额 | 处理 HTTP 请求 |
| `VendorAssetService` | 校验文件类型、大小、生成相对 URL、保存本地文件 | 读取 Controller 或订单上下文 |
| `VendorOrderCommandService` | 将明确命令映射到订单服务，并做商家归属校验 | 暴露任意字符串 action |

推荐步骤：

1. 为 `VendorAdminController.summary`、`users`、`userOrders`、`uploadProductImage`、`transitionOrder` 补充或扩展集成测试。
2. 提取 `VendorWorkspaceService`，保持返回 `VendorWorkspaceDTO` 不变。
3. 提取 `VendorAssetService`，将 `MAX_PRODUCT_IMAGE_SIZE`、扩展名判断和 `/uploads/stores/...` URL 规则集中。
4. 将 `/orders/{orderId}/{action}` 内部先改为 `VendorOrderAction` 枚举映射，外部 URL 暂时保持不变。
5. Controller 只保留 `resolveStore(request)`、参数绑定和 `Result.success(...)`。

验收：

- `VendorAdminController` 不再包含文件系统、销售额计算、用户集合筛选逻辑。
- `VendorAdminController` 单个方法保持短小，复杂度主要转移到可测试服务。
- `./gradlew test` 通过。

## 方案二：店铺域服务拆分为目录、装修、风格包三个内聚能力

目标：

- 拆解 `StoreServiceImpl` 的职责，不破坏现有 `StoreService` 对外接口。
- 为未来 Spring Modulith 模块边界打基础。

建议先在 `store/internal/service` 内做内部拆分，不急于改变公开接口：

```text
store/internal/service/
  StoreProfileService.java
  CatalogCommandService.java
  CatalogQueryService.java
  StoreDecorationAssembler.java
  StorefrontCategoryAssembler.java
  StylePackageService.java
  ProductSnapshotMapper.java
```

分工：

| 类 | 职责 |
| --- | --- |
| `StoreProfileService` | 店铺基础信息读取与更新 |
| `CatalogCommandService` | 商品、分类、SKU、规格写入校验 |
| `CatalogQueryService` | 商品、分类、规格查询，后续承接批量加载优化 |
| `StoreDecorationAssembler` | 合并风格包默认值与店铺装修覆盖值 |
| `StorefrontCategoryAssembler` | 根据分类和 icon library 生成小程序展示分类 |
| `StylePackageService` | 风格包创建、更新、上架、下架、删除和契约校验 |
| `ProductSnapshotMapper` | 商品 DTO 和订单/购物车快照所需字段转换 |

推荐步骤：

1. 先为 `getDecoration`、`createProduct`、`updateProduct`、`deleteSpec`、`listProducts` 增加服务级测试，锁定当前行为。
2. 提取纯函数型 assembler，例如 `mergeMap`、`resolveCategoryIconLibrary`、`resolveStorefrontCategories`、`normalizeCategoryIconKey`。
3. 提取风格包校验与状态规范化，避免 `ACTIVE`、`INACTIVE` 字符串继续扩散。
4. 提取商品写入服务，保留 `StoreServiceImpl` 作为门面，先委托给内部服务。
5. 最后再评估是否将 `product/`、`style/` 从 DTO 包提升为独立应用服务，避免一次性大迁移。

验收：

- `StoreServiceImpl` 变成薄门面，主要做接口兼容和委托。
- 装修合并、风格包校验、商品 SKU 校验均可独立单测。
- 单个服务类职责能用一句话说清楚。

## 方案三：状态模型、查询优化与事务边界收敛

目标：

- 降低字符串状态和 N+1 查询造成的稳定性风险。
- 让订单、购物车和商品查询更适合真实 MySQL 数据量。

建议新增：

```text
order/internal/model/OrderStatus.java
order/internal/service/OrderStatusTransition.java
order/internal/service/OrderNumberGenerator.java
store/internal/model/ProductStatus.java
store/internal/model/StyleStatus.java
```

推荐步骤：

1. 为订单状态流转补充测试：合法流转、非法流转、拒单、完成后不可回退。
2. 引入 `OrderStatus` 枚举，但 DTO 仍输出字符串，保持 API 不变。
3. 用 `OrderStatusTransition` 表达 `NEW -> ACCEPTED -> PREPARING -> READY -> COMPLETED` 和 `NEW -> REJECTED`。
4. 提取 `OrderNumberGenerator`，集中管理订单号和取餐码生成规则。
5. Repository 增加批量查询明细方法，例如 `findByOrderIdInOrderByOrderIdAscIdAsc`、`findByCartIdInOrderByCartIdAscIdAsc`。
6. 列表查询先加载主表，再批量加载明细，并在 mapper 中按 id 分组。
7. 对工作台聚合类查询增加服务级测试，验证返回结构不变。

数据库注意事项：

- 订单列表按 `store_id, created_at` 查询，购物车按 `store_id, updated_at` 查询，后续迁移应补充组合索引。
- 避免在循环内调用 Repository。
- 删除或替换 SKU 时要评估历史订单快照，订单 item 必须继续保留商品名称、规格文案和单价快照。

验收：

- 订单状态流转规则集中在一个可测试类中。
- 列表接口查询次数不随订单数量线性增长。
- `@Transactional` 仍放在创建订单、订单状态流转、商品写入、装修更新等应用服务方法上。

## 推荐执行顺序

1. 先做方案一，风险最低，能快速降低 Controller 复杂度。
2. 再做方案三中的订单状态模型和查询优化，因为它直接影响稳定性。
3. 最后做方案二的大服务拆分，逐步把 `StoreServiceImpl` 拆成薄门面和内部能力类。

## 测试与回归清单

每个阶段至少执行：

```bash
cd server
./gradlew test
```

重点补充：

- `VendorWorkspaceServiceImplTest`
- `LocalVendorAssetServiceTest`
- `VendorOrderCommandServiceImplTest`
- `StoreDecorationAssemblerTest`
- `StylePackageServiceTest`
- `OrderStatusTransitionTest`
- `OrderServiceImplTest` 的列表明细批量加载回归

## 不建议现在做的事

- 不建议一次性改 API 路径或 DTO 字段。
- 不建议立即拆成多服务或引入消息总线。
- 不建议为了 Spring Modulith 形式而强行移动包，先让依赖方向和公开接口自然变清晰。
- 不建议把所有状态都数据库枚举化后再改代码，先在 Java 侧收敛规则。
