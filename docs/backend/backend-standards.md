# 后端开发规范

本文拆解自外层 `review/code-style.md`，并结合当前 `backend/` 代码结构落地为后端强制规范。

## 分层职责

| 层 | 路径 | 允许做什么 | 禁止做什么 |
| --- | --- | --- | --- |
| Controller | `controller/` | 路由、入参校验、读取认证上下文、VO 组装 | 写复杂业务逻辑、直接访问 Mapper |
| Service | `service/` | 定义业务接口 | 暴露 HTTP 细节 |
| Service Impl | `service/impl/` | 业务规则、事务、状态流转 | 拼接 SQL、返回 Entity 给前端 |
| Repository | `repository/` | MyBatis-Plus Mapper、数据访问 | 写业务判断 |
| Model DTO | `model/dto/` | 请求体结构 | 作为数据库实体使用 |
| Model VO | `model/vo/` | 响应结构 | 暴露敏感字段 |
| Entity | `model/entity/` | 数据库表映射 | 直接作为 API 响应 |
| Common | `common/` | 统一响应、异常、安全上下文、过滤器 | 承载业务模块规则 |
| Config | `config/` | 框架配置、JWT、Redis、Swagger | 写业务逻辑 |

## 命名规范

| 类型 | 规则 | 示例 |
| --- | --- | --- |
| 类名 | UpperCamelCase | `OrderController`, `UserServiceImpl` |
| 方法名 | lowerCamelCase | `findById`, `createOrder` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 包名 | 全小写 | `com.stallmart.controller` |
| 数据库表 | 小写下划线，业务上允许单数 | `order_item` |
| 数据库字段 | 小写下划线 | `created_at`, `store_id` |

## 类结构顺序

```java
public class UserServiceImpl implements UserService {
    // 1. static final 常量
    // 2. private final 依赖
    // 3. 构造注入或 Lombok RequiredArgsConstructor
    // 4. public 方法
    // 5. private 辅助方法
}
```

## API 规范

- 路径统一以 `/api/v1` 开头。
- Controller 返回 `Result<T>`。
- 列表接口需要明确是否分页。分页响应统一使用 `PageResult<T>` 或等价结构。
- 新增接口必须补 Swagger 注解。
- 新增接口必须同步 `docs/backend/api-reference.md`。
- 前端需要调用时，同步 `mini-program/src/app-config/index.ts`。

## 错误码规范

错误码分段：

| 范围 | 模块 |
| --- | --- |
| `1xxxx` | 通用错误、参数、认证、权限、系统 |
| `2xxxx` | 用户模块 |
| `3xxxx` | 订单模块 |
| `4xxxx` | 商品模块 |
| `5xxxx` | 店铺模块 |
| `9xxxx` | 第三方服务 |

不要在 Controller 中手写魔法数字。优先扩展 `ErrorCode`。

## 异常处理

- 业务异常使用 `BusinessException`。
- 参数校验错误交给 `GlobalExceptionHandler` 统一处理。
- Controller 不吞异常，不返回裸字符串。
- 外部服务错误需要保留可排查日志，但不能泄露密钥或完整 token。

## 日志规范

使用占位符，不拼接字符串：

```java
log.info("create order success: orderId={}, userId={}", orderId, userId);
```

敏感字段必须脱敏：

- 手机号: `138****1234`
- token: 只允许打印前后少量字符或 trace id
- 密码、JWT secret、微信密钥: 禁止打印

## 数据库访问规范

- 默认使用 MyBatis-Plus Mapper。
- 禁止字符串拼接 SQL。
- 查询条件必须参数化。
- 状态字段使用明确枚举值或常量，不在多处散落字符串。
- JSON 字段必须有明确结构文档。

## 事务规范

以下操作必须考虑事务：

- 创建订单及订单明细。
- 修改订单状态并写入相关记录。
- 绑定手机号并刷新 token。
- 删除或下架商品影响订单可见性。

## 日期时间规范

- API 响应建议统一使用时间戳或 ISO 8601，必须前后端一致。
- 数据库存储统一使用 `created_at`、`updated_at`。
- 日志时间由日志框架处理，不手写字符串时间。
