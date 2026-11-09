---
name: safe-large-scale-refactoring
description: Systematic, behavior-preserving refactoring protocol, characterization testing, incremental extraction, adapter layers, and regression prevention.
---

# Safe Large-Scale Refactoring Skill

## 1. Purpose
Execute significant code restructuring and architectural transformations without introducing behavioral regressions, runtime exceptions, or breaking existing user workflows.

## 2. Activation Criteria
Activate this skill when:
- Splitting monolithic classes into multiple specialized classes.
- Renaming core components (e.g., renaming `GoogleMap` activity to `MapActivity`).
- Modifying widely-used constructors, method signatures, or data transfer objects.
- Replacing legacy patterns (`findViewById`) with modern alternatives (`ViewBinding`).

## 3. Engineering Standards
- **Strangler Fig Pattern**: Introduce the new abstraction alongside the old, migrate consumers sequentially, and remove the obsolete implementation only after full verification.
- **Characterization Testing**: Write unit tests establishing existing behavior before refactoring.
- **Micro-Commits**: Test after each atomic change; never bundle ten unrelated refactors into one unverified step.

## 4. Step-by-Step Procedure
1. Identify all references to the target class/method across the codebase.
2. Ensure test coverage exists for the current behavior.
3. Introduce new interface and implementation.
4. Update consumers one-by-one.
5. Remove dead legacy code.
6. Run full regression suite (`.\gradlew.bat testDebugUnitTest assembleDebug`).

