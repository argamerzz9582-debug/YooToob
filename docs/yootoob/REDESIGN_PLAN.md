# YooToob: redesign plan (Phase 0)

**Name:** YooToob. **Tagline:** Your video world, reimagined.

## Direction

A calm, content-first video app. The thumbnails are the colour; the interface is tinted
neutrals plus one violet accent. "Liquid Glass" is used with restraint: only layers that float
over content (top capsule, bottom dock, mini player, sheets) are glass, and they are made of
translucency, a hairline edge and a lit top edge, never a live blur over a scrolling grid
(unsupported below API 31 and a full-screen pass per frame).

Not YouTube: no red, no play-triangle logo, no "YouTube for Android" claims. The mark is two
overlapping rings (the "oo"), violet and spark pink.

## Principles

1. One accent for "selected / primary / link", never for destructive actions (those use `yoo_danger`).
2. Every colour from a token; Java reads tokens with `ContextCompat`, never ARGB literals
   (the seek bar mirrors two tokens as constants because it is a hot draw path; documented).
3. Contrast ≥ 4.5:1 for text in both themes, checked with a script, not by eye.
4. Player chrome stays dark in both themes (video context).
5. Change presentation, not behaviour. No feature, pref key, identifier or network path changes.

## Scope, in order

| Step | What | Status |
|---|---|---|
| 1 | Brand tokens (`colors_yootoob.xml`, light + night) | done |
| 2 | Re-point existing role colours (`colors_mobile`, `colors_settings_search`, selectors) to tokens | done |
| 3 | Theme accent (`colorAccent/Secondary/ControlActivated`) | done |
| 4 | Hardcoded colours in Java: wordmark, seek bar, dock, menu icons, chips | done |
| 5 | Glass dock: tokenized, lit edge; floating dock ON by default | done |
| 6 | Strings: ViewTube → YooToob (TV app → SmartTube) | done (en, es, da, fi) |
| 7 | Logo: vector mark, adaptive icon (bg/fg/monochrome), legacy PNGs, site/README icons | done |
| 8 | Website: palette, hero copy, glass header with fallbacks, rebrand | done |
| 9 | README, fastlane, docs | done |
| 10 | Typography / spacing scale changes per screen | **not done**: needs on-device review; see QA_REPORT |
| 11 | Per-screen layout changes (empty/error states, tablet widths) | **not done**: existing states kept; see Next steps |

Steps 10–11 were deliberately left out: without a device or Gradle in this environment, layout
changes could not be looked at, and the brief forbids claiming unverified work.
