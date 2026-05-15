# 管理端规范

管理端位于 `web/`，使用 Nuxt + Vue 3。目录参考 `edusoho-lms/web`，按职责拆分：

```text
web/app/
  api/          接口 client，统一处理 Result 响应
  components/   跨页面通用组件
  composables/  组合式函数
  pages/        Nuxt 路由页面
  types/        管理端类型定义
  assets/css/   全局样式
```

## 页面边界

- 平台管理页面放在 `web/app/pages/platform/`。
- 商家 H5 管理页面放在 `web/app/pages/vendor/`。
- 平台进入单个商家模块时，读取服务端 `/admin/platform/vendors/{storeId}/summary`。
- 商家自己的后台使用 `/admin/vendor/me/*`，当前开发种子商家账号为 `vendor / stallmart&v@2026..`。
- 登录页放在 `web/app/pages/auth/login/index.vue`。
- 登录页连续失败 3 次后显示服务端验证码，验证码接口为 `/admin/auth/captcha`。
- 操作日志页面放在 `platform/operation-logs/index.vue`、`platform/vendors/[id]/operation-logs/index.vue` 和 `vendor/operation-logs/index.vue`。
- 全局鉴权放在 `web/app/middleware/auth.global.ts`，页面通过 `definePageMeta({ role: 'ADMIN' | 'VENDOR' })` 区分角色。

## Nuxt 路由目录结构规范

**强制要求**：所有页面必须使用目录 + `index.vue` 的结构，不得使用单文件页面。

原因：当 `xxx.vue` 和 `xxx/` 目录同时存在时，Nuxt 路由会产生冲突，导致子路由无法正确匹配。

正确结构：

```text
web/app/pages/
  index.vue                           # /
  auth/
    login/index.vue                   # /auth/login
    platform/
    vendors/
      index.vue                       # /platform/vendors
      [id]/index.vue                  # /platform/vendors/:id
      [id]/operation-logs/index.vue   # /platform/vendors/:id/operation-logs
    operation-logs/index.vue          # /platform/operation-logs
    styles/index.vue                  # /platform/styles
  vendor/
    index.vue                         # /vendor
    products/
      index.vue                       # /vendor/products
      [id]/index.vue                  # /vendor/products/:id
    orders/
      index.vue                       # /vendor/orders
      [id]/index.vue                  # /vendor/orders/:id
    categories/index.vue              # /vendor/categories
    specs/index.vue                   # /vendor/specs
    users/index.vue                   # /vendor/users
    store/index.vue                   # /vendor/store
    decoration/index.vue              # /vendor/decoration
    carts/index.vue                   # /vendor/carts
    operation-logs/index.vue          # /vendor/operation-logs
```

错误示例（禁止）：

```text
web/app/pages/
  vendor/
    products.vue                      # ❌ 与 products/ 目录冲突
    products/
      [id]/index.vue
```

## 接口约定

- 管理端不得维护独立 mock 数据；页面数据通过 `web/app/api/stallmart-api.ts` 访问服务端。
- 服务端返回统一 `Result<T>` 时，API client 负责解包 `data`。
- 管理端写入商品、店铺、装修后，小程序公开接口必须能读取同一份服务数据。
- 管理端请求必须携带 `Authorization: Bearer <accessToken>`。
- 管理端登录失败 3 次后必须提交 `captchaId/captchaAnswer`，该校验由服务端兜底。
- 商家只能读取 `/admin/vendor/me/operation-logs` 返回的自己店铺日志；平台可读取 `/admin/platform/operation-logs` 和 `/admin/platform/vendors/{storeId}/operation-logs`。
- 本地开发必须走 Nuxt 同源代理：浏览器请求 `/api/v1`，`nuxt.config.ts` 转发到 `NUXT_API_PROXY_TARGET`，不要默认直连 `http://localhost:8080/api/v1`，避免 CORS 预检失败。
- 平台管理员只能访问平台路由，商家管理员只能访问商家路由；服务端仍必须做同样的权限校验。
- 平台风格包管理支持新增、编辑、上架、下架和删除。删除已被店铺引用的风格包时，页面必须展示服务端错误，不在前端假装删除成功。
- 商家装修页只展示服务端返回的已上架风格包，并只允许保存风格包选择、Logo、封面、Banner 和店铺展示描述。颜色、文案、语义 icon、分类 icon 库和主题图片属于平台风格包管理范围。
- 前端过滤不能替代服务端校验；商家构造请求提交下架风格包或风格包覆盖字段时，服务端必须拒绝。

## 样式约定

- 管理端样式统一使用 Tailwind CSS。页面优先写 Tailwind utility class；少量跨页面语义类放在 `web/app/assets/css/main.css`，只能通过 `@apply` 组合 Tailwind token，不维护裸 CSS 颜色、间距和阴影。
- 平台管理以桌面运营台为主，信息密度优先。
- 商家管理以手机 H5 为主，但必须同时适配 PC 端运营场景；PC 下不能只显示窄手机壳。
- 复用 `web/app/assets/css/main.css` 的 Tailwind 组件类，例如布局、表格、表单、状态标签和按钮。
- 页面内不写 `style` 属性；确有动态值时优先通过数据驱动 class 或受控 CSS 变量解决。
