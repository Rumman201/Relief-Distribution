---
name: security-regression-engineering
description: Continuous security verification, automated regression prevention, invariant testing, and security fitness gates across CI/CD and refactor cycles.
---

# Security Regression Engineering Skill

## 1. Purpose
Embed automated security invariants and regression tests into the development lifecycle, ensuring that refactorings, feature additions, and optimization phases never degrade established security controls.

## 2. Activation Criteria
Activate this skill when:
- Establishing security regression gates for pull requests or build pipelines.
- Verifying that architectural changes preserve least-privilege invariants.
- Writing automated fitness tests for permissions, exported components, and cryptographic configs.
- Preparing phase transitions (e.g., Phase 6 to Phase 7).

## 3. Project Security Context
- **Security Invariants**:
  - Invariant 1: No plain-text passwords stored anywhere.
  - Invariant 2: Scoped permissions only (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `INTERNET`, `ACCESS_NETWORK_STATE`).
  - Invariant 3: Emergency calling must use `Intent.ACTION_DIAL`, never `Intent.ACTION_CALL`.
  - Invariant 4: Cleartext network traffic disabled in `network_security_config.xml`.
  - Invariant 5: Only launcher activity has `android:exported="true"`.
  - Invariant 6: Firebase security rules enforce strict authentication and RBAC.

## 4. Engineering Standards
- **Automated Verification**: Every invariant must be covered by automated test assertions or static lint checks.
- **Zero Breakage Guarantee**: A build or test failure in a security invariant halts the release pipeline immediately.
- **Traceability**: All security test cases must link back to specific threat model entries or vulnerability IDs.

## 5. Step-by-Step Execution Procedure
1. **Regression Test Suites**:
   - Implement unit tests asserting domain invariants (input sanitization, role separation, coordinate validity).
2. **Build Gate Integration**:
   - Verify `.\gradlew.bat testDebugUnitTest` and `.\gradlew.bat assembleDebug` execute and pass cleanly.
3. **Phase Handoff Security Gates**:
   - Document immutable security constraints for subsequent engineering phases (e.g. Phase 7 performance tuning must not disable validation or re-enable cleartext traffic).

## 6. Decision Tree for Ambiguous Cases
- *Can a security test be disabled to unblock an urgent performance optimization?*
  -> Never. If an optimization violates a security invariant, the optimization design must be revised.
