# 贡献与协作说明

本项目当前按私有小程序项目维护。提交代码或让 Agent 修改代码前，必须先阅读对应规范。

## 必读顺序

1. [AGENTS.md](AGENTS.md)
2. [docs/README.md](docs/README.md)
3. [docs/standards/README.md](docs/standards/README.md)
4. 按改动范围阅读模块 README：
   - 小程序: [mini-program/README.md](mini-program/README.md)
   - 后端: [backend/README.md](backend/README.md)
   - Docker: [docker/README.md](docker/README.md)

## 提交前检查

按改动范围运行：

```bash
git diff --check
```

```bash
cd mini-program
npm run lint
npm run build:weapp
```

```bash
cd backend
mvn test
```

```bash
cd docker
docker compose config
```

## 不应提交

- `node_modules/`
- `dist/`
- `target/`
- `.env`
- `application-local.yml`
- `project.private.config.json`
- 真实密钥、密码、token、微信密钥

## 文档同步要求

修改 API、环境变量、启动命令、Docker 服务、数据库脚本、页面模块、状态结构时，必须同步更新 `docs/` 中对应文档。

文档目录职责见 [docs/README.md](docs/README.md)。
