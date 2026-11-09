# Phase 2 Technical Debt Analysis & Retirement Register

**Project**: Relief Distribution Android Application  
**Package**: `com.example.reliefdistribution`  
**Phase**: Phase 2 of 10  
**Status**: ACTIVE  

---

## 1. Technical Debt Retired in Phase 2

| Debt Item | Category | Original Severity | Remediation Strategy | Post-Phase 2 Status |
|---|---|---|---|---|
| **TD-001: Direct Firebase Calls in Activities** | Architecture / Coupling | High | Created `AuthRepository`, `LocationRepository`, `FirebaseAuthRepository`, and `FirebaseLocationRepository`. ViewModels now encapsulate all domain calls. | **RETIRED** |
| **TD-002: Type-Unsafe `findViewById`** | UI Safety | Medium | Enabled Android ViewBinding in Gradle and converted all 9 Activities to generated binding objects. | **RETIRED** |
| **TD-003: SDK Class Name Shadowing** | Code Quality | Medium | Renamed `GoogleMap.java` to `MapActivity.java`. Map engine `com.google.android.gms.maps.GoogleMap` is now cleanly imported. | **RETIRED** |
| **TD-004: Unreferenced Boilerplate Activity** | Dead Code | Low | Deleted template files `MainActivity.java` and `activity_main.xml`. Removed entry from manifest. | **RETIRED** |
| **TD-005: Play Services Deprecated API** | Maintenance | Low | Updated `LocationRequest.create()` to modern `LocationRequest.Builder` with `Priority.PRIORITY_HIGH_ACCURACY`. | **RETIRED** |
| **TD-006: Untested View Logic & State** | Quality Assurance | High | Added 14 unit tests in Phase 2 across `AuthViewModelTest`, `MapViewModelTest`, and `ResourceTest` using Mockito and `InstantTaskExecutorRule`. Total unit tests now 19. | **RETIRED** |

---

## 2. Technical Debt Carried Forward to Subsequent Phases

| Debt Item | Category | Severity | Description | Target Phase |
|---|---|---|---|---|
| **TD-007: Plaintext `SharedPreferences`** | Security | Medium | Non-sensitive session tokens/cached names are stored in `MyAppPrefs`. Should be migrated to AndroidX `EncryptedSharedPreferences` / Jetpack DataStore. | Phase 3 (Security & Hardening) |
| **TD-008: Manual Dependency Instantiation** | Architecture | Medium | ViewModels instantiate default repositories via `new FirebaseAuthRepository()` instead of a dependency injection framework (Hilt/Dagger). | Phase 4 (Modularization & DI) |
| **TD-009: Inline Disaster Advisory Strings** | Localization / UX | Low | Advisory text in `Informations.java` and dashboard layouts is hardcoded in XML rather than externalized in `res/values/strings.xml`. | Phase 5 (UI/UX Engineering) |
| **TD-010: Offline Database Persistence** | Resilience | Medium | Firebase database does not have `.setPersistenceEnabled(true)` enabled for zero-connectivity disaster relief zones. | Phase 6 (Database Engineering) |

