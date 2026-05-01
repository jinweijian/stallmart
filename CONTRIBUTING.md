# 协作说明

提交代码前先阅读：

1. [AGENTS.md](AGENTS.md)
2. [docs/README.md](docs/README.md)
3. 按变更范围阅读 `server/README.md` 或 `app/README.md`
4. [docs/guide/testing.md](docs/guide/testing.md)

## 提交前检查

服务端：

```bash
cd server
mvn test
```

小程序：

```bash
cd app
npm run lint
```

Docker：

```bash
cd docker
docker compose config
```

## 提交要求

- 一次提交只解决一个清晰问题。
- API、配置、目录结构变更必须同步文档。
- 不提交生成物、依赖目录、本地私有配置和真实密钥。
