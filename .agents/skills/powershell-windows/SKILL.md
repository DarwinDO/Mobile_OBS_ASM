---
name: powershell-windows
description: Windows-safe command patterns for Gradle, Android SDK paths, quoting, and common shell pitfalls.
---

# PowerShell Windows

## Rules

- Quote full Windows paths.
- Prefer `gradlew.bat` on Windows.
- Keep commands ASCII-safe.
- Use parentheses around cmdlet calls in logical expressions.

## Examples

- `& '.\\gradlew.bat' lint`
- `Get-ChildItem 'app\\src\\main\\java' -Recurse`

