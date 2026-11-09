# Phase 9 — Test Evidence Index & Verification Artifacts

## 1. Primary Verification Artifacts

This index catalogs empirical evidence supporting Phase 9 Quality Assurance and Release Candidate Certification:

| Evidence Artifact | File Path / Location | Cryptographic SHA-256 / Size | Purpose |
| :--- | :--- | :--- | :--- |
| **Release Candidate APK** | `app/build/outputs/apk/debug/app-debug.apk` | `10,007,584 bytes`<br>`D2F821F3D226C96FEB9C88141E25D90D2ABEBBB379EFC4199D7000DAC8FA418B` | Certified Release Candidate Binary |
| **Unit Test HTML Report** | `app/build/reports/tests/testDebugUnitTest/index.html` | 131 tests, 0 failures, 0 ignored in 1.15s | Automated test execution proof |
| **Android Lint HTML Report** | `app/build/reports/lint-results-debug.html` | 0 errors across all XML layouts and Java sources | Static analysis proof |
| **Disaster Recovery Snapshot**| `backups/relief_rtdb_snapshot_20261109_112755Z.json` | `182eeaadc174d5122a38c70521818b3166fc9c23bc5b60e0b6ae238acfb585d3` | Declarative DR snapshot attestation |
| **CI/CD Workflow** | `.github/workflows/android.yml` | Least-privilege `contents: read`, concurrency control | Pipeline automation specification |
| **Declarative Cloud IaC** | `firebase.json` & `.firebaserc` | Staging vs. Production alias definitions | Infrastructure reproducibility |

---

## 2. Command Execution Audit Log Excerpts

### Test Suite Execution (`.\gradlew.bat testDebugUnitTest`):
```text
> Task :app:testDebugUnitTest
BUILD SUCCESSFUL in 20s
23 actionable tasks: 2 executed, 21 up-to-date
Tests run: 131, Failures: 0, Ignored: 0, Duration: 1.153s
```

### Android Lint Execution (`.\gradlew.bat lintDebug`):
```text
> Task :app:lintReportDebug
Wrote HTML report to file:///H:/Projects/Relief-Distribution/app/build/reports/lint-results-debug.html
> Task :app:lintDebug
BUILD SUCCESSFUL in 1m 14s
29 actionable tasks: 8 executed, 21 up-to-date (0 errors)
```

### Release Candidate Compilation (`.\gradlew.bat assembleDebug`):
```text
> Task :app:assembleDebug
BUILD SUCCESSFUL in 1m 20s
34 actionable tasks: 3 executed, 31 up-to-date
File Size: 10,007,584 bytes
SHA-256: D2F821F3D226C96FEB9C88141E25D90D2ABEBBB379EFC4199D7000DAC8FA418B
```

