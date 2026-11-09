# Phase 5 — Operational Readiness & Runtime Reliability

**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: 5 of 10  
**Date**: 2026-11-09  
**Status**: ACTIVE  

---

## 1. Operational Configuration & Prerequisites

| Configuration Item | Location | Environment Variable / Property | Default Fallback |
| :--- | :--- | :--- | :--- |
| **Google Maps API Key** | `local.properties` | `MAPS_API_KEY` | Manifest placeholder fallback to empty string |
| **Firebase Configuration** | `app/google-services.json` | Google Services JSON | Packaged client config (Project ID: `relief-distribution-2w5`) |
| **Min Supported Android API** | `app/build.gradle.kts` | `minSdk` | Android 7.0 (API Level 24) |
| **Target Android API** | `app/build.gradle.kts` | `targetSdk` / `compileSdk` | Android 14 (API Level 34) |

---

## 2. Circuit Breakers & Graceful Degradation

1. **Cellular Connectivity Severance**:
   - `FirebaseConnectionRepository` monitors `/.info/connected`.
   - UI displays dynamic connection badge: `Online` (Green) vs `Offline` (Orange).
   - In-flight GPS coordinates automatically route into `OfflineTelemetryBuffer` (FIFO ring, max 100).
   - Firebase Realtime Database disk persistence (`setPersistenceEnabled(true)`) ensures read/write queueing during blackouts.
2. **Google Play Services Unavailable**:
   - Turn-by-turn navigation checks for Google Maps app package (`com.google.android.apps.maps`).
   - If absent, seamlessly falls back to the in-app `MapActivity`.
3. **AI Triage Failure Defense**:
   - If notes or inputs are null or corrupted, `ReliefTriageAdvisor` safely clamps values and assigns a deterministic fallback priority tier (`LOW` or `MEDIUM`) with zero runtime exceptions.

---

## 3. Kill-Switches & Rollback Strategy

- **Feature Toggleability**: Advanced features (AI Triage, Automated Inventory Reservation, Live Map Tracking) are modularized behind clean service boundaries.
- **Database Compatibility**: New fields (`aiUrgencyScore`, `aiPriorityTier`, `threshold`, `location`) are fully non-breaking and declared with `@IgnoreExtraProperties` to ensure backward compatibility with older client versions.
- **Rollback Path**: The entire codebase compiles cleanly with Git tags, allowing instant rollback without data migration scripts.
