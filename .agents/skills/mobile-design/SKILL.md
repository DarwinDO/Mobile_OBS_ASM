---
name: mobile-design
description: Mobile-first design and implementation rules for this Android Java app. Focus on touch-friendly UI, XML screens, backend-aligned flows, and MVP-safe architecture.
allowed-tools: Read, Glob, Grep, Bash
---

# Mobile Design

Read the relevant references before substantial UI work.

## Required References

- `android-architecture.md`
- `android-ui-views.md`
- `mobile-backend.md`

## Core Rules

- Mobile is not a small desktop screen.
- Primary actions should be obvious and easy to tap.
- Every screen needs loading, empty, and error thinking.
- Keep one-hand usage in mind for critical actions.
- Match UI decisions to the real backend flow.

## Script

- `python .agents/skills/mobile-design/scripts/mobile_audit.py <project_path>`

