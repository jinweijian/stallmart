# Concerns

## Active

- App mock mode is enabled and should not be changed without explicit integration work.
- `admin-web/` is referenced by Docker Compose but not present.
- App endpoint constants `USER_BIND_PHONE` and `STORE_INFO` are referenced but missing.
- Server persistence and real JWT filter are not yet implemented.

## Recently Addressed

- Root directories were normalized to `app/` and `server/`.
- Server packages were reorganized by domain.
- Java version was normalized to 21.
