# SaaS 主题装修与 DB 持久化设计

## 背景

服务端当前核心数据仍由内存 `ConcurrentHashMap` 提供，数据库初始化脚本也停留在旧表结构和旧风格包 JSON。管理端已经有店铺、商品、分类、订单、购物车和装修页面，小程序端也已经有 `app/src/utils/customer-theme.ts` 负责缓存顾客端主题，但两端还没有建立在真实数据库和稳定主题契约之上。

本设计把两条工作合并：

- 服务端 mock 数据迁移到 MySQL。
- 平台风格包、商家选择风格包、小程序按 AppID 加载主题打通。

## 目标

- 服务端使用 `JPA + Flyway + MySQL` 管理业务数据，重启后数据不丢失。
- 当前 mock 数据迁移为 dev seed，保留本地联调账号、演示店铺、商品、风格包、订单和购物车。
- 平台角色管理风格包，包含创建、修改、素材上传、上下架和预览。
- 商家角色只选择平台已上架风格包，暂不编辑风格包内容。
- 小程序启动时按 AppID 获取租户基础配置和主题，写入 `customer-theme.ts` 缓存。
- H5 调试可关闭 mock 并读取真实 API。

## 数据模型

服务端使用 Flyway 迁移业务表，避免继续维护 `docker/mysql/init/01-init.sql` 中的业务建表逻辑。

核心表：

- `users`：微信用户、后台用户和角色。
- `admin_account`：后台账号、BCrypt 密码、绑定用户、绑定店铺、入口路径。
- `store`：商家店铺，包含 `app_id`、`owner_id`、`style_id`、二维码和状态。
- `store_style`：平台风格包，包含 `code/name/status/version/preview_url/theme_json`。
- `store_decoration`：店铺覆盖层，保存 banner、颜色、icon、分类 icon、主题图片和文案覆盖。
- `category`：店铺分类，保存 `module/name/icon_key/sort_order/status`。
- `product` 与 `product_sku`：商品基础信息和 SKU。
- `product_spec`：绑定风格包的规格定义。
- `cart` 与 `cart_item`：用户购物车和商品快照。
- `orders` 与 `order_item`：订单和订单明细快照。

JSON 字段用于主题包、装修覆盖、规格选项、SKU 规格值和订单/购物车快照，避免第一阶段拆分过多 token 子表。

## 主题链路

平台创建风格包后，完整 `StorefrontThemeDTO` 存入 `store_style.theme_json`。风格包字段必须覆盖颜色、语义 icon、图片、文案、分类 icon 库、`assetSizes`、`pageThemes` 和 tabBar 配置。

商家选择风格包时只更新 `store.style_id`。商家自定义内容暂时只保留在 `store_decoration` 覆盖层；v1 不开放商家自由编辑风格包。

小程序启动时调用 bootstrap 接口，后端按 AppID 定位店铺，返回店铺基础信息、风格包版本、合并主题和装修配置。app 端成功后调用 `persistCustomerTheme()`；失败时优先使用本地缓存，再 fallback 到 `DEFAULT_STORE_THEME`。

素材不重新打包进小程序目录。上传端负责压缩、校验和生成小程序可用的网络 URL；小程序读取 URL 并保留本地占位图兜底。

## 迁移策略

第一阶段优先保证现有 API 不变，只替换数据来源。Controller 与 Service 接口保持稳定，Service 内部改为 Repository 访问和 DTO 组装。

迁移步骤：

1. 接入 JPA/Flyway/MySQL 配置。
2. 新建 schema migration 和 dev seed。
3. 新增 Entity、Repository 和 JSON 转换能力。
4. 替换用户/认证、店铺/风格/商品、购物车、订单服务。
5. 新增 AppID bootstrap。
6. 在平台端增加风格包管理基础接口。
7. 同步 app/web/docs，并关闭 H5 mock 调试路径。

## 验收标准

- `./gradlew test` 通过。
- 后端启动后 Flyway 自动建表并写入 dev seed。
- 使用 `platform/platform123` 和 `vendor/vendor123` 可登录管理端。
- `/stores/1`、`/stores/qr/stall-001`、`/styles/6/specs`、`/admin/vendor/me/summary` 返回 DB 数据。
- 更新店铺、装修、商品、分类、购物车和订单后，重启服务数据仍存在。
- app/H5 关闭 mock 后能读取真实店铺、商品、规格、订单和主题数据。
