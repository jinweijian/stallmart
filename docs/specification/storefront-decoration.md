# 小程序店铺装修规范

本文定义商家装修配置如何驱动小程序顾客端展示页。后续开发店铺首页、点单、订单、我的、装修后台、服务端装修接口时，必须按本文扩展，不要在页面里新增散落色值、硬编码 icon 或临时 banner 字段。

## 配置层级

店铺前台装修分两层：

| 层级 | 来源 | 职责 |
| --- | --- | --- |
| 风格包 | `StyleDTO.theme` / 小程序 `STORE_THEME_PACKAGES` | 定义一套可复用的颜色、图标槽位、图片槽位、默认文案、分类 icon 库和布局版本。 |
| 店铺覆盖 | `StoreDecorationDTO` | 覆盖当前店铺的 Logo、封面、banner、文案、icon 文件地址、分类 icon 库文件地址和图片资源。 |

小程序最终只消费合并后的装修配置。合并顺序是：

```text
风格包默认值 -> 店铺装修覆盖值 -> 页面兜底值
```

## 标准字段

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `layoutVersion` | string | 小程序首页布局版本，第一版固定为 `customer-storefront-v1`。 |
| `colors` | map | 颜色 token，至少包含 `primary/secondary/accent/background/surface/text/mutedText/border/price`。 |
| `iconNames` | map | 语义 icon 名称，如 `location/cart/checkout/delivery/sectionLeaf`。 |
| `iconUrls` | map | 对应 icon 文件地址。素材未补齐前允许用占位图，但字段必须存在。 |
| `imageUrls` | map | 主题图片地址，如 `heroIllustration/mascot/productPlaceholder/promoIllustration`。 |
| `copywriting` | map | 可切换文案，如 `branchName/heroEyebrow/heroTitle/heroSubtitle/promoTitle/promoSubtitle/promoActionText`。 |
| `categoryIconLibrary` | list | 当前风格可选分类 icon 库，包含 `key/name/iconUrl/fallbackText`，由装修设置维护素材地址。 |
| `categories` | list | 首页左侧实际分类入口，来自分类管理，包含 `id/name/iconKey/iconUrl/fallbackText/sortOrder/status`。 |
| `banners` | list | 店铺轮播图，包含 `id/imageUrl/title/subtitle/actionText/targetCategory`，按顺序展示。 |
| `assetSizes` | map | 小程序展示尺寸 token。上传素材可以更大，但页面展示必须使用这里的统一尺寸。 |
| `pageThemes` | map | 跨页面展示配置，包含 `home/cart/orders/my`，用于四个 tab 的 banner、文案、菜单 icon 和状态色；确认订单页复用 `pageThemes.cart.headerBanner` 保持点单链路风格一致。 |

所有会展示在小程序端的内容，都必须归入这些字段之一。新增位置时先新增语义 key 并更新本文，再改服务端、小程序和管理端。

## SaaS 加载规则

同一套小程序代码服务多个商家时，小程序启动阶段通过 AppID 识别租户：

```text
小程序启动 -> GET /app/bootstrap?appId={appId} -> 写入 customer-theme 缓存 -> 页面读取缓存和接口数据
```

平台角色负责创建、修改、上传素材、上下架和删除未使用的风格包；已被店铺引用的风格包不能硬删，只能下架来阻止后续选择。商家角色第一版只能选择已上架风格包，服务端必须拒绝商家切换到下架风格包。后台上传素材后保存网络 URL，小程序运行时按配置读取，不把商家素材重新打包到 `app/src/static`。

## 第一版森系风格

第一版风格编码为 `forestFruitTeaCrayon`，名称为“森系水果茶-小白款”，服务端种子店铺默认使用该风格。

基础 token：

| Token | 值 |
| --- | --- |
| `primary` | `#6F9646` |
| `secondary` | `#B8C77A` |
| `accent` | `#F2B94B` |
| `background` | `#FBFAEF` |
| `surface` | `#FFFDF4` |
| `text` | `#4C6040` |
| `mutedText` | `#7A866D` |
| `border` | `#DCE6C7` |
| `price` | `#6F9646` |

预留素材路径：

```text
/static/storefront/forest/icons/location.png
/static/storefront/forest/icons/cart.png
/static/storefront/forest/icons/checkout.png
/static/storefront/forest/icons/recommend.png
/static/storefront/forest/icons/citrus.png
/static/storefront/forest/icons/grape.png
/static/storefront/forest/icons/mango.png
/static/storefront/forest/icons/tea.png
/static/storefront/forest/icons/extra.png
/static/storefront/forest/icons/category-1.png
/static/storefront/forest/icons/category-2.png
/static/storefront/forest/banner-seasonal.jpg
/static/storefront/forest/banner-tea.jpg
/static/storefront/forest/hero-forest-tea.jpg
/static/storefront/forest/cart-icon.png
/static/storefront/forest/mascot.png
/static/storefront/forest/product-placeholder.png
```

当前仓库已放置占位 PNG，后续替换同名文件即可。用户提供的小白款素材落点约定为：

| 原始素材 | 目标路径 |
| --- | --- |
| `home-banner.png` | `/static/storefront/forest/hero-forest-tea.jpg`，原始素材保留在 `tmp/image`。 |
| `lunbo-1.png` | `/static/storefront/forest/banner-seasonal.jpg`，原始素材保留在 `tmp/image`。 |
| `lunbo-2.png` | `/static/storefront/forest/banner-tea.jpg`，原始素材保留在 `tmp/image`。 |
| `购物车.png` | `/static/storefront/forest/icons/cart.png` |
| `类目icon1.png` | `/static/storefront/forest/icons/category-1.png` |
| `类目icon2.png` | `/static/storefront/forest/icons/category-2.png` |
| `首页icon.png` | `/static/tabbar/home.png` 与 `/static/tabbar/home-active.png` |
| `点单icon.png` | `/static/tabbar/cart.png` 与 `/static/tabbar/cart-active.png` |
| `订单icon.png` | `/static/tabbar/order.png` 与 `/static/tabbar/order-active.png` |
| `我的Icon.png` | `/static/tabbar/my.png` 与 `/static/tabbar/my-active.png` |

展示尺寸：

| 位置 | token | 展示尺寸 |
| --- | --- | --- |
| 首页头图 | `heroBanner` | `750rpx x 536rpx` |
| 首页轮播图 | `promoBanner` | `692rpx x 220rpx` |
| 左侧分类 icon | `categoryIcon` | `54rpx x 54rpx` |
| 定位 icon | `locationIcon` | `30rpx x 30rpx` |
| 分区叶子 icon | `sectionIcon` | `30rpx x 30rpx` |
| 底部结算/外卖 icon | `bottomActionIcon` | `32rpx x 32rpx` |
| 商品图展示位 | `productImage` | `144rpx x 144rpx` |
| 点单页商品图展示位 | `cartProductImage` | `148rpx x 148rpx` |
| 订单页商品图/骨架展示位 | `orderProductImage` | `112rpx x 112rpx` |
| 首页吉祥物 | `mascot` | `120rpx x 120rpx` |
| 底部购物车吉祥物 | `cartMascot` | `140rpx x 140rpx`，等效 375px 设备上的 `70px x 70px` |
| 我的页头像 | `profileAvatar` | `132rpx x 132rpx` |
| 我的页菜单 icon | `menuIcon` | `44rpx x 44rpx` |
| 数量步进器按钮 | `qtyStepper` | `44rpx x 44rpx` |
| 订单进度 icon | `progressIcon` | `44rpx x 44rpx` |
| 点单/订单页顶部 banner | `cartHeaderBanner` | `692rpx x 120rpx` |
| 我的页邀请 banner | `myInviteBanner` | `692rpx x 124rpx` |
| 页面底部操作栏 | `bottomBarHeight` | `128rpx` |
| 首页购物车浮条底部偏移 | `cartBarBottomOffset` | `60rpx`，用于控制浮条贴近原生 tabBar 的视觉位置 |
| 原生 tabBar 预留 | `tabBarReserve` | `132rpx` |

强制规则：

- 小程序展示尺寸只从 `assetSizes` 生成的 CSS 变量读取，不在页面局部重新定义 icon 大小。
- `assetSizes` 仍以 `rpx` 作为后台和小程序设计 token；H5 调试端由 `src/utils/customer-theme.ts` 在生成 CSS 变量时转换为浏览器可识别的单位，避免运行时 CSS 变量保留 `rpx` 后导致图片容器高度失效。
- 风格包不得绑定固定商品分类；分类名称、排序、启停和 `iconKey` 归分类管理。风格包只提供 `categoryIconLibrary`。
- 分类管理保存 `iconKey`，小程序首页渲染服务端返回的 `categories[].iconUrl`，缺失时使用 `fallbackText`。
- `pages/customer/index/index`、`pages/customer/cart/cart`、`pages/customer/my-orders/my-orders`、`pages/customer/my/my` 必须通过 `src/utils/customer-theme.ts` 读取同一份主题变量。
- banner 使用 `swiper` 自动轮播，超过一张时开启 `autoplay` 和 `circular`。
- 当前顾客端不展示配送/外卖、会员、优惠券、卡包、礼品、成长值入口；后续新增时必须先补齐对应 `pageThemes` 槽位和页面职责说明。
- 上传端不强制要求素材等于展示尺寸；建议上传不小于展示尺寸的 2 倍，避免真机模糊。

## 小程序样式约束

小程序样式必须按微信 WXSS/Taro 可编译能力来写：

- 不使用 Tailwind arbitrary value 类名，例如 `text-[28rpx]`、`bg-[#fff]`。
- 不使用全局复杂选择器，例如 `*`、`::before` 全局 reset、深层后代通配。
- 不使用 `filter`、`backdrop-filter`、`position: sticky` 等小程序兼容性不稳定样式。
- 不依赖 Web 端鼠标交互，例如 `hover` 才能看到的信息。
- 优先使用 `rpx`、语义化 class 和 CSS 变量。
- 固定底部栏必须预留 `env(safe-area-inset-bottom)`，列表底部必须留出遮挡空间。
- 图片必须有兜底，素材未补齐时使用 `fallbackText` 或占位图。

## 开发流程

1. 新增风格包时，先在服务端 `store_style.theme_json` 和小程序 `STORE_THEME_PACKAGES` 中补齐同名 token 与 `categoryIconLibrary`。
2. 新增装修字段时，同步更新 `StoreDecorationDTO`、管理端装修页、小程序首页消费逻辑和 API 文档。
3. 管理端只保存语义字段，不保存页面私有 class 名或布局实现细节；分类 icon 只保存 `iconKey`。
4. 小程序页面只消费合并后的装修配置，不直接判断商家名称或风格名称来切换样式。
