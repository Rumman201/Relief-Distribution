# Phase 8 — Secure CI/CD Engineering & Pipeline Automation

## 1. Executive Summary

This document specifies the architecture, security hardening, automated quality gates, and provenance attestation of the Continuous Integration and Continuous Delivery (CI/CD) pipelines for the Relief Distribution System (`com.example.reliefdistribution`).

---

## 2. GitHub Actions Pipeline Specification

The CI/CD pipeline is implemented in `.github/workflows/android.yml` with the following architectural controls:

1. **Explicit Principle of Least Privilege**:
   ```yaml
   permissions:
     contents: read
   ```
   Ensures that compromised dependencies or injection vectors cannot write to repository branches or alter pull request states without authorization.

2. **Superseded Run Cancellation**:
   ```yaml
   concurrency:
     group: ${{ github.workflow }}-${{ github.ref }}
     cancel-in-progress: true
   ```
   Conserves compute runners and prevents race conditions by canceling stale pipeline runs when new commits are pushed.

3. **Multi-Stage Verification Sequencing**:
   - **Stage 1 (Static Analysis & Lint)**: Runs `./gradlew lintDebug --stacktrace` verifying XML layout attributes, AppCompat tint conformance, and Android API safety.
   - **Stage 2 (Hermetic Unit Testing)**: Executes all 124 unit, security, concurrency, performance, and observability tests (`./gradlew testDebugUnitTest --stacktrace`).
   - **Stage 3 (Binary Compilation)**: Assembles the debug APK binary (`./gradlew assembleDebug --stacktrace`).
   - **Stage 4 (Cryptographic Attestation)**: Calculates SHA-256 digest (`sha256sum`) and outputs checksum verification file.
   - **Stage 5 (Secure Archival)**: Uploads compiled APK, attestation checksum, and detailed test/lint reports to GitHub Actions artifact storage.

---

## 3. Hermetic Build & Caching Strategy

- **JDK Distribution**: Eclipse Temurin OpenJDK 17 with automated Gradle caching (`cache: gradle`).
- **Dependency Locking**: All library coordinates and plugins are declared in `gradle/libs.versions.toml`, guaranteeing deterministic resolution.
- **Fail-Closed Execution**: Any lint error or single test failure halts pipeline execution immediately with exit code 1, preventing defective artifacts from advancing.

