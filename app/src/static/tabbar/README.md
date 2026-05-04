# 小程序 tabBar 图标规范

本目录存放微信小程序原生 tabBar 使用的图标。图标路径由 `app/src/app.config.ts` 的 `tabBar.list` 引用。

## 强制要求

- 文件格式：PNG。
- 文件尺寸：不强制固定为 `81 x 81`；为避免真机模糊，当前建议输出为 `220 x 220` 到 `280 x 280` 像素的正方形透明 PNG。
- 文件大小：单个图标必须小于 `40KB`，建议控制在 `38KB` 到 `39.5KB`，既保留清晰度又避免触发微信限制。
- 背景：必须是真实透明 alpha，不允许导出设计软件里的棋盘格假透明背景。
- 主体占比：图标主体建议占画布 `88%` 到 `94%`，四周只保留少量安全边距，避免在原生 tabBar 中显得过小。
- 风格：四个 tab 使用同一套吉祥物插画语言、同一线条粗细和同一明度，不要混用不同风格。
- 选中态：当前版本使用同图选中态，由微信 `selectedColor` 和文字状态表达选中；如后续补充选中态图，仍需保持同尺寸、同主体占比。

## 文件清单

| 文件名 | Tab 文案 | 页面路径 | 作用 |
| --- | --- | --- | --- |
| `home.png` | 首页 | `pages/customer/index/index` | 首页未选中图标 |
| `home-active.png` | 首页 | `pages/customer/index/index` | 首页选中图标 |
| `cart.png` | 点单 | `pages/customer/cart/cart` | 点单未选中图标 |
| `cart-active.png` | 点单 | `pages/customer/cart/cart` | 点单选中图标 |
| `order.png` | 订单 | `pages/customer/my-orders/my-orders` | 订单未选中图标 |
| `order-active.png` | 订单 | `pages/customer/my-orders/my-orders` | 订单选中图标 |
| `my.png` | 我的 | `pages/customer/my/my` | 我的未选中图标 |
| `my-active.png` | 我的 | `pages/customer/my/my` | 我的选中图标 |

## 处理规范

- 上传源图可以是大图，但进入本目录前必须先裁掉大面积留白，再按每张图在 `40KB` 内可承载的最大尺寸输出，不要过度压缩到明显模糊。
- 如果源图带棋盘格背景，必须重新抠透明或重绘，不要直接缩放提交。
- 每次替换图标后，优先将源图放到项目根目录下的临时素材目录，再运行：

```bash
cd app
npm run optimize:tabbar -- ../tmp/image2
```

该脚本会从源目录读取 8 个 tabBar 文件，在 `40KB` 内自动寻找最大可用尺寸，写入 `src/static/tabbar/`，同步到 `dist/static/tabbar/`，并在存在管理端静态目录时镜像到 `web/public/static/tabbar/`。

- 每次替换图标后都要运行 `npm run build:weapp`，确认微信端构建产物没有触发 tabBar 图标大小错误。
