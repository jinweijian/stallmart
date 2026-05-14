# 大白的技术实验室

个人技术官网，使用 Vue 3、Vue Router、Vite、TypeScript 和 Vitest。

## 命令

```bash
npm install
npm run dev
npm run build
npm test -- --run
```

## 页面结构

这是一个静态构建的 SPA：

| 路由 | 说明 |
| --- | --- |
| `/` | 能力地图首页，展示工程闭环、分层技术栈、案例索引和笔记索引。 |
| `/cases/:slug` | 匿名架构治理案例详情。 |
| `/notes/:slug` | 技术笔记详情。 |

生产环境需要让 Nginx 或静态服务器把未知路径回退到 `index.html`，以支持详情页刷新。

## 内容维护

核心内容集中在：

```text
src/profile.ts
src/content.ts
```

- `profile.ts` 维护身份、导航、首屏和全站详情入口。
- `content.ts` 维护工程闭环能力地图、架构治理案例和技术笔记。
- 案例和笔记必须使用稳定唯一的 `slug`，测试会检查数量、slug 和详情链接。
- 文案可以技术化表达 Taro、多端、管理端、服务端和接口契约，但不要写商业、交易、客户、营收、转化等营销叙事。

## 静态编译

这是一个静态站点，服务器端只需要托管编译后的文件。

```bash
npm install
npm run build
```

编译产物会输出到 `dist/`。部署时将 `site/dist/` 目录内的文件放到服务器 Web 根目录即可。

## 内容边界

本应用只展示个人技术介绍。测试会检查昵称、工程闭环能力、案例/笔记数量、slug 唯一性和禁用词约束。
