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

## 必读文档

- [服务端规范](../docs/specification/server.md)
- [服务端 API](../docs/api-server/index.md)
- [测试说明](../docs/guide/testing.md)
