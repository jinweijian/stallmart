# SaaS Theme DB Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将服务端内存 mock 数据迁移到 MySQL，并打通平台风格包、商家选择风格包、小程序 AppID bootstrap 与 H5 真实数据调试。

**Architecture:** Flyway 管理 schema 和 dev seed，JPA Repository 替换服务层内存 Map。Controller 和 DTO 契约尽量保持不变，新增平台风格包和 app bootstrap 作为独立增量接口。

**Tech Stack:** Spring Boot 3.5、Java 21、Spring Data JPA、Flyway、MySQL、Jackson JSON converter、Nuxt 管理端、Taro 小程序/H5。

---

## Tasks

### Task 1: Document the Combined Design

**Files:**

- Create: `docs/superpowers/specs/2026-05-04-saas-theme-db-integration-design.md`
- Create: `docs/superpowers/plans/2026-05-04-saas-theme-db-integration.md`

- [ ] Add the merged design document with DB model, theme flow, migration strategy, and acceptance criteria.
- [ ] Add this implementation plan as the single execution source.
- [ ] Verify both files contain no placeholder decisions.

### Task 2: Add Persistence Infrastructure

**Files:**

- Modify: `server/build.gradle`
- Modify: `server/src/main/resources/application.yml`
- Modify: `server/src/test/resources/application-test.yml`
- Create: `server/src/test/java/com/stallmart/persistence/PersistenceMigrationTest.java`

- [ ] Write a failing Spring Boot test asserting Flyway has applied migrations and `store_style` contains the dev seed.
- [ ] Add Spring Data JPA, MySQL connector, Flyway core and Flyway MySQL dependencies.
- [ ] Configure datasource, JPA repository access, and Flyway migration locations.
- [ ] Configure test profile to use H2 MySQL mode or a test datasource compatible with Flyway.
- [ ] Run the persistence test and make it pass.

### Task 3: Create Schema and Dev Seed

**Files:**

- Create: `server/src/main/resources/db/migration/V1__init_schema.sql`
- Create: `server/src/main/resources/db/migration/V2__seed_dev_data.sql`

- [ ] Create schema for users, admin accounts, stores, styles, decorations, categories, products, SKUs, specs, carts, orders, and order items.
- [ ] Seed current mock users, admin accounts, demo store, forest style package, categories, products, SKUs, specs, cart, and example orders.
- [ ] Use `orders` instead of reserved table name `order`.
- [ ] Store style/theme/decorations/spec/SKU JSON in JSON-compatible columns.

### Task 4: Add Repository Layer

**Files:**

- Create: `server/src/main/java/com/stallmart/support/persistence/JsonMapConverter.java`
- Create: `server/src/main/java/com/stallmart/support/persistence/JsonListConverter.java`
- Create: domain entity and repository classes under each module `internal/repository`.

- [ ] Add focused JPA entities matching the Flyway schema.
- [ ] Add repositories for user/admin account, store/style/decoration/category/product/SKU/spec, cart/item, order/item.
- [ ] Keep DTO mapping inside service layer, not inside controllers.

### Task 5: Replace User/Auth Persistence

**Files:**

- Modify: `server/src/main/java/com/stallmart/user/internal/service/UserServiceImpl.java`
- Create/modify tests under `server/src/test/java/com/stallmart/user/`

- [ ] Write failing tests for DB-backed admin login and user profile update persistence.
- [ ] Replace in-memory user/admin account maps with repositories.
- [ ] Store admin password hashes and verify with `PasswordEncoder`.
- [ ] Preserve JWT session response shape.

### Task 6: Replace Store/Style/Product Persistence

**Files:**

- Modify: `server/src/main/java/com/stallmart/store/internal/service/StoreServiceImpl.java`
- Create/modify tests under `server/src/test/java/com/stallmart/store/`

- [ ] Write failing tests for DB-backed storefront decoration merge, product creation, category update, and style specs.
- [ ] Replace store/style/spec/category/product maps with repositories.
- [ ] Preserve `/stores/{id}`, `/stores/qr/{qrCode}`, `/styles`, `/styles/{styleId}/specs`, and admin product/category endpoints.

### Task 7: Replace Cart/Order Persistence

**Files:**

- Modify: `server/src/main/java/com/stallmart/cart/internal/service/CartServiceImpl.java`
- Modify: `server/src/main/java/com/stallmart/order/internal/service/OrderServiceImpl.java`
- Create/modify tests under `server/src/test/java/com/stallmart/cart/` and `server/src/test/java/com/stallmart/order/`

- [ ] Write failing tests for cart updates surviving service reload and order state transitions from DB.
- [ ] Replace cart/order maps with repositories.
- [ ] Add service-level transactions for create order, update cart, and state transitions.

### Task 8: Add App Bootstrap and Platform Style Management

**Files:**

- Modify: `server/src/main/java/com/stallmart/store/StoreService.java`
- Modify/Create controllers under `server/src/main/java/com/stallmart/store/internal/api/` and `server/src/main/java/com/stallmart/management/internal/api/`
- Modify: `web/app/api/stallmart-api.ts`
- Modify/Create platform style pages under `web/app/pages/platform/`

- [ ] Add `GET /app/bootstrap` that resolves store by AppID header/query and returns lightweight theme/store config.
- [ ] Add platform style package list/create/update/publish/unpublish APIs.
- [ ] Add web platform style package management basics.
- [ ] Restrict vendor decoration to selecting active style packages.

### Task 9: Wire App/H5 Theme Loading

**Files:**

- Modify: `app/src/app-config/index.ts`
- Modify: `app/src/app.ts`
- Modify: `app/src/utils/customer-theme.ts`

- [ ] Add environment-controlled mock toggle for H5.
- [ ] Add bootstrap endpoint constant.
- [ ] Load bootstrap on app launch, persist theme on success, fallback to cached/default theme on failure.
- [ ] Keep static fallback assets for failed network loads.

### Task 10: Sync Documentation and Verification

**Files:**

- Modify: `docs/api-server/index.md`
- Modify: `docs/api-app/index.md`
- Modify: `docs/specification/storefront-decoration.md`
- Modify: `docs/specification/app-module.md`
- Modify: `docs/guide/configuration.md`
- Modify: `docs/deploy/index.md`
- Modify: `docs/guide/testing.md`

- [ ] Document DB/Flyway configuration and migration ownership.
- [ ] Document bootstrap and platform style APIs.
- [ ] Document H5 real API debugging flow.
- [ ] Run server tests and app/web relevant verification commands.
