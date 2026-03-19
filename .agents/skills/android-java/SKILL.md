---
name: android-java
description: Native Android Java implementation guidance for activities, fragments, XML layouts, adapters, repositories, secure storage, and Gradle-based verification.
---

# Android Java

This skill is the main implementation guide for this repository.

## Preferred Stack

- Java
- XML Views
- AndroidX
- `RecyclerView`
- `Material Components`
- Repository-style data access

## Default Package Thinking

- `ui/` for activities, fragments, adapters
- `data/` for repositories and local data
- `network/` for API services
- `model/` for DTOs and domain models
- `util/` for focused helpers

## Good Patterns

- UI triggers intent
- state holder or controller-like class coordinates
- repository talks to network or storage
- UI renders the returned state

## Avoid

- Compose code in Java/XML tasks
- networking directly in activities
- raw long-running work on the main thread
- plain-text token storage
- hardcoded backend URLs in multiple files

## Verification

- `gradlew.bat lint`
- `gradlew.bat testDebugUnitTest`
- `gradlew.bat assembleDebug`

