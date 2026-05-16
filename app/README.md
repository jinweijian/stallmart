# StallMart 小程序端

本目录是 StallMart 微信小程序端，基于 Taro 4、Vue 3、TypeScript、Pinia 和 Tailwind CSS。

## 目录职责

```text
app/
  src/
    app-config/   API baseURL、endpoint、业务常量
    pages/        customer 与 vendor 页面
    static/       小程序静态资源
    store/        Pinia 全局状态
    utils/        request、auth、storage 等基础工具
```

## 必读文档

| 工作类型 | 文档 |
| --- | --- |
| 页面或模块 | [../docs/specification/app-module.md](../docs/specification/app-module.md), [../docs/specification/app-pages.md](../docs/specification/app-pages.md) |
| API、登录、缓存 | [../docs/api-app/index.md](../docs/api-app/index.md), [../docs/api-server/index.md](../docs/api-server/index.md) |
| 样式和交互 | [../docs/specification/app-style.md](../docs/specification/app-style.md) |
| 测试和提交 | [../docs/guide/testing.md](../docs/guide/testing.md) |

## 本地开发

```bash
npm ci
npm run dev:weapp
```

用微信开发者工具打开 `app/`，构建产物位于 `dist/`。

## 当前约束

- 小程序和 H5 均使用真实 API；本地运行前先启动服务端，或通过 `TARO_APP_API_BASE_URL` 指向可用后端。
- H5 真实 API 调试：`TARO_APP_ID=wx-stallmart-demo npm run dev:h5`。
- Docker H5 联调使用 `cd ../docker && docker compose up -d app-h5`，浏览器访问 `http://localhost:10086/`，端口由 `TARO_APP_H5_PORT` 控制。
- 业务页面不得直接调用 `Taro.request`，统一走 `src/utils/request.ts`。
- 新增 endpoint 必须写入 `src/app-config/index.ts` 并同步 API 文档。
