# 个人技术官网规范

`site/` 是与 `app/`、`web/` 同级的个人官网应用，使用 Vue 3、Vite、TypeScript 和 Vitest。

## 定位

- 展示昵称 `大白` 和个人技术介绍。
- 定位为实战全栈与架构深度并重的个人技术官网。
- 内容围绕工程闭环、前端工程、后端服务、数据演进、部署配置、测试验证、代码质量和技术记录。
- 可以技术化表达 Taro、多端、管理端、服务端和接口契约。
- 不写交易、营收、转化、套餐、SaaS、公司、客户等营销叙事。

## 目录职责

```text
site/
  src/
    App.vue          全站布局、导航和 RouterView。
    content.ts       能力地图、匿名架构案例和技术笔记内容。
    main.ts          Vue 挂载入口。
    profile.ts       个人官网身份、导航和首屏内容。
    profile.test.ts  内容约束测试。
    router.ts        Vue Router 路由定义。
    styles.css       页面视觉、响应式和动画样式。
    views/           首页、案例详情和笔记详情页面。
  index.html
  package.json
  vite.config.ts
```

## 内容规则

- 页面首屏必须直接呈现个人技术主页，不做产品落地页。
- 核心标题使用 `大白的技术实验室`。
- 首页按工程闭环展示能力地图：需求建模、前端体验、后端服务、数据与部署、测试验证。
- 技术栈采用分层折叠：首屏展示核心栈，展开后展示扩展栈和能力证据。
- 首版包含 4 个匿名架构治理案例和 6 篇技术笔记。
- 案例详情必须包含问题背景、约束条件、架构决策、实现方式、验证方式和复盘收获。
- 笔记详情必须包含核心问题、技术拆解、实践清单、常见坑和延伸方向。
- 修改文案时优先维护 `src/content.ts` 和 `src/profile.ts`。
- 长期热更和素材分流按 [site-evolution.md](site-evolution.md) 执行。
- 修改文案时必须运行 `npm --prefix site test -- --run`，确保禁用词约束仍通过。

## 视觉规则

- 采用高级工程师档案作为结构骨架，少量加入终端 / 技术实验室气质。
- 动画可用于首屏进入、背景扫描、模块浮动、卡片滚动显现。
- 动画必须轻量，不影响正文阅读；需要支持 `prefers-reduced-motion`。
- 移动端布局必须避免文字重叠和横向溢出。

## 路由规则

```text
/              能力地图首页
/cases/:slug   匿名架构治理案例详情
/notes/:slug   技术笔记详情
```

生产部署时静态服务器必须将未知路径回退到 `index.html`，以支持详情页刷新。

## 常用命令

```bash
npm --prefix site install
npm --prefix site run dev
npm --prefix site test -- --run
npm --prefix site run build
```
