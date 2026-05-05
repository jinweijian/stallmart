# 顾客端森系水果茶风格包设计

## 已确认决策

用户选择 `B+C`：

- `B`：风格包从首页扩展到顾客端四个 tab 页面，包含首页、点单、订单、我的。
- `C`：引入轻量共享主题结构，页面通过同一份主题配置和 CSS 变量读取颜色、图片、icon 和展示尺寸。

## 范围

本次只改顾客端小程序展示层，不新增配送、会员、优惠券、卡包、礼品、成长值等未启用功能。参考图用于视觉方向，页面功能以现有代码为准。

覆盖页面：

- `pages/customer/index/index`：首页、banner、轮播、分类、商品列表、底部结算入口。
- `pages/customer/cart/cart`：点单/购物车页，保留现有购物车数量、清空、结算能力。
- `pages/customer/my-orders/my-orders`：订单列表页，保留现有状态筛选、展开详情、取消、确认取餐、再来一单能力。
- `pages/customer/my/my`：我的页，保留登录、绑定手机、订单统计、摊主入口、常见问题、客服、关于我们；不展示会员、优惠券、卡包、礼品、成长值。

订单确认页属于下单流程，本次只接入共享主题基础变量，不改变业务结构。

## 风格包契约

`STORE_THEME_PACKAGES.forestFruitTeaCrayon` 必须包含：

- 颜色 token：`primary/secondary/accent/background/surface/text/mutedText/border/price`。
- 图片与 icon 槽位：`iconUrls`、`imageUrls`。
- 首页配置：`banners`。
- 分类 icon 库：`categoryIconLibrary`。风格包只提供可选 icon，不绑定具体分类。
- 跨页配置：`pageThemes.home/cart/orders/my`。
- 展示尺寸：`assetSizes`。上传素材可以更大，但页面展示尺寸必须统一由这里输出。

分类与 icon 的关系归分类数据所有。后台分类创建/编辑保存 `iconKey`，小程序分类列表使用后端返回的 `iconKey/iconUrl/fallbackText` 渲染；装修设置只维护当前风格可选的分类 icon 库。

必须强制统一的尺寸：

| 位置 | token | 展示尺寸 |
| --- | --- | --- |
| 首页头图 | `heroBanner` | `750rpx x 536rpx` |
| 首页轮播图 | `promoBanner` | `692rpx x 220rpx` |
| 分类 icon | `categoryIcon` | `54rpx x 54rpx` |
| 定位 icon | `locationIcon` | `30rpx x 30rpx` |
| 分区 icon | `sectionIcon` | `30rpx x 30rpx` |
| 底部动作 icon | `bottomActionIcon` | `32rpx x 32rpx` |
| 首页商品图 | `productImage` | `144rpx x 144rpx` |
| 点单商品图 | `cartProductImage` | `148rpx x 148rpx` |
| 订单商品图 | `orderProductImage` | `112rpx x 112rpx` |
| 首页吉祥物 | `mascot` | `120rpx x 120rpx` |
| 购物车吉祥物 | `cartMascot` | `140rpx x 140rpx` |
| 我的头像 | `profileAvatar` | `132rpx x 132rpx` |
| 我的菜单 icon | `menuIcon` | `44rpx x 44rpx` |
| 数量步进器 | `qtyStepper` | `44rpx x 44rpx` |
| 进度 icon | `progressIcon` | `44rpx x 44rpx` |
| 点单页顶部 banner | `cartHeaderBanner` | `692rpx x 120rpx` |
| 我的页邀请 banner | `myInviteBanner` | `692rpx x 124rpx` |
| 底部栏预留 | `tabBarReserve` | `132rpx` |

## 实现约束

- 首页加载到的合并风格包需要持久化，其他 tab 页面从缓存读取，缓存缺失时使用默认风格包。
- 页面根节点绑定共享 CSS 变量，不在局部散落旧色值或临时尺寸。
- 首页轮播必须使用 `swiper`，多于一张图时开启 `autoplay` 和 `circular`。
- 固定底部操作条必须预留原生 tabBar 高度和 `env(safe-area-inset-bottom)`。
- 所有页面样式使用 `rpx`，避免 `px`。
