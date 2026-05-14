---
name: stallmart-site-update
description: Use when the user asks to update the StallMart personal site, update site, refresh the personal homepage, add recent project progress to site/, or optimize the site as an evolving technical profile.
---

# StallMart Site Update

## Overview

Update `site/` as an evolving personal technical profile for Da Bai. Treat the project as early-stage: never invent maturity, metrics, or production outcomes; convert real progress into honest technical depth.

## Required Context

Read these before changing files:

1. `AGENTS.md`
2. `site/README.md`
3. `docs/specification/site.md`
4. `docs/specification/site-evolution.md`
5. `site/src/profile.ts`
6. `site/src/content.ts`

If the request mentions recent work, inspect recent truth sources first:

- `git status --short`
- `git diff --stat`
- `git log --oneline -n 10`
- relevant docs under `docs/specification/`, `docs/guide/`, `docs/deploy/`

## Update Heuristic

Classify the update into the smallest useful bucket:

| Signal | Update target |
| --- | --- |
| New verified implementation or refactor | Add or deepen an architecture case |
| New learning, reading, debugging, or design judgment | Add or deepen a technical note |
| New recurring capability across project work | Update capability map |
| Homepage feels stale or too generic | Improve hero, ordering, or visual hierarchy |
| Deployment/build/testing changed | Update delivery or verification content |

Prefer deepening existing entries before adding new ones unless there is a distinct new theme.

## Content Rules

- Keep positioning: practical full-stack plus architecture depth.
- Use "qualitative + process" evidence: problem, constraints, decisions, implementation, validation, retrospective.
- Early-stage wording is a feature, not a weakness. Say what is being designed, verified, or made repeatable.
- Do not write fake numbers, production claims, user scale, revenue, conversion, customer, company, pricing, or SaaS narratives.
- Technical terms such as Taro, multi-end, admin, server, API contract, Docker, Flyway, MySQL, Redis, Nginx, Vitest, JUnit are allowed when used technically.
- Keep all public copy anonymous and non-sensitive. Never include secrets, tokens, real private names, or internal credentials.

## Implementation Pattern

1. Decide whether the change affects content only or also layout/routes.
2. Update tests first when adding behavior, counts, routes, slugs, or content constraints.
3. Edit `site/src/content.ts` for capability/case/note content.
4. Edit `site/src/profile.ts` for identity, navigation, hero, or detail links.
5. Edit views/styles only when content cannot be represented by the current structure.
6. Sync docs when structure, routing, content boundaries, deployment, or maintenance rules change.
7. Build `site/dist/` after source changes.

## Verification

Always run:

```bash
npm --prefix site test -- --run
npm --prefix site run build
```

For visual or route changes, also preview and check:

- `/`
- one `/cases/:slug`
- one `/notes/:slug`
- mobile width has no horizontal overflow
- `site/dist/favicon.ico` still exists

## Default Response Shape

After updating, report:

- what content/structure changed
- which evidence bucket was used
- tests/build result
- whether `site/dist/` was regenerated
