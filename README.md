# StallMart

StallMart 是一个面向摊位、小店和临时经营场景的小程序项目。项目目录参考 `edusoho-lms` 的多端组织方式：按端拆分代码，按用途拆分文档。

## 开发环境配置

1. 克隆代码后先阅读 [AGENTS.md](AGENTS.md)。
2. [服务端](./server/README.md)
3. [小程序端](./app/README.md)
4. [管理端](./web/README.md)
5. [开发文档](./docs/README.md)

## 项目目录结构

```text
server/             服务端，基于 Spring Boot
app/                微信小程序端，基于 Taro 4 + Vue 3
web/                后台管理端，基于 Nuxt + Vue 3
docs/               项目文档
docker/             Docker 本地开发与构建配置
README.md           项目 README
AGENTS.md           Agent 工作入口
```

## 常用命令

```bash
cd server
./gradlew test
./gradlew bootRun
```

```bash
cd app
npm ci
npm run lint
npm run dev:weapp
```

```bash
cd web
npm install
npm run dev
npm run build
```

```bash
cd docker
docker compose config
docker compose up -d mysql redis
```

## 当前约束

- 前端 mock 数据保持开启，除非明确进入联调，不修改 mock 策略。
- 后端 API 修改必须同步 `docs/api-server/index.md`、`docs/api-app/index.md` 和测试说明。
- 目录、命名、测试组织遵循 `docs/specification/` 下的规范。
