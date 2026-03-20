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
- If any agent, skill, or workflow is used for non-trivial work, explicitly tell the user which ones are active and why before substantial work starts.
- After the task is complete, state again which agent, skill, or workflow was used so the user can trace how the task was handled.

## Mobile Knowledge Capture

- After every substantive mobile task, add or update at least one note under `docs/knowledge/` inside `Mobile_OBS_ASM`.
- Treat these notes as beginner-facing material for first-year students or new developers.
- Write these notes in Vietnamese with proper diacritics by default.
- Use simple wording suitable for a student who is still new to Android and API-based app development.
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
- When useful, map the flow back to the actual mobile files so the reader can jump from the note to the code.
- Prefer this explanation order when relevant:
  - problem or context
  - concept definition
  - why it matters
  - small example
  - how it was applied in this project
  - common mistakes
- Before creating a new knowledge note, check whether a related note already exists in `docs/knowledge/` and extend it instead of duplicating content.

## Mobile Default Routing For Codex

- Activities, fragments, adapters, XML layouts, navigation, API integration, local storage: `mobile-developer`
- Test work, regression protection, JUnit, instrumentation planning: `test-engineer`
- Root-cause investigation: `debugger`
- Token handling, secure storage, network safety, sensitive-data review: `security-auditor`
- Cross-domain mobile and backend coordination: `orchestrator`
- Documentation only when explicitly requested: `documentation-writer`
- Read-only project mapping or file discovery: `explorer-agent`

## Mobile User Visibility

- Announce which mobile agent is being used before substantial work.
- If one or more skills are used, state the skill names and why they are relevant.
- If a workflow is being followed, state that workflow briefly so the user understands how the task is being executed.
- Do not silently switch to another specialized agent for non-trivial work.

## Mobile Working Standard

- Keep Android work aligned with the real backend endpoints and DTOs.
- Favor simple MVP architecture over abstract layers that the project does not need yet.
- Use Java plus XML Views for new screens unless the user explicitly requests another UI approach.
- Treat a mobile feature as `Done` only when UI flow, API integration, basic error handling, and at least one verification step are aligned.
- Whenever an accepted mobile or cross-domain change modifies user flows, feature scope, business rules, role visibility, status semantics, or API behavior, update the relevant SRS Markdown in the same slice of work. Use the shared root SRS `../SRS-Old-Bicycles-Marketplace (1).md` unless the task explicitly uses another synchronized project copy.
- If the accepted change introduces or depends on new backend tables, payout records, evidence records, or other persisted schema concepts, make sure the SRS database section and feature descriptions are updated clearly.
- Do not mark a mobile slice as fully done if the implemented behavior changed but the corresponding SRS Markdown was left outdated.
- Do not claim a screen is complete if it only has static XML with no realistic data path.
- Do not store tokens in plain `SharedPreferences`; use encrypted storage when authentication is implemented.
