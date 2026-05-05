# 店铺装修静态资源规范

本目录存放小程序端店铺装修风格包资源。页面只能通过 `STORE_THEME_PACKAGES`、服务端 `StoreDecorationDTO` 或后台装修配置引用这些资源，不要在页面里硬编码素材来源路径。

## 通用要求

- 小程序展示资源必须是可解码、非空白图片，不允许提交 1px 透明图、纯透明占位图或带“棋盘格假透明背景”的图片。
- 需要透明背景的图标必须使用真实 PNG alpha 透明，不要把设计软件的透明棋盘格导出到图片内容里。
- 上传原图可以更大；进入本目录的展示版必须按下表尺寸裁切或压缩，页面展示尺寸由风格包 `assetSizes` 强制控制。
- 本地小程序图片建议单文件不超过 `512KB`；tabBar 图标按微信限制另见 `../tabbar/README.md`。
- 图片命名使用小写英文、数字和连字符。已有被配置引用的文件名不要随意改名，避免破坏后端装修配置。

## 森系水果茶-小白款资源

| 文件路径 | 文件名 | 图片作用 | 展示版规格 |
| --- | --- | --- | --- |
| `forest/hero-forest-tea.jpg` | `hero-forest-tea.jpg` | 首页首屏主题头图 | `750 x 536`，JPG |
| `forest/banner-seasonal.jpg` | `banner-seasonal.jpg` | 首页/我的活动轮播图 | `692 x 220`，JPG |
| `forest/banner-tea.jpg` | `banner-tea.jpg` | 首页/点单活动轮播图 | `692 x 220`，JPG |
| `forest/product-placeholder.png` | `product-placeholder.png` | 商品无图时的兜底展示图 | `256 x 256`，PNG/JPG 均可 |
| `forest/promo-drink.png` | `promo-drink.png` | 活动卡片内饮品插画兜底 | `256 x 256`，PNG |
| `forest/mascot.png` | `mascot.png` | 我的页欢迎区、空状态吉祥物 | `256 x 256`，PNG 真实透明 |
| `forest/cover.png` | `cover.png` | 后台风格包封面 | `360 x 360`，PNG/JPG 均可 |
| `forest/preview.png` | `preview.png` | 后台风格包预览图 | `360 x 360`，PNG/JPG 均可 |
| `forest/icons/cart.png` | `cart.png` | 首页底部结算栏购物车吉祥物 | `256 x 256`，PNG；展示尺寸由 `assetSizes.cartMascot` 固定为 `140rpx x 140rpx` |
| `forest/icons/category-1.png` | `category-1.png` | 分类 icon 库：用户自定义类目 1 | `108 x 108`，PNG |
| `forest/icons/category-2.png` | `category-2.png` | 分类 icon 库：用户自定义类目 2 | `108 x 108`，PNG |
| `forest/icons/*.png` | 其他 icon | 定位、结算、菜单、分类 icon 库默认项 | `108 x 108`，PNG 真实透明 |

## 需要重绘或正式补图的资源清单

以下文件曾是透明占位图，当前已替换为可显示的规范占位图。后续如果使用 GPT-imagegen2 重绘，按提示词生成后仍需裁切到上表规格。

| 文件路径 | 文件名称 | 图片作用 | GPT-imagegen2 中文提示词 |
| --- | --- | --- | --- |
| `app/src/static/storefront/forest/cover.png` | `cover.png` | 后台风格包封面 | `生成一张森系水果茶小程序风格包封面，正方形构图，水彩插画风，浅奶油背景，绿色树叶、柠檬、葡萄、菠萝和一只白色棕耳小狗，中央有水果茶屋氛围但不要文字，柔和明亮，可爱精致，适合后台风格包预览。` |
| `app/src/static/storefront/forest/preview.png` | `preview.png` | 后台风格包预览图 | `生成一张森系水果茶小程序界面预览插画，正方形构图，浅绿色与奶油色，包含水果茶杯、白色棕耳小狗、叶子和水果篮，留白干净，不要文字，不要手机边框，水彩质感。` |
| `app/src/static/storefront/forest/mascot.png` | `mascot.png` | 页面吉祥物、空状态插画 | `生成一只可爱的白色棕耳小狗吉祥物，抱着一杯柠檬水果茶，正面半身，水彩手绘风，柔和线条，透明背景，不要文字，不要阴影底板，适合小程序 UI 插画。` |
| `app/src/static/storefront/forest/promo-drink.png` | `promo-drink.png` | 活动卡片饮品插画 | `生成一杯透明杯装水果茶，杯中有橙子、葡萄柚、青柠、冰块和薄荷，杯身带可爱小狗贴纸，水彩手绘风，透明背景，不要文字，适合放在活动 banner 内。` |
| `app/src/static/storefront/forest/icons/location.png` | `location.png` | 门店定位 icon | `生成森系水果茶风格定位图标，绿色定位针形状，内部带小叶子点缀，扁平水彩质感，透明背景，无文字，适合 108x108 小程序 icon。` |
| `app/src/static/storefront/forest/icons/checkout.png` | `checkout.png` | 去结算按钮 icon | `生成森系水果茶风格结算图标，绿色小购物袋或收银小票，带白色对勾，圆润可爱，透明背景，无文字，适合 108x108 小程序按钮 icon。` |
| `app/src/static/storefront/forest/icons/delivery.png` | `delivery.png` | 取餐/外带能力预留 icon | `生成森系水果茶风格外带图标，绿色线条的小手提袋和一杯水果茶，水彩扁平风，透明背景，无文字，适合 108x108 小程序 icon。` |
| `app/src/static/storefront/forest/icons/leaf.png` | `leaf.png` | 区块标题叶子装饰 icon | `生成一枚小绿叶 icon，森系水彩风，形状简洁，透明背景，无文字，适合标题后缀装饰，108x108 画布内主体居中。` |
| `app/src/static/storefront/forest/icons/recommend.png` | `recommend.png` | 分类 icon 库：人气推荐 | `生成森系水果茶分类图标，人气推荐主题，绿色小徽章加白色星星和叶子，可爱水彩扁平风，透明背景，无文字，108x108。` |
| `app/src/static/storefront/forest/icons/citrus.png` | `citrus.png` | 分类 icon 库：清爽柠檬 | `生成森系水果茶分类图标，柠檬与青柠切片组合，浅绿黄色，水彩手绘风，透明背景，无文字，108x108。` |
| `app/src/static/storefront/forest/icons/grape.png` | `grape.png` | 分类 icon 库：多肉葡萄 | `生成森系水果茶分类图标，一串紫葡萄和绿色叶子，水彩手绘风，透明背景，无文字，108x108。` |
| `app/src/static/storefront/forest/icons/mango.png` | `mango.png` | 分类 icon 库：香甜芒果 | `生成森系水果茶分类图标，切开的黄色芒果和小绿叶，水彩手绘风，透明背景，无文字，108x108。` |
| `app/src/static/storefront/forest/icons/tea.png` | `tea.png` | 分类 icon 库：鲜果茶桶 | `生成森系水果茶分类图标，一杯大桶水果茶，杯中有冰块和水果切片，绿色杯贴，水彩手绘风，透明背景，无文字，108x108。` |
| `app/src/static/storefront/forest/icons/extra.png` | `extra.png` | 分类 icon 库：加料小料 | `生成森系水果茶分类图标，小碗珍珠、果冻和椰果配料，浅绿色点缀，水彩手绘风，透明背景，无文字，108x108。` |

## 分类 icon 库规则

- 风格包只提供 `categoryIconLibrary`，不要把分类与 icon 的固定对应关系写死在风格包里。
- 后台创建或修改分类时保存 `iconKey` 或最终 `iconUrl`，小程序按后端返回结果展示。
- 分类 icon 在页面上的展示尺寸由 `assetSizes.categoryIcon` 控制，当前为 `54rpx`；源文件统一 `108 x 108`，保证高分屏清晰。
