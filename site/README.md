# 白哥的技术实验室

个人技术官网，使用 Vue 3、Vite、TypeScript 和 Vitest。

## 命令

```bash
npm install
npm run dev
npm run build
npm test -- --run
```

## 静态编译

这是一个静态站点，服务器端只需要托管编译后的文件。

```bash
npm install
npm run build
```

编译产物会输出到 `dist/`。部署时将 `site/dist/` 目录内的文件放到服务器 Web 根目录即可。

## 内容边界

本应用只展示个人技术介绍。文案集中在 `src/profile.ts`，测试会检查昵称、技术内容和禁用词约束。
