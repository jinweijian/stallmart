# StallMart Mini Program

StallMart 微信小程序端，基于 Taro 4、Vue 3、TypeScript 和 Pinia。

## 目录结构

```text
mini-program/
  package.json
  project.config.json
  taro.config.ts
  src/
    app.ts
    app.vue
    app.config.ts
    app-config/
    pages/
      customer/
      vendor/
    static/
    store/
    utils/
```

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

| 任务 | 命令 |
| --- | --- |
| 微信小程序开发 | `npm run dev:weapp` |
| 微信小程序构建 | `npm run build:weapp` |
| H5 开发构建 | `npm run dev:h5` |
| H5 生产构建 | `npm run build:h5` |
| Lint | `npm run lint` |
| 格式化 | `npm run format` |

## 配置入口

- API 地址和 endpoint: `src/app-config/index.ts`
- 页面、分包、tabBar: `src/app.config.ts`
- 构建配置: `taro.config.ts`
- 微信项目配置: `project.config.json`
- 本地私有配置: `project.private.config.json`

## 当前注意事项

- `project.private.config.json` 不应提交。
- `node_modules/` 和 `dist/` 不应提交。
- `taro.config.ts` 当前包含本机绝对路径 alias，需要改为相对路径解析。
- `src/utils/request.ts` 与后端 `Result.success` 的成功码判断当前不一致，需要联调前修复。
