# 小程序页面地图

本文定义小程序页面职责、跳转关系和状态边界。页面新增、删除、改名时必须同步更新。

## 当前 app.config 页面

主包页面：

| 页面路径 | 页面文件 | 模块 | 职责 |
| --- | --- | --- | --- |
| `pages/customer/index/index` | `src/pages/customer/index/` | 顾客端店铺浏览 | 扫码进入、店铺展示、商品浏览、商品详情弹层和 SKU 加购入口。 |
| `pages/customer/cart/cart` | `src/pages/customer/cart/` | 顾客端购物车 | 查看购物车、改数量、进入确认订单。 |
| `pages/customer/my-orders/my-orders` | `src/pages/customer/my-orders/` | 顾客端订单 | 查看订单列表、刷新订单状态。 |
| `pages/customer/my/my` | `src/pages/customer/my/` | 我的 | 用户信息、登录状态、摊主入口。 |
| `pages/customer/confirm-order/confirm-order` | `src/pages/customer/confirm-order/` | 确认订单 | 提交订单、展示确认码。 |

分包页面：

| 分包 root | 页面路径 | 页面文件 | 职责 |
| --- | --- | --- | --- |
| `pages/vendor` | `my-stall/my-stall` | `src/pages/vendor/my-stall/` | 摊位总览、今日数据、订单入口。 |
| `pages/vendor` | `order-manage/order-manage` | `src/pages/vendor/order-manage/` | 摊主订单处理。 |
| `pages/vendor` | `stall-settings/stall-settings` | `src/pages/vendor/stall-settings/` | 摊位设置。 |

## tabBar

当前 tabBar 指向：

| Tab | 页面 | 职责 |
| --- | --- | --- |
| 首页 | `pages/customer/index/index` | 店铺首页或扫码进入后的店铺展示。 |
| 购物车 | `pages/customer/cart/cart` | 购物车。 |
| 订单 | `pages/customer/my-orders/my-orders` | 我的订单。 |
| 我的 | `pages/customer/my/my` | 用户与摊主入口。 |

## 顾客端推荐流程

```text
扫码或打开首页
  -> 店铺首页
  -> 加入购物车
  -> 购物车
  -> 确认订单
  -> 我的订单
```

规则：

- 店铺首页负责商品浏览和商品详情弹层，不负责订单提交。
- 店铺首页支持后台 `styleCode` 与 `decoration` 驱动风格包渲染；当前已支持森系水果茶版首页框架，包含主题头图、自动轮播 banner、左侧分类、右侧推荐列表和底部购物车栏。头图和轮播 banner 默认只展示风格包图片，不再叠加示意文案、按钮或占位符号。首页会持久化合并后的风格包，点单、订单、我的三个 tab 读取同一份主题。icon、banner、商品图、头像、菜单 icon、步进器、进度 icon 和底部栏预留等展示尺寸必须从风格包 `assetSizes` 读取，装修字段标准见 [storefront-decoration.md](storefront-decoration.md)。
- 商品详情不新增独立路由，作为首页底部弹层实现；弹层必须使用后端商品 `specIds/skus` 和 `/styles/{styleId}/specs` 生成 SKU 选项，加入购物车时只保存已选 SKU、规格文案、价格和商品快照。弹层打开时必须临时隐藏原生 tabBar，关闭或离开页面时恢复，并保证内容可滚动、底部提交栏不被遮挡。
- 购物车负责商品集合管理，不负责用户认证细节。
- 购物车页面作为“点单”展示页使用 `pageThemes.cart`，保留现有数量修改、清空和结算能力，不展示配送/外卖入口。
- 确认订单负责下单前校验和提交，必须读取同一份顾客端主题变量；顶部风格 banner 复用 `pageThemes.cart.headerBanner`，商品图、底部提交栏和安全区预留由 `assetSizes` 控制。
- 我的订单负责订单查询和状态展示，使用 `pageThemes.orders` 的 banner 和状态色，不新增独立订单详情路由；订单卡必须常显取餐码，底部必须预留原生 tabBar 和安全区，避免最后一张订单被遮挡。
- 我的页面使用 `pageThemes.my` 的欢迎文案、邀请 banner 和菜单 icon，保留现有登录、绑定手机、订单统计、摊主入口、常见问题、客服、关于我们；当前不展示会员、优惠券、卡包、礼品、成长值。

## 摊主端推荐流程

```text
我的
  -> 我的摊位
  -> 订单管理
  -> 接单/拒单/备餐/待取餐/完成
```

规则：

- 普通顾客不应看到摊主操作入口，入口可隐藏，但权限必须由后端兜底。
- 摊主订单操作必须遵循订单状态机。
- 摊位设置保存后必须刷新我的摊位页面数据。

## 页面文件结构约束

每个页面目录应包含：

```text
page-name/
  page-name.vue
  page-name.scss
  page-name.config.ts
```

约束：

- 页面目录使用 kebab-case。
- 页面 `.vue`、`.scss`、`.config.ts` 文件名与目录名一致。
- 页面内不放跨页面公共工具函数。
- 可复用 UI 后续应抽到 `src/components/`，当前仓库尚未建立该目录。

## 计划或历史提及但当前未注册页面

历史文档和 Review 提到以下页面，但当前 `app.config.ts` 未注册：

| 页面 | 来源 | 当前处理 |
| --- | --- | --- |
| 商品详情页 `pages/customer/product/detail` | Review 待补充功能 | 当前不新增独立路由；首页内已使用底部弹层承载商品详情和 SKU 选择。 |
| 个人信息编辑页 | Review 待补充功能 | 当前不存在。 |
| 摊主入驻申请页 | Review 待补充功能 | 当前不存在。 |

新增这些页面前，必须先更新本文和 `app.config.ts`。
