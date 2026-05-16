# Admin Auth Audit Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** 加固管理端登录并新增平台/商户后台审计日志。

**Architecture:** 服务端新增登录安全服务管理失败次数与验证码，后台账号使用显式账户盐加 BCrypt 校验。审计日志使用独立 JPA 表和服务，由管理端写接口在成功后记录，web 增加验证码登录和日志列表入口。

**Tech Stack:** Spring Boot 4、Java 25、Spring Data JPA、Flyway、BCryptPasswordEncoder、Nuxt 4、Vue 3、TypeScript。

---

## Tasks

### Task 1: Auth Security Tests and Schema

**Files:**

- Modify: `server/src/test/java/com/stallmart/persistence/PersistenceMigrationTest.java`
- Modify: `server/src/test/java/com/stallmart/web/ApiControllerTest.java`
- Create: `server/src/main/resources/db/migration/V3__admin_auth_audit_hardening.sql`
- Modify: `server/src/main/java/com/stallmart/user/internal/repository/AdminAccountEntity.java`

- [x] Write failing tests for new admin passwords, old password rejection, `password_salt` presence, and distinct account salts.
- [x] Write failing tests that three failed logins require captcha, invalid captcha is rejected, and valid captcha allows login.
- [x] Add Flyway migration with `password_salt`, updated hashes, and `admin_operation_log`.
- [x] Add `passwordSalt` to `AdminAccountEntity`.
- [x] Run `cd server && ./gradlew test --tests com.stallmart.persistence.PersistenceMigrationTest --tests com.stallmart.web.ApiControllerTest`.

### Task 2: Login Security Service

**Files:**

- Modify: `server/src/main/java/com/stallmart/support/exception/ErrorCode.java`
- Modify: `server/src/main/java/com/stallmart/support/exception/GlobalExceptionHandler.java`
- Modify: `server/src/main/java/com/stallmart/auth/dto/AdminLoginParams.java`
- Create: `server/src/main/java/com/stallmart/auth/dto/AdminCaptchaDTO.java`
- Create: `server/src/main/java/com/stallmart/auth/dto/AdminLoginFailureDTO.java`
- Create: `server/src/main/java/com/stallmart/auth/internal/service/AdminLoginSecurityService.java`
- Modify: `server/src/main/java/com/stallmart/user/UserService.java`
- Modify: `server/src/main/java/com/stallmart/user/internal/service/UserServiceImpl.java`
- Modify: `server/src/main/java/com/stallmart/management/internal/api/AdminAuthController.java`

- [x] Implement captcha generation, TTL, one-time verification, and failure count tracking.
- [x] Change admin login to compare `passwordEncoder.matches(password + passwordSalt, passwordHash)`.
- [x] Return structured login errors with `captchaRequired`.
- [x] Add `GET /admin/auth/captcha`.
- [x] Run targeted server tests until green.

### Task 3: Audit Log Backend

**Files:**

- Create: `server/src/main/java/com/stallmart/management/OperationLogService.java`
- Create: `server/src/main/java/com/stallmart/management/dto/OperationLogDTO.java`
- Create: `server/src/main/java/com/stallmart/management/dto/OperationLogRecordParams.java`
- Create: `server/src/main/java/com/stallmart/management/internal/model/OperationLogScope.java`
- Create: `server/src/main/java/com/stallmart/management/internal/model/OperationLogResult.java`
- Create: `server/src/main/java/com/stallmart/management/internal/repository/OperationLogEntity.java`
- Create: `server/src/main/java/com/stallmart/management/internal/repository/OperationLogRepository.java`
- Create: `server/src/main/java/com/stallmart/management/internal/service/OperationLogServiceImpl.java`
- Modify: `server/src/main/java/com/stallmart/user/internal/repository/AdminAccountRepository.java`
- Modify: `server/src/main/java/com/stallmart/management/internal/api/VendorAdminController.java`
- Modify: `server/src/main/java/com/stallmart/management/internal/api/PlatformAdminController.java`
- Modify: `server/src/main/java/com/stallmart/management/internal/api/AdminAuthController.java`

- [x] Add repository/service methods for platform logs, vendor logs, and recording sanitized events.
- [x] Log admin login success/failure.
- [x] Log successful platform style writes.
- [x] Log successful vendor writes.
- [x] Add log listing endpoints with role/store boundaries.
- [x] Run targeted API tests.

### Task 4: Web Login and Logs

**Files:**

- Modify: `web/app/types/admin.ts`
- Modify: `web/app/api/auth-api.ts`
- Modify: `web/app/api/platform-api.ts`
- Modify: `web/app/api/vendor-api.ts`
- Modify: `web/app/api/stallmart-api.ts`
- Modify: `web/app/pages/auth/login/index.vue`
- Create: `web/app/pages/platform/operation-logs/index.vue`
- Create: `web/app/pages/platform/vendors/[id]/operation-logs/index.vue`
- Create: `web/app/pages/vendor/operation-logs/index.vue`

- [x] Add captcha and operation log types/API wrappers.
- [x] Show captcha after server requires it or local failures reach three.
- [x] Add concise log tables for platform logs, vendor logs, and platform view of a vendor.
- [x] Run `cd web && npm run build`.

### Task 5: Docs and Verification

**Files:**

- Modify: `docs/api-server/index.md`
- Modify: `docs/api-app/index.md`
- Modify: `docs/guide/testing.md`
- Modify: `docs/specification/web.md`
- Modify: `docs/standards/security.md`
- Modify: `web/README.md`

- [x] Document changed credentials, captcha flow, and operation-log endpoints.
- [x] Document password salt and audit-log safety rules.
- [x] Run `git diff --check`.
- [x] Run `cd server && ./gradlew test`.
- [x] Run `cd web && npm run build`.

