-- ================================================
-- StallMart 数据库初始化脚本
-- ================================================
-- 自动执行顺序：01-init.sql → 按字母顺序执行其他 .sql 文件
-- ================================================

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    union_id        VARCHAR(100) UNIQUE COMMENT '微信 UnionID',
    open_id         VARCHAR(100) NOT NULL UNIQUE COMMENT '微信 OpenID',
    nickname        VARCHAR(100) COMMENT '微信昵称',
    avatar_url      VARCHAR(500) COMMENT '微信头像',
    phone           VARCHAR(20) UNIQUE COMMENT '脱敏手机号',
    has_phone       TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已授权手机号',
    role            VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' COMMENT 'CUSTOMER / VENDOR / ADMIN',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / DISABLED',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE INDEX idx_user_union_id ON `user`(union_id);
CREATE INDEX idx_user_open_id ON `user`(open_id);
CREATE INDEX idx_user_phone ON `user`(phone);
CREATE INDEX idx_user_role ON `user`(role);

-- 创建风格包表
CREATE TABLE IF NOT EXISTS `store_style` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(50) NOT NULL COMMENT '风格包名称',
    code            VARCHAR(50) NOT NULL UNIQUE COMMENT '风格包代码',
    theme           JSON NOT NULL COMMENT '配色方案',
    icon_position   JSON NOT NULL COMMENT '图标位置配置',
    layout          JSON NOT NULL COMMENT '布局信息',
    config          JSON NOT NULL COMMENT '按钮/卡片样式配置',
    preview_url     VARCHAR(500) COMMENT '预览图',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风格包表';

-- 创建商品规格定义表
CREATE TABLE IF NOT EXISTS `product_spec` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_id        BIGINT NOT NULL COMMENT '关联风格包ID',
    name            VARCHAR(50) NOT NULL COMMENT '规格名称',
    spec_type       VARCHAR(20) NOT NULL COMMENT 'SIZE / SPICE / SWEET / OTHER',
    is_required     TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否必选',
    options         JSON NOT NULL COMMENT '规格选项及价格',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (style_id) REFERENCES `store_style`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格表';

CREATE INDEX idx_product_spec_style ON `product_spec`(style_id);

-- 创建店铺表
CREATE TABLE IF NOT EXISTS `store` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id        BIGINT NOT NULL COMMENT '摊主用户ID',
    style_id        BIGINT COMMENT '风格包ID',
    name            VARCHAR(50) NOT NULL COMMENT '店铺名称',
    category        VARCHAR(50) COMMENT '店铺分类',
    description     VARCHAR(500) COMMENT '店铺描述',
    logo_url        VARCHAR(500) COMMENT '店铺Logo',
    cover_url       VARCHAR(500) COMMENT '店铺封面',
    qr_code         VARCHAR(500) COMMENT '摊位二维码',
    latitude        DECIMAL(10, 8) COMMENT '纬度',
    longitude       DECIMAL(11, 8) COMMENT '经度',
    address         VARCHAR(200) COMMENT '地址',
    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN / CLOSED / DISABLED',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES `user`(id),
    FOREIGN KEY (style_id) REFERENCES `store_style`(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺表';

CREATE INDEX idx_store_owner ON `store`(owner_id);
CREATE INDEX idx_store_status ON `store`(status);
CREATE INDEX idx_store_category ON `store`(category);
CREATE INDEX idx_store_location ON `store`(latitude, longitude);

-- 创建商品表
CREATE TABLE IF NOT EXISTS `product` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id        BIGINT NOT NULL COMMENT '店铺ID',
    name            VARCHAR(100) NOT NULL COMMENT '商品名称',
    base_price      DECIMAL(10, 2) NOT NULL COMMENT '基础价格',
    category        VARCHAR(50) COMMENT '商品分类',
    stock           INTEGER NOT NULL DEFAULT 0 COMMENT '库存',
    image_url       VARCHAR(500) COMMENT '商品图片',
    spec_ids        JSON COMMENT '关联规格ID列表',
    description     VARCHAR(500) COMMENT '商品描述',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / INACTIVE / SOLD_OUT',
    sort_order      INTEGER DEFAULT 0 COMMENT '排序',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES `store`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

CREATE INDEX idx_product_store ON `product`(store_id);
CREATE INDEX idx_product_status ON `product`(status);
CREATE INDEX idx_product_category ON `product`(category);

-- 创建订单表（表名单数 order）
CREATE TABLE IF NOT EXISTS `order` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no        VARCHAR(32) NOT NULL UNIQUE COMMENT '业务订单号',
    user_id         BIGINT NOT NULL COMMENT '下单用户ID',
    store_id        BIGINT NOT NULL COMMENT '店铺ID',
    status          VARCHAR(20) NOT NULL DEFAULT 'NEW' COMMENT 'NEW / ACCEPTED / PREPARING / READY / COMPLETED / REJECTED',
    confirm_code    VARCHAR(10) NOT NULL COMMENT '取餐确认码',
    total_amount    DECIMAL(10, 2) NOT NULL DEFAULT 0 COMMENT '订单总金额',
    remark          VARCHAR(200) COMMENT '备注',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (store_id) REFERENCES `store`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

CREATE INDEX idx_order_user ON `order`(user_id);
CREATE INDEX idx_order_store ON `order`(store_id);
CREATE INDEX idx_order_status ON `order`(status);
CREATE INDEX idx_order_created ON `order`(created_at);
CREATE INDEX idx_order_confirm_code ON `order`(confirm_code);

-- 创建订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id        BIGINT NOT NULL COMMENT '订单ID',
    product_id      BIGINT NOT NULL COMMENT '商品ID',
    product_name    VARCHAR(100) NOT NULL COMMENT '商品名称快照',
    quantity        INTEGER NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price      DECIMAL(10, 2) NOT NULL COMMENT '单价快照',
    specs_snapshot   JSON COMMENT '规格快照',
    FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES `product`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

CREATE INDEX idx_order_item_order ON `order_item`(order_id);

-- ================================================
-- 初始化风格包数据
-- ================================================
INSERT INTO `store_style` (name, code, theme, icon_position, layout, config) VALUES
('夏威夷风', 'hawaiian', 
 '{"primary": "#FF6B35", "secondary": "#2EC4B6", "background": "#FFF8F0", "text": "#333333", "accent": "#FF9F1C"}',
 '{"header_logo": {"x": 20, "y": 20, "width": 60, "height": 60}, "category_icons": []}',
 '{"header_height": 200, "cover_style": "full_width", "button_radius": 12, "card_spacing": 16, "product_grid": "2_columns"}',
 '{"button_style": "rounded", "card_style": "shadow", "icon_style": "filled"}'
),
('烧烤风', 'bbq',
 '{"primary": "#C73E1D", "secondary": "#2D2D2D", "background": "#1A1A1A", "text": "#FFFFFF", "accent": "#FF9500"}',
 '{"header_logo": {"x": 20, "y": 20, "width": 60, "height": 60}, "category_icons": []}',
 '{"header_height": 200, "cover_style": "full_width", "button_radius": 8, "card_spacing": 16, "product_grid": "2_columns"}',
 '{"button_style": "square", "card_style": "bordered", "icon_style": "outline"}'
),
('市集风', 'market',
 '{"primary": "#E8A838", "secondary": "#FFF8F0", "background": "#FFFDF5", "text": "#5D4037", "accent": "#C73E1D"}',
 '{"header_logo": {"x": 20, "y": 20, "width": 60, "height": 60}, "category_icons": []}',
 '{"header_height": 200, "cover_style": "full_width", "button_radius": 16, "card_spacing": 12, "product_grid": "2_columns"}',
 '{"button_style": "rounded", "card_style": "flat", "icon_style": "filled"}'
);
