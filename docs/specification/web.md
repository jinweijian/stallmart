# 管理端规范

管理端位于 `web/`，使用 Nuxt + Vue 3。目录参考 `edusoho-lms/web`，按职责拆分：

```text
web/app/
  api/          接口 client，统一处理 Result 响应
  components/   跨页面通用组件
  pages/        Nuxt 路由页面
  types/        管理端类型定义
  assets/css/   全局样式
```

## 页面边界

- 平台管理页面放在 `web/app/pages/platform/`。
- 商家 H5 管理页面放在 `web/app/pages/vendor/`。
- 平台进入单个商家模块时，读取服务端 `/admin/platform/vendors/{storeId}/summary`。
- 商家自己的后台使用 `/admin/vendor/me/*`，当前开发种子商家用户为 `X-User-Id: 2`。

## 接口约定

- 管理端不得维护独立 mock 数据；页面数据通过 `web/app/api/stallmart-api.ts` 访问服务端。
- 服务端返回统一 `Result<T>` 时，API client 负责解包 `data`。
- 管理端写入商品、店铺、装修后，小程序公开接口必须能读取同一份服务数据。

## 样式约定

- 平台管理以桌面运营台为主，信息密度优先。
- 商家管理以手机 H5 为主，页面宽度和交互保持移动端优先。
- 复用 `web/app/assets/css/main.css` 的布局、表格、表单、状态标签和按钮样式。
