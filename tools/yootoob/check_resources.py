#!/usr/bin/env python3
"""Static resource check for the stmobile (phone) flavor.

There is no Android SDK in every environment this repo is edited from, so this
script catches the resource mistakes a redesign is most likely to make before
aapt2 ever runs:

  * every XML file under the checked res/ folders must parse;
  * every @type/name reference in stmobile resources, and every R.type.name in
    stmobile Java, must be defined by one of the repo's own modules - or be on
    the list of names that come from libraries (Material, AppCompat, media3).

It is not a substitute for `./gradlew :smarttubetv:assembleStmobileDebug`.
Run it with and without a change and compare: a new unresolved name is a bug.

    python3 tools/yootoob/check_resources.py            # report
    python3 tools/yootoob/check_resources.py --strict   # exit 1 on new problems
"""
import argparse
import os
import re
import sys
import xml.etree.ElementTree as ET

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
STMOBILE = os.path.join(ROOT, "smarttubetv", "src", "stmobile")

VALUE_TAGS = {
    "color": "color", "dimen": "dimen", "string": "string", "style": "style",
    "bool": "bool", "integer": "integer", "array": "array", "string-array": "array",
    "integer-array": "array", "plurals": "plurals", "attr": "attr", "fraction": "fraction",
    "declare-styleable": "styleable", "drawable": "drawable",
}
FILE_TYPES = {"drawable", "mipmap", "layout", "anim", "animator", "color", "xml", "raw",
              "font", "menu", "interpolator", "transition", "navigation"}
REF_TYPES = "color|drawable|dimen|string|style|mipmap|layout|anim|animator|bool|integer|array|font|menu|xml|plurals|raw|interpolator"
XML_REF = re.compile(r"@(?:\+)?(" + REF_TYPES + r")/([A-Za-z0-9_.]+)")
JAVA_REF = re.compile(r"(?<![\w.])(?:com\.liskovsoft\.smartyoutubetv2\.tv\.)?R\.(" + REF_TYPES + r")\.([A-Za-z0-9_]+)")

# Resources supplied by dependencies rather than by a module in this repo. Only the
# prefixes the stmobile flavor already used before the YooToob redesign belong here.
LIBRARY_PREFIXES = ("abc_", "mtrl_", "material_", "design_", "exo_", "m3_", "notification_",
                    "Theme.", "ThemeOverlay.", "Widget.", "TextAppearance.", "ShapeAppearance",
                    "Animation.", "Base.", "Platform.")


def res_dirs():
    skip = {".git", "build", "node_modules", ".gradle"}
    for base, dirs, _files in os.walk(ROOT):
        dirs[:] = [d for d in dirs if d not in skip and not d.startswith(".")]
        if os.path.basename(base) == "res" and os.sep + "src" + os.sep in base + os.sep:
            yield base


def collect_definitions():
    defined = {}
    bad_xml = []
    for res in res_dirs():
        for folder in os.listdir(res):
            path = os.path.join(res, folder)
            if not os.path.isdir(path):
                continue
            rtype = folder.split("-")[0]
            for name in os.listdir(path):
                full = os.path.join(path, name)
                stem = name.split(".")[0]
                if rtype in FILE_TYPES:
                    defined.setdefault(rtype, set()).add(stem)
                if not name.endswith(".xml"):
                    continue
                try:
                    tree = ET.parse(full)
                except ET.ParseError as err:
                    bad_xml.append((full, str(err)))
                    continue
                root = tree.getroot()
                if rtype == "values":
                    for el in root:
                        tag = el.tag
                        rname = el.get("name")
                        if not rname:
                            continue
                        if tag == "item" and el.get("type"):
                            defined.setdefault(el.get("type"), set()).add(rname)
                        elif tag in VALUE_TAGS:
                            defined.setdefault(VALUE_TAGS[tag], set()).add(rname)
                            if tag == "style":
                                defined["style"].add(rname.replace(".", "_"))
    return defined, bad_xml


def iter_refs():
    for base, _dirs, files in os.walk(STMOBILE):
        for name in files:
            full = os.path.join(base, name)
            if name.endswith(".xml"):
                text = open(full, encoding="utf-8").read()
                text = re.sub(r"<!--.*?-->", "", text, flags=re.S)
                for m in XML_REF.finditer(text):
                    if text[max(0, m.start() - 8):m.start()].endswith("android:"):
                        continue
                    yield full, m.group(1), m.group(2)
            elif name.endswith(".java") or name.endswith(".kt"):
                text = open(full, encoding="utf-8").read()
                text = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
                text = re.sub(r"//[^\n]*", "", text)
                for m in JAVA_REF.finditer(text):
                    prefix = text[max(0, m.start() - 9):m.start()]
                    if prefix.endswith("android.") or prefix.endswith("material."):
                        continue
                    yield full, m.group(1), m.group(2)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--strict", action="store_true", help="exit 1 when anything is unresolved")
    args = parser.parse_args()

    defined, bad_xml = collect_definitions()
    unresolved = {}
    for path, rtype, name in iter_refs():
        lookup = "style" if rtype == "style" else rtype
        names = defined.get(lookup, set())
        if name in names or name.replace("_", ".") in names:
            continue
        if rtype == "color" and name in defined.get("drawable", set()):
            continue
        if name.startswith(LIBRARY_PREFIXES):
            continue
        unresolved.setdefault((rtype, name), set()).add(os.path.relpath(path, ROOT))

    for path, err in bad_xml:
        print("XML PARSE ERROR  %s: %s" % (os.path.relpath(path, ROOT), err))
    for (rtype, name), paths in sorted(unresolved.items()):
        print("UNRESOLVED  @%s/%s  <- %s" % (rtype, name, ", ".join(sorted(paths))))
    print("checked: %d xml parse errors, %d unresolved references" % (len(bad_xml), len(unresolved)))
    if args.strict and (bad_xml or unresolved):
        sys.exit(1)


if __name__ == "__main__":
    main()
