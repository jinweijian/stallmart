# StallMart Mini Program

本目录是 StallMart 微信小程序端，当前代码基于 Taro 4、Vue 3、TypeScript、Pinia 和 Tailwind CSS。

## 目录职责

```text
mini-program/
  package.json
  project.config.json
  taro.config.ts
  tsconfig.json
  src/
    app.ts
    app.vue
    app.config.ts
    app-config/   # API baseURL、endpoint、业务常量
    pages/        # customer 与 vendor 页面
    static/       # 小程序静态资源
    store/        # Pinia 全局状态
    utils/        # request、auth、storage 等基础工具
```

## 先读哪些文档

| 工作类型                 | 必读文档                                                                                                                                                           |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 新增页面或调整页面归属   | [../docs/mini-program/module-design.md](../docs/mini-program/module-design.md), [../docs/mini-program/page-map.md](../docs/mini-program/page-map.md)               |
| 修改接口调用、登录、缓存 | [../docs/mini-program/state-and-api.md](../docs/mini-program/state-and-api.md), [../docs/backend/api-reference.md](../docs/backend/api-reference.md)               |
| 修改样式或交互           | [../docs/mini-program/style-and-interaction.md](../docs/mini-program/style-and-interaction.md), [../docs/standards/code-style.md](../docs/standards/code-style.md) |
| 提交代码前自查           | [../docs/standards/review-checklist.md](../docs/standards/review-checklist.md), [../docs/quality/testing.md](../docs/quality/testing.md)                           |

## 本地开发

安装依赖：

```bash
npm ci
```

微信小程序开发构建：

```bash
npm run dev:weapp
```

生产构建：

```bash
npm run build:weapp
```

用微信开发者工具打开 `mini-program/`，构建产物位于 `dist/`。

## 常用命令

| 任务           | 命令                  |
| -------------- | --------------------- |
| 微信小程序开发 | `npm run dev:weapp`   |
| 微信小程序构建 | `npm run build:weapp` |
| H5 开发构建    | `npm run dev:h5`      |
| H5 生产构建    | `npm run build:h5`    |
| Lint           | `npm run lint`        |
| 自动修复 Lint  | `npm run lint:fix`    |
| 格式化         | `npm run format`      |

## 配置入口

- API 地址和 endpoint：`src/app-config/index.ts`
- 页面、分包、tabBar：`src/app.config.ts`
- 构建配置：`taro.config.ts`
- TypeScript 路径别名：`tsconfig.json`
- 微信项目配置：`project.config.json`
- 本地私有配置：`project.private.config.json`

## 当前注意事项

- `project.private.config.json` 不应提交。
- `node_modules/` 和 `dist/` 不应提交。
- `src/utils/request.ts` 与后端 `Result.success` 的成功码判断当前存在不一致，联调前需要修复。
- 新增接口常量必须写入 `src/app-config/index.ts`，页面不得直接硬编码 URL。
