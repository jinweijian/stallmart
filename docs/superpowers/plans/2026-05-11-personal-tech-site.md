# Personal Tech Site Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Vue 3 personal technical homepage for `白哥` in a new top-level `site/` directory.

**Architecture:** The new app is isolated from existing business apps. Static content lives in a typed profile module, Vue components render that model, and tests guard required/forbidden copy.

**Tech Stack:** Vue 3, Vite, TypeScript, Vitest, CSS animations.

---

### Task 1: Project Skeleton And Content Tests

**Files:**
- Create: `site/package.json`
- Create: `site/index.html`
- Create: `site/tsconfig.json`
- Create: `site/vite.config.ts`
- Create: `site/src/profile.test.ts`

- [ ] Create the Vite / Vue package skeleton and a Vitest test that imports `siteProfile`.
- [ ] Run `npm --prefix site install`.
- [ ] Run `npm --prefix site test -- --run` and confirm the test fails because `src/profile.ts` does not exist.

### Task 2: Profile Model

**Files:**
- Create: `site/src/profile.ts`

- [ ] Add the typed `siteProfile` model with hero, stack, principles, notes, forbidden terms, and footer copy.
- [ ] Run `npm --prefix site test -- --run` and confirm the profile tests pass.

### Task 3: Vue Page And Animations

**Files:**
- Create: `site/src/App.vue`
- Create: `site/src/main.ts`
- Create: `site/src/styles.css`

- [ ] Render the profile model in Vue 3.
- [ ] Add the dark technical-lab visual system and CSS animations.
- [ ] Run `npm --prefix site test -- --run`.
- [ ] Run `npm --prefix site run build`.

### Task 4: Documentation

**Files:**
- Modify: `README.md`
- Modify: `docs/README.md`
- Create: `docs/specification/site.md`

- [ ] Document the new `site/` app, commands, content constraints, and relationship to the rest of the repo.
- [ ] Run final verification commands.
