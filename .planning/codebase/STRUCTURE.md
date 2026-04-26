# Codebase Structure

**Analysis Date:** 2026-04-26

## Directory Layout

```
StallMart/src/
├── backend/                    # Java/Spring Boot backend
│   ├── pom.xml               # Maven dependencies
│   ├── mysql/                # Database schema (legacy)
│   └── src/main/java/com/stallmart/
│       ├── StallmartApplication.java
│       ├── common/           # Shared utilities
│       ├── config/           # Spring configuration
│       ├── controller/       # REST endpoints
│       ├── model/            # Entities, DTOs, VOs
│       ├── repository/       # MyBatis mappers
│       └── service/          # Business logic
├── docker/                    # Docker configuration
│   ├── Dockerfile.api        # Backend container
│   ├── Dockerfile.admin-web  # Admin web container
│   ├── docker-compose.yml   # Service orchestration
│   ├── mysql/init/           # Database initialization
│   └── .env                  # Environment variables
├── mini-program/              # Taro + Vue3 WeChat Mini Program
│   ├── taro.config.ts       # Taro build configuration
│   ├── project.config.json  # WeChat Mini Program config
│   ├── package.json         # Node dependencies
│   ├── src/
│   │   ├── app.ts           # App lifecycle
│   │   ├── app.vue          # Root component
│   │   ├── app-config/      # App constants, API endpoints
│   │   ├── pages/           # Page components
│   │   ├── store/           # Pinia stores
│   │   └── utils/           # Request, auth, storage utilities
│   └── dist/                # Build output
└── .planning/                # GSD planning output
    └── codebase/             # This directory
```

## Directory Purposes

**Backend (`src/backend/`):**
- Purpose: REST API backend for StallMart SaaS
- Contains: Spring Boot application, controllers, services, repositories
- Key files:
  - `pom.xml` - Maven dependencies (Spring Boot 3.2.3, MyBatis-Plus 3.5.5, JWT 0.12.5)
  - `src/main/java/com/stallmart/StallmartApplication.java` - Entry point

**Docker (`src/docker/`):**
- Purpose: Container orchestration for local development
- Contains: Dockerfiles, docker-compose.yml, MySQL init scripts
- Key files:
  - `docker-compose.yml` - Defines mysql, redis, api, admin-web services
  - `mysql/init/01-init.sql` - Database schema and seed data

**Mini Program (`src/mini-program/`):**
- Purpose: WeChat Mini Program for customers and vendors
- Contains: Taro + Vue3 frontend application
- Key files:
  - `taro.config.ts` - Taro build config (webpack5, Vue3, weapp platform)
  - `project.config.json` - WeChat Mini Program project config
  - `package.json` - Dependencies (@tarojs/* 4.x, vue 3.4, pinia 2.1)

## Key File Locations

**Entry Points:**
- `src/backend/src/main/java/com/stallmart/StallmartApplication.java` - Spring Boot main class
- `src/mini-program/src/app.ts` - Mini Program app lifecycle

**Configuration:**
- `src/backend/src/main/java/com/stallmart/config/JwtService.java` - JWT configuration
- `src/backend/src/main/java/com/stallmart/config/RedisConfig.java` - Redis configuration
- `src/backend/src/main/java/com/stallmart/config/SwaggerConfig.java` - API documentation
- `src/backend/src/main/java/com/stallmart/config/WebConfig.java` - Web MVC config
- `src/mini-program/taro.config.ts` - Taro build configuration
- `src/mini-program/src/app-config/index.ts` - App constants and API endpoints
- `src/docker/docker-compose.yml` - Service orchestration
- `src/docker/.env` - Environment variables

**Backend Core Logic:**
- `src/backend/src/main/java/com/stallmart/controller/AuthController.java` - Auth endpoints
- `src/backend/src/main/java/com/stallmart/service/impl/AuthServiceImpl.java` - Auth business logic
- `src/backend/src/main/java/com/stallmart/common/filter/JwtAuthFilter.java` - JWT authentication filter
- `src/backend/src/main/java/com/stallmart/model/entity/` - JPA/MyBatis entities (User, Store, Product, Order)
- `src/backend/src/main/java/com/stallmart/repository/` - MyBatis mappers

**Frontend Core Logic:**
- `src/mini-program/src/store/user.ts` - User state (Pinia)
- `src/mini-program/src/utils/request.ts` - HTTP request wrapper with interceptors
- `src/mini-program/src/pages/customer/index/index.vue` - Customer home page
- `src/mini-program/src/pages/vendor/order-manage/order-manage.vue` - Vendor order management

**Database:**
- `src/docker/mysql/init/01-init.sql` - Schema with user, store, product, order tables

## Naming Conventions

**Backend (Java):**
- Controllers: `XxxController.java` (e.g., `AuthController`)
- Services: `XxxService.java` (interface), `XxxServiceImpl.java` (implementation)
- Repositories: `XxxMapper.java` (MyBatis)
- Entities: `Xxx.java` in `model/entity/`
- DTOs: `XxxDTO.java` in `model/dto/`
- VOs: `XxxVO.java` in `model/vo/`
- Package: `com.stallmart`

**Frontend (TypeScript/Vue):**
- Pages: `xxx.vue` in `pages/{customer,vendor}/xxx/`
- Stores: `xxx.ts` in `store/`
- Utils: `xxx.ts` in `utils/`
- Config: `xxx.config.ts` or `index.ts`

## Where to Add New Code

**New Backend Feature:**

1. Add Controller: `src/backend/src/main/java/com/stallmart/controller/XxxController.java`
2. Add Service Interface: `src/backend/src/main/java/com/stallmart/service/XxxService.java`
3. Add Service Implementation: `src/backend/src/main/java/com/stallmart/service/impl/XxxServiceImpl.java`
4. Add Mapper (if needed): `src/backend/src/main/java/com/stallmart/repository/XxxMapper.java`
5. Add Entity/DTO/VO: `src/backend/src/main/java/com/stallmart/model/`
6. Update docker-compose if new dependencies needed

**New Mini Program Page:**

1. Create directory: `src/mini-program/src/pages/{customer,vendor}/xxx/`
2. Add page config: `xxx.config.ts`
3. Add page component: `xxx.vue`
4. Add page styles: `xxx.scss`
5. Register in `src/mini-program/src/app.config.ts` pages list

**New API Endpoint:**

1. Backend: Add method to existing Controller or create new Controller
2. Frontend: Add endpoint to `src/mini-program/src/app-config/index.ts` (API_ENDPOINTS)
3. Add request function to `src/mini-program/src/utils/request.ts`

**New State (Pinia Store):**

1. Create: `src/mini-program/src/store/xxx.ts`
2. Export from: `src/mini-program/src/store/index.ts`

---

*Structure analysis: 2026-04-26*
