---
name: lint-and-validate
description: Validation steps for Android Java work. Use after code changes to catch build, lint, and test regressions.
allowed-tools: Read, Glob, Grep, Bash
---

# Lint And Validate

## Default Quality Loop

1. Run lint.
2. Run unit tests.
3. Run assemble debug when the changed path affects app startup or wiring.

## Commands

- `gradlew.bat lint`
- `gradlew.bat testDebugUnitTest`
- `gradlew.bat assembleDebug`

## Scripts

- `python .agents/skills/lint-and-validate/scripts/lint_runner.py <project_path>`

