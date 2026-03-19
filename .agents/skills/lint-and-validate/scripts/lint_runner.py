from __future__ import annotations

import subprocess
import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: python lint_runner.py <project_path>")
        return 1

    root = Path(sys.argv[1]).resolve()
    gradle = root / "gradlew.bat"
    if not gradle.exists():
        print("gradlew.bat not found.")
        return 1

    result = subprocess.run([str(gradle), "lint"], cwd=root)
    return result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
