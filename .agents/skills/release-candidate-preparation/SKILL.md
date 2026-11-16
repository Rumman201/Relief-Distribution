---
name: release-candidate-preparation
description: Release candidate versioning, binary cryptographic attestation, build verification, known issue tracking, scope freeze, and independent Phase 10.2 handoff preparation.
---

# Release Candidate Preparation Skill

## 1. Purpose & Scope
Governs the assembly, cryptographic attestation, build reproducibility, and scope freeze of the final release candidate package (`1.0.0-rc2` / `1.0.0-final-candidate`), preparing an unambiguous, verified handoff for independent Phase 10.2 verification.

## 2. Invariants & Acceptance Gates
- **Compilation Gate**: `compileDebugJavaWithJavac` exit code 0.
- **Unit Test Gate**: 100% pass rate across all test suites, 0 failures, 0 ignored.
- **Lint Gate**: Android Lint zero fatal errors (`lintDebug` exit code 0).
- **Packaging Gate**: `assembleDebug` exit code 0.
- **Attestation Gate**: Cryptographic SHA-256 digest calculated and published alongside byte size.
- **Traceability Gate**: Commit SHA, git status, environment variables, and build tools explicitly logged.

## 3. Step-by-Step Procedure
1. Execute clean build: `./gradlew.bat clean assembleDebug`.
2. Generate SHA-256 checksum of generated APK artifact.
3. Inventory all modified files, unresolved medium/low risks, and operational prerequisites.
4. Prepare Phase 10.2 verification test scripts and independent audit instructions.
