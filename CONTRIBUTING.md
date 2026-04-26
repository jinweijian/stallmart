# 贡献与协作说明

本项目当前按私有小程序项目维护。提交代码前请先阅读：

- `docs/PROJECT-STANDARDS.md`
- `docs/DEVELOPMENT.md`
- `docs/TESTING.md`

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

## 不应提交

- `node_modules/`
- `dist/`
- `target/`
- `.env`
- `application-local.yml`
- `project.private.config.json`
- 真实密钥、密码、token、微信密钥

## 文档同步

修改 API、环境变量、启动命令、Docker 服务、数据库脚本时，需要同步更新 `docs/` 中对应文档。
