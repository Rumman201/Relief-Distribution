---
description: Android code quality, ViewBinding, and lifecycle rules
---

# Android Code Quality Rules

1. **ViewBinding Invariant**:
   - Prefer Jetpack `ViewBinding` over `findViewById`.
   - Never reference raw view IDs after ViewBinding is enabled.

2. **Form Layout Invariant**:
   - All input forms and vertically stacked activities must be contained within a `ScrollView` or `NestedScrollView` with `android:fillViewport="true"`.
   - Manifest should configure `android:windowSoftInputMode="adjustResize"` to ensure keyboard does not cover input controls.

3. **Lifecycle & Memory Invariant**:
   - Unregister location callbacks (`fusedLocationClient.removeLocationUpdates(locationCallback)`) and Firebase event listeners in `onStop()` / `onDestroy()`.
   - Avoid long-lived anonymous listeners holding implicit outer references to Activity contexts.

