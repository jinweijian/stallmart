# mini-program 文档目录

本目录负责小程序端设计、模块划分、页面边界、状态管理、API 约束和视觉交互规范。

## 文档列表

| 文档 | 作用 |
| --- | --- |
| [module-design.md](module-design.md) | 小程序业务模块划分、目录边界、跨模块依赖规则。 |
| [page-map.md](page-map.md) | 顾客端和摊主端页面地图、页面职责、跳转关系。 |
| [state-and-api.md](state-and-api.md) | Pinia、Storage、请求封装、认证刷新、endpoint 管理规则。 |
| [style-and-interaction.md](style-and-interaction.md) | 小程序视觉、组件、风格包、交互和适配规范。 |

## 做什么事应参考这里

- 新增页面或调整页面路由。
- 修改 `app.config.ts`、tabBar、分包。
- 修改 `store/`、`utils/request.ts`、`utils/auth.ts`、`app-config/index.ts`。
- 调整小程序 UI 样式、风格包、页面布局。
- 对接或修改后端 API。

## 当前代码模块事实

当前代码位于 `mini-program/src/`：

| 路径 | 职责 |
| --- | --- |
| `app.ts`, `app.vue`, `app.config.ts` | 小程序启动、生命周期、页面/分包/tabBar 配置。 |
| `app-config/` | API 地址、endpoint、存储 key、设计 token。 |
| `pages/customer/` | 顾客端页面。 |
| `pages/vendor/` | 摊主端分包页面。 |
| `store/` | Pinia 状态。当前已有 `user`。 |
| `utils/` | 请求、认证、Storage 封装。 |
| `static/` | 静态资源和 tabBar 图标。 |

## 重要约束

- 页面只负责视图状态和用户交互，不直接拼接 API URL。
- API endpoint 统一写在 `src/app-config/index.ts`。
- 请求统一走 `src/utils/request.ts`。
- 登录和 token 生命周期统一走 `src/utils/auth.ts` 与 `src/utils/storage.ts`。
- 可复用状态进入 Pinia，不在多个页面重复维护。
- 业务模块新增后必须更新 [module-design.md](module-design.md) 和 [page-map.md](page-map.md)。
