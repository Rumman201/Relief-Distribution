# Relief Distribution System — Agent Engineering Invariants

This document establishes persistent engineering standards, invariants, and guidelines for AI coding agents and human engineers working on the Relief Distribution System Android application.

---

## 1. Core Operating Principles

1. **Understand Before Modifying**: Never edit or refactor code without thoroughly reading the surrounding implementation, understanding its purpose, and identifying all dependencies.
2. **Preserve Existing Working Capabilities**: All existing features (authentication, role-based dashboards, GPS tracking, emergency assistance) must remain functional through every transformation phase.
3. **Evidence-Based Engineering**: Every claim, defect report, or architectural decision must be substantiated with verified file paths, line references, or reproducible build/test commands.
4. **Zero Fabricated Results**: Never claim an automated test or command passed without executing it and observing exit code 0. Mark unmeasured items as **Not Measured**.
5. **No Secret Leaks**: Never commit API keys, cloud tokens, keystores, or user credentials to version control. Load secrets from `local.properties` or environment variables.

---

## 2. Android & Technology Invariants

- **Minimum Supported API**: Android 7.0 (API level 24).
- **Target & Compile SDK**: Android 14 (API level 34).
- **Language Compatibility**: Java 17 / Kotlin 1.9+.
- **Build System**: Gradle 8.9 (Kotlin DSL) with Android Gradle Plugin (AGP) 8.7.3.
- **ViewBinding**: All new and refactored UI code must use Android Jetpack `ViewBinding` instead of unsafe `findViewById`.
- **UI Responsiveness**: All form screens and content layouts must be enclosed in `ScrollView` or `NestedScrollView` to guarantee usability across small screens and soft keyboard appearances.
- **Safety Over Direct Dialing**: Emergency calls must invoke `Intent.ACTION_DIAL` (`Uri.parse("tel:999")`), never dangerous `Intent.ACTION_CALL` requiring privileged permissions.

---

## 3. Data & Security Invariants

- **Zero Plain-Text Passwords**: Passwords must NEVER be stored in the Firebase Realtime Database, client logs, or unencrypted `SharedPreferences`. Authentication is handled strictly through Firebase Authentication.
- **Scoped Permissions**: The app must only request permissions that are strictly necessary (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `INTERNET`, `ACCESS_NETWORK_STATE`).
- **Resource Lifecycle**: All Firebase listeners (`ValueEventListener`, `ChildEventListener`) and Google Play Services location callbacks (`LocationCallback`) must be unregistered in `onStop()` / `onDestroy()` / ViewModel `onCleared()` to prevent battery drain and memory leaks.

---

## 4. Quality & Verification Gates

Before completing any task or phase:
1. Run `./gradlew.bat testDebugUnitTest` to verify all unit tests pass.
2. Run `./gradlew.bat assembleDebug` to verify that the APK builds without errors.
3. Verify that Git working tree contains only intended, properly scoped modifications.
4. Update corresponding documentation in `docs/upgrade/`.

---

## 5. Specialist Skill Registry

Specialist agent skills are located in `.agents/skills/`:
- `project-intelligence-audit`: Reconnaissance, dependency mapping, and feature inventory.
- `system-architecture-review`: Architecture evaluation, MVVM modernization, and ADRs.
- `backend-engineering`: Firebase Auth, Realtime Database, and cloud resiliency.
- `frontend-experience-engineering`: Material 3 UI/UX, ViewBinding, and responsive layouts.
- `security-hardening`: OWASP MASVS compliance, secrets management, and permissions.
- `database-engineering`: Realtime Database schema, indexing, and consistency.
- `performance-optimization`: Battery optimization, memory leaks, and APK shrinking.
- `qa-verification`: Unit testing, Mockito, Espresso, and quality gates.
- `devops-reliability`: Gradle configuration, CI/CD, and reproducible builds.
- `technical-documentation`: Architecture documentation, changelogs, and handoffs.
- `mobile-device-qa`: Multi-screen layout testing, touch ergonomics, and permissions.

