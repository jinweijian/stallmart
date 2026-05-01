# StallMart 管理端

本目录是 StallMart 后台管理端，使用 Nuxt + Vue 3。目录组织参考 `edusoho-lms/web`，按 API、类型、组件、页面拆分。

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
  package.json
```

## 本地命令

```bash
npm install
npm run dev
npm run build
```

默认 API 地址为 `http://localhost:8080/api/v1`，可通过 `NUXT_PUBLIC_API_BASE` 覆盖。

## 页面入口

- `/` 平台总览
- `/platform/vendors` 平台商家列表
- `/platform/vendors/:id` 平台进入商家模块
- `/vendor` 商家 H5 管理工作台
- `/vendor/products` 商品管理
- `/vendor/orders` 订单管理
- `/vendor/store` 店铺管理
- `/vendor/decoration` 装修设置
- `/vendor/users` 用户管理
- `/vendor/carts` 购物车管理
