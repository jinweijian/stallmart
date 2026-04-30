-- StallMart 数据库初始化脚本
-- 版本：v1.0

CREATE DATABASE IF NOT EXISTS stallmart DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE stallmart;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `openid` VARCHAR(128) NOT NULL COMMENT '微信openid',
    `unionid` VARCHAR(128) COMMENT '微信unionid',
    `nickname` VARCHAR(64) COMMENT '昵称',
    `avatar_url` VARCHAR(512) COMMENT '头像URL',
    `phone` VARCHAR(20) COMMENT '手机号',
    `has_phone` TINYINT(1) DEFAULT 0 COMMENT '是否有手机号',
    `role` VARCHAR(20) DEFAULT 'customer' COMMENT '角色：customer/vendor/admin',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 店铺表
CREATE TABLE IF NOT EXISTS `store` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '店铺ID',
    `owner_id` BIGINT NOT NULL COMMENT '摊主用户ID',
    `name` VARCHAR(64) NOT NULL COMMENT '店铺名称',
    `description` VARCHAR(256) COMMENT '店铺描述',
    `avatar_url` VARCHAR(512) COMMENT '店铺头像',
    `qr_code` VARCHAR(128) NOT NULL COMMENT '店铺二维码',
    `style_id` BIGINT COMMENT '风格包ID',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active/closed',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY `uk_qr_code` (`qr_code`),
    KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺表';

-- 风格包表
CREATE TABLE IF NOT EXISTS `store_style` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '风格ID',
    `name` VARCHAR(64) NOT NULL COMMENT '风格名称',
    `theme` VARCHAR(32) NOT NULL COMMENT '主题：hawaii/bbq/market/ocean/fresh',
    `config` JSON COMMENT '风格配置JSON',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风格包表';

-- 规格表
CREATE TABLE IF NOT EXISTS `product_spec` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格ID',
    `style_id` BIGINT NOT NULL COMMENT '风格包ID',
    `name` VARCHAR(64) NOT NULL COMMENT '规格名称',
    `description` VARCHAR(256) COMMENT '规格描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记',
    KEY `idx_style_id` (`style_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规格表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    `store_id` BIGINT NOT NULL COMMENT '店铺ID',
    `name` VARCHAR(64) NOT NULL COMMENT '商品名称',
    `description` VARCHAR(512) COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `image_url` VARCHAR(512) COMMENT '商品图片',
    `spec_ids` VARCHAR(256) COMMENT '关联规格ID列表，逗号分隔',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active/off_sale',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记',
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `customer_id` BIGINT NOT NULL COMMENT '顾客用户ID',
    `store_id` BIGINT NOT NULL COMMENT '店铺ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending/accepted/preparing/ready/completed/rejected/cancelled',
    `confirm_code` VARCHAR(8) COMMENT '取餐确认码',
    `remark` VARCHAR(256) COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记',
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(64) NOT NULL COMMENT '商品名称（冗余）',
    `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `spec_name` VARCHAR(64) COMMENT '规格名称（冗余）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标记',
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- 预置风格包数据
INSERT INTO `store_style` (`name`, `theme`, `config`) VALUES
('夏威夷风', 'hawaii', '{"primaryColor": "#FF6B6B", "backgroundColor": "#FFF5E6", "fontFamily": "sans-serif"}'),
('烧烤风', 'bbq', '{"primaryColor": "#8B4513", "backgroundColor": "#2F2F2F", "fontFamily": "sans-serif"}'),
('市集风', 'market', '{"primaryColor": "#228B22", "backgroundColor": "#F5F5DC", "fontFamily": "serif"}'),
('海洋风', 'ocean', '{"primaryColor": "#0077B6", "backgroundColor": "#E0F7FA", "fontFamily": "sans-serif"}'),
('清新风', 'fresh', '{"primaryColor": "#90EE90", "backgroundColor": "#FFFFFF", "fontFamily": "sans-serif"}');
