# operations 文档目录

本目录负责环境、配置、Docker、部署和运行方式。

## 文档列表

| 文档 | 作用 |
| --- | --- |
| [configuration.md](configuration.md) | 后端、小程序、Docker 环境变量和配置入口。 |
| [deployment.md](deployment.md) | 本地 Docker、后端容器、小程序发布前检查。 |

## 做什么事应参考这里

- 启动本地 MySQL、Redis、API。
- 修改 `application.yml`。
- 修改 `docker/.env.example`。
- 修改 Dockerfile 或 docker-compose。
- 调整小程序生产 API 地址。
- 准备部署或发布。

## 当前注意事项

- `docker/.env` 是本地私有文件，不能提交。
- `docker/docker-compose.yml` 引用 `admin-web/`，但该目录当前不存在。
- `Dockerfile.api` 使用 Java 26 镜像，需要与 `backend/pom.xml` 一致。
- API healthcheck 使用 `/actuator/health`，当前后端依赖中未确认 Actuator 已接入。
