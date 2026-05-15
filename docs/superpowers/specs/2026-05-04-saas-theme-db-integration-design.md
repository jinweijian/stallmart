# SaaS 主题装修与 DB 持久化设计

## 背景

服务端当前核心数据仍由内存 `ConcurrentHashMap` 提供，数据库初始化脚本也停留在旧表结构和旧风格包 JSON。管理端已经有店铺、商品、分类、订单、购物车和装修页面，小程序端也已经有 `app/src/utils/customer-theme.ts` 负责缓存顾客端主题，但两端还没有建立在真实数据库和稳定主题契约之上。

本设计把两条工作合并：

- 服务端 mock 数据迁移到 MySQL。
- 平台风格包、商家选择风格包、小程序按 AppID 加载主题打通。

## 目标

- 服务端使用 `JPA + Flyway + MySQL` 管理业务数据，重启后数据不丢失。
- 后端开发、测试和 Docker 运行时统一使用 Java 25。
- 当前 mock 数据迁移为 dev seed，保留本地联调账号、演示店铺、商品、风格包、订单和购物车。
- 平台角色管理风格包，包含创建、修改、素材上传、上下架和预览。
- 平台删除风格包必须有安全约束：已被店铺引用的风格包不能硬删，只能拒绝删除或转为归档/下架状态。
- 商家角色只选择平台已上架风格包，暂不编辑风格包内容。
- App/H5 启动时按 AppID 获取租户基础配置和主题，写入 `customer-theme.ts` 缓存。
- 本阶段先完成 Taro H5 构建与真实 API 调试链路；小程序 request 合法域名申请完成后，再切到 weapp 真机和开发者工具联调。

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

商家选择风格包时只更新 `store.style_id`，且只能选择 `ACTIVE` 风格包；服务端必须校验这个约束，前端过滤只是体验优化。商家自定义内容暂时只保留在 `store_decoration` 覆盖层；v1 不开放商家自由编辑风格包。

App/H5 启动时调用 bootstrap 接口，后端按 AppID 定位店铺，返回店铺基础信息、风格包版本、合并主题和装修配置。app 端成功后调用 `persistCustomerTheme()`；失败时优先使用本地缓存，再 fallback 到 `DEFAULT_STORE_THEME`。

开发优先级是 H5 先行：本地通过 `npm run dev:h5` 快速调试页面、主题、API 和后台配置联动，构建交付必须包含 `npm run build:h5`。小程序端保持 mock/静态兜底可用，等域名配置就绪后再关闭 mock 做 weapp 真实 API 联调。

素材不重新打包进小程序目录。上传端负责压缩、校验和生成小程序可用的网络 URL；小程序读取 URL 并保留本地占位图兜底。

## 迁移策略

第一阶段优先保证现有 API 不变，只替换数据来源。Controller 与 Service 接口保持稳定，Service 内部改为 Repository 访问和 DTO 组装。

迁移步骤：

1. 统一 Java 25 toolchain、Docker 镜像和文档说明。
2. 先修复现有 DB 基线失败：订单创建必须在首次 insert 前生成非空 `order_no`。
3. 接入 JPA/Flyway/MySQL 配置。
4. 新建 schema migration 和 dev seed。
5. 新增 Entity、Repository 和 JSON 转换能力。
6. 替换用户/认证、店铺/风格/商品、购物车、订单服务。
7. 新增 AppID bootstrap。
8. 在平台端增加完整风格包管理接口：列表、详情、创建、编辑、上架、下架、删除/安全拒删。
9. 同步 app/web/docs，并完成 H5 dev/build 验证；weapp 构建保持可用，真实小程序联调留到域名就绪后执行。

## 验收标准

- `./gradlew test` 通过。
- `server/build.gradle`、API Dockerfile 和部署文档均指向 Java 25。
- 后端启动后 Flyway 自动建表并写入 dev seed。
- 使用当前管理端 seed 账号可登录管理端；认证账号密码以 `docs/api-server/index.md` 为准。
- `/stores/1`、`/stores/qr/stall-001`、`/styles/6/specs`、`/admin/vendor/me/summary` 返回 DB 数据。
- 平台管理员可以新增、编辑、上架、下架、删除或安全拒删风格包；商家管理员无法调用平台风格包写接口。
- 商家装修页和接口只能选择已上架风格包，不能通过构造请求切换到下架风格包。
- 更新店铺、装修、商品、分类、购物车和订单后，重启服务数据仍存在。
- `npm run dev:h5` 可用于关闭 mock 后调试真实店铺、商品、规格、订单和主题数据。
- `npm run build:h5` 通过，H5 构建产物可作为本阶段 app 端调试交付。
- `npm run build:weapp` 保持通过；真实小程序 API 联调等 request 合法域名申请完成后再执行。
