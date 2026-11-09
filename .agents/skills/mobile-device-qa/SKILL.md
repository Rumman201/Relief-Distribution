---
name: mobile-device-qa
description: Mobile UX testing, Android layout inspection across screen densities and orientations, touch responsiveness, permissions lifecycle, and edge-case validation.
---

# Mobile Device QA Skill

## 1. Purpose
Validate Android mobile user experience, touch interactions, layout responsiveness across small/large screens and landscape mode, permissions request flows, and offline behavior under simulated disaster conditions.

## 2. Activation Criteria
Activate this skill when:
- Testing mobile UI screens and forms on Android devices or emulators.
- Validating dynamic runtime permissions (Location, Fine/Coarse, Dialing).
- Testing orientation changes (portrait to landscape) and Activity recreation.
- Stress-testing input forms with boundary conditions and keyboard interactions.

## 3. Project Context
- **Target Platform**: Android 7.0+ (API 24 to 34).
- **Critical UI/Device Scenarios**:
  1. Low-end device screen sizes (e.g. 4.5" - 5.0" 720p screens) where large fixed layouts overflow.
  2. Soft keyboard appearance hiding form submission buttons.
  3. Denied location permissions preventing map crashes.
  4. Activity lifecycle events (device rotation or home button press while location updates are active).

## 4. Specialist Engineering Standards
- **Configuration Change Resilience**: Ensure Activities survive orientation changes without crashing or losing form inputs (`onSaveInstanceState` or ViewModel state preservation).
- **Permission Flow UX**:
  - Never crash if permission is denied.
  - Explain *why* permission is needed before requesting if previously rejected (`shouldShowRequestPermissionRationale`).
  - Provide a fallback UI when location permission is unavailable (e.g. manual location selection).
- **Touch & Accessibility Standards**:
  - Touch targets minimum 48x48dp.
  - Screen reader support (`android:contentDescription` on all ImageViews).

## 5. Step-by-Step Execution Procedure
1. **Layout Stress Test**: Evaluate layouts against small screens (mdpi, hdpi) and wide screens (sw600dp tablets).
2. **Keyboard Ergonomics**: Verify that clicking inputs scrolls the field into view and keeps the action button accessible.
3. **Permission Denial Simulation**: Verify that rejecting location permissions displays a helpful message rather than crashing `GoogleMap`.
4. **Lifecycle Audit**: Rotate screen during live GPS polling; ensure no duplicate listeners or memory leaks.

## 6. Decision Tree for Ambiguous Cases
- *Physical device vs Emulator vs Headless validation?*
  -> When an attached device or emulator is present, run instrumented Espresso tests. In headless environments, run JVM unit tests, Robolectric, and static layout lint validation, transparently recording emulator absence.

## 7. Tool Instructions
- Inspect connected devices: `adb devices`.
- Take screenshots: `adb exec-out screencap -p > screen.png`.
- Run instrumented tests: `.\gradlew.bat connectedAndroidTest`.

## 8. Safety Constraints
- Never execute commands that wipe device state (`adb wipe`, `fastboot`) without explicit authorization.

## 9. Verification Steps
- Validate that all ImageViews possess meaningful `android:contentDescription` attributes.
- Confirm zero crashes on orientation toggle.

## 10. Expected Output & Evidence Format
- UX audit checklist and accessibility score reports in `docs/upgrade/03-engineering-audit.md`.

