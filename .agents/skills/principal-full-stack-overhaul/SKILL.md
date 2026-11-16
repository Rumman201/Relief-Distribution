---
name: principal-full-stack-overhaul
description: Cross-layer diagnosis, architectural constraint enforcement, backend/frontend integration, database consistency, regression protection, and incremental implementation.
---

# Principal Full-Stack Overhaul Skill

## 1. Purpose & Scope
Governs deep cross-layer system refactoring and modernization across the Android presentation layer, domain service logic, database transactions, background workers, and external service contracts.

## 2. Standards & Constraints
- **Android Standards**: Minimum SDK 24, Target SDK 34, AGP 8.7.3, ViewBinding mandatory across 100% of UI screens.
- **Clean Architecture Boundaries**: Views interact only through ViewModels; business logic is encapsulated in pure domain or repository components; models maintain non-negative invariants.
- **Fail-Safe Contracts**: Strict nullability checks, input sanitization, non-zero validation, and graceful degradation on network partitions.

## 3. Step-by-Step Procedure
1. **Diagnosis**: Identify architectural disconnects between UI state and backend datastores.
2. **Contract Hardening**: Enforce strict data contract models (`ReliefRequest`, `InventoryItem`, `User`, `Notification`).
3. **Database Consistency**: Validate Firebase RTDB paths, atomic transactions, and local memory representations.
4. **Integration Verification**: Verify end-to-end data flow: user input -> validation -> repository -> Firebase -> local state -> UI rendering.
5. **Regression Protection**: Author targeted unit tests covering happy path, edge cases, and fault states.
