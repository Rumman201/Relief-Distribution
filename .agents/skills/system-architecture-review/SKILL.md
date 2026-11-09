---
name: system-architecture-review
description: Architectural evaluation, separation of concerns, modern Android architecture (MVVM/MVI, Clean Architecture, Repository pattern), and technical decision-making.
---

# System Architecture Review Skill

## 1. Purpose
Evaluate and evolve the application architecture from legacy Android God-Activities into a robust, decoupled, testable, and maintainable Clean Architecture with MVVM/MVI patterns.

## 2. Activation Criteria
Activate this skill when:
- Designing or refactoring application components, layers, and service boundaries.
- Resolving tight coupling between UI (Activities), business logic, and Firebase data access.
- Authoring Architecture Decision Records (ADRs) or technical upgrade plans.
- Migrating from monolithic Activity classes to ViewModel, StateFlow/LiveData, and Repository patterns.

## 3. Project Context
- **Current State**: God-Activities (`StartActivity`, `SignUpActivity`, `LoginActivity`, `GoogleMap`, `DistributorDashboard`, `SurvivorDashboard`). UI logic, Firebase network calls, location polling, and SharedPreferences writes are all tangled inside `Activity.onCreate()`.
- **Target Architecture**: Clean Architecture with MVVM:
  - **Data Layer**: Remote data sources (Firebase Realtime Database, Firebase Auth, Google Location Provider) behind clean Repository interfaces (`AuthRepository`, `UserRepository`, `LocationRepository`).
  - **Domain Layer**: Use cases / Interactors (`RegisterUserUseCase`, `AuthenticateUseCase`, `TrackLocationUseCase`, `RequestReliefUseCase`).
  - **Presentation Layer**: ViewModels (`AuthViewModel`, `MapViewModel`, `DashboardViewModel`) holding UI State (`StateFlow` / `LiveData`), observed by ViewBound Activities/Fragments.

## 4. Specialist Engineering Standards
- Follow Android Official Architecture Guidelines (MAD - Modern Android Development).
- Enforce Unidirectional Data Flow (UDF): Events go Up from UI to ViewModel; State flows Down from ViewModel to UI.
- Activities must only render UI state and forward user gestures. No direct database or networking calls in Activity classes.
- Repositories must be interface-driven to enable deterministic unit testing with mock data sources.

## 5. Step-by-Step Execution Procedure
1. **Coupling Analysis**: Inspect class dependencies and measure coupling (e.g. Activity directly calling `FirebaseDatabase.getInstance()`).
2. **Boundary Definition**: Identify entity models, boundary interfaces, and data transfer objects.
3. **Layer Separation**: Plan the separation of concerns:
   - Extract raw Firebase SDK calls into dedicated Repositories.
   - Extract state management into ViewModels.
   - Decouple background threading and callbacks using Coroutines or modern Executor services.
4. **ADR Formulation**: Document architectural trade-offs, rationale, and consequences in `docs/upgrade/08-architecture-decisions.md`.

## 6. Decision Tree for Ambiguous Cases
- *Should we rewrite in Kotlin immediately or stay in Java?*
  -> Maintain Java compatibility in Phase 1-2, adopt modern Java 17 + ViewBinding first, and introduce Kotlin incrementally in Phase 2 for new features/ViewModels without breaking legacy Java classes.
- *Should we introduce Jetpack Compose or stay with XML Views?*
  -> Maintain and modernize XML Views with ViewBinding and Material 3 in Phase 1-4. Migrate isolated components to Compose in later phases if justified.

## 7. Tool Instructions
- Inspect dependency graphs using Gradle: `.\gradlew.bat app:dependencies`.
- Track class dependencies via static analysis or structural inspection.

## 8. Safety Constraints
- Never perform a "big bang" architectural rewrite that breaks existing user flows.
- Modernize incrementally using the Strangler Fig pattern.
- Keep backwards compatibility with minSdk 24 (Android 7.0 Nougat).

## 9. Verification Steps
- Verify that presentation layer has zero direct references to Firebase SDK packages.
- Ensure all business logic can be tested without Android runtime or Context mocking.

## 10. Expected Output & Evidence Format
- Architecture diagrams (Mermaid flowchart / class diagram).
- Concrete ADR entries documenting context, decision, consequences, and compliance checklist.

