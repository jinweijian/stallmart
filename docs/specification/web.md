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
- 商家自己的后台使用 `/admin/vendor/me/*`，当前开发种子商家账号为 `vendor / vendor123`。
- 登录页放在 `web/app/pages/auth/login.vue`。
- 全局鉴权放在 `web/app/middleware/auth.global.ts`，页面通过 `definePageMeta({ role: 'ADMIN' | 'VENDOR' })` 区分角色。

## 接口约定

- 管理端不得维护独立 mock 数据；页面数据通过 `web/app/api/stallmart-api.ts` 访问服务端。
- 服务端返回统一 `Result<T>` 时，API client 负责解包 `data`。
- 管理端写入商品、店铺、装修后，小程序公开接口必须能读取同一份服务数据。
- 管理端请求必须携带 `Authorization: Bearer <accessToken>`。
- 本地开发必须走 Nuxt 同源代理：浏览器请求 `/api/v1`，`nuxt.config.ts` 转发到 `NUXT_API_PROXY_TARGET`，不要默认直连 `http://localhost:8080/api/v1`，避免 CORS 预检失败。
- 平台管理员只能访问平台路由，商家管理员只能访问商家路由；服务端仍必须做同样的权限校验。

## 样式约定

- 管理端样式统一使用 Tailwind CSS。页面优先写 Tailwind utility class；少量跨页面语义类放在 `web/app/assets/css/main.css`，只能通过 `@apply` 组合 Tailwind token，不维护裸 CSS 颜色、间距和阴影。
- 平台管理以桌面运营台为主，信息密度优先。
- 商家管理以手机 H5 为主，但必须同时适配 PC 端运营场景；PC 下不能只显示窄手机壳。
- 复用 `web/app/assets/css/main.css` 的 Tailwind 组件类，例如布局、表格、表单、状态标签和按钮。
- 页面内不写 `style` 属性；确有动态值时优先通过数据驱动 class 或受控 CSS 变量解决。
