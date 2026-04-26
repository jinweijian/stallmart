# 代码风格规范

本文是项目代码风格的落地版本，拆解自外层 `review/code-style.md`，并结合当前代码实际调整。后续新增代码以本文为准。

## 通用原则

- 单个文件只承担一个清晰职责。
- 方法和函数优先短小，超过 80 行需要考虑拆分。
- 使用早返回减少嵌套。
- 禁止本机绝对路径。
- 禁止真实密钥进入代码、文档、日志。
- 命名要表达业务含义，不使用 `data1`、`temp`、`foo` 这类模糊名称。
- 不为了抽象而抽象，只有重复和复杂度真实存在时才抽取。

## TypeScript / 小程序

### 命名

| 类型 | 规则 | 示例 |
| --- | --- | --- |
| 页面目录 | kebab-case | `confirm-order` |
| 页面文件 | 与目录同名 | `confirm-order.vue` |
| Store | `useXxxStore` | `useUserStore` |
| 工具函数 | lowerCamelCase | `getAccessToken` |
| 类型接口 | UpperCamelCase | `RequestOptions` |
| 常量 | UPPER_SNAKE_CASE | `API_BASE_URL` |
| endpoint key | UPPER_SNAKE_CASE | `AUTH_WECHAT_LOGIN` |

### Vue 组件

页面组件建议顺序：

```vue
<script setup lang="ts">
// imports
// types
// state
// computed
// lifecycle
// public handlers
// private helpers
</script>

<template>
  <!-- view -->
</template>

<style lang="scss">
/* page style */
</style>
```

约束：

- 页面内不要直接写复杂 API URL。
- 不要在 template 里写复杂表达式。
- 复杂计算放入 computed 或 helper。
- 所有异步操作必须有失败处理。
- 关键按钮必须处理 loading 和 disabled。

### TypeScript

- 避免 `any`，只有对 Taro 事件或第三方 SDK 无法精确建模时可以局部使用。
- 对 API 响应定义明确类型。
- 不允许把后端 Entity 形状直接当作前端 UI 类型，必要时定义 ViewModel。
- `tsconfig` 已启用 strict，新增代码必须符合 strict。

### Pinia

- Store 只维护跨页面共享状态。
- 页面局部状态留在页面。
- Store action 的副作用要清晰，不要同时做请求、跳转、toast、复杂计算。
- 用户态、购物车、订单状态建议拆分 store。

### 样式

- 页面样式放同目录 `.scss`。
- 通用设计 token 放配置或全局样式。
- 不在多个页面硬编码同一颜色。
- 不大量使用 inline style。
- rpx 规则必须统一，不要 SCSS 和 Tailwind 各自写一套。

## Java / Spring Boot

### 命名

| 类型 | 规则 | 示例 |
| --- | --- | --- |
| Controller | `XxxController` | `OrderController` |
| Service | `XxxService` | `OrderService` |
| Service Impl | `XxxServiceImpl` | `OrderServiceImpl` |
| Mapper | `XxxMapper` | `OrderMapper` |
| DTO | `XxxDTO` | `CreateOrderDTO` |
| VO | `XxxVO` | `OrderVO` |
| Entity | 业务名词 | `OrderItem` |

### 方法与控制流

使用早返回：

```java
if (user == null) {
    throw new BusinessException(ErrorCode.USER_NOT_FOUND);
}
```

不要多层嵌套：

```java
if (user != null) {
    if (user.getStatus().equals("ACTIVE")) {
        // ...
    }
}
```

### Controller

- 只处理 HTTP 入参、认证上下文、调用 service、返回 VO。
- 不直接访问 Mapper。
- 不写复杂业务判断。
- 所有请求体使用 DTO。
- 所有响应使用 VO 或简单结果，不直接返回 Entity。

### Service

- 业务状态机、事务、权限语义放 Service。
- 修改订单状态必须检查当前状态是否合法。
- 创建订单必须校验商品、店铺、金额和用户身份。

### Repository / Mapper

- 禁止拼接 SQL。
- 条件查询使用 MyBatis-Plus wrapper 或 XML 参数。
- XML Mapper 变更必须有对应测试或手动验证说明。

## API 风格

- 路径使用 `/api/v1`。
- 资源名使用复数，如 `/stores/{id}/products`。
- 状态动作可使用动作子资源，如 `/orders/{id}/accept`。
- 成功码必须统一。当前项目存在 `0` 与 `200` 冲突，修复后以最终实现为准。
- 错误码按模块分段，不随意新增魔法数字。

## 日期时间

推荐分层：

| 场景 | 格式 |
| --- | --- |
| API 响应 | 统一 timestamp 或 ISO 8601，不能混用 |
| 日志 | 由日志框架输出 ISO 风格时间 |
| 数据库 | `created_at`, `updated_at` |
| 用户展示 | 前端本地化为 Asia/Shanghai |

## 注释

- 只写解释“为什么”的注释，不写“这行代码做什么”的空注释。
- 复杂状态机、权限判断、第三方约束需要注释。
- 临时 TODO 必须带原因和后续处理方向。

## 禁止项

- 禁止提交生成产物和依赖目录。
- 禁止真实密钥。
- 禁止本机绝对路径。
- 禁止页面直接发业务 `Taro.request`。
- 禁止后端 Controller 写 SQL 或复杂业务。
- 禁止文档写已知不真实的功能状态。
