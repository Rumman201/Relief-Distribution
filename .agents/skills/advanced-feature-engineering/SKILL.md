---
name: advanced-feature-engineering
description: End-to-end feature discovery, user value assessment, vertical slice implementation, domain modeling, and user journey completion for disaster relief operations.
---

# Advanced Feature Engineering Skill

## 1. Purpose
Guide the systematic discovery, specification, architecture, implementation, and verification of vertical full-stack features in the Relief Distribution System.

## 2. Activation Criteria
Activate this skill when:
- Designing or implementing new user journeys for Survivors or Distributors.
- Repairing incomplete or defective workflows (e.g. relief request submission, distributor acceptance, notifications).
- Transforming UI prototypes into fully wired, database-backed, tested production features.

## 3. Engineering Invariants & Standards
1. **Vertical Slice Execution**: Every feature must span from UI layer (ViewBinding + Material 3) through ViewModel/Repository down to persistent storage (Firebase RTDB) and domain validation.
2. **Deterministic Domain Rules**: Domain state machines (e.g. `ReliefRequest` transitions) govern all state updates; UI cannot force illegal state transitions.
3. **Graceful Degraded Mode**: All feature journeys must function or cleanly alert when offline or in degraded connectivity.
4. **Accessible Ergonomics**: Minimum 48dp touch targets, clear error messages, and scrollable containers.

## 4. Execution Procedure
1. **Discover & Specify**: Identify user persona, problem statement, input requirements, output states, and acceptance criteria.
2. **Domain & Data Modeling**: Update domain entities with immutability, validation bounds, and Firebase serialization mappings.
3. **Repository Layer**: Expose atomic CRUD operations with proper callbacks (`RepositoryCallback<T>`).
4. **UI & ViewBinding**: Implement responsive Material 3 layouts enclosed in `ScrollView` with status chips and loading progress.
5. **Wire & Test**: Connect ViewModel to Activity/Fragment, test happy and error paths with unit tests.
