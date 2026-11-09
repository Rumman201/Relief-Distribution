---
name: performance-security-regression
description: Performance versus security verification, Phase 6 security invariant preservation, cache authorization testing, and regression prevention.
---

# Performance & Security Regression Skill

## 1. Purpose
Verify that all performance optimizations, caching layers, concurrency models, and battery-saving throttles strictly preserve all Phase 6 security, privacy, and zero-trust invariants without introducing authorization bypasses or cross-tenant data leaks.

## 2. Activation Criteria
Activate this skill when:
- Evaluating any performance optimization that alters data access, caching, or network paths.
- Running Phase 6 security regression test suites after performance modifications.
- Verifying cache key isolation, tenant scoping, and authorization checks.
- Ensuring input sanitization, rate limits, and safe dialers remain 100% active.

## 3. Project Context
- **Security Invariants (Phase 6)**:
  - Invariant 1: Zero plain-text passwords in databases, caches, or logs.
  - Invariant 2: Scoped permissions only (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `INTERNET`, `ACCESS_NETWORK_STATE`).
  - Invariant 3: Emergency calls must use `Intent.ACTION_DIAL`, never `Intent.ACTION_CALL`.
  - Invariant 4: Cleartext network traffic disabled (`cleartextTrafficPermitted="false"`).
  - Invariant 5: Internal activities remain unexported (`android:exported="false"`).
  - Invariant 6: Firebase security rules enforce strict server-side RBAC and role immutability.
  - Invariant 7: Android backup isolation excludes `sharedpref` and cached databases.
  - Invariant 8: Input sanitization via `SecuritySanitizer` is mandatory.

## 4. Engineering Standards
- **Zero Compromise Rule**: Performance gains that violate any security invariant are classified as critical regressions and immediately blocked.
- **Cache Authorization Parity**: Cached data must never be served to a requester who would be denied access by the underlying datastore rules.
- **Automated Verification**: Every release candidate must pass all 31 Phase 6 security unit tests with exit code 0.

## 5. Step-by-Step Execution Procedure
1. **Security Invariant Audit**:
   - Inspect every optimized file to verify no security checks were removed.
2. **Execute Security Suites**:
   - Run `SecuritySanitizerTest`, `DatabaseRulesSecurityTest`, `TelemetryReplaySecurityTest`, and `ZeroTrustAccessTest`.
3. **Document Verification**:
   - Record test pass status in `docs/upgrade/83-phase-7-security-regression.md`.

## 6. Decision Tree for Ambiguous Cases
- *Can we skip SecuritySanitizer input checks to shave 0.2ms off form submission?*
  -> Absolutely not. Security sanitization is mandatory to prevent XSS and injection attacks.
