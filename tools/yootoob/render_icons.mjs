// Renders the YooToob raster icons from .github/assets/icon.svg with headless Chromium.
//
//   npm i --no-save playwright-core            # plus a Chromium: `npx playwright install chromium`
//   node tools/yootoob/render_icons.mjs [path-to-chromium]
//
// Writes: legacy launcher icons (mipmap-*/app_icon.png, app_icon_alt.png; Android 7.x, where
// adaptive icons don't exist), the README/F-Droid/website 512px icons. Adaptive icons (API 26+)
// are vector drawables and need no rendering.
import { readFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { chromium } from 'playwright-core';

const root = join(dirname(fileURLToPath(import.meta.url)), '..', '..');
const svg = readFileSync(join(root, '.github/assets/icon.svg'), 'utf8');
const res = join(root, 'smarttubetv/src/stmobile/res');
const targets = [
  ...[['mdpi', 48], ['hdpi', 72], ['xhdpi', 96], ['xxhdpi', 144], ['xxxhdpi', 192]].flatMap(([d, px]) => [
    [join(res, `mipmap-${d}/app_icon.png`), px],
    [join(res, `mipmap-${d}/app_icon_alt.png`), px],
  ]),
  [join(root, '.github/assets/icon.png'), 512],
  [join(root, 'fastlane/metadata/android/en-US/images/icon.png'), 512],
  [join(root, 'website/src/static/assets/icon-512.png'), 512],
];

const browser = await chromium.launch({ executablePath: process.argv[2] || undefined });
const page = await browser.newPage();
for (const [out, px] of targets) {
  await page.setViewportSize({ width: px, height: px });
  const sized = svg.replace(/width="\d+" height="\d+"/, `width="${px}" height="${px}"`);
  await page.setContent(`<html><body style="margin:0;background:transparent">${sized}</body></html>`);
  await page.locator('svg').screenshot({ path: out, omitBackground: true });
  console.log(out.replace(root + '/', ''), px);
}
await browser.close();
