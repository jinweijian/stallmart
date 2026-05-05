# SaaS Theme DB Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将服务端内存 mock 数据迁移到 MySQL，并打通平台风格包、商家选择风格包、AppID bootstrap 与 Taro H5 优先调试链路。

**Architecture:** Flyway 管理 schema 和 dev seed，JPA Repository 替换服务层内存 Map。Controller 和 DTO 契约尽量保持不变，新增平台风格包和 app bootstrap 作为独立增量接口。

**Tech Stack:** Spring Boot 3.5、Java 25、Spring Data JPA、Flyway、MySQL、Jackson JSON converter、Nuxt 管理端、Taro 小程序/H5。

---

## Tasks

### Task 0: Baseline and Java 25 Alignment

**Files:**

- Modify: `server/build.gradle`
- Modify: `docker/Dockerfile.api`
- Modify: `docs/guide/development-environment.md`
- Modify: `docs/guide/configuration.md`
- Modify: `docs/deploy/index.md`
- Modify: `docker/README.md` if it mentions the Java runtime version

- [x] Update the Gradle Java toolchain from `JavaLanguageVersion.of(21)` to `JavaLanguageVersion.of(25)`.
- [x] Update API Docker build and runtime images from `eclipse-temurin:21-*` to a Java 25 JDK/JRE image that exists in the chosen registry.
- [x] Run `cd server && ./gradlew -version` and confirm Gradle resolves Java 25.
- [x] Run `cd docker && docker compose config` and confirm the `api` and `admin-web` service definitions still render correctly.
- [x] Sync all Java runtime references in configuration and deploy docs from 21 to 25.

### Task 1: Document the Combined Design

**Files:**

- Create: `docs/superpowers/specs/2026-05-04-saas-theme-db-integration-design.md`
- Create: `docs/superpowers/plans/2026-05-04-saas-theme-db-integration.md`

- [x] Add the merged design document with DB model, theme flow, migration strategy, and acceptance criteria.
- [x] Add this implementation plan as the single execution source.
- [x] Verify both files contain no placeholder decisions.

### Task 2: Restore DB Baseline Test Pass

**Files:**

- Modify: `server/src/main/java/com/stallmart/order/internal/service/OrderServiceImpl.java`
- Modify: `server/src/test/java/com/stallmart/order/OrderServiceTest.java`
- Modify: `server/src/test/java/com/stallmart/web/ApiControllerTest.java`

- [x] Reproduce current failure with `cd server && ./gradlew test`; expected failures are order creation tests with `NULL not allowed for column "order_no"`.
- [x] Add or keep a failing test that creates an order through the service/API under the Flyway schema.
- [x] Fix order creation so `order_no` is non-null before the first insert. Do not weaken the schema constraint.
- [x] Run `cd server && ./gradlew test`; expected result: all server tests pass before broader persistence work continues.

### Task 3: Add Persistence Infrastructure

**Files:**

- Modify: `server/build.gradle`
- Modify: `server/src/main/resources/application.yml`
- Modify: `server/src/test/resources/application-test.yml`
- Create: `server/src/test/java/com/stallmart/persistence/PersistenceMigrationTest.java`

- [x] Write a failing Spring Boot test asserting Flyway has applied migrations and `store_style` contains the dev seed.
- [x] Add Spring Data JPA, MySQL connector, Flyway core and Flyway MySQL dependencies.
- [x] Configure datasource, JPA repository access, and Flyway migration locations.
- [x] Configure test profile to use H2 MySQL mode or a test datasource compatible with Flyway.
- [x] Run the persistence test and make it pass.

### Task 4: Create Schema and Dev Seed

**Files:**

- Create: `server/src/main/resources/db/migration/V1__init_schema.sql`
- Create: `server/src/main/resources/db/migration/V2__seed_dev_data.sql`

- [x] Create schema for users, admin accounts, stores, styles, decorations, categories, products, SKUs, specs, carts, orders, and order items.
- [x] Seed current mock users, admin accounts, demo store, forest style package, categories, products, SKUs, specs, cart, and example orders.
- [x] Use `orders` instead of reserved table name `order`.
- [x] Store style/theme/decorations/spec/SKU JSON in JSON-compatible columns.

### Task 5: Add Repository Layer

**Files:**

- Create: `server/src/main/java/com/stallmart/support/persistence/JsonMapConverter.java`
- Create: `server/src/main/java/com/stallmart/support/persistence/JsonListConverter.java`
- Create: domain entity and repository classes under each module `internal/repository`.

- [x] Add focused JPA entities matching the Flyway schema.
- [x] Add repositories for user/admin account, store/style/decoration/category/product/SKU/spec, cart/item, order/item.
- [x] Keep DTO mapping inside service layer, not inside controllers.

### Task 6: Replace User/Auth Persistence

**Files:**

- Modify: `server/src/main/java/com/stallmart/user/internal/service/UserServiceImpl.java`
- Create/modify tests under `server/src/test/java/com/stallmart/user/`

- [x] Write failing tests for DB-backed admin login and user profile update persistence.
- [x] Replace in-memory user/admin account maps with repositories.
- [x] Store admin password hashes and verify with `PasswordEncoder`.
- [x] Preserve JWT session response shape.

### Task 7: Replace Store/Style/Product Persistence

**Files:**

- Modify: `server/src/main/java/com/stallmart/store/internal/service/StoreServiceImpl.java`
- Create/modify tests under `server/src/test/java/com/stallmart/store/`

- [x] Write failing tests for DB-backed storefront decoration merge, product creation, category update, and style specs.
- [x] Replace store/style/spec/category/product maps with repositories.
- [x] Preserve `/stores/{id}`, `/stores/qr/{qrCode}`, `/styles`, `/styles/{styleId}/specs`, and admin product/category endpoints.

### Task 8: Replace Cart/Order Persistence

**Files:**

- Modify: `server/src/main/java/com/stallmart/cart/internal/service/CartServiceImpl.java`
- Modify: `server/src/main/java/com/stallmart/order/internal/service/OrderServiceImpl.java`
- Create/modify tests under `server/src/test/java/com/stallmart/cart/` and `server/src/test/java/com/stallmart/order/`

- [x] Write failing tests for cart updates surviving service reload and order state transitions from DB.
- [x] Replace cart/order maps with repositories.
- [x] Add service-level transactions for create order, update cart, and state transitions.

### Task 9: Add App Bootstrap and Platform Style Management

**Files:**

- Modify: `server/src/main/java/com/stallmart/store/StoreService.java`
- Create: `server/src/main/java/com/stallmart/style/dto/StyleUpsertParams.java`
- Modify/Create controllers under `server/src/main/java/com/stallmart/store/internal/api/` and `server/src/main/java/com/stallmart/management/internal/api/`
- Modify: `web/app/api/stallmart-api.ts`
- Modify/Create platform style pages under `web/app/pages/platform/`
- Modify: `web/app/pages/vendor/decoration.vue`
- Modify: `web/app/types/admin.ts`

- [x] Add `GET /app/bootstrap` that resolves store by AppID header/query and returns lightweight theme/store config.
- [x] Add platform style package list/detail/create/update/publish/unpublish/delete APIs. Delete should be blocked or converted to a safe inactive/archive state when a store still references the style.
- [x] Add request validation for style code uniqueness, required theme fields, status values, and JSON theme contract (`colors`, `categoryIconLibrary`, `assetSizes`, `pageThemes`).
- [x] Add server tests proving platform admins can create, edit, publish, unpublish, and delete or safely reject deletion of a style package; vendor tokens must receive 403.
- [x] Add web platform style package management with add, edit, publish/unpublish, delete, preview, and error states.
- [x] Restrict vendor decoration APIs and UI to active style packages only; direct API updates to an inactive style must fail server-side.

### Task 10: Wire App/H5 Theme Loading

**Files:**

- Modify: `app/src/app-config/index.ts`
- Modify: `app/src/app.ts`
- Modify: `app/src/utils/customer-theme.ts`
- Modify: `app/taro.config.ts` if H5 runtime/build config needs adjustment
- Modify: `app/config/index.js` if Taro CLI reads this file for the active build

- [x] Add environment-controlled mock toggle for H5.
- [x] Add bootstrap endpoint constant.
- [x] Load bootstrap on app launch, persist theme on success, fallback to cached/default theme on failure.
- [x] Make H5 the first app-side integration target: with `TARO_APP_ENABLE_API_MOCK=false` and `TARO_APP_ID=wx-stallmart-demo`, H5 should request `/app/bootstrap` and then real store/product/style/order APIs.
- [x] Run `cd app && npm run dev:h5` during development and manually verify the customer tabs can load through H5 before switching to weapp.
- [x] Add Docker Compose `app-h5` service for H5-first debugging at `http://localhost:10086/`.
- [x] Run `cd app && npm run build:h5`; expected result: production H5 build succeeds.
- [x] Keep static fallback assets for failed network loads.
- [x] Keep `npm run build:weapp` in verification, but defer real weapp API/domain debugging until the mini-program request domain is approved.

### Task 11: Sync Documentation and Verification

**Files:**

- Modify: `docs/api-server/index.md`
- Modify: `docs/api-app/index.md`
- Modify: `docs/specification/storefront-decoration.md`
- Modify: `docs/specification/app-module.md`
- Modify: `docs/guide/configuration.md`
- Modify: `docs/deploy/index.md`
- Modify: `docs/guide/testing.md`
- Modify: `docs/specification/web.md`

- [x] Document DB/Flyway configuration and migration ownership.
- [x] Document bootstrap and full platform style APIs: create, edit, publish, unpublish, delete, and deletion constraints.
- [x] Document that vendor decoration can only select active platform style packages.
- [x] Document Java 25 as the backend toolchain and Docker runtime target.
- [x] Document H5-first app debugging flow, including `dev:h5`, `build:h5`, mock toggle, AppID, and the later weapp domain-dependent verification.
- [x] Run verification:

```bash
cd server
./gradlew test
cd ../app
npm run test:weapp-assets
npm run lint
npm run build:h5
npm run build:weapp
cd ../web
npm run build
cd ../docker
docker compose config
git diff --check
```
