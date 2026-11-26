# YooToob: toolkit usage (Phase 1)

The [100x-Agent-Toolkit](https://github.com/NafisRayan/100x-Agent-Toolkit) was installed in the
agent's environment (`~/.agents`, skills linked into `~/.claude/skills`), **not** in this repo.
The app has no runtime dependency on it or on any MCP server.

| Toolkit piece | Used for |
|---|---|
| Design skills (frontend/UI design guidance) | Palette rules (tinted neutrals, one accent, no pure black/white), no gradient text, no em dashes in UI copy, restrained glass |
| Accessibility guidance | 4.5:1 text contrast target, 48dp touch targets, reduced-motion and reduced-transparency handling |
| Motion guidance | 150–250 ms ease-out, no bounce; matched to the existing `Motion` tokens rather than adding a second system |
| Code-review / QA checklists | Self-review of the diff; the "don't claim unrun checks" rule in QA_REPORT |

Scripts written for this work (in the repo, plain Python/Node, no network):

| Script | Purpose |
|---|---|
| `tools/yootoob/check_resources.py [--strict]` | Parses every XML resource and checks `@color/@drawable/@string/@dimen/@style` references resolve |
| `tools/yootoob/java_syntax_check.py` | tree-sitter syntax check of changed Java files (no JDK here) |
| `tools/yootoob/render_icons.mjs` | Renders legacy launcher PNGs and 512px icons from `.github/assets/icon.svg` |

Outside the repo, in the sandbox only: a WCAG contrast calculator, and headless Chromium for
website screenshots at 390 px and 1280 px in light and dark.
