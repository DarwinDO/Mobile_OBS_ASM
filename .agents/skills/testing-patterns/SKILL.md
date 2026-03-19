---
name: testing-patterns
description: Android Java testing guidance for unit tests, instrumentation planning, and focused regression coverage.
---

# Testing Patterns

## Testing Priorities

1. repository and mapper logic
2. state or controller logic
3. selected UI flows when worth the cost

## Default Commands

- `gradlew.bat testDebugUnitTest`
- `gradlew.bat connectedDebugAndroidTest`

## Rules

- Prefer small deterministic unit tests first.
- Add instrumentation tests only for flows that are hard to trust otherwise.
- Do not add snapshot-like UI tests with weak assertions.

