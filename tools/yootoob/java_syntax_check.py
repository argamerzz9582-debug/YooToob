#!/usr/bin/env python3
"""Java *syntax* check (no type checking) for files changed on this branch.

Needs `pip install tree-sitter tree-sitter-java`. Used where no JDK/Android SDK is available; a
real `./gradlew :smarttubetv:compileStmobileDebugJavaWithJavac` is still the authority.

    python3 tools/yootoob/java_syntax_check.py [files...]   # default: Java files changed vs main
"""
import subprocess
import sys

import tree_sitter_java
from tree_sitter import Language, Parser


def changed_files():
    out = subprocess.run(["git", "diff", "--name-only", "--diff-filter=AM", "origin/main...HEAD"],
                         capture_output=True, text=True).stdout.split()
    out += subprocess.run(["git", "diff", "--name-only", "--diff-filter=AM"],
                          capture_output=True, text=True).stdout.split()
    return sorted({f for f in out if f.endswith(".java")})


def errors(node, acc):
    if node.type == "ERROR" or node.is_missing:
        acc.append(node)
    for child in node.children:
        errors(child, acc)
    return acc


def main():
    parser = Parser(Language(tree_sitter_java.language()))
    files = sys.argv[1:] or changed_files()
    bad = 0
    for path in files:
        tree = parser.parse(open(path, "rb").read())
        found = errors(tree.root_node, [])
        for node in found:
            bad += 1
            print("%s:%d:%d: syntax error near %r" % (path, node.start_point[0] + 1,
                                                      node.start_point[1] + 1, node.text[:40]))
        if not found:
            print("ok  " + path)
    sys.exit(1 if bad else 0)


if __name__ == "__main__":
    main()
