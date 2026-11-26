# YooToob: toolkit and environment limitations

| Wanted | Status | Effect |
|---|---|---|
| JDK 17 + Android SDK (Gradle build, lint, unit tests) | unavailable in the sandbox | Java and resource changes are checked statically only. CI (`CI.yml`) must run before merging. |
| Emulator / device | unavailable | No screenshots of the Android app; no TalkBack or font-scale pass. |
| Browser/Playwright MCP, Figma or design MCPs | not configured | Website checked with a local headless Chromium script instead. |
| API keys for toolkit services needing them | not provided | Those skills were not used. |
| GitHub Actions on the fork | no runs; permissions API 403 | CI status unknown until the user enables Actions. |
| Network | only GitHub, npm, PyPI reachable | No external design references fetched at build time. |

What this means for trust: anything in QA_REPORT marked "not run" really was not run.
