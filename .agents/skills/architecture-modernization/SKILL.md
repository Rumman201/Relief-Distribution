---
name: architecture-modernization
description: Comprehensive architecture assessment, boundary definition, Clean Architecture/MVVM design, dependency direction enforcement, and evolutionary migration sequencing.
---

# Architecture Modernization Skill

## 1. Purpose
Guide the systematic transformation of legacy Android God-Activities into decoupled, maintainable, testable, and production-grade Clean Architecture with Model-View-ViewModel (MVVM) patterns.

## 2. Activation Criteria
Activate this skill when:
- Refactoring monoliths or God-Classes that violate Single Responsibility Principle.
- Establishing package layers (Data, Domain, Presentation).
- Decoupling UI components from direct database, network, and hardware sensor SDKs.
- Designing architecture migration sequences without breaking working functionality.

## 3. Project Context
- **Target Application**: Relief Distribution System (`com.example.reliefdistribution`).
- **Target Architecture**: Clean MVVM:
  - `data/`: Repositories, data sources, entity models, network/database adapters.
  - `domain/`: Business use cases and interactor interfaces.
  - `ui/`: ViewModels, LiveData/StateFlow, ViewBinding Activities and Fragments.

## 4. Specialist Engineering Standards
- **Unidirectional Data Flow**: UI dispatches intent/events to ViewModel; ViewModel executes Use Case / Repository; Repository emits Result/Resource; UI observes state.
- **Dependency Inversion**: High-level modules must not depend on low-level details. Activities depend on ViewModels; ViewModels depend on Repository interfaces.
- **No Direct SDK Coupling in UI**: Zero `FirebaseDatabase` or `FirebaseAuth` calls inside Android Activities.

## 5. Step-by-Step Procedure
1. Create data contracts (`Resource<T>`, repository interfaces).
2. Implement repositories encapsulating raw SDK calls.
3. Build ViewModels exposing observable `LiveData<Resource<T>>`.
4. Refactor Activities to inflate `ViewBinding` and observe ViewModel states.
5. Verify behavior with characterization and unit tests.

## 6. Safety Constraints
- Never delete existing public intent contracts or route extras.
- Migrate one activity/workflow at a time.
- Verify build after every intermediate step.

