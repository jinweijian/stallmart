INSERT INTO users (id, open_id, nickname, avatar_url, phone, has_phone, role, status) VALUES
(1, 'dev-customer-openid', '市集顾客', '/static/default-avatar.png', NULL, FALSE, 'CUSTOMER', 'ACTIVE'),
(2, 'dev-vendor-openid', '海边摊主', '/static/vendor-avatar.png', '139****1201', TRUE, 'VENDOR', 'ACTIVE'),
(99, 'dev-admin-openid', '平台管理员', '/static/admin-avatar.png', '138****0099', TRUE, 'ADMIN', 'ACTIVE');

INSERT INTO store_style (id, name, code, status, version, preview_url, theme_json) VALUES
(1, '夏威夷风', 'hawaiian', 'ACTIVE', 1, NULL,
'{"code":"hawaiian","name":"夏威夷风","layoutVersion":"customer-storefront-v1","colors":{"primary":"#2ECC71","secondary":"#F9D66E","accent":"#FF8E5E","background":"#FEF9E7","surface":"#FFFFFF","text":"#2D3436","mutedText":"#7A7A6A","border":"#E8E2C8","price":"#2ECC71"},"iconNames":{"location":"hawaiian-location","cart":"hawaiian-cart","checkout":"hawaiian-checkout","delivery":"hawaiian-delivery","sectionLeaf":"hawaiian-section"},"iconUrls":{},"imageUrls":{},"copywriting":{"heroEyebrow":"今日推荐","heroTitle":"夏威夷风","heroSubtitle":"新鲜现制 · 即点即取","promoTitle":"本店上新","promoSubtitle":"精选好物限时供应","promoActionText":"去看看"},"categoryIconLibrary":[{"key":"recommend","name":"人气推荐","iconUrl":null,"fallbackText":"荐"},{"key":"tea","name":"饮品","iconUrl":null,"fallbackText":"饮"},{"key":"extra","name":"加料","iconUrl":null,"fallbackText":"料"}]}'),
(2, '烧烤风', 'BBQ', 'ACTIVE', 1, NULL,
'{"code":"BBQ","name":"烧烤风","layoutVersion":"customer-storefront-v1","colors":{"primary":"#E74C3C","secondary":"#F39C12","accent":"#FF8E5E","background":"#FDEDEC","surface":"#FFFFFF","text":"#2D3436","mutedText":"#7A7A6A","border":"#E8E2C8","price":"#E74C3C"},"iconNames":{"location":"BBQ-location","cart":"BBQ-cart","checkout":"BBQ-checkout","delivery":"BBQ-delivery","sectionLeaf":"BBQ-section"},"iconUrls":{},"imageUrls":{},"copywriting":{"heroEyebrow":"今日推荐","heroTitle":"烧烤风","heroSubtitle":"新鲜现制 · 即点即取","promoTitle":"本店上新","promoSubtitle":"精选好物限时供应","promoActionText":"去看看"},"categoryIconLibrary":[{"key":"recommend","name":"人气推荐","iconUrl":null,"fallbackText":"荐"},{"key":"tea","name":"饮品","iconUrl":null,"fallbackText":"饮"},{"key":"extra","name":"加料","iconUrl":null,"fallbackText":"料"}]}'),
(3, '市集风', 'market', 'ACTIVE', 1, NULL,
'{"code":"market","name":"市集风","layoutVersion":"customer-storefront-v1","colors":{"primary":"#F39C12","secondary":"#8BC34A","accent":"#FF8E5E","background":"#FFF8E7","surface":"#FFFFFF","text":"#2D3436","mutedText":"#7A7A6A","border":"#E8E2C8","price":"#F39C12"},"iconNames":{"location":"market-location","cart":"market-cart","checkout":"market-checkout","delivery":"market-delivery","sectionLeaf":"market-section"},"iconUrls":{},"imageUrls":{},"copywriting":{"heroEyebrow":"今日推荐","heroTitle":"市集风","heroSubtitle":"新鲜现制 · 即点即取","promoTitle":"本店上新","promoSubtitle":"精选好物限时供应","promoActionText":"去看看"},"categoryIconLibrary":[{"key":"recommend","name":"人气推荐","iconUrl":null,"fallbackText":"荐"},{"key":"tea","name":"饮品","iconUrl":null,"fallbackText":"饮"},{"key":"extra","name":"加料","iconUrl":null,"fallbackText":"料"}]}'),
(6, '森系水果茶-小白款', 'forestFruitTeaCrayon', 'ACTIVE', 1, '/static/storefront/forest/preview.png',
'{"code":"forestFruitTeaCrayon","name":"森系水果茶-小白款","layoutVersion":"customer-storefront-v1","colors":{"primary":"#6F9646","secondary":"#B8C77A","accent":"#F2B94B","background":"#FBFAEF","surface":"#FFFDF4","text":"#4C6040","mutedText":"#7A866D","border":"#DCE6C7","price":"#6F9646"},"iconNames":{"location":"forest-location","cart":"forest-cart","checkout":"forest-checkout","delivery":"forest-delivery","sectionLeaf":"forest-leaf"},"iconUrls":{"location":"/static/storefront/forest/icons/location.png","cart":"/static/storefront/forest/icons/cart.png","checkout":"/static/storefront/forest/icons/checkout.png","delivery":"/static/storefront/forest/icons/delivery.png","sectionLeaf":"/static/storefront/forest/icons/leaf.png"},"imageUrls":{"heroIllustration":"/static/storefront/forest/hero-forest-tea.jpg","mascot":"/static/storefront/forest/mascot.png","productPlaceholder":"/static/storefront/forest/product-placeholder.png","promoIllustration":"/static/storefront/forest/promo-drink.png"},"copywriting":{"branchName":"上海环球港店","heroEyebrow":"小新の","heroTitle":"水果茶屋","heroSubtitle":"自然水果 · 新鲜现制","promoTitle":"鲜果时令上新","promoSubtitle":"当季水果 · 清爽一夏","promoActionText":"立即尝鲜"},"categoryIconLibrary":[{"key":"recommend","name":"人气推荐","iconUrl":"/static/storefront/forest/icons/recommend.png","fallbackText":"荐"},{"key":"citrus","name":"清爽柠檬","iconUrl":"/static/storefront/forest/icons/citrus.png","fallbackText":"柠"},{"key":"grape","name":"多肉葡萄","iconUrl":"/static/storefront/forest/icons/grape.png","fallbackText":"葡"},{"key":"mango","name":"香甜芒果","iconUrl":"/static/storefront/forest/icons/mango.png","fallbackText":"芒"},{"key":"tea","name":"鲜果茶桶","iconUrl":"/static/storefront/forest/icons/tea.png","fallbackText":"茶"},{"key":"extra","name":"加料小料","iconUrl":"/static/storefront/forest/icons/extra.png","fallbackText":"料"},{"key":"category1","name":"类目图标 1","iconUrl":"/static/storefront/forest/icons/category-1.png","fallbackText":"类"},{"key":"category2","name":"类目图标 2","iconUrl":"/static/storefront/forest/icons/category-2.png","fallbackText":"类"}]}');

INSERT INTO store (id, owner_id, style_id, app_id, name, category, description, logo_url, cover_url, qr_code, address, status) VALUES
(1, 2, 6, 'wx-stallmart-demo', '小新の水果茶屋', '饮品', '当季鲜果茶，清爽一夏', '/static/default-avatar.png', '/static/storefront/forest/cover.png', 'stall-001', '市集东入口 12 号', 'OPEN');

INSERT INTO admin_account (account, password_hash, user_id, store_id, entry_path, status) VALUES
('platform', '$2a$10$okgWBelmZskPqgwrW.w4We7kz1107BxGE1GQ0cSkMdwiLBgS2w.8a', 99, NULL, '/platform/vendors', 'ACTIVE'),
('vendor', '$2a$10$d7ikXINb726uiRrFXs23l.apDKIdu9sav9CQkjvne7jREPvGLVatW', 2, 1, '/vendor', 'ACTIVE');

INSERT INTO store_decoration (store_id, banners_json, image_urls_json, copywriting_json) VALUES
(1,
'["/static/storefront/forest/banner-seasonal.jpg","/static/storefront/forest/banner-tea.jpg"]',
'{"heroIllustration":"/static/storefront/forest/hero-forest-tea.jpg","mascot":"/static/storefront/forest/mascot.png","productPlaceholder":"/static/storefront/forest/product-placeholder.png"}',
'{"branchName":"上海环球港店","heroEyebrow":"小新の","heroTitle":"水果茶屋","heroSubtitle":"自然水果 · 新鲜现制","promoTitle":"鲜果时令上新","promoSubtitle":"当季水果 · 清爽一夏","promoActionText":"立即尝鲜"}');

INSERT INTO category (id, store_id, module, name, icon_key, sort_order, status) VALUES
(1, 1, 'PRODUCT', '清爽柠檬', 'category1', 1, 'ACTIVE'),
(2, 1, 'PRODUCT', '多肉葡萄', 'category2', 2, 'ACTIVE'),
(3, 1, 'PRODUCT', '香甜芒果', 'mango', 3, 'ACTIVE');

INSERT INTO product_spec (id, style_id, name, spec_type, is_required, options_json) VALUES
(1, 6, '杯型', 'SIZE', TRUE, '["中杯","大杯"]'),
(2, 6, '甜度', 'SWEET', TRUE, '["少糖","正常糖"]'),
(3, 1, '杯型', 'SIZE', TRUE, '["中杯","大杯"]'),
(4, 1, '甜度', 'SWEET', FALSE, '["无糖","少糖","正常糖"]');

INSERT INTO product (id, store_id, category_id, name, description, main_image_url, status, sort_order, spec_ids_json) VALUES
(1, 1, 1, '百香果柠檬茶', '酸甜清爽', NULL, 'ACTIVE', 1, '[1,2]'),
(2, 1, 2, '阳光青提多多', '阳光玫瑰青提 + 乳酸菌', NULL, 'ACTIVE', 2, '[1,2]'),
(3, 1, 3, '芒芒百香绿茶', '大颗芒果肉 + 百香果', NULL, 'ACTIVE', 3, '[1,2]');

INSERT INTO product_sku (id, product_id, spec_values_json, price, stock, status) VALUES
(1, 1, '["中杯","少糖"]', 12.00, 99, 'ACTIVE'),
(2, 1, '["大杯","少糖"]', 15.00, 99, 'ACTIVE'),
(3, 2, '["中杯","正常糖"]', 16.00, 99, 'ACTIVE'),
(4, 2, '["大杯","正常糖"]', 19.00, 99, 'ACTIVE'),
(5, 3, '["中杯","少糖"]', 17.00, 99, 'ACTIVE'),
(6, 3, '["大杯","正常糖"]', 20.00, 99, 'ACTIVE');

INSERT INTO cart (id, user_id, store_id, status, updated_at) VALUES
(1, 1, 1, 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO cart_item (cart_id, product_id, product_name, quantity, unit_price, specs_text) VALUES
(1, 2, '芒果椰椰', 1, 16.00, '少糖');

INSERT INTO orders (id, order_no, user_id, store_id, status, confirm_code, total_amount, remark, created_at) VALUES
(1, 'SM20260504000001', 1, 1, 'NEW', '1001', 12.00, '少冰，现场取餐', CURRENT_TIMESTAMP);

INSERT INTO order_item (order_id, product_id, product_name, quantity, unit_price, specs_text) VALUES
(1, 1, '百香果柠檬茶', 1, 12.00, '中杯');
