# YooToob: QA report

Date: 2026-11-26. Environment: Linux sandbox, Python 3, Node 22, headless Chromium.
**No JDK, Android SDK or device**, so nothing Android was compiled or run.

## Run, with results

| Check | Command | Result |
|---|---|---|
| Resource references | `python3 tools/yootoob/check_resources.py --strict` | 0 XML parse errors, 0 unresolved references (same as baseline) |
| XML well-formed | minidom parse of every `stmobile/res/**/*.xml` and `common` `values*/strings.xml` | 0 failures |
| Java syntax (changed files) | `python3 tools/yootoob/java_syntax_check.py` (tree-sitter) | 8/8 files ok. Not a compiler: types, imports and symbols are **not** verified by it |
| Removed symbols | grep for `updateThemeMode`, `mIsDarkTheme`, `RED_VIEWTUBE`, `ic_viewtube_logo`, `app_icon_foreground/monochrome` | no remaining references |
| Tooling tests | `cd tools && python -m pytest -q` | 98 passed, 1 skipped (same as baseline) |
| Changelog parser | `tools/update_manifest.parse_changelog` on both changelogs | newest versions `1.16.0, 1.15.0`; the new "Unreleased" section is ignored as designed |
| Website build | `python3 website/build.py --offline --out /tmp/site_new` | built 9 pages |
| Website visual | headless Chromium at 320, 390, 768, 1280 px, light and dark | no overflow; hero, header, download page render; see notes |
| Contrast (WCAG 2.x) | script over every text/background token pair | all text pairs ≥ 4.5:1 (lowest: nav inactive 5.06, danger 5.03) |
| Icons | rendered PNGs inspected at 48 px and 512 px | mark legible at 48 px; transparent corners |

## NOT run (must happen before release)

| Check | Why not | How |
|---|---|---|
| `./gradlew :smarttubetv:assembleStmobileDebug` | no JDK/SDK | locally or a CI job. Note: `CI.yml` builds the **stbeta** (TV) flavor, so it will **not** compile the phone code changed here. Run the stmobile task explicitly. |
| `./gradlew lintStmobileDebug` / unit tests | same | same |
| On-device: light/dark switch, floating dock on/off, mini player, PiP, sheets | no device | Pixel-class phone + a 320dp emulator + a tablet |
| TalkBack, font scale 200%, Remove animations | no device | same |
| Launcher icon on real launchers (circle, squircle, themed icons) | no device | same |
| Website online build (fetches release data from GitHub) | used `--offline` | `python3 website/build.py` in `pages.yml` |

## Known nits

* Offline website builds show an empty version badge; online builds fill it.
* The download page still shows `ViewTube_<version>_*.apk`: these are the real release file
  names (see MIGRATION_NOTES), not an oversight.
