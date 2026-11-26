# YooToob: feature inventory (Phase 0)

Everything here worked on the baseline and must keep working. "Touched" says whether this
redesign changed code on that path; "visual only" means colours, copy or drawables, never logic.

| Area | Feature | Where | Touched |
|---|---|---|---|
| Browse | Home / Subscriptions / Library tabs, chips, infinite feed, pull to refresh, skeletons | `ui/browse/*`, `ui/common/Feed*`, `SkeletonReveal` | visual only (chip colours, dock, wordmark) |
| Browse | Floating glass header and bottom dock (toggles in Settings > Appearance) | `BrowseTopBar`, `LiquidGlassBottomNavigationView`, `AppearancePrefs` | visual + default of the dock toggle (now ON) |
| Search | Suggestions, history, results, filters | `ui/search/*` | no |
| Channel | Channel page, uploads, playlists, load-more failure row | `ui/channel/*` | no |
| Player | Media3 playback, SABR, quality/audio tracks, captions, speed | `ui/playback/*`, `sabr-media3/` | seek bar colours only |
| Player | Gestures (seek, brightness, volume, pinch zoom), chapters, comments, live chat | `ui/playback/*` | no |
| Player | Background playback, notification, mini player, system PiP, sleep timer | `MobilePlaybackService`, `MiniPlayerBridge`, `SystemPipBridge`, `SleepTimer*` | mini-player glass rim drawable only |
| Player | SponsorBlock, DeArrow, Return YouTube Dislike | `common/` | no |
| Account | Code sign-in, multiple accounts, keep-alive | `ui/signin/*`, `AccountsSheet` | strings only |
| Library | History, Watch later, playlists, save/create playlist | `common/`, `DialogRowAdapter` | menu icon colours |
| Offline | Downloads / saved videos | `common/`, `ui/playback` | no |
| Cast | Pair with SmartTube on a TV (remote code), Chromecast | `ui/adddevice/*`, `docs/mobile-port/CASTING.md` | strings (now name SmartTube) |
| Settings | All pages, search in settings, destructive rows | `ui/settings/*` | colours (danger token), strings |
| Theme | Light / Dark / System, persisted, applied without restart | `ThemeMode`, `ThemeRefresh` | no logic change; palettes replaced |
| Backup | Local zip + Google Drive backup/restore (`ViewTubeBackup`) | `common/misc/*Backup*` | no (folder name kept on purpose) |
| Updates | In-app OTA check, changelog sheet, install | `ui/update`, `common` `AppUpdates`, `update_urls.xml` | no |
| About | Repo/site links, share rows | `ui/about`, `strings_mobile.xml` | URLs point to the YooToob repo |
| Website | Landing, download, FAQ, trust, 404 (en/es), dark mode, lightbox | `website/` | copy, palette, header glass, icon |
| Store | fastlane title/descriptions/icon | `fastlane/` | rebranded |
| Tooling | Release/update-feed scripts and their tests | `tools/`, `.github/workflows` | no (names kept) |
