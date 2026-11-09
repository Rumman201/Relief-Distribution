# Phase 2 Refactoring Register

**Project**: Relief Distribution Android Application  
**Package**: `com.example.reliefdistribution`  
**Phase**: Phase 2 of 10  
**Status**: COMPLETE  

---

## 1. Inventory of Refactored Components

| Refactoring ID | Component Target | Refactoring Type | Description | Status |
|---|---|---|---|---|
| REF-001 | `GoogleMap.java` | Class & File Rename | Renamed class and file to `MapActivity.java` to prevent class name shadowing with Google Maps SDK `com.google.android.gms.maps.GoogleMap`. | Complete |
| REF-002 | `activity_google_map.xml` | Layout File Rename | Renamed layout file to `activity_map.xml` to match `MapActivity` and produce `ActivityMapBinding`. | Complete |
| REF-003 | `MainActivity.java` | Dead Code Removal | Deleted default template activity which had zero references in launcher intent-filters or navigation. | Complete |
| REF-004 | `activity_main.xml` | Dead Code Removal | Deleted unused layout file. | Complete |
| REF-005 | `AndroidManifest.xml` | Manifest Pruning & Update | Removed `.MainActivity` declaration; updated `.GoogleMap` to `.MapActivity`. | Complete |
| REF-006 | `StartActivity.java` | Architecture Migration | Converted from direct `FirebaseAuth` / `FirebaseDatabase` singleton access to `AuthViewModel` + `ActivityStartBinding`. | Complete |
| REF-007 | `LoginActivity.java` | Architecture Migration | Converted from direct Firebase Auth/Database calls to `AuthViewModel` + `ActivityLoginBinding`. | Complete |
| REF-008 | `SignUpActivity.java` | Architecture Migration | Converted from direct Firebase Auth/Database calls to `AuthViewModel` + `ActivitySignUpBinding`. | Complete |
| REF-009 | `DistributorDashboard.java` | ViewBinding Migration | Converted from `findViewById` to `ActivityDistributorDashboardBinding`; updated navigation intent to `MapActivity`. | Complete |
| REF-010 | `SurvivorDashboard.java` | ViewBinding Migration | Converted from `findViewById` to `ActivitySurvivorDashboardBinding`; updated navigation intent to `MapActivity`. | Complete |
| REF-011 | `Profile.java` | ViewBinding Migration | Converted from `findViewById` to `ActivityProfileBinding`. | Complete |
| REF-012 | `Informations.java` | ViewBinding Migration | Converted from `findViewById` to `ActivityInformationsBinding`. | Complete |
| REF-013 | `WelcomeActivity.java` | ViewBinding & Pruning | Converted from `findViewById` to `ActivityWelcomeBinding`; removed dead Firebase imports. | Complete |
| REF-014 | `MapActivity.java` | Architecture & API Modernization | Migrated to `ActivityMapBinding` and `MapViewModel`; updated deprecated `LocationRequest.create()` to modern `LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)`. | Complete |

---

## 2. Granular Code Transformation Details

### 2.1 Class Shadowing Elimination (`GoogleMap` -> `MapActivity`)
- **Before**: An Activity named `GoogleMap` declared in `com.example.reliefdistribution.GoogleMap`. Inside its own source file, referencing the Google Maps SDK map engine required `com.google.android.gms.maps.GoogleMap myMap` fully-qualified field declarations, causing code readability hazards and subtle compiler lookup ambiguities.
- **After**: The Activity is named `MapActivity`. Imports `com.google.android.gms.maps.GoogleMap` directly and cleanly. All caller activities (`DistributorDashboard`, `SurvivorDashboard`) route explicitly to `MapActivity.class`.

### 2.2 ViewBinding Adoption (Zero `findViewById`)
- Enabled `viewBinding = true` in Gradle build features.
- All layout elements now accessed through strongly-typed bindings generated at compile-time.
- Unsafe view casting, typo-prone integer IDs, and potential `NullPointerException` risks are permanently resolved at compile time.

### 2.3 Play Services Location API Deprecation Fix
- **Before**: `LocationRequest locationRequest = LocationRequest.create(); locationRequest.setInterval(10000); locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);` (Deprecated in Play Services Location 21+).
- **After**:
  ```java
  LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
          .setMinUpdateIntervalMillis(5000)
          .build();
  ```
  Zero deprecation warnings produced during compilation.

