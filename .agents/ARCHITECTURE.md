# Mobile Agent Architecture

This repository uses a lightweight local agent system for a native Android Java app.

## Overview

The mobile agent system is intentionally smaller than the web frontend system. It focuses on:

- native Android Java implementation
- XML view screens
- backend API consumption
- debugging, testing, and documentation

## Directory Structure

```text
.agents/
├── ARCHITECTURE.md
├── agents/
├── rules/
├── skills/
└── workflows/
```

## Agents

| Agent | Focus |
|------|-------|
| `mobile-developer` | Android Java screens, XML layouts, adapters, API integration |
| `orchestrator` | Multi-domain coordination, especially mobile plus backend |
| `project-planner` | Scope clarification, plans, phased delivery |
| `debugger` | Root-cause analysis and bug fixing |
| `test-engineer` | Unit and instrumentation testing strategy |
| `security-auditor` | Token storage, network safety, mobile auth review |
| `documentation-writer` | Docs only when explicitly requested |
| `explorer-agent` | Fast read-only project mapping |

## Skills

| Skill | Purpose |
|------|---------|
| `android-java` | Native Android Java and XML implementation guidance |
| `mobile-design` | Mobile UX, layouts, backend consumption, Android UI decisions |
| `clean-code` | Keep code simple and maintainable |
| `dev-lifecycle` | Documented implementation flow in `docs/ai/` |
| `plan-writing` | Planning doc structure |
| `architecture` | Trade-offs and structure decisions |
| `brainstorming` | Discovery questions when scope is vague |
| `testing-patterns` | Android unit and UI testing |
| `systematic-debugging` | Step-by-step investigation |
| `lint-and-validate` | Build, lint, and verification commands |
| `powershell-windows` | Windows-safe command patterns |
| `documentation-templates` | Knowledge note structure |
| `parallel-agents` | Multi-agent task split rules |
| `behavioral-modes` | Planning vs execution behavior |

## Workflows

| Workflow | Use |
|---------|-----|
| `plan` | Create a concrete mobile implementation plan |
| `create` | Start a new mobile feature |
| `enhance` | Improve an existing flow |
| `debug` | Investigate a bug |
| `test` | Add or review tests |
| `capture-knowledge` | Write beginner-friendly notes |
| `execute-plan` | Implement tasks from a plan |
| `writing-test` | Add targeted test coverage |

## Key Constraint

This app is Android-first and Java-first. Do not drift into Compose, Kotlin, React Native, or Flutter guidance unless the task explicitly asks for that change.

