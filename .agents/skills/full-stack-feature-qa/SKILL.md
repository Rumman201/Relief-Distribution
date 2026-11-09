---
name: full-stack-feature-qa
description: End-to-end verification, unit test coverage, AI quality benchmarks, automation failure testing, and regression protection for mobile applications.
---

# Full-Stack Feature QA Skill

## 1. Purpose
Define comprehensive verification protocols for end-to-end feature completeness, domain unit testing, failure injection, and automated quality gates.

## 2. Activation Criteria
Activate this skill when:
- Writing or executing unit tests for domain models, repositories, and ViewModels.
- Validating end-to-end user journeys (Survivor Request -> Distributor Acceptance -> Delivery Completion).
- Verifying regression resistance after refactoring or feature additions.

## 3. Engineering Invariants & Standards
1. **Zero Fabricated Results**: Every test claim must be substantiated by real executed commands observing exit code 0.
2. **Boundary & Edge Testing**: Tests must explicitly cover null values, boundary extremes, invalid states, and network exceptions.
3. **Execution Invariants**: All tests must run locally via `./gradlew.bat testDebugUnitTest` and compile via `./gradlew.bat assembleDebug`.
4. **Clean Assertions**: Use descriptive assertion messages and verify both return values and side effects.

## 4. Execution Procedure
1. **Identify Critical Paths**: Map domain logic, state machines, and calculations.
2. **Author Test Suites**: Implement JUnit4 test classes testing positive, negative, and edge-case inputs.
3. **Execute Automated Suites**: Run `./gradlew.bat testDebugUnitTest` and review test reports.
4. **Record Evidence**: Log test execution counts, run durations, and results in `docs/upgrade/45-phase-5-test-evidence.md`.
