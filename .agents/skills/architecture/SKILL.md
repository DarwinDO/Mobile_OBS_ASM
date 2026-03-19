---
name: architecture
description: Practical trade-off analysis for Android Java app structure, navigation, and backend integration.
---

# Architecture

## Decision Rules

- Prefer the simplest structure that still protects the code from obvious growth pain.
- Use fragments only when they simplify navigation, not by default.
- Use repositories once more than one screen depends on the same API path.
- Keep network and local storage behind focused classes.

## Typical Trade-Offs

- Activity-per-screen is faster for tiny flows.
- Single-activity with fragments scales better once bottom navigation appears.
- ViewBinding improves safety, but only if the project enables it consistently.

