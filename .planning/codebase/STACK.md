# Technology Stack

**Analysis Date:** 2026-04-26

## Languages

**Primary:**
- TypeScript 5.3.3 - Mini Program frontend
- Java 21 - Backend API (specified as `java.version>26` in pom.xml, which appears to be a typo for Java 21 or 26 early access)

**Secondary:**
- Vue 3.4.21 - Mini Program UI framework
- JavaScript - Build scripts and tooling

## Runtime

**Environment:**
- Node.js 18+ - Mini Program build tooling
- Docker - Container runtime for all services

**Package Manager:**
- npm - Mini Program dependencies
- Maven - Backend dependencies (pom.xml)

## Frameworks

**Backend:**
- Spring Boot 3.2.3 - Java backend framework
- MyBatis-Plus 3.5.5 - ORM layer
- Spring Data Redis - Caching
- SpringDoc OpenAPI 2.3.0 - API documentation (Swagger)

**Frontend (Mini Program):**
- Taro 4.0.0 - Cross-platform framework (WeChat Mini Program target)
- Pinia 2.1.7 - State management
- Vue 3.4.21 - UI framework

**Admin Web:**
- Nuxt UI - Management console (referenced in docker-compose.yml)

**Testing:**
- JUnit / Spring Boot Starter Test - Backend unit testing

**Build/Dev:**
- Webpack 5.90.0 - Taro build bundler
- Taro CLI 4.1.11 - Build tooling
- Babel 7.24.0 - JavaScript transpilation

## Key Dependencies

**Backend Critical:**
- `mysql-connector-j` - MySQL JDBC driver (runtime scope)
- `jjwt-api 0.12.5` - JWT token handling
- `spring-boot-starter-validation` - Request validation
- `lombok` - Boilerplate reduction

**Frontend Critical:**
- `@tarojs/taro 4.0.0` - Core Taro framework
- `@tarojs/components 4.0.0` - WeChat component library
- `@vicons/ionicons5 0.12.0` - Icon library
- `tailwindcss 3.4.1` - CSS utility framework

## Infrastructure

**Database:**
- MySQL 9.0 - Primary relational database

**Cache:**
- Redis 7.2-alpine - Session cache and token blacklist

## Configuration

**Environment:**
- Spring Profiles - `dev` mode configured
- `.env` files - Docker environment variables
- `application.yml` / `application-local.yml` - Spring Boot config

**Build:**
- `pom.xml` - Maven build configuration
- `project.config.json` - Taro/Mini Program config
- `taro.config.ts` (implied) - Taro build config

## Platform Requirements

**Development:**
- Docker 20.10+
- Docker Compose v2+
- Node.js 18+
- Java 21 (or 26 early access as specified in pom.xml)

**Production (Referenced):**
- WeChat Mini Program platform
- Aliyun OSS (optional file storage)

---

*Stack analysis: 2026-04-26*
