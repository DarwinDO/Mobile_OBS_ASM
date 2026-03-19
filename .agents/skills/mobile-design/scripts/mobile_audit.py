from __future__ import annotations

import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: python mobile_audit.py <project_path>")
        return 1

    root = Path(sys.argv[1]).resolve()
    checks = {
        "AGENTS.md": root / "AGENTS.md",
        ".agents": root / ".agents",
        "AndroidManifest.xml": root / "app" / "src" / "main" / "AndroidManifest.xml",
        "main java": root / "app" / "src" / "main" / "java",
        "main res layout": root / "app" / "src" / "main" / "res" / "layout",
        "docs/knowledge": root / "docs" / "knowledge",
    }

    missing = [name for name, path in checks.items() if not path.exists()]
    if missing:
        print("Mobile audit failed.")
        for item in missing:
            print(f"- Missing: {item}")
        return 1

    print("Mobile audit passed.")
    for name in checks:
        print(f"- Found: {name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
