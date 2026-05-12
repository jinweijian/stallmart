# 配置说明

## 后端配置

主配置文件：

```text
server/src/main/resources/application.yml
```

当前默认值：

| 配置 | 默认值 |
| --- | --- |
| Server port | `8080` |
| Spring application name | `stallmart-api` |
| Context path | `/api/v1` |
| Swagger UI | `/api/v1/swagger-ui/index.html` |
| OpenAPI docs | `/api/v1/v3/api-docs` |
| Healthcheck | `/api/v1/actuator/health` |

后端核心依赖基线：

| 依赖 | 版本 |
| --- | --- |
| Spring Boot | `4.0.6` |
| Spring Framework | `7.0.7` |
| SpringDoc OpenAPI WebMVC UI | `3.0.3` |
| JJWT | `0.12.6` |
| Logback | `1.5.25` |
| Gradle Wrapper | `9.4.0` |

本地私有覆盖建议使用：

```text
server/src/main/resources/application-local.yml
```

该文件已被 `.gitignore` 排除。

## Docker 环境变量

模板文件：

```text
docker/.env.example
```

实际本地文件：

```text
docker/.env
```

`docker/.env` 不应提交。关键变量包括：

| 变量 | 用途 |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | 后端 Spring profile。 |
| `SERVER_PORT` | API 服务端口。 |
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码。 |
| `MYSQL_DATABASE` | 默认数据库名。 |
| `MYSQL_USER` | 应用数据库用户。 |
| `MYSQL_PASSWORD` | 应用数据库密码。 |
| `REDIS_VERSION` | Redis 镜像版本。 |
| `REDIS_PASSWORD` | Redis 密码。 |
| `JWT_SECRET` | JWT 签名密钥。 |
| `JWT_ACCESS_TOKEN_VALIDITY` | Access Token 有效期，单位秒。 |
| `JWT_REFRESH_TOKEN_VALIDITY` | Refresh Token 有效期，单位秒。 |
| `STALLMART_CORS_ALLOWED_ORIGINS` | 允许跨域访问 API 的 H5 调试源，默认 `http://localhost:10086,http://127.0.0.1:10086`。 |
| `WECHAT_APP_ID` | 微信小程序 App ID。 |
| `WECHAT_APP_SECRET` | 微信小程序密钥。 |
| `SPRING_DATASOURCE_URL` | 后端 MySQL JDBC 地址。 |
| `SPRING_DATASOURCE_USERNAME` | 后端数据库用户名。 |
| `SPRING_DATASOURCE_PASSWORD` | 后端数据库密码。 |

可选 OSS 配置目前只在模板中出现，当前代码未发现完整接入链路：

| 变量 | 用途 |
| --- | --- |
| `OSS_ACCESS_KEY_ID` | 阿里云 OSS Access Key。 |
| `OSS_ACCESS_KEY_SECRET` | 阿里云 OSS Secret。 |
| `OSS_BUCKET` | OSS Bucket。 |
| `OSS_ENDPOINT` | OSS Endpoint。 |

## 小程序配置

主要配置文件：

| 文件 | 说明 |
| --- | --- |
| `app/project.config.json` | 微信开发者工具项目配置。 |
| `app/project.private.config.json` | 本地私有配置，已忽略。 |
| `app/config/index.js` | Taro CLI 实际读取的项目构建配置。 |
| `app/taro.config.ts` | Taro 构建配置。 |
| `app/src/app-config/index.ts` | API 地址、endpoint、设计 token、存储 key。 |
| `app/src/app.config.ts` | 小程序页面、分包、tabBar、权限配置。 |
| `app/src/index.html` | H5 入口 HTML 模板，Taro H5 构建依赖该文件生成 `dist/index.html`。 |

H5 是本阶段 app 端真实 API 优先调试入口。小程序 request 合法域名申请完成前，先通过 H5 关闭 mock 验证主题和数据链路：

| 变量 | 用途 |
| --- | --- |
| `TARO_APP_ENABLE_API_MOCK=false` | 关闭小程序端 mock，让 H5 请求真实 API。 |
| `TARO_APP_ID=wx-stallmart-demo` | 本地按 AppID 调用 `/app/bootstrap` 识别演示商家。 |
| `TARO_APP_API_BASE_URL=http://localhost:8081/api/v1` | Docker H5 调试时浏览器侧访问后端 API 的地址。 |
| `TARO_APP_H5_PORT=10086` | Docker H5 dev server 监听端口。 |

常用命令：

```bash
cd app
TARO_APP_ENABLE_API_MOCK=false TARO_APP_ID=wx-stallmart-demo npm run dev:h5
TARO_APP_ENABLE_API_MOCK=false TARO_APP_ID=wx-stallmart-demo npm run build:h5
```

Docker H5 联调：

```bash
cd docker
docker compose up -d app-h5
```

默认访问 `http://localhost:10086/`，API 走 `http://localhost:8081/api/v1`。H5 的 host、端口、hash 路由、warning overlay 和 `src/static` 静态资源 copy 规则写在 `app/config/index.js`，该文件是 Taro CLI 实际读取的构建配置；`app/taro.config.ts` 保持同等配置，避免小程序与 H5 构建入口漂移。

当前 API base URL：

| 环境 | 地址 |
| --- | --- |
| development | `http://localhost:8080/api/v1` |
| production | `https://api.stallmart.com/api/v1` |

H5 容器会通过 `TARO_APP_API_BASE_URL` 覆盖 development 默认值。`TARO_APP_*` 变量必须在 Taro 构建期通过 `app/config/index.js` 的 `env` 注入，避免 H5 bundle 在浏览器中残留 `process.env` 表达式。

## 管理端配置

管理端浏览器端 API base 固定为同源 `/api/v1`，不得使用 `NUXT_PUBLIC_API_BASE` 或其他公开变量暴露后端真实地址。

| 配置 | 默认值 | 说明 |
| --- | --- | --- |
| `NUXT_API_PROXY_TARGET` | `http://localhost:8081` | Nuxt 服务端代理目标；Docker Compose 内部设置为 `http://api:8080`。 |

## 配置风险

- `app/config/index.js` 和 `app/taro.config.ts` 都包含 Taro 构建配置；当前 CLI 构建以 `config/index.js` 为准，alias 变更必须保持两处一致。
- `docker/docker-compose.yml` 的 `admin-web` build context 指向 `web/`，`app-h5` build context 指向 `app/`。
- `server/build.gradle` 声明 Java 25 toolchain，Dockerfile 也必须保持 Java 25。
- 业务表结构以 `server/src/main/resources/db/migration/` 下的 Flyway migration 为准，`docker/mysql/init/01-init.sql` 不再承载业务 schema 演进。
