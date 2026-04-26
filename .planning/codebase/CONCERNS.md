# Codebase Concerns

**Analysis Date:** 2026-04-26

## Security Concerns

### CRITICAL: Secrets in .env Files

**Issue:** Environment files with credentials are present and may be committed to git.

- `docker/.env` contains plaintext secrets:
  - `MYSQL_ROOT_PASSWORD=stallmart_dev_root`
  - `MYSQL_PASSWORD=stallmart_dev_pass`
  - `REDIS_PASSWORD=stallmart_dev_redis`
  - `JWT_SECRET=your-256-bit-secret-key-here-change-in-production-min-32-chars`
  - `WECHAT_APP_ID=your_wechat_app_id`
  - `WECHAT_APP_SECRET=your_wechat_app_secret`

- `docker/.env.example` is identical to `.env` - this should contain placeholder values only, not actual credentials

**Files:** `docker/.env`, `docker/.env.example`

**Impact:** If committed to git, all credentials are exposed. JWT secret is a placeholder that would be insecure in production.

**Fix:** Use environment variable substitution (${VAR}) in .env, keep only placeholder values in .env.example, add .env to .gitignore.

---

### CRITICAL: WeChat Integration Uses Mock Data

**Issue:** `AuthServiceImpl.wechatLogin()` and `bindPhone()` use mock data instead of calling WeChat APIs.

```java
// AuthServiceImpl.java:41-46 - Mock implementation
String openid = "mock_openid_" + dto.getCode();
String unionid = "mock_unionid";
// ...
// AuthServiceImpl.java:66 - Hardcoded phone
String phone = "13800138000";
```

**Files:** `backend/src/main/java/com/stallmart/service/impl/AuthServiceImpl.java`

**Impact:** Any user can authenticate with arbitrary credentials. No actual WeChat verification.

**Fix:** Implement actual WeChat code exchange and phone decryption.

---

### HIGH: Role-Based Access Control Not Enforced

**Issue:** JWT filter extracts and validates tokens but does not enforce role-based access. Controllers rely solely on token presence.

```java
// JwtAuthFilter.java - Only checks token existence
Long userId = jwtService.getUserIdFromToken(token);
request.setAttribute("userId", userId);
// No role verification here
```

**Files:** `backend/src/main/java/com/stallmart/common/filter/JwtAuthFilter.java`

**Impact:** Any authenticated user can access vendor/admin endpoints. No vendor-only or admin-only protection.

**Fix:** Add @RolesAllowed annotations or implement method-level security.

---

### HIGH: Swagger UI Exposed in Production

**Issue:** SpringDoc Swagger UI is enabled and accessible in production (`/swagger-ui.html`, `/api-docs`).

**Files:** `backend/src/main/resources/application.yml`

**Impact:** API documentation and endpoints are publicly discoverable.

**Fix:** Disable Swagger in production profile or restrict to authenticated users.

---

### MEDIUM: Tokens Stored in LocalStorage (Mini-Program)

**Issue:** Access and refresh tokens stored using `Taro.setStorageSync()` which persists on disk.

**Files:** `mini-program/src/utils/storage.ts`

**Impact:** Tokens could be extracted from device storage if device is compromised.

**Fix:** Consider using memory-only storage for access tokens, longer-lived refresh tokens with device binding.

---

### MEDIUM: No Rate Limiting

**Issue:** No rate limiting on authentication or API endpoints.

**Files:** `backend/src/main/java/com/stallmart/controller/*.java`

**Impact:** Brute force attacks on login, denial of service.

**Fix:** Implement rate limiting via Redis or gateway.

---

## Technical Debt

### Incomplete WeChat Integration

**Issue:** TODO comments indicate WeChat API calls are not implemented.

- `AuthServiceImpl.java:41` - "TODO: 调用微信接口验证 code，获取 openid/unionid"
- `AuthServiceImpl.java:65` - "TODO: 调用微信接口解密获取手机号"

**Files:** `backend/src/main/java/com/stallmart/service/impl/AuthServiceImpl.java`

**Fix:** Implement WeChat API integration.

---

### Hardcoded Phone Number in BindPhone

**Issue:** Phone binding returns hardcoded "13800138000" instead of actual decrypted phone.

**Files:** `backend/src/main/java/com/stallmart/service/impl/AuthServiceImpl.java`

**Fix:** Implement WeChat phone decryption.

---

### Incomplete Vendor Stall Settings

**Issue:** TODO in stall-settings page indicates leaving stall is not implemented.

**Files:** `mini-program/src/pages/vendor/stall-settings/stall-settings.vue:190`

**Fix:** Implement leave stall API call.

---

### Missing .gitignore Files

**Issue:** No .gitignore files found in backend, docker, or mini-program directories.

**Impact:** Sensitive files could be committed (node_modules, target/, .env).

**Fix:** Add appropriate .gitignore files to each project.

---

### OrderController N+1 Query Issue

**Issue:** `toVO()` method in OrderController performs additional query per order item:

```java
// OrderController.java:89-91 - N+1 query
List<OrderItemVO> items = orderItemMapper.selectList(
    new LambdaQueryWrapper<OrderItem>()
        .eq(OrderItem::getOrderId, order.getId())
);
```

**Files:** `backend/src/main/java/com/stallmart/controller/OrderController.java`

**Impact:** Performance degrades with order item count.

**Fix:** Use batch query or JOIN in mapper.

---

### No Database Indexes on Foreign Keys

**Issue:** `product` table has only `idx_store_id` but `order` table queries by `customer_id` and `store_id` without compound index.

**Files:** `backend/mysql/init.sql`

**Fix:** Add composite indexes: `(customer_id, created_at)`, `(store_id, status)`.

---

### Test Coverage Gaps

**Issue:** Only one test class exists (`UserServiceTest.java`). No tests for:
- AuthService (WeChat login, token refresh, logout)
- OrderService
- ProductService
- Controllers

**Files:** `backend/src/test/java/com/stallmart/service/UserServiceTest.java`

**Fix:** Add comprehensive unit and integration tests.

---

### Application Uses Java 26

**Issue:** `pom.xml` specifies `<java.version>26</java.version>` which is not yet released (as of 2026-04).

**Files:** `backend/pom.xml:21`

**Impact:** Code will not compile with current JDKs.

**Fix:** Use Java 21 or 25 (latest stable LTS).

---

### Health Check Endpoint Does Not Exist

**Issue:** Dockerfile references `/actuator/health` but Spring Boot Actuator is not in dependencies.

**Files:** `docker/Dockerfile.api:43`

**Impact:** Health checks will always fail.

**Fix:** Add spring-boot-starter-actuator or remove health check.

---

### Debug Logging Enabled in Production Config

**Issue:** `application.yml` sets `com.stallmart: DEBUG` regardless of profile.

**Files:** `backend/src/main/resources/application.yml:49`

**Impact:** Excessive log volume, potential info leakage.

**Fix:** Use profile-based logging configuration.

---

## Performance Concerns

### Redis Password in Command Line

**Issue:** Redis password passed via command line in docker-compose:

```yaml
command: redis-server --requirepass ${REDIS_PASSWORD}
```

**Files:** `docker/docker-compose.yml:43`

**Impact:** Password visible in process list (`ps aux`).

**Fix:** Use redis.conf file or environment variable in config.

---

### No Connection Pooling Configuration

**Issue:** MySQL connection pool not explicitly configured (uses defaults).

**Files:** `backend/src/main/resources/application.yml`

**Impact:** May not handle production load optimally.

**Fix:** Configure HikariCP pool settings for production.

---

### Mini-Program Token Refresh Race Condition

**Issue:** Both `request.ts` and `auth.ts` implement token refresh with similar but separate logic. `refreshSubscribers` array not properly cleared on failure.

**Files:** `mini-program/src/utils/request.ts`, `mini-program/src/utils/auth.ts`

**Impact:** Race conditions during concurrent requests with expired tokens.

**Fix:** Consolidate to single auth module, handle queued subscribers properly on failure.

---

## Fragile Areas

### Order Status State Machine Not Enforced

**Issue:** Order status transitions (`accept`, `reject`, `prepare`, `ready`, `complete`) have no state validation.

```java
// OrderService - no state validation
public void accept(Long id) { ... }
public void reject(Long id, String reason) { ... }
```

**Files:** `backend/src/main/java/com/stallmart/service/OrderService.java`

**Impact:** Orders can transition to invalid states (e.g., complete before accept).

**Fix:** Implement state machine pattern with valid transition rules.

---

### QR Code Generation Not Implemented

**Issue:** Store entity requires `qr_code` but no generation logic found in codebase.

**Files:** `backend/src/main/java/com/stallmart/model/entity/Store.java`

**Impact:** Cannot create functional stores.

**Fix:** Implement QR code generation service.

---

### Refresh Token Blacklist Uses Full Token as Key

**Issue:** Refresh token blacklist stores exact token string. If token format changes, blacklist becomes incompatible.

```java
redisTemplate.opsForValue().set("blacklist:refresh:" + refreshToken, "1", ttl, TimeUnit.SECONDS);
```

**Files:** `backend/src/main/java/com/stallmart/service/impl/AuthServiceImpl.java`

**Impact:** Token rotation may break logout functionality.

**Fix:** Use token ID (jti claim) or hash of token as key.

---

## Scaling Limits

### Single MySQL Instance

**Issue:** Architecture uses single MySQL instance with no read replicas.

**Impact:** Cannot scale reads independently of writes.

**Fix:** Add read replicas, implement read/write splitting.

---

### No Caching Layer Beyond Redis Basic Ops

**Issue:** Redis used only for token blacklist. No application-level caching.

**Impact:** Repeated DB queries for static data (styles, products).

**Fix:** Implement cache-aside pattern for frequently accessed data.

---

### No File Storage Solution

**Issue:** Product images, store avatars reference `image_url` but no OSS/infrastructure for upload.

**Files:** `backend/src/main/java/com/stallmart/model/entity/Product.java`

**Impact:** Cannot actually upload and serve images.

**Fix:** Integrate Aliyun OSS or S3.

---

## Dependency Risks

### JJWT 0.12.5

**Issue:** jjwt 0.12.x is relatively new. Verify compatibility with Spring Boot 3.2.3.

**Files:** `backend/pom.xml:23`

**Fix:** Verify integration or consider maintained fork.

---

### MyBatis-Plus Version 3.5.5

**Issue:** Using 3.5.5 while newer versions (3.5.6+) exist.

**Files:** `backend/pom.xml:22`

**Fix:** Update to latest 3.5.x for bug fixes.

---

## Missing Critical Features

### No Payment Integration

**Issue:** Orders created but no payment processing.

**Impact:** Cannot actually process transactions.

---

### No Notification System

**Issue:** No push notifications for order status updates.

**Impact:** Users must manually poll for order status.

---

### No Admin Panel

**Issue:** Only mini-program frontend exists. No admin dashboard.

**Impact:** Cannot manage users, stores, view analytics.

---

*Concerns audit: 2026-04-26*
