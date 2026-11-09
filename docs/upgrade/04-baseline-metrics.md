# 04 — Baseline Engineering Metrics & Measurement Record

## 1. Environment & Runtime Baseline

| Component | Measured Version / Location | Baseline Verification Status |
| :--- | :--- | :--- |
| **Operating System** | Windows 11 (10.0 amd64) | Verified |
| **Java Development Kit** | Eclipse Adoptium Temurin OpenJDK 17.0.21+9 (`C:\Users\tamim\.jdk\jdk-17.0.21+9`) | **Installed & Verified** (`java -version`: 17.0.21) |
| **Android SDK** | Android SDK Platform 34 (`C:\Users\tamim\AppData\Local\Android\Sdk`) | **Installed & Verified** (API Level 34) |
| **Android Build-Tools**| Build-Tools 34.0.0 (`build-tools;34.0.0`) | **Installed & Verified** |
| **Android Platform-Tools**| Platform-Tools v37.0.1 (adb 1.0.41) | **Installed & Verified** |
| **Build Automation** | Gradle 8.9 / AGP 8.7.3 | **Verified** |
| **Version Control** | Git 2.47+ initialized in workspace | **Verified** |

---

## 2. Baseline Build & Test Execution Log

### Run 1: Unit Test Suite Baseline
- **Command Executed**:
  ```powershell
  $env:JAVA_HOME = "C:\Users\tamim\.jdk\jdk-17.0.21+9"
  $env:ANDROID_HOME = "C:\Users\tamim\AppData\Local\Android\Sdk"
  .\gradlew.bat testDebugUnitTest
  ```
- **Exit Code**: `0` (Success)
- **Execution Time**: `1m 28s`
- **Actionable Tasks**: 21 executed
- **Test Results**:
  - Total Tests Executed: `1`
  - Tests Passed: `1` (`com.example.reliefdistribution.ExampleUnitTest.addition_isCorrect`)
  - Tests Failed: `0`
  - Tests Skipped: `0`
- **Compiler Warnings Observed**:
  - `Note: GoogleMap.java uses or overrides a deprecated API.` (Play Services Location v21+ deprecation of `LocationRequest.create()`).

### Run 2: Debug APK Assembly Baseline
- **Command Executed**:
  ```powershell
  $env:JAVA_HOME = "C:\Users\tamim\.jdk\jdk-17.0.21+9"
  $env:ANDROID_HOME = "C:\Users\tamim\AppData\Local\Android\Sdk"
  .\gradlew.bat assembleDebug
  ```
- **Exit Code**: `0` (Success)
- **Execution Time**: `1m 25s`
- **Actionable Tasks**: 32 total (16 executed, 16 up-to-date)
- **Generated Artifacts**:
  - Output File: `app/build/outputs/apk/debug/app-debug.apk`
  - File Size: `9,786,903 bytes` (~9.33 MB)
  - Android Test APK: `app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk` (`557,024 bytes`)

---

## 3. Security & Code Quality Baseline Metrics

| Metric | Measured Baseline | Target Standard | Status |
| :--- | :--- | :--- | :--- |
| **Committed Plaintext Secrets** | 1 (Google Maps API key in strings.xml) | 0 | **FAIL (P0)** |
| **Plaintext Password Storage** | 2 locations (Firebase RTDB + SharedPreferences) | 0 | **FAIL (P0)** |
| **Dangerous Permissions** | 1 (`CALL_PHONE`) | 0 (Use `ACTION_DIAL`) | **FAIL (P1)** |
| **Unit Test Code Coverage** | < 1% (Only default 2+2 boilerplate) | > 75% for Domain/Utils | **FAIL (P2)** |
| **Duplicate Library Dependencies**| 1 (`firebase-database` v21 & v20.1) | 0 | **FAIL (P1)** |
| **Deprecated Location API Calls**| 4 calls in `GoogleMap.java` | 0 | **FAIL (P2)** |
| **Dead Code / Stubs** | 3 (`MainActivity`, `Informations`, `ivHomeIcon`) | 0 | **FAIL (P2)** |
| **Unscrollable Overflowing Forms**| 1 (`activity_sign_up.xml`) | 0 | **FAIL (P0)** |
| **Connected Device / AVD** | 0 devices attached (`adb devices`) | Target: 1+ emulator | **Not Measured** |

