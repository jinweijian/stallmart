# 服务端规范

本文参考 `edusoho-lms/server` 的组织方式，并结合 StallMart 当前 Spring Boot 服务落地。

## 模块结构

服务端代码按业务领域拆包：

```text
com.stallmart.order
com.stallmart.store
com.stallmart.product
com.stallmart.style
com.stallmart.user
com.stallmart.auth
com.stallmart.cart
com.stallmart.management
com.stallmart.support
```

领域模块必须遵守：

| 路径 | 职责 |
| --- | --- |
| `{domain}/{Domain}Service.java` | 对外暴露的业务服务接口。 |
| `{domain}/dto/*DTO.java` | 响应对象或跨层传输对象。 |
| `{domain}/dto/*Params.java` | 请求或服务入参对象。 |
| `{domain}/internal/api/*Controller.java` | HTTP API。 |
| `{domain}/internal/service/*ServiceImpl.java` | 业务实现。 |
| `{domain}/internal/repository/*Repository.java` | 后续持久化接入后的仓储。 |
| `{domain}/internal/model/*Status.java` | 状态枚举，替代字符串常量。 |

`support/` 只放通用能力，例如统一响应、异常、安全上下文、工具类。业务规则不得放入 `support/`。

## 命名

- Controller: `Admin*Controller`、`User*Controller` 或明确业务名的 `*Controller`。
- Service: 接口无 `I` 前缀，实现类使用 `Impl` 后缀。
- DTO: 响应对象使用 `DTO` 后缀。
- Params: 请求或命令入参使用 `Params` 后缀。
- Repository: 持久化接口使用 `Repository` 后缀。
- 包名全小写，目录使用领域名。

## Controller

- Controller 只处理路由、入参校验、认证上下文读取和响应转换。
- Controller 返回 `Result<T>`。
- Controller 不写业务规则，不直接访问 Repository。
- Controller 不处理文件上传逻辑，文件操作交给应用服务。
- Controller 不做订单动作分发，命令映射交给应用服务。
- 新增接口必须同步 [../api-server/index.md](../api-server/index.md)。

## Service

- 业务规则放在 `internal/service/*ServiceImpl.java`。
- 状态流转、权限校验和数据一致性必须在服务端兜底。
- 后续接入数据库时，创建订单、状态流转、手机号绑定等操作必须考虑事务。
- 复杂业务拆分为应用服务（Application Service）和领域服务（Domain Service）。

应用服务拆分规范：

| 服务类型 | 职责 | 示例 |
| --- | --- | --- |
| Application Service | 编排多个领域服务、处理事务边界 | VendorWorkspaceService, VendorOrderCommandService |
| Domain Service | 单一领域内的业务规则 | OrderStatusTransition, ProductCatalogService |
| Assembler | DTO 转换和数据组装 | StoreDecorationAssembler, StorefrontCategoryAssembler |

拆分原则：

- 单个 Service 类职责能用一句话说清楚。
- 超过 300 行的 Service 必须考虑拆分。
- 私有方法之间有隐式依赖时，考虑提取独立的 Assembler 或 Domain Service。
- 事务边界放在 Application Service，Repository 只做数据访问。

## 异常和错误码

- 业务异常使用 `AppException`。
- 错误码集中维护在 `support/exception/ErrorCode.java`。
- 参数校验错误交给 `GlobalExceptionHandler`。
- 不在 Controller 中手写魔法数字。

## 测试

- 测试目录镜像主包结构，放在 `server/src/test/java`。
- 服务测试命名为 `*ServiceTest` 或 `*ServiceImplTest`。
- 集成测试使用 `@SpringBootTest` 和 `@ActiveProfiles("test")`。
- 断言优先使用 AssertJ。
- 测试方法命名使用 `should<Outcome>_when<Condition>`。

## 状态枚举规范

**强制要求**：所有业务状态必须使用枚举类，禁止使用字符串常量。

枚举类放在 `{domain}/internal/model/` 目录下，命名格式为 `{Entity}Status`。

示例：

```text
store/internal/model/
  ProductStatus.java      # ACTIVE, INACTIVE, SOLD_OUT
  SkuStatus.java          # ACTIVE, INACTIVE, SOLD_OUT
  CategoryStatus.java     # ACTIVE, INACTIVE
  CategoryModule.java     # PRODUCT, STYLE
  StoreStatus.java        # ACTIVE, INACTIVE, CLOSED
  StoreStyleStatus.java   # ACTIVE, INACTIVE
  SpecType.java           # TEXT, COLOR, IMAGE
order/internal/model/
  OrderStatus.java        # NEW, ACCEPTED, PREPARING, READY, COMPLETED, REJECTED, CANCELLED
cart/internal/model/
  CartStatus.java         # ACTIVE, CHECKED_OUT, EXPIRED
user/internal/model/
  UserRole.java           # ADMIN, VENDOR, CUSTOMER
  UserStatus.java         # ACTIVE, INACTIVE, BANNED
  AdminAccountStatus.java # ACTIVE, INACTIVE
```

枚举使用规则：

- DTO 输出时使用枚举的 `name()` 或自定义 `code` 字段，保持 API 响应为字符串。
- 内部逻辑使用枚举进行状态判断和流转，禁止字符串比较。
- 状态流转必须在 Service 层通过枚举校验合法性。
