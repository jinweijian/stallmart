# 综合技术规范

本文整合了 StallMart 项目的核心技术规范，作为长期维护的大型业务系统标准。所有新代码必须遵循本规范。

## 目标

- 提高代码可读性
- 提高长期可维护性
- 提高系统稳定性
- 提高模块边界清晰度
- 降低耦合
- 提高内聚
- 降低未来重构成本
- 保持合理工程复杂度

**请始终以"长期维护的大型业务系统"标准进行代码设计。**

## 技术栈

后端：
- Java
- Spring Boot
- Spring Modulith
- JPA / MyBatis
- MySQL

前端：
- Nuxt
- Vue3
- TypeScript
- TailwindCSS

其他：
- 微信小程序
- RESTful API
- 插件化架构
- SaaS 系统
- 单元测试

## 后端代码规范（Java）

### 必须遵循

- Controller 仅负责参数接收与响应
- 业务逻辑放入 Application Service / Domain Service
- Repository 仅负责数据访问
- 不允许 Controller 直接操作数据库
- 不允许 Service 无限互相调用
- 避免循环依赖
- 避免 God Object（上帝类）
- 避免超大 Service
- 优先使用构造器注入
- 不使用字段注入 `@Autowired field`

### 类设计要求

- 单一职责
- 高内聚
- 低耦合
- 一个类只解决一类问题

### 方法设计要求

- 方法尽量短小
- 避免超过 50 行
- 避免超过 3 层嵌套
- 优先 early return
- if/switch 过长时考虑策略模式

### 命名要求

避免：
- `Util`
- `Helper`
- `Manager`
- `Common`
- `Base`
- `Processor`

优先：
- 明确业务语义
- 名称可直接表达职责

例如：
- `LearningProgressCalculator`
- `ExamScoreService`
- `QuestionImportValidator`

## 数据库规范

- 避免 N+1 查询
- 避免在循环内查数据库
- 注意索引命中
- 避免无意义 join
- 避免 Repository 承担业务逻辑
- 注意事务边界
- `@Transactional` 优先放 Application Service

## DTO 规范

严格区分：

- Request DTO
- Response DTO
- Command
- Query
- VO
- Entity
- Snapshot

不要一个 DTO 全系统通用。

不要直接返回 Entity 给前端。

## 重构原则

重构时：

1. 先理解业务
2. 保持原有行为一致
3. 小步重构
4. 降低回归风险
5. 不做无关格式化
6. 保持 diff 清晰
7. 优先可维护性

不要：

- 为了设计模式而设计模式
- 为了抽象而抽象
- 提前抽象
- 过度封装
- 过度泛化

## 注释规范

不要写"翻译代码"的注释。

错误示例：

```java
// 设置用户名
user.setName(name);
```

正确注释应该解释：

- 为什么这样做
- 历史兼容原因
- 业务规则
- 性能权衡
- 特殊边界

例如：

```java
// 必须保存快照，避免题目后续修改影响历史考试记录
snapshot.setQuestionTitle(question.getTitle());
```

## 单元测试规范

生成测试时：

- 使用 JUnit5
- 使用 Mockito
- 测试方法名清晰
- 一个测试只验证一个核心行为
- 覆盖：
  - 正常流程
  - 边界情况
  - 异常情况

避免：

- 无意义 Mock
- 过度 Mock
- 测试实现细节
- 脆弱测试

优先测试：

- 业务规则
- 权限逻辑
- 边界条件
- 状态流转

## Vue3 / Nuxt 规范

### 必须遵循

- 组件职责单一
- 页面不要写过多业务逻辑
- composable 抽离复用逻辑
- API 请求统一管理
- 类型完整
- 避免 props drilling
- Tailwind 优先使用语义化结构

### Nuxt 路由目录结构

**强制要求**：所有页面必须使用目录 + `index.vue` 的结构。

```text
pages/
  products/
    index.vue           # /products
    [id]/
      index.vue         # /products/:id
  orders/
    index.vue           # /orders
    [id]/
      index.vue         # /orders/:id
```

原因：当 `xxx.vue` 和 `xxx/` 目录同时存在时，Nuxt 路由会产生冲突。

### 避免

- 超大组件
- 超长 setup
- 过多 watch
- 到处 ref/reactive
- 页面直接操作复杂状态

### 推荐

- composables
- hooks
- 分层组件
- 明确状态边界

## TailwindCSS 规范

- 保持 class 有组织
- 避免超长 class 串
- 可复用样式抽离组件
- 优先响应式设计
- 保持视觉一致性

## 微信小程序规范

- 避免页面逻辑过重
- API 封装统一
- 避免重复 setData
- 注意性能与首屏
- 控制页面通信复杂度
- 注意兼容性

## 插件化 / 模块化规范

- 模块边界明确
- 插件通过 API 通信
- 不允许跨模块直接访问内部实现
- 避免共享数据库逻辑
- 避免反向依赖

## 输出要求

输出代码时：

- 优先生产可用代码
- 风格统一
- 保持可读性
- 必要时解释为什么这样重构
- 指出潜在风险
- 给出更安全方案

如果发现架构问题：

请先：
1. 说明问题
2. 说明风险
3. 给出渐进式重构方案
4. 再开始改代码

## 最重要原则

最好的代码不是最复杂的代码。

最好的代码是：

- 容易理解
- 容易修改
- 容易排查
- 容易测试
- 容易扩展
- 新人容易接手

## 参考文档

- [代码风格规范](code-style.md)
- [开发规范](development.md)
- [项目规范](project-standards.md)
- [服务端规范](../specification/server.md)
- [管理端规范](../specification/web.md)
- [小程序模块规范](../specification/app-module.md)
