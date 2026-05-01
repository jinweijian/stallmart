# External Integrations

## WeChat Mini Program

- App code: `app/`.
- Runtime: Taro WeChat Mini Program target.
- Current API mode: mock enabled.

## Docker

- Compose file: `docker/docker-compose.yml`.
- Local services: MySQL, Redis, API.
- `admin-web` is referenced but not present.

## Server

- Health check: `GET /api/v1/actuator/health`.
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui/index.html`.
- OpenAPI JSON: `http://localhost:8080/api/v1/v3/api-docs`.

Persistence and real JWT integration are follow-up tasks.
