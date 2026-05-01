# Codebase Structure

## Directory Layout

```text
stallmart/
├── app/        # Taro + Vue 3 WeChat Mini Program
├── server/     # Spring Boot API
├── docker/     # Local Docker Compose and images
├── docs/       # Guide, API, specification and deploy docs
└── README.md
```

## Server

Server code follows the `edusoho-lms/server` style: domain modules expose public services at module root, DTO/Params in `dto/`, and implementation details in `internal/`.

```text
server/src/main/java/com/stallmart/
  auth/
  order/
  product/
  store/
  style/
  user/
  support/
```

## App

```text
app/src/
  app-config/
  pages/customer/
  pages/vendor/
  static/
  store/
  utils/
```

Frontend mock mode remains enabled until explicit integration work.
