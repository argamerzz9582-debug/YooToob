# YooToob design system

Source of truth is the resource files; this page explains them. If they disagree, the
resources win and this page is out of date.

## Files

| Kind | File(s) |
|---|---|
| Brand tokens | `smarttubetv/src/stmobile/res/values/colors_yootoob.xml`, `values-night/colors_yootoob.xml` |
| Role colours (what layouts use) | `res/values{,-night,-v27}/colors_mobile.xml`, `colors_settings_search.xml` (all point at tokens or tinted neutrals) |
| State colours | `res/color/mobile_bottom_nav_item.xml`, `settings_switch_thumb.xml`, `settings_switch_track.xml`, `settings_radio_button.xml` |
| Theme | `res/values/styles_mobile.xml` (`Theme.NewTube`: `colorAccent`, `colorSecondary`, `colorControlActivated` = `yoo_accent`) |
| Shape / spacing / sizes | `res/values/dimens_mobile.xml` |
| Motion | `java/com/newtube/mobile/ui/common/Motion.java` |
| Theme mode + persistence | `ui/common/ThemeMode.java` (Light / Dark / System, stored in prefs, applied live by `ThemeRefresh`) |
| Logo | `res/drawable/ic_yootoob_logo.xml` (in-app), `ic_launcher_{background,foreground,monochrome}.xml` (adaptive icon), master SVG `.github/assets/icon.svg` |
| Website | `website/src/static/styles.css` `:root` variables mirror the app palette |

## Colour

Tinted neutrals (a trace of violet, never pure black or white) plus one accent.

| Role | Light | Dark | Notes |
|---|---|---|---|
| Page | `#FBFAFE` | `#0C0B12` | |
| Surface | `#FEFDFF` | `#17151F` | cards, sheets |
| Ink | `#14121C` | `#F3F1FA` | primary text, icons, wordmark |
| Ink secondary | `#5E5A6E` | `#F3F1FA` @ 70% | metadata |
| Nav inactive | `#6E6980` (5.1:1) | `#8E8A9C` | |
| Accent `yoo_accent` | `#5B3DF5` | `#A794FF` | selection, primary buttons, links, switches |
| On accent | `#FFFFFF` (6.1:1) | `#0C0B12` (7.8:1) | white on the dark-theme accent is only 2.5:1, never use it |
| Danger `yoo_danger` | `#D12B3A` (5.0:1) | `#FF7A82` (7.2:1) | sign out, delete, clear, block |
| Progress `yoo_progress` | `#7C66FF` | same | played part of every progress line, over video |
| Spark `yoo_spark` | `#FF5C8A` | same | the mark's second ring and the seek bar's leading tail only |
| Brand canvas | `#5B3DF5` | same | launcher icon background, site icon |

Rules: the accent never marks destructive actions. Menu action icons are neutral ink on a
neutral bubble; colour is reserved for state. Player chrome is dark in both themes.

## Glass

Glass is only for layers floating over content: the floating header capsule, the bottom dock,
the mini player, sheets.

* Body: surface colour at ~90% opacity (`yoo_glass_dock_body`), so labels keep full contrast
  over any thumbnail.
* Edge: 1dp hairline (`yoo_glass_dock_outline`, `mobile_glass_outline`).
* Light: a top-edge highlight fading out over the top third (`yoo_glass_dock_highlight`,
  `bg_glass_card_highlight`).
* Shadow: soft, tinted (`yoo_glass_dock_shadow`).
* No live backdrop blur on Android (not available below API 31; costly over scrolling grids).
  The website header uses `backdrop-filter` and falls back to an opaque ground when it is
  unsupported or the user prefers reduced transparency.

## Shape and spacing (dimens_mobile.xml)

| Token | Value |
|---|---|
| `mobile_glass_corner_radius_capsule` | 32dp (header capsule, dock) |
| `mobile_sheet_corner_radius` | 24dp |
| `mobile_glass_corner_radius_card` | 16dp (mini player, cards) |
| chip radius | 16dp (full pill at 32dp height) |
| `mobile_glass_corner_radius_control` | 10dp |
| `mobile_glass_border_width` | 1dp |
| spacing | 4 / 8 / 12 / 16 / 20 / 24dp, via the `mobile_*_padding` and `*_inset` dimens |
| touch targets | ≥ 48dp (`mobile_player_icon_button`, settings rows 56dp) |

## Typography

System font (Roboto / the device font) to keep the APK small and respect user font choices.
Sizes come from `dimens_mobile.xml` (`mobile_settings_title_size` 20sp, row title 16sp,
summary 14sp, player time 13sp) and Material text appearances. All in `sp`, so they follow
the system font scale. The website uses a subset Roboto Flex for display type
("YooToob Display") and the system UI font for text.

## Motion (single solution: `Motion.java`)

Material 3 curves (`EMPHASIZED`, `EMPHASIZED_DECELERATE` for arrivals, `EMPHASIZED_ACCELERATE`
for exits, `STANDARD` for fades) and shared durations: fade in 150ms, fade out 90ms, enter
250ms, exit 200ms, pop 260ms. Animations run on `ValueAnimator`/`ViewPropertyAnimator`, which
Android scales to zero when the user turns on *Remove animations*, so reduced motion is
honoured system-wide. No new animation library was added. The website wraps smooth scrolling and
transitions in `prefers-reduced-motion` queries.

## Logo

Two overlapping rings, the "oo" in YooToob. In-app: first ring `yoo_accent` (so it holds
contrast in both themes), second ring `yoo_spark`. Launcher: white and spark rings on brand
violet, all vector; the monochrome layer feeds Android 13 themed icons. Never a play triangle,
never red, nothing resembling YouTube's marks.

## Breakpoints

Android: `ui/common/MobileGrid` gives one full-width card per row on phone portrait and one
column per ~460dp beyond that (2 on landscape / small tablets, 3 on wide tablets), so the feed
reflows from 320dp phones to tablets; sheets cap at `mobile_sheet_max_width` (640dp). Website: gutters
step at 700px and 1100px, content max width 1200px; checked at 390px and 1280px.

## Focus

Android: default Material focus/ripple states on all clickable views; the dock and chips are
focusable views. Website: visible `:focus-visible` outlines (unchanged from the baseline).
