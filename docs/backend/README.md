# backend 文档目录

本目录负责后端 API、接口契约、分层边界和前后端一致性。

## 文档列表

| 文档 | 作用 |
| --- | --- |
| [api-reference.md](api-reference.md) | 当前代码中已经暴露的 API 路由、认证要求和响应结构。 |
| [backend-standards.md](backend-standards.md) | 后端分层、命名、异常、日志、数据库访问约束。 |

## 做什么事应参考这里

- 新增或修改 Controller。
- 修改 DTO、VO、Entity、Mapper、Service。
- 修改认证、JWT、公开路径。
- 调整 `Result<T>`、错误码、分页结构。
- 修改数据库初始化脚本或 MyBatis 映射。

## 必须同步的文档

- 修改 API 时，同步 [api-reference.md](api-reference.md) 和 [../mini-program/state-and-api.md](../mini-program/state-and-api.md)。
- 修改错误码或响应结构时，同步 [../standards/code-style.md](../standards/code-style.md)。
- 修改数据库或 Docker 依赖时，同步 [../operations/configuration.md](../operations/configuration.md)。

## 当前后端注意事项

- 当前成功响应为 `code: 200`，但历史规格和小程序请求封装使用 `code: 0`。
- `AuthController` 的 `/auth/phone/bind` 和 `/auth/logout` 在公开路径下，但方法读取 `userId`，需要重新梳理认证策略。
- `backend/mysql/init.sql` 和 `docker/mysql/init/01-init.sql` 不一致，需要统一。
