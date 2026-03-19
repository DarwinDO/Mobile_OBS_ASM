---
trigger: always_on
---

# AGENTS.md - Mobile Codex Adapter

This file is the Codex-facing adapter for the Android mobile repository at `e:\Old_bicycle_system\Mobile_OBS_ASM`.

## Codex-Specific Additions

- The canonical mobile rulebook for this repository is `.agents/rules/GEMINI.md`.
- Prefer these project-local paths:
  - `.agents/agents/`
  - `.agents/skills/`
  - `.agents/workflows/`
  - `.agents/rules/GEMINI.md`
  - `.agents/ARCHITECTURE.md`
- This repository is a native Android project. Do not default to React Native, Flutter, Compose, or Kotlin-only patterns unless the user explicitly changes scope.
- The current mobile stack is:
  - Android Studio
  - Gradle Kotlin DSL
  - Java
  - XML Views
  - AndroidX
  - JUnit for unit tests
- For user-facing Vietnamese copy, docs, and handoff notes, preserve proper Vietnamese diacritics.
- Keep code identifiers and code comments in English unless the file is explicitly documentation in Vietnamese.
- If a file already contains generated Android boilerplate, simplify it only when the current task needs it.
- If the task touches backend APIs, also read `../BE_old_bicycle_project/old_bicycle_project/AGENTS.md` before doing substantial work.
- The web frontend at `../old-bicycles-project/fe/` is not the source of truth for this mobile app. Use it only as a product and UX reference when relevant.
- Ignore any `AGENTS.md` inside `.gradle/`, `build/`, or third-party generated folders.

## Mobile Knowledge Capture

- After every substantive mobile task, add or update at least one note under `docs/knowledge/`.
- Treat these notes as beginner-facing material for first-year students or new developers.
- When relevant, explain the runtime flow in this order:
  - user action
  - activity or fragment
  - view model or controller-style class
  - repository or local cache
  - API service
  - backend response
  - UI state update
- If the task touches authentication, navigation, forms, lists, or API integration, document both:
  - what changed in code
  - how data moves through the app

## Mobile Default Routing For Codex

- Activities, fragments, adapters, XML layouts, navigation, API integration, local storage: `mobile-developer`
- Test work, regression protection, JUnit, instrumentation planning: `test-engineer`
- Root-cause investigation: `debugger`
- Token handling, secure storage, network safety, sensitive-data review: `security-auditor`
- Cross-domain mobile and backend coordination: `orchestrator`
- Documentation only when explicitly requested: `documentation-writer`
- Read-only project mapping or file discovery: `explorer-agent`

## Mobile Working Standard

- Keep Android work aligned with the real backend endpoints and DTOs.
- Favor simple MVP architecture over abstract layers that the project does not need yet.
- Use Java plus XML Views for new screens unless the user explicitly requests another UI approach.
- Treat a mobile feature as `Done` only when UI flow, API integration, basic error handling, and at least one verification step are aligned.
- Do not claim a screen is complete if it only has static XML with no realistic data path.
- Do not store tokens in plain `SharedPreferences`; use encrypted storage when authentication is implemented.

