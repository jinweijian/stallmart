CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    union_id VARCHAR(100),
    open_id VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    has_phone BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_role ON users(role);

CREATE TABLE admin_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL,
    user_id BIGINT NOT NULL,
    store_id BIGINT,
    entry_path VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE store_style (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    code VARCHAR(80) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 1,
    preview_url VARCHAR(500),
    theme_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE store (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    style_id BIGINT NOT NULL,
    app_id VARCHAR(120) UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    description VARCHAR(500),
    logo_url VARCHAR(500),
    cover_url VARCHAR(500),
    qr_code VARCHAR(120) NOT NULL UNIQUE,
    address VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_store_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_store_style FOREIGN KEY (style_id) REFERENCES store_style(id)
);

CREATE INDEX idx_store_owner ON store(owner_id);
CREATE INDEX idx_store_app_id ON store(app_id);

ALTER TABLE admin_account
    ADD CONSTRAINT fk_admin_account_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE admin_account
    ADD CONSTRAINT fk_admin_account_store FOREIGN KEY (store_id) REFERENCES store(id);

CREATE TABLE store_decoration (
    store_id BIGINT PRIMARY KEY,
    banners_json TEXT,
    colors_json TEXT,
    icon_urls_json TEXT,
    category_icon_urls_json TEXT,
    image_urls_json TEXT,
    copywriting_json TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_store_decoration_store FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE
);

CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    module VARCHAR(40) NOT NULL DEFAULT 'PRODUCT',
    name VARCHAR(80) NOT NULL,
    icon_key VARCHAR(80) NOT NULL DEFAULT 'recommend',
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_category_store FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE
);

CREATE INDEX idx_category_store_module ON category(store_id, module);

CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    main_image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    spec_ids_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_store FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE INDEX idx_product_store ON product(store_id);

CREATE TABLE product_sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    spec_values_json TEXT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_product_sku_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE product_spec (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    style_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    spec_type VARCHAR(40) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    options_json TEXT NOT NULL,
    CONSTRAINT fk_product_spec_style FOREIGN KEY (style_id) REFERENCES store_style(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_spec_style ON product_spec(style_id);

CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_cart_store FOREIGN KEY (store_id) REFERENCES store(id)
);

CREATE INDEX idx_cart_user_store ON cart(user_id, store_id);

CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    specs_text VARCHAR(200),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(40) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    confirm_code VARCHAR(10) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    remark VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_store FOREIGN KEY (store_id) REFERENCES store(id)
);

CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_store ON orders(store_id);

CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    specs_text VARCHAR(200),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
