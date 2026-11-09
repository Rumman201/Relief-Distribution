---
name: performance-optimization
description: Android runtime profiling, battery consumption mitigation, location polling throttling, memory leak prevention, and APK size reduction.
---

# Performance Optimization Skill

## 1. Purpose
Identify, profile, and remediate performance bottlenecks, excessive battery consumption, UI frame drops, memory leaks, and network overhead in the Relief Distribution Android application.

## 2. Activation Criteria
Activate this skill when:
- Profiling battery consumption, CPU usage, or network payloads.
- Optimizing GPS location polling frequencies and accuracy trade-offs.
- Diagnosing memory leaks from retained Activity contexts in async listeners.
- Analyzing APK size, ProGuard/R8 dead-code shrinking, and resource bloat.

## 3. Project Context
- **Performance Deficiencies Discovered**:
  1. **Aggressive Location Polling**: `GoogleMap.java` requests high accuracy GPS updates every 2000-5000ms (`setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)`), rapidly draining the device battery during disaster conditions where power sources are scarce.
  2. **Toast Spamming on Main Thread**: Displays a `Toast` message on every location update every 2-5 seconds, overloading Android's NotificationManagerService and stuttering the UI thread.
  3. **Context Leak in Anonymous Listeners**: `ValueEventListener` and `LocationCallback` hold implicit references to outer `Activity` instances; if the user rotates or exits the screen while a request is in flight, the entire Activity is leaked in memory.
  4. **Deprecated APIs**: Using deprecated `LocationRequest.create()` instead of modern `LocationRequest.Builder`.
  5. **Duplicate Library Inclusions**: `app/build.gradle.kts` imports both `firebase-database` (v21.0.0) and `firebase-database-v2010` (v20.1.0), increasing dex size and causing runtime symbol ambiguities.

## 4. Specialist Engineering Standards
- **Battery-Conscious Location Strategy**:
  - Distribute Mode Active: Balanced power accuracy or high accuracy with adaptive intervals (e.g., 10s moving, 60s stationary) using `LocationRequest.Builder.setMinUpdateDistanceMeters(15f)`.
  - Distribute Mode Off / App Backgrounded: Completely halt location updates (`stopLocationUpdates()`).
- **Memory Management**:
  - Always clean up callbacks in `onStop()` / `onDestroy()`.
  - Use `WeakReference` or lifecycle-aware components (Jetpack Lifecycle, `LifecycleCoroutineScope`, or LiveData/StateFlow).
- **ProGuard / R8 Optimization**:
  - Enable `isMinifyEnabled = true` and `isShrinkResources = true` in release builds.
  - Define explicit `-keep` rules for Firebase data model classes so reflection deserialization does not break.

## 5. Step-by-Step Execution Procedure
1. **Dependency De-duplication**: Remove conflicting library imports from `build.gradle.kts`.
2. **Modernize Location APIs**: Refactor to `LocationRequest.Builder` with battery-friendly intervals and distance thresholds.
3. **Eliminate Main-Thread Spam**: Remove debug Toasts and heavy calculations from high-frequency callbacks.
4. **Lifecycle Coupling**: Integrate AndroidX Lifecycle (`DefaultLifecycleObserver`) to automatically pause location polling when an Activity is not in foreground.

## 6. Decision Tree for Ambiguous Cases
- *High Accuracy (GPS) vs Balanced Power Accuracy (Network/Cell)?*
  -> For survivor aid points, balanced power accuracy (100m) is sufficient and saves 70% battery. For active distributor en-route delivery, use adaptive high accuracy only while actively moving.

## 7. Tool Instructions
- Measure build and APK metrics: `.\gradlew.bat assembleDebug`, check output size in `app/build/outputs/apk/debug/`.
- Inspect lint performance warnings: `.\gradlew.bat lint`.

## 8. Safety Constraints
- Never compromise emergency responsiveness for battery savings; maintain quick GPS lock when emergency distress is triggered.

## 9. Verification Steps
- Verify that `stopLocationUpdates()` unregisters the client from `FusedLocationProviderClient`.
- Verify memory leak resolution with LeakCanary or lifecycle testing.

## 10. Expected Output & Evidence Format
- Before/after benchmarks for build time, APK size, and location update frequencies.

