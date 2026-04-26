# External Integrations

**Analysis Date:** 2026-04-26

## APIs & External Services

**WeChat Mini Program:**
- Platform: WeChat (Tencent)
- Purpose: Customer-facing mini program for end users
- SDK: `@tarojs/plugin-platform-weapp` 4.0.0
- Config: `project.config.json` with `appid: wxe4f198ad2958a1fe`
- Auth: WeChat App ID/Secret in `.env`

**Aliyun OSS (Optional):**
- Service: Alibaba Cloud Object Storage
- Purpose: File/image storage
- Env vars: `OSS_ACCESS_KEY_ID`, `OSS_ACCESS_KEY_SECRET`, `OSS_BUCKET`, `OSS_ENDPOINT`
- Status: Referenced but commented out in default config

## Data Storage

**MySQL 9.0:**
- Type: Relational database (containerized via Docker)
- Connection: `jdbc:mysql://mysql:3306/stallmart` (container network)
- Env vars: `MYSQL_ROOT_PASSWORD`, `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`
- Client: MyBatis-Plus ORM
- Port: 3306 (localhost: 127.0.0.1:3306)

**Redis 7.2-alpine:**
- Type: Key-value cache
- Purpose: Session cache + JWT token blacklist
- Env vars: `REDIS_PASSWORD`
- Port: 6379 (localhost: 127.0.0.1:6379)

## Authentication & Identity

**JWT (JJWT 0.12.5):**
- Implementation: Self-contained JWT tokens
- Library: `io.jsonwebtoken:jjwt-api`
- Secret: `JWT_SECRET` env var (min 32 chars recommended)
- Endpoints: `/api/v1/*` requires `Authorization: Bearer <token>` header

## Monitoring & Observability

**Spring Actuator:**
- Health check: `GET /actuator/health`
- Used in docker-compose healthchecks

**SpringDoc OpenAPI (Swagger):**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`

## CI/CD & Deployment

**Docker Compose:**
- Orchestration: Docker Compose v2
- Services: mysql, redis, api, admin-web
- Network: `stallmart-network` (bridge, subnet 172.20.0.0/16)

**Containers:**
| Service | Image | Local Port |
|---------|-------|------------|
| mysql | mysql:9.0 | 127.0.0.1:3306 |
| redis | redis:7.2-alpine | 127.0.0.1:6379 |
| api | custom (Dockerfile) | 127.0.0.1:8080 |
| admin-web | custom (Dockerfile) | 127.0.0.1:3000 |

## Environment Configuration

**Required env vars (docker/.env):**
```
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
MYSQL_ROOT_PASSWORD=<password>
MYSQL_DATABASE=stallmart
MYSQL_USER=<user>
MYSQL_PASSWORD=<password>
REDIS_VERSION=7.2-alpine
REDIS_PASSWORD=<password>
JWT_SECRET=<256-bit-secret>
WECHAT_APP_ID=<app_id>
WECHAT_APP_SECRET=<app_secret>
```

**Optional:**
```
OSS_ACCESS_KEY_ID=<key>
OSS_ACCESS_KEY_SECRET=<secret>
OSS_BUCKET=stallmart
OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
```

**Secrets location:**
- `.env` file in `src/docker/` directory
- Not committed to version control

## Webhooks & Callbacks

**WeChat Payment (implied):**
- Referenced in error code ranges (5xxxx for payment module)
- Not detailed in current config

**API Endpoints Structure:**
- Base URL: `/api/v1`
- Auth: Bearer token in Authorization header

---

*Integration audit: 2026-04-26*
