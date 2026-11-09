---
name: software-supply-chain-security
description: Dependency risk assessment, Gradle build provenance, ProGuard/R8 code hardening, CI/CD pipeline integrity, and artifact verification.
---

# Software Supply Chain Security Skill

## 1. Purpose
Protect the project's software supply chain, prevent dependency confusion and compromised third-party libraries, harden release artifacts via R8/ProGuard, and secure CI/CD build pipelines.

## 2. Activation Criteria
Activate this skill when:
- Adding, updating, or reviewing dependencies in `build.gradle.kts` and `libs.versions.toml`.
- Configuring ProGuard / R8 rules (`app/proguard-rules.pro`).
- Modifying GitHub Actions or build scripts.
- Auditing third-party licenses and known CVEs.

## 3. Project Security Context
- **Build System**: Gradle 8.9 (Kotlin DSL), Android Gradle Plugin 8.7.3.
- **Repository Sources**: Google Maven (`google()`), Maven Central (`mavenCentral()`).
- **Obfuscation**: R8 code shrinker and optimizer.
- **CI/CD**: GitHub Actions workflow (`.github/workflows/android.yml`).

## 4. Engineering Standards
- **Explicit Version Pinning**: Use typed Gradle version catalogs (`libs.versions.toml`) with deterministic version numbers. Never use dynamic `+` versions.
- **Trusted Repositories Only**: Disallow unverified HTTP or unofficial third-party Maven repositories.
- **Code Hardening & Log Stripping**: R8 must strip all logging calls (`android.util.Log`) in release builds and obfuscate class/method names while retaining necessary serialization models.
- **Reproducible Builds**: Guarantee consistent compilation from clean checkout.

## 5. Step-by-Step Execution Procedure
1. **R8/ProGuard Hardening**:
   - Add keep rules for data models (`com.example.reliefdistribution.data.model.**`, `User`, `ReliefRequest`, `InventoryItem`, `Notification`).
   - Add rules to strip `android.util.Log` calls in release configuration.
2. **Dependency Audit**:
   - Verify all library dependencies are from official `com.google.*` or `androidx.*` repositories.
3. **Pipeline Verification**:
   - Ensure CI build runs unit tests and static checks without exposing deployment secrets.

## 6. Decision Tree for Ambiguous Cases
- *Should ProGuard obfuscate Firebase data models?*
  -> No. Obfuscating field names breaks Firebase reflection deserialization (`snapshot.getValue(Class)`). Use `@Keep` or `-keepclassmembers` for data model fields.
