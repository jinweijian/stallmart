# Architecture

**Analysis Date:** 2026-04-26

## Pattern Overview

**Overall:** Multi-service SaaS with WeChat Mini Program frontend

**Key Characteristics:**
- Backend: Spring Boot REST API with MyBatis-Plus ORM
- Frontend: Taro + Vue3 WeChat Mini Program
- Authentication: JWT-based with Redis token blacklisting
- Database: MySQL 9.0 with Redis 7.2 for caching

## Layers

**Web Layer (Mini Program):**
- Purpose: User-facing WeChat Mini Program for customers and vendors
- Location: `src/mini-program/`
- Contains: Taro + Vue3 pages, Pinia stores, API request utilities
- Depends on: Backend REST API
- Used by: End users (customers browsing stores, vendors managing orders)

**API Layer (Spring Boot):**
- Purpose: RESTful API backend handling business logic
- Location: `src/backend/src/main/java/com/stallmart/controller/`
- Contains: REST controllers, DTOs, services, MyBatis mappers
- Depends on: MySQL, Redis
- Used by: Mini Program, Admin Web

**Data Layer:**
- Purpose: Persistent storage and caching
- Locations: `src/docker/mysql/`, `src/docker/redis/`
- Contains: MySQL tables (user, store, product, order), Redis tokens

## Data Flow

**User Authentication Flow:**

1. Mini Program calls `/api/v1/auth/wechat/login` with WeChat code
2. Backend validates (mocked currently) and creates/updates user
3. Backend generates JWT access token (2hr) + refresh token (7 days)
4. Frontend stores tokens and includes `Authorization: Bearer <token>` in requests
5. On 401, frontend automatically refreshes using refresh token
6. On logout, refresh token is added to Redis blacklist

**Order Flow:**

1. Customer views store products via `/api/v1/stores/{id}/products`
2. Customer adds items to cart (stored locally in Mini Program)
3. Customer confirms order via `/api/v1/orders` POST
4. Vendor receives order via `/api/v1/vendor/orders`
5. Vendor updates status: ACCEPTED -> PREPARING -> READY -> COMPLETED
6. Customer picks up with confirm code

## Key Abstractions

**Backend:**

**Controller Layer:**
- `AuthController` - Authentication endpoints (login, bind phone, refresh, logout)
- `UserController` - User profile management
- `StoreController` - Store CRUD, QR code lookup
- `ProductController` - Product management
- `OrderController` - Order creation and status updates
- `SpecController` - Product specifications (style packages)
- `StyleController` - Store style management

**Service Layer:**
- `AuthService` - JWT generation, token refresh, phone binding
- `UserService` - User CRUD operations
- `StoreService` - Store business logic
- `ProductService` - Product business logic
- `OrderService` - Order processing and status management

**Repository Layer (MyBatis-Plus):**
- `UserMapper`, `StoreMapper`, `ProductMapper`, `OrderMapper`, `OrderItemMapper`, `ProductSpecMapper`, `StoreStyleMapper`

**Frontend (Mini Program):**

**State Management (Pinia):**
- `store/user.ts` - User authentication state, login/logout actions
- `store/index.ts` - Pinia setup and store exports

**API Layer:**
- `utils/request.ts` - Axios-like request wrapper with interceptors, auto-refresh
- `utils/auth.ts` - Authentication utilities
- `utils/storage.ts` - LocalStorage wrapper for tokens
- `app-config/index.ts` - API endpoints, colors, constants

**Pages (Vue3 + Taro):**
- Customer: `index` (store browsing), `cart`, `confirm-order`, `my` (profile), `my-orders`
- Vendor: `my-stall`, `order-manage`, `stall-settings`

## Entry Points

**Backend:**
- `src/backend/src/main/java/com/stallmart/StallmartApplication.java` - Spring Boot entry point
- `@MapperScan("com.stallmart.repository")` auto-discovers MyBatis mappers

**Frontend:**
- `src/mini-program/src/app.ts` - Mini Program lifecycle, Pinia initialization
- `src/mini-program/src/app.vue` - Root component

## Error Handling

**Backend:**
- `BusinessException` - Custom exception with error codes
- `GlobalExceptionHandler` - Catches and formats all exceptions
- `JwtAuthFilter` - Returns 401 for missing/invalid tokens
- Standard error codes in `ErrorCode` enum

**Frontend:**
- Request interceptor catches HTTP errors
- 401 triggers automatic token refresh
- Global error handler in `app.ts` shows toast on errors
- `onPageNotFound` redirects to home page

## Cross-Cutting Concerns

**Logging:** Slf4j with `@Slf4j` annotation on backend; `console.log/error` on frontend

**Validation:** Jakarta Bean Validation (`@Valid`) on DTOs

**Authentication:** JWT filter (`JwtAuthFilter`) validates Bearer tokens; refresh tokens stored in Redis with blacklist

**API Documentation:** SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html`)

---

*Architecture analysis: 2026-04-26*
