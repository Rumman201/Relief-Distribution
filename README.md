# GPS Guided Relief Distribution System

An Android application engineered for disaster relief operations and humanitarian logistics coordination, connecting Disaster Survivors with Relief Distributors.

---

## 1. System Capabilities & Personas

- **Disaster Survivors**:
  - Request emergency food rations, clean water, and medical aid.
  - Locate nearby relief distribution hubs and active aid convoys.
  - Access disaster response advisories and emergency 999 telephone hotlines.
- **Relief Distributors**:
  - Broadcast live GPS tracking telemetry to disaster coordination hubs.
  - Manage distribution supply drops and relief operations.
  - Monitor incoming emergency assistance requests in real-time.

---

## 2. Technology Stack

- **Target OS**: Android 7.0 (API 24) to Android 14 (API 34)
- **Runtime**: OpenJDK 17 (Java 11/17 compatibility)
- **Build System**: Gradle 8.9 (Kotlin DSL) with Android Gradle Plugin (AGP) 8.7.3
- **Cloud Backend**: Google Firebase (Authentication & Realtime Database)
- **Geospatial & Maps**: Google Play Services Maps & Location SDK

---

## 3. Developer Onboarding & Quickstart

### Prerequisites
1. **Java Development Kit**: OpenJDK 17 (`JAVA_HOME` pointing to JDK 17).
2. **Android SDK**: Android SDK Platform 34 and Build-Tools 34.0.0 (`ANDROID_HOME` configured).

### Setup Configuration
1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd Relief-Distribution-App
   ```
2. Set up local configuration from the provided template:
   ```bash
   cp local.properties.example local.properties
   ```
3. Edit `local.properties` to ensure `sdk.dir` matches your local Android SDK path and provide your Google Maps API key:
   ```properties
   sdk.dir=C\:\\Users\\<Username>\\AppData\\Local\\Android\\Sdk
   MAPS_API_KEY=AIzaSy...YourKey...
   ```

### Build & Verification Commands
- **Execute Unit Test Suite**:
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **Assemble Debug APK**:
  ```bash
  ./gradlew assembleDebug
  ```
  Generated artifact: `app/build/outputs/apk/debug/app-debug.apk`

---

## 4. Architecture & Engineering Documentation

Persistent engineering intelligence and decision records are maintained in [`docs/upgrade/`](docs/upgrade/):
- [`01-project-inventory.md`](docs/upgrade/01-project-inventory.md): Full component and dependency catalog.
- [`02-architecture-assessment.md`](docs/upgrade/02-architecture-assessment.md): Architectural topology and Clean MVVM strategy.
- [`03-engineering-audit.md`](docs/upgrade/03-engineering-audit.md): Specialist audit findings across 10 disciplines.
- [`04-baseline-metrics.md`](docs/upgrade/04-baseline-metrics.md): Verified build, test, and artifact measurements.
- [`05-master-roadmap.md`](docs/upgrade/05-master-roadmap.md): Complete 10-phase transformation roadmap.
- [`06-phase-1-execution.md`](docs/upgrade/06-phase-1-execution.md): Phase 1 implementation record and verification.
- [`07-risk-register.md`](docs/upgrade/07-risk-register.md): Technical and operational risk matrix.
- [`08-architecture-decisions.md`](docs/upgrade/08-architecture-decisions.md): Architecture Decision Records (ADRs).
- [`09-phase-handoff.md`](docs/upgrade/09-phase-handoff.md): Multi-agent phase transition and prerequisites.
- [`skill-registry.md`](docs/upgrade/skill-registry.md): Active specialist agent skills in `.agents/skills/`.

---

## 5. Security & Privacy Invariants

1. **Zero Plaintext Passwords**: Passwords are never stored in client databases, models, or SharedPreferences. Authentication is handled exclusively by Google Firebase Authentication.
2. **Secrets Protection**: API keys are injected via `local.properties` and Gradle manifest placeholders; never committed in `strings.xml`.
3. **Safe Telephony**: Emergency 999 calls utilize `Intent.ACTION_DIAL` requiring user confirmation, eliminating dangerous `CALL_PHONE` permissions.

---

## 6. Transformation Program Status

| Phase | Title | Status |
| :---: | :--- | :---: |
| **Phase 1** | **Project Intelligence, Foundations & Critical Fixes** | **COMPLETED** |
| Phase 2 | Architecture Modernization & Clean MVVM Decoupling | Planned (Next) |
| Phase 3 | Backend, Data Engineering & Offline Resiliency | Planned |
| Phase 4 | Frontend Experience, Design System & Accessibility | Planned |
| Phase 5 | Advanced Capabilities & Relief Operations | Planned |
| Phase 6 | Security, Privacy & Compliance Hardening | Planned |
| Phase 7 | Performance, Battery & Scalability Engineering | Planned |
| Phase 8 | DevOps, CI/CD & Operational Excellence | Planned |
| Phase 9 | Comprehensive QA, Adversarial & Multi-Device Testing | Planned |
| Phase 10 | Final Overhaul, Production Certification & Release | Planned |

