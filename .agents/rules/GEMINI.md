# GEMINI.md - Mobile Edition

This file defines the core AI behavior for the native Android Java workspace.

## Mandatory Protocol

Before substantial work:

1. Read `.agents/ARCHITECTURE.md`.
2. Select the correct agent from `.agents/agents/`.
3. Read the agent file.
4. Read only the skills listed in that agent's frontmatter.
5. Apply those rules before implementation.

## Project Defaults

- Platform: Android
- Language: Java
- UI: XML Views
- Build: Gradle
- OS: Windows-friendly commands first
- Backend context: Spring Boot API in `../BE_old_bicycle_project/old_bicycle_project/`

## Request Classifier

| Request Type | Result |
|-------------|--------|
| Question | Explain with minimal file reads |
| Survey | Inspect structure without editing |
| Simple Code | Edit directly after reading the right agent and skills |
| Complex Code | Create or update docs in `docs/ai/` before large implementation |
| Debug | Reproduce, isolate, verify |

## Mobile-Specific Rules

- Do not default to Jetpack Compose.
- Do not default to Kotlin snippets when the project is Java.
- Do not place business logic directly in activities when a repository or view model style class is more appropriate.
- Do not store access tokens in plain text storage.
- Keep screen flows practical and MVP-friendly.
- Respect touch targets, loading states, empty states, and basic retry states.

## Language Rules

- Match the user's language.
- Keep code identifiers in English.
- Keep Vietnamese prose properly accented.

## Verification Rules

Before calling a mobile task complete, prefer this order:

1. `gradlew.bat lint`
2. `gradlew.bat testDebugUnitTest`
3. `gradlew.bat assembleDebug`

If a task only changes docs or agent-system files, a filesystem verification is enough.

## Knowledge Capture

Meaningful code changes should add or update a note in `docs/knowledge/`.

