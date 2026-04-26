# 安全规范

## 密钥与配置

禁止提交：

- `.env`
- `application-local.yml`
- 真实 `JWT_SECRET`
- 数据库生产密码
- Redis 生产密码
- 微信小程序 secret
- OSS access key
- 私钥文件

允许提交：

- `.env.example`
- 非敏感默认配置
- 本地开发占位值

## 认证

- 小程序请求使用 `Authorization: Bearer <access_token>`。
- Access Token 和 Refresh Token 生命周期必须与后端一致。
- Refresh Token 失效时必须清理本地状态。
- 登出必须清理本地 token、用户信息、购物车。
- 前端只负责体验层入口控制，后端必须做最终权限判断。

## API 安全

- 公开路径必须最小化。
- 读取 `userId` 的接口不应被公开路径误放行。
- 用户只能访问自己的订单，摊主只能操作自己店铺的订单。
- 参数校验失败返回统一错误结构。
- 不把 Entity 直接返回给前端。

## 日志

禁止打印：

- 完整 token
- 密码
- JWT secret
- 微信 secret
- OSS secret
- 完整手机号，除非业务必须且日志隔离明确

推荐：

```text
phone=138****1234
token=eyJ...abcd
```

## 输入安全

- 后端参数必须校验。
- 禁止 SQL 字符串拼接。
- 用户输入展示前需要考虑 XSS 和小程序富文本风险。
- 文件/图片上传必须校验类型、大小和来源。

## Docker 本地环境

- 本地服务优先绑定 `127.0.0.1`。
- 不要把开发 Docker Compose 暴露到公网。
- 生产部署必须启用 HTTPS。
- 生产数据库账号使用最小权限。

## 文档安全

- 文档中只能写占位密钥。
- 文档命令示例不要包含真实 token。
- 发现密钥进入 Git 后，不能只删除文件，需要轮换密钥。
