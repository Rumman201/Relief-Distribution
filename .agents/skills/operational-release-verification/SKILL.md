---
name: operational-release-verification
description: Release gate validation, pre-flight verification, APK checksum attestation, and operational sign-off.
---

# Operational Release Verification Skill

## 1. Purpose
Execute rigorous, evidence-based pre-flight release verification, quality gate enforcement, cryptographic checksum attestation, and operational sign-off protocols before any build is deployed to staging or production.

## 2. Activation Criteria
Activate this skill when:
- Conducting formal release readiness assessments prior to binary distribution.
- Verifying all quality gates (unit tests, static analysis, security rules, performance budgets).
- Calculating and recording cryptographic digests (SHA-256) and artifact byte sizes.
- Authoring release notes, deployment checklists, and operational handoff documentation.

## 3. Project Context
- **Artifact**: `app-debug.apk` and `app-release.apk` (or AAB bundle).
- **Mandatory Gates**:
  - Gate 1: 100% unit and regression test suite pass rate (exit code 0).
  - Gate 2: Clean Gradle build without unresolved warnings or deprecated API failures.
  - Gate 3: Cryptographic verification of artifact SHA-256 hash.
  - Gate 4: Zero unencrypted credentials or plain-text passwords in code or configuration.
  - Gate 5: ProGuard / R8 mapping file generation and obfuscation integrity.

## 4. Engineering Standards
- **Zero Hallucinated Approvals**: Every release check must be backed by verifiable terminal output, exit codes, and timestamps.
- **Immutable Checksums**: Release verification reports must publish exact SHA-256 digests and file sizes in bytes.
- **Sign-Off Authority**: A build that fails any single quality gate is rejected without exception.

## 5. Step-by-Step Execution Procedure
1. **Gate Evaluation**: Execute test suite (`./gradlew testDebugUnitTest`) and compile APK (`./gradlew assembleDebug`).
2. **Artifact Attestation**: Calculate SHA-256 checksum and exact byte size of compiled binaries.
3. **Configuration Audit**: Verify `AndroidManifest.xml` exports, permission declarations, and ProGuard rules.
4. **Handoff Documentation**: Record release attestation in operational release logs and notify platform leads.

## 6. Decision Tree for Ambiguous Cases
- *Can a build be approved if only 1 test out of 122 failed?*
  -> Absolutely not. Zero-defect test passage is mandatory for humanitarian life-safety applications.

