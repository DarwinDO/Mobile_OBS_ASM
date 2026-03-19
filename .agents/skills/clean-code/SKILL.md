---
name: clean-code
description: Pragmatic code standards for Android Java projects. Keep code simple, cohesive, and readable.
---

# Clean Code

## Core Principles

- Prefer small classes with one clear role.
- Keep activities and fragments thin.
- Name things by purpose, not by abbreviation.
- Remove dead code and unused boilerplate.
- Favor simple MVP structure over speculative abstractions.

## Android-Specific Rules

- Avoid god activities.
- Extract adapter, repository, and mapper logic when it becomes reusable.
- Prefer explicit state names like `isLoading`, `hasError`, `selectedProductId`.
- Add comments only when the code intent is not obvious.

