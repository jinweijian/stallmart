# 小程序店铺装修规范

本文定义商家装修配置如何驱动小程序店铺首页。后续开发店铺首页、装修后台、服务端装修接口时，必须按本文扩展，不要在页面里新增散落色值、硬编码 icon 或临时 banner 字段。

## 配置层级

店铺前台装修分两层：

| 层级 | 来源 | 职责 |
| --- | --- | --- |
| 风格包 | `StyleDTO.theme` / 小程序 `STORE_THEME_PACKAGES` | 定义一套可复用的颜色、图标槽位、图片槽位、默认文案、分类入口和布局版本。 |
| 店铺覆盖 | `StoreDecorationDTO` | 覆盖当前店铺的 Logo、封面、banner、文案、icon 文件地址和图片资源。 |

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
| `categories` | list | 首页左侧分类入口，包含 `id/name/iconName/iconUrl/fallbackText`。 |
| `banners` | list | 店铺 banner 地址，按顺序展示。 |

所有会展示在小程序端的内容，都必须归入这些字段之一。新增位置时先新增语义 key 并更新本文，再改服务端、小程序和管理端。

## 第一版森系风格

第一版风格编码为 `forestFruitTeaCrayon`，名称为“森系水果茶”，服务端种子店铺默认使用该风格。

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
/static/storefront/forest/icons/delivery.png
/static/storefront/forest/icons/recommend.png
/static/storefront/forest/icons/citrus.png
/static/storefront/forest/icons/grape.png
/static/storefront/forest/icons/mango.png
/static/storefront/forest/icons/tea.png
/static/storefront/forest/icons/extra.png
/static/storefront/forest/banner-seasonal.png
/static/storefront/forest/banner-tea.png
/static/storefront/forest/hero-forest-tea.png
/static/storefront/forest/mascot.png
/static/storefront/forest/product-placeholder.png
```

当前仓库已放置占位 PNG，后续替换同名文件即可。

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

1. 新增风格包时，先在服务端 `StyleDTO.theme` 和小程序 `STORE_THEME_PACKAGES` 中补齐同名 token。
2. 新增装修字段时，同步更新 `StoreDecorationDTO`、管理端装修页、小程序首页消费逻辑和 API 文档。
3. 管理端只保存语义字段，不保存页面私有 class 名或布局实现细节。
4. 小程序页面只消费合并后的装修配置，不直接判断商家名称或风格名称来切换样式。
