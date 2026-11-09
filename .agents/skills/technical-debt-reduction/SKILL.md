---
name: technical-debt-reduction
description: Systematic technical debt identification, dead-code pruning, naming clarity, complexity reduction, and eliminating obsolete abstractions.
---

# Technical Debt Reduction Skill

## 1. Purpose
Eliminate verified technical debt, remove dead boilerplate scaffold files, replace confusing or misleading identifiers, and de-duplicate shared logic across the codebase.

## 2. Activation Criteria
Activate this skill when:
- Pruning unused classes, layouts, or assets confirmed to have zero active references.
- Renaming confusing or shadowed classes (e.g. `GoogleMap.java` shadowing Google Maps SDK).
- Refactoring copy-pasted UI or navigation patterns.

## 3. Targeted Technical Debt Items in Phase 2
1. **Dead Scaffold Code**: Remove `MainActivity.java` and `activity_main.xml`.
2. **SDK Shadowing**: Rename `com.example.reliefdistribution.GoogleMap` to `MapActivity`.
3. **Ambiguous Identifiers**: Rename `linearLayout1..6` in `SurvivorDashboard` to semantic identifiers.
4. **Duplicated Auth Logic**: Extract repeated Firebase sign-in and session-checking logic into `AuthRepository`.

