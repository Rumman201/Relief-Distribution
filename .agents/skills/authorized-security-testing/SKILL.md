---
name: authorized-security-testing
description: Scoped security test automation, boundary fuzzing, negative authorization test cases, and vulnerability verification.
---

# Authorized Security Testing Skill

## 1. Purpose
Design, implement, and automate scoped security test cases to verify that defensive controls function as intended, access boundaries cannot be bypassed, and vulnerability remediations remain intact.

## 2. Activation Criteria
Activate this skill when:
- Creating or executing security regression unit tests in `app/src/test/java/`.
- Verifying fix effectiveness for reported security vulnerabilities.
- Testing authorization boundaries, input sanitizers, and validation routines.
- Performing negative testing (attempting illegal operations to verify graceful rejection).

## 3. Project Security Context
- **Testing Framework**: JUnit 4, Mockito.
- **Security Test Suites**:
  - `SecuritySanitizerTest.java`: Testing input sanitization against XSS, script injection, and payload flooding.
  - `DatabaseRulesSecurityTest.java`: Verifying simulated security rules logic and role separation.
  - `ZeroTrustAccessTest.java`: Verifying state transition enforcement and permission boundaries.
  - `TelemetryReplaySecurityTest.java`: Verifying anti-replay and telemetry bounds.

## 4. Engineering Standards
- **Zero Fabricated Results**: Every test assertion must be executed and verified with exit code 0.
- **Negative Testing Priority**: For every permitted action, test that unauthenticated or unauthorized users are denied.
- **Idempotent Tests**: Tests must run deterministically in isolated unit environments without network side effects.

## 5. Step-by-Step Execution Procedure
1. **Design Security Test Cases**:
   - Write tests for malicious inputs: strings with HTML tags (`<script>`), SQL markers (`' OR '1'='1`), oversized strings.
   - Write tests for role escalation: survivor trying to change status to `COMPLETED` or write to `Inventory`.
   - Write tests for telemetry spoofing: coordinates outside valid ranges (lat > 90, lon > 180).
2. **Execute and Validate**:
   - Run `.\gradlew.bat testDebugUnitTest` and inspect test results.
3. **Capture Evidence**:
   - Record test names, counts, and exit status in security test evidence records.

## 6. Decision Tree for Ambiguous Cases
- *Should security tests hit the live Firebase database?*
  -> No. Live database tests introduce flaky network dependencies and risk production data corruption. Security logic must be validated via hermetic unit tests simulating rules and client validators.
