---
name: comprehensive-browser-e2e
description: End-to-end screen navigation, user journeys, form validation, intent dispatch, and lifecycle verification across Android activities.
---

# Comprehensive E2E & Screen Navigation QA Skill

## 1. Purpose
Verify complete user journeys, activity transitions, form validations, deep linking, and interactive components across the Android client to guarantee that survivors and distributors experience uninterrupted mission-critical functionality.

## 2. Activation Criteria
Activate this skill when:
- Testing multi-screen navigation flows and back-stack integrity.
- Verifying input forms (registration, login, aid request, stock management) under boundary and invalid data.
- Inspecting Intent dispatch (e.g. `Intent.ACTION_DIAL`, Google Maps geo-intents).
- Auditing UI state preservation across activity lifecycle events (pause, resume, destroy).

## 3. Project Context
- **Primary Screens / Routes**:
  - `StartActivity` -> `WelcomeActivity` -> `LoginActivity` / `SignUpActivity`.
  - `SurvivorDashboard` -> `CreateReliefRequestActivity`, `MyReliefRequestsActivity`, `NotificationsActivity`, `Informations`, `Profile`.
  - `DistributorDashboard` -> `ManageRequestsActivity`, `InventoryManagementActivity`, `MapActivity`, `Profile`.
- **Navigation Invariants**:
  - Emergency dialing must use `Intent.ACTION_DIAL` (`tel:999`).
  - Navigation between dashboard tabs must preserve back-stack hygiene without memory leaks.

## 4. Engineering Standards
- **Exhaustive Input Verification**: Forms must reject empty fields, negative numbers, malformed emails, and invalid phone formats with clear inline error indicators.
- **Fail-Safe Intent Dispatch**: All external intent calls must verify `resolveActivity()` or catch `ActivityNotFoundException` to prevent application crashes.
- **State Recovery**: Screen rotations and backgrounding must not lose unsubmitted user input.

## 5. Step-by-Step Execution Procedure
1. **Route Mapping**: Catalog all 11 activities and their launch intents in `AndroidManifest.xml`.
2. **Journey Validation**: Trace execution paths from authentication through aid dispatch.
3. **Form Stress Testing**: Submit extreme strings, HTML markup, emoji, and boundary payloads.
4. **Diagnostic Inspection**: Inspect Logcat for unhandled runtime exceptions or UI thread stalls.

## 6. Decision Tree for Ambiguous Cases
- *What if Google Maps is not installed on the field device?*
  -> The app must gracefully fall back to web-based maps or alert the user via an accessible Material Dialog rather than crashing.

