# YooToob: repository audit (Phase 0)

Baseline: `main` @ `1b95217` (fork of `Astrionix/ViewTube`), audited 2026-11-26 before any edit.

## What the project is

A native Android app (Java, XML views; no Compose, no web view UI). It is a phone/tablet front
end ("stmobile" flavor) on top of SmartTube's engine, which was built for Android TV.

| Item | Value |
|---|---|
| Language / UI | Java 17 source, Android Views, Material Components 1.14.0 |
| Player | AndroidX Media3 1.10.1 + an in-repo SABR source (`sabr-media3/`) |
| SDK | minSdk 24 (Android 7.0), targetSdk 37 |
| App module | `smarttubetv/` (flavors: `stmobile` phone, `stbeta`/`ststable`/... TV) |
| Phone UI source | `smarttubetv/src/stmobile/{java/com/newtube/mobile, res}` (186 Java files, 57 layouts) |
| Shared logic | `common/` (presenters, prefs, backup, updates), `MediaServiceCore/`, `SharedModules/` (submodules) |
| Package id | `io.github.aleixrodriala.arc` (must not change: it is the install/update identity) |
| Website | `website/`: static, Python `build.py`, strict CSP, no JS framework |
| Store text | `fastlane/metadata/android/` |
| Tests | `tools/` pytest suite (release/update tooling); Android unit tests under `smarttubetv/src/test*` |
| CI | `.github/workflows/CI.yml` (JDK 17: `lintStbetaRelease`, `assembleStbetaRelease`), `release.yml`, `pages.yml`, `fdroid-twin.yml`, `update-feed.yml` |
| License | MIT (`LICENSE`), upstream credits in `THIRD_PARTY_NOTICES.md` |

## Screens (phone)

Activities declared in `smarttubetv/src/stmobile/AndroidManifest.xml`:
Splash → `MobileBrowseActivity` (Home, Shorts-filtered feeds, Subscriptions, Library/You),
`MobileSearchActivity`, `MobileChannelActivity`, `MobileChannelUploadsActivity`,
`MobilePlaybackActivity` (+ `MobilePlaybackService`, mini player, PiP), `MobileSettingsActivity`
(pages in `ui/settings/`), `MobileSignInActivity`, `MobileAddDeviceActivity` (cast pairing),
`MobileAppDialogActivity` (generic menus/dialogs), `MobileUpdateActivity`, `MobileWebBrowserActivity`.

## How styling worked before

* Colours: `res/values{,-night,-v27}/colors_mobile.xml` + `colors_settings_search.xml`,
  named by role (`mobile_color_primary`, `mobile_glass_surface_*`). Primary was ViewTube red
  `#FF0033` in places, an indigo in others; menu icons each had their own hue.
* Several hardcoded colours in Java: the wordmark (`RED_VIEWTUBE` in `BrowseTopBar`), the
  seek bar (`PlayerTimeBar`), the floating dock's palette (`LiquidGlassBottomNavigationView`,
  two hand-written ARGB sets switched by an `mIsDarkTheme` flag), per-action menu tints
  (`DialogRowAdapter`).
* Theme: `Theme.NewTube` (DayNight Material) in `styles_mobile.xml`; light/dark/system persisted
  by `ui/common/ThemeMode` (already working, kept).
* Shape/spacing: `dimens_mobile.xml` (glass radii, spacing). Motion: `ui/common/Motion`
  (Material 3 curves + a few durations).

## Branding found

"ViewTube" in ~70 phone strings (en + es), common da/fi strings, launcher label, README, website
(~120 occurrences), fastlane, comments; a red play-style raster logo (`ic_viewtube_logo` PNGs,
`mipmap-*/app_icon*`), a red/navy SVG on GitHub and the website.

Some upstream "ViewTube" text was a bad find-and-replace of "SmartTube": e.g. "Pair with
ViewTube on the TV" and "not endorsed by ViewTube's developer". The TV app is SmartTube
(see `docs/mobile-port/CASTING.md`), so those now read SmartTube.

## Identifiers that are infrastructure, not branding (kept)

| Where | Why it stays |
|---|---|
| `io.github.aleixrodriala.arc` | install/update identity; changing it makes a different app |
| `com.newtube.*` Java packages, `newtube_*` prefs, `Theme.NewTube` | code/prefs identity; renaming risks prefs loss for no user gain |
| `common/src/main/res/values/update_urls.xml` (`viewtube.json`) | OTA feed the installed base polls |
| `common` `about_source_code_url` (Astrionix/ViewTube) | `AppUpdates.getReleasePageUrl` builds release links from it |
| `release.yml` / `update-feed.yml` / `tools/update_manifest.py` (`ViewTube_<v>_*.apk`, `REPO`) | the release workflow's signature check and feed generation match these names |
| `ViewTubeBackup` folder (`BackupAndRestoreHelper`, `GDriveBackupManager`) | renaming would hide users' existing backups from Restore |
| `website/src/static/CNAME` `viewtube.app`, `build.py` `SITE` | a domain is DNS, not a string |
| Signing cert subject `CN=ViewTube` | it is a fact about the key; the trust page now explains it |

## Baseline checks (run before editing)

| Command | Result |
|---|---|
| `cd tools && python -m pytest -q` | 98 passed, 1 skipped |
| `python3 website/build.py --offline --out /tmp/site_base` | built 9 pages |
| `python3 tools/yootoob/check_resources.py --strict` (added in this work) | 0 parse errors, 0 unresolved refs |
| `./gradlew ...` | **not runnable here**: no JDK / Android SDK in the sandbox |
| GitHub Actions on the fork | no runs; Actions permissions API returns 403 |

## Risks noted up front

* No Gradle locally: Java/resource compile errors can only be caught by static checks here and by CI.
* `CLAUDE.md` hard rules: bottom-sheet styling through the theme only; edge-to-edge via
  `MobileActivity.installContentInsets()`; don't touch player/network internals
  (`VIDEO_INFO_TYPE_LIST`, `GOOGLEVIDEO_RANGE_QUERY`).
* The OTA pipeline points at upstream releases (see MIGRATION_NOTES).
