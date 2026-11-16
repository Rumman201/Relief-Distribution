---
name: cross-system-regression-engineering
description: Cross-service testing, contract compatibility, database integrity, authentication flows, performance benchmarks, AI/IoT edge behavior, and deployment verification.
---

# Cross-System Regression Engineering Skill

## 1. Purpose & Scope
Orchestrates automated full-system regression verification across all integrated modules to guarantee that modifications in one layer (e.g. backend automator) do not degrade guarantees in another layer (e.g. UI rendering, cache invalidation, security rules).

## 2. Invariants Under Verification
- **Security Invariants**: Zero plaintext passwords, scoped Android permissions, default-deny RBAC.
- **Performance Invariants**: AI triage latency < 1ms warm, cache hit ratio > 80%, bounded thread pools.
- **Data Integrity Invariants**: Non-negative physical stock, valid geo-coordinates ($-90 \le lat \le 90$, $-180 \le lng \le 180$), atomic transitions.
- **Operational Invariants**: Android Lint 0 errors, reproducible build, SHA-256 binary attestation.

## 3. Verification Protocol
1. Execute unit test harness: `./gradlew.bat testDebugUnitTest`.
2. Execute static analysis: `./gradlew.bat lintDebug`.
3. Assemble release candidate: `./gradlew.bat assembleDebug`.
4. Attest cryptographic checksums of generated APK binaries.
5. Record benchmark timings and memory footprints.
