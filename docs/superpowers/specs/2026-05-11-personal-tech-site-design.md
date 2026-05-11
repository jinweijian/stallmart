# Personal Tech Site Design

## Goal

Create a new `site/` application beside `app/` and `web/` for Bai Ge's personal technical homepage.

## Scope

- Use Vue 3 for the page implementation.
- Keep the page independent from the existing mini program, admin web, and server apps.
- Present only technical introduction content.
- Avoid money, company, mini program SaaS, and related commercial positioning.

## Direction

The approved visual direction is a technical lab / technical blog style. The page should use the nickname `白哥` prominently and frame the homepage as `白哥的技术实验室`.

## Content

- Hero: `白哥的技术实验室`, concise technical positioning, animated technical background.
- Technical stack: Vue 3, TypeScript, engineering practices, Java / Spring Boot, API design, testing and quality.
- Methods: component design, state modeling, interface contracts, maintainability, automated verification.
- Lab notes: source reading, performance optimization, engineering standards, architecture notes.
- Footer: lightweight signature only.

## Visual Design

Use a polished dark technical-blog look with restrained contrast, subtle green/amber/cyan accents, animated grid lines, floating tags, card reveal transitions, and readable typography. The first screen should be the actual personal homepage, not a marketing landing page.

## Testing

Add tests for the content model so the core copy includes the required personal and technical signals and excludes forbidden commercial terms.
