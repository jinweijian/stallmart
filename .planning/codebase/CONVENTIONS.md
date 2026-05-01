# Coding Conventions

## Server

- Domain packages: `com.stallmart.<domain>`.
- Public service interface: `<domain>/<Domain>Service.java`.
- Request objects: `*Params`.
- Response objects: `*DTO`.
- Controllers: `<domain>/internal/api/*Controller.java`.
- Service implementations: `<domain>/internal/service/*ServiceImpl.java`.
- Shared code: `support/`.

## App

- Source root: `app/src`.
- Pages: `app/src/pages/customer` and `app/src/pages/vendor`.
- API constants: `app/src/app-config/index.ts`.
- Request wrapper: `app/src/utils/request.ts`.
- Do not change frontend mock data unless integration work explicitly asks for it.
