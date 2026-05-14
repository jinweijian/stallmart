---
name: stallmart-api-contract-update
description: Use when updating StallMart API contracts, adding or changing endpoints, syncing server/app/web API usage, fixing endpoint constants, updating API docs, or aligning request/response DTOs across server, app, web, docs, and tests.
---

# StallMart API Contract Update

## Overview

Keep StallMart API changes closed-loop across server, app, web, docs, and tests. Use this skill whenever an API route, request body, response shape, endpoint constant, auth behavior, error code, or integration contract changes.

## Required Context

Read these before changing files:

1. `AGENTS.md`
2. `README.md`
3. `server/README.md`
4. `docs/specification/server.md`
5. `docs/api-server/index.md`
6. `docs/api-app/index.md`
7. `docs/guide/testing.md`

Then read by affected surface:

- App API or mock impact: `app/README.md`, `app/src/app-config/endpoints.ts`, `app/src/utils/request.ts`, `app/src/mock/customer-api.ts`
- Admin web impact: `web/README.md`, `web/app/api/`, `web/app/types/`
- Config, Docker, or deployment impact: `docs/guide/configuration.md`, `docs/deploy/index.md`

## Source of Truth Pass

Before editing, inspect the current contract from code and docs:

```bash
rg -n "@(Get|Post|Put|Delete|Patch|Request)Mapping|@RequestMapping" server/src/main/java/com/stallmart
rg -n "API_ENDPOINTS|request\\(|get\\(|post\\(|put\\(|del\\(" app/src web/app
rg -n "/api/v1|Endpoint|endpoint|成功码|Authorization|mock" docs/api-server docs/api-app docs/guide/testing.md
```

Use implementation facts over stale docs, then update docs to match the final behavior.

## Contract Rules

- Base path stays `/api/v1`.
- Server responses use `Result<T>` and success `code: 200`.
- Authenticated requests use `Authorization: Bearer <token>`.
- Admin web browser code must call same-origin `/api/v1`; do not expose backend host URLs in public runtime config.
- App pages must not hardcode full API URLs or call `Taro.request` directly for business requests.
- New app endpoints belong in `app/src/app-config/endpoints.ts`.
- App mock data must keep the same shape as the real DTO while mock mode remains enabled.
- Admin web API methods belong in the correct `web/app/api/*-api.ts`; types belong in `web/app/types/*`.
- Do not treat app mock behavior as proof of real API integration.
- Never add secrets, real tokens, passwords, WeChat secrets, or machine-specific absolute paths.

## Update Flow

1. Classify the change: public app API, admin API, auth/session, DTO field, error behavior, or endpoint rename.
2. Update server routes, Params/DTOs, services, and tests when the backend contract changes.
3. Sync app endpoint constants, request usage, adapters, and mock shape when app-facing fields or paths change.
4. Sync admin web API client and types when `/admin/*` paths or DTOs change.
5. Sync docs every time:
   - `docs/api-server/index.md` for routes, auth, response fields, and shared constraints.
   - `docs/api-app/index.md` for app endpoints, mock contract, storage, request, and success-code expectations.
   - `docs/guide/testing.md` for changed verification commands or integration scenarios.
6. If config, Docker, CORS, port, database, or deployment assumptions changed, also update the matching deploy/config docs.

## Verification

Choose the smallest command set that covers the changed surfaces:

```bash
git diff --check
npm --prefix app run lint
npm --prefix app run test:weapp-assets
npm --prefix web run build
npm --prefix web run test:runtime-config
npm --prefix web run test:proxy-headers
cd server && ./gradlew test
```

For API docs-only changes, still run `git diff --check` and inspect affected links/paths. For real app integration work, prefer H5 with mock disabled before treating the flow as verified.

## Response Shape

Report:

- which contract changed
- which surfaces were synchronized
- which verification commands ran
- any remaining mock-vs-real API caveat
