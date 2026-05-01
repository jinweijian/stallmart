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
| `WECHAT_APP_ID` | 微信小程序 App ID。 |
| `WECHAT_APP_SECRET` | 微信小程序密钥。 |

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

当前 API base URL：

| 环境 | 地址 |
| --- | --- |
| development | `http://localhost:8080/api/v1` |
| production | `https://api.stallmart.com/api/v1` |

## 配置风险

- `app/config/index.js` 和 `app/taro.config.ts` 都包含 Taro 构建配置；当前 CLI 构建以 `config/index.js` 为准，alias 变更必须保持两处一致。
- `docker/docker-compose.yml` 引用 `admin-web/`，但该目录当前不存在。
- `server/pom.xml` 声明 Java 21，Dockerfile 也必须保持 Java 21。
- 数据库脚本当前以 `docker/mysql/init/01-init.sql` 为准，服务端接入持久化后再补迁移策略。
