# StallMart 管理端

本目录是 StallMart 后台管理端，使用 Nuxt + Vue 3 + Tailwind CSS。目录组织参考 `edusoho-lms/web`，按 API、类型、组件、页面拆分。

## 目录结构

```text
web/
  app/
    api/          服务端接口封装
    assets/css/   全局样式
    components/   通用布局组件
    pages/        路由页面
    types/        前端类型定义
  nuxt.config.ts
  tailwind.config.ts
  package.json
```

## 本地命令

```bash
npm install
npm run dev
npm run build
```

默认 API 地址为同源代理 `/api/v1`，Nuxt 会把请求转发到 `http://localhost:8080/api/v1`，避免浏览器 CORS 预检问题。后端代理目标可通过 `NUXT_API_PROXY_TARGET` 覆盖，例如 `http://localhost:8080`；只有明确需要直连外部 API 时才设置 `NUXT_PUBLIC_API_BASE`。

管理端会额外代理本地素材路径：`/uploads/**` 访问后端上传文件，`/static/**` 读取小程序侧 `app/src/static/**` 内置素材，用于商品占位图和店铺装修预览。

样式统一走 Tailwind CSS。页面优先使用 utility class，跨页面通用的 `.button`、`.panel`、`.data-table` 等组件类集中在 `app/assets/css/main.css` 并通过 `@apply` 维护，不在页面写内联样式。

## 页面入口

- `/auth/login` 后台登录
- `/` 平台总览
- `/platform/vendors` 平台商家列表
- `/platform/vendors/:id` 平台进入商家模块
- `/vendor` 商家 H5 管理工作台
- `/vendor/products` 商品管理
- `/vendor/products/:id` 商品详情与上下架
- `/vendor/categories` 分类管理
- `/vendor/specs` 商品规格管理
- `/vendor/orders` 订单管理
- `/vendor/orders/:id` 订单详情与备餐操作
- `/vendor/store` 店铺管理
- `/vendor/decoration` 装修设置，维护小程序风格包、banner、文案、icon 地址和主题图片地址
- `/vendor/users` 用户管理
- `/vendor/carts` 购物车管理

## 本地账号

| 角色 | 账号 | 密码 |
| --- | --- | --- |
| 平台管理员 | `platform` | `platform123` |
| 商家管理员 | `vendor` | `vendor123` |
