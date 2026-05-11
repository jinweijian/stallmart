# 个人技术官网规范

`site/` 是与 `app/`、`web/` 同级的个人官网应用，使用 Vue 3、Vite、TypeScript 和 Vitest。

## 定位

- 展示昵称 `白哥` 和个人技术介绍。
- 内容只围绕前端工程、后端架构、系统设计、测试验证、代码质量和技术记录。
- 不承载业务系统介绍，不写交易、营收、转化、套餐、SaaS、公司或小程序相关叙事。

## 目录职责

```text
site/
  src/
    App.vue          页面结构和 Vue 渲染入口。
    main.ts          Vue 挂载入口。
    profile.ts       个人官网内容模型。
    profile.test.ts  内容约束测试。
    styles.css       页面视觉、响应式和动画样式。
  index.html
  package.json
  vite.config.ts
```

## 内容规则

- 页面首屏必须直接呈现个人技术主页，不做产品落地页。
- 核心标题使用 `白哥的技术实验室`。
- 技术栈、方法和记录内容优先从 `src/profile.ts` 维护。
- 修改文案时必须运行 `npm --prefix site test -- --run`，确保禁用词约束仍通过。

## 视觉规则

- 采用技术博客 / 技术实验室气质。
- 动画可用于首屏进入、背景扫描、模块浮动、卡片滚动显现。
- 动画必须轻量，不影响正文阅读；需要支持 `prefers-reduced-motion`。
- 移动端布局必须避免文字重叠和横向溢出。

## 常用命令

```bash
npm --prefix site install
npm --prefix site run dev
npm --prefix site test -- --run
npm --prefix site run build
```
