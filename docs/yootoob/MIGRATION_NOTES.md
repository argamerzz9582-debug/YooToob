# YooToob: migration notes

## For users

Nothing to migrate. Package name (`io.github.aleixrodriala.arc`), settings keys, accounts,
history and the `ViewTubeBackup` folder are unchanged. A YooToob build signed with the same key
installs over ViewTube and keeps everything.

Visible changes: new name and icon, violet accent instead of red, neutral menu icons, the
floating glass dock is now on by default (Settings > Appearance turns it off; if you had set it
before, your choice is kept).

## For maintainers: what was deliberately NOT renamed

| Thing | Why | To change it later |
|---|---|---|
| Package id `io.github.aleixrodriala.arc` | install/update identity | never, unless shipping a separate app |
| Java packages `com.newtube.*`, `Theme.NewTube`, `newtube_*` prefs | code identity; prefs rename would reset users' settings | not worth it |
| `update_urls.xml` → Astrionix `viewtube.json` | the OTA feed the installed base polls | see "Release identity" |
| `common` `about_source_code_url` (Astrionix/ViewTube) | `AppUpdates` builds release-tag links from it | together with the OTA feed |
| `release.yml` / `update-feed.yml` APK names `ViewTube_<v>_*.apk`, `build.gradle` output name, `tools/update_manifest.py` `REPO` | the release workflow's signature check and the feed generator match these exact names | rename all of them in one commit, and run the `tools/` tests |
| `ViewTubeBackup` folder | renaming hides existing backups from Restore | add a read-both migration first |
| Website domain `viewtube.app` (`CNAME`, `build.py` `SITE`) | DNS, not a string | buy/point a domain, then update both |
| Signing cert subject `CN=ViewTube` | property of the existing key | can't change without a new key (which breaks updates) |

## Release identity: decision needed before publishing YooToob builds

The app's OTA checker reads the upstream ViewTube feed. A YooToob APK built and signed by this
fork with a **different** key will be offered upstream ViewTube updates that Android will refuse
to install (signature mismatch). Before releasing from this fork, choose one:

1. Keep upstream's feed and key (only possible if upstream publishes the build), or
2. Point `update_urls.xml`, `about_source_code_url`, `tools/update_manifest.py` `REPO` and the
   workflows at `argamerzz9582-debug/YooToob`, and sign with the fork's own key (a fresh
   install for existing ViewTube users).

This needs the maintainer's decision and repo secrets, so it was not changed here.

## Copy decision: casting names SmartTube

Strings about casting/pairing now name the TV app **SmartTube**. Upstream's text said
"ViewTube on your TV", but other upstream passages ("ViewTube can cast to [SmartTube]",
`docs/mobile-port/CASTING.md`) show the receiver is SmartTube; the old wording came from a
find-and-replace. Review if a YooToob TV app ever exists.

## Removed files

`drawable-*/ic_viewtube_logo.png`, `mipmap-*/app_icon_foreground.png`,
`mipmap-*/app_icon_monochrome.png`: replaced by vector drawables (`ic_yootoob_logo`,
`ic_launcher_*`). No remaining references (`check_resources.py --strict`).
