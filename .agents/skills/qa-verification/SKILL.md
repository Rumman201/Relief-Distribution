---
name: qa-verification
description: Test strategy, unit testing with JUnit4/5, Mockito, Robolectric, instrumented UI testing with Espresso, and regression protection.
---

# QA & Test Automation Skill

## 1. Purpose
Design and execute automated testing strategies for Android native components, business logic, data models, and UI workflows to guarantee zero regressions and deterministic quality gates.

## 2. Activation Criteria
Activate this skill when:
- Writing or executing unit, integration, or UI automation tests.
- Verifying bug fixes and foundational improvements in Phase 1 through 10.
- Creating test doubles, mocks, and simulated Firebase responses.
- Running regression test suites before marking any phase complete.

## 3. Project Context
- **Baseline Test Suite**: Only 1 default boilerplate test (`ExampleUnitTest.java` testing `2 + 2 == 4`).
- **Zero Real Test Coverage**: Core business logic (role validation, registration validation, coordinates formatting, session parsing) has 0% automated test coverage.
- **Testing Tools**: JUnit 4, AndroidX Test Runner, Espresso Core.

## 4. Specialist Engineering Standards
- **Test Pyramid**:
  - 70% Fast Unit Tests: Domain logic, model validation, utility functions (runs on host JVM in < 5 seconds).
  - 20% Integration / Repository Tests: Mockito/MockK verifying Firebase abstraction layers, state transitions.
  - 10% Instrumented / UI Tests: Espresso testing core screen workflows and navigation.
- **Deterministic & Isolated**: Unit tests must never depend on external network access, live Firebase backends, or real GPS hardware.
- **Fail Fast & Clear**: Assertions must contain informative failure messages explaining expected vs actual behavior.

## 5. Step-by-Step Execution Procedure
1. **Identify Testable Logic**: Extract pure business logic from Activities into testable helper classes or Use Cases (e.g., `InputValidator`, `UserRoleHelper`).
2. **Implement Unit Tests**: Create JUnit test cases covering positive, negative, and edge cases (empty strings, invalid emails, passwords mismatched, boundary coordinates).
3. **Run Test Suites**: Execute `./gradlew.bat testDebugUnitTest` and capture pass/fail counts and execution times.
4. **Instrumented Testing**: Add Espresso tests for critical user journeys (Login -> Dashboard routing).

## 6. Decision Tree for Ambiguous Cases
- *Test with Robolectric or Pure JUnit?*
  -> Prefer pure JUnit tests with mocked interfaces for speed (<100ms). Use Robolectric only when testing Android framework dependencies (e.g. Parcelable, Context, Intent).

## 7. Tool Instructions
- Run unit tests: `.\gradlew.bat testDebugUnitTest`.
- Generate HTML test report: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Run Android Lint: `.\gradlew.bat lint`.

## 8. Safety Constraints
- Never commit flaky tests with arbitrary `Thread.sleep()` delays; use deterministic synchronization (e.g., `CountDownLatch` or IdlingResource).
- Never report a test as passed without executing it and verifying exit code 0.

## 9. Verification Steps
- Verify test passes across clean builds: `./gradlew.bat clean testDebugUnitTest`.
- Validate error paths: Ensure invalid inputs fail assertions reliably.

## 10. Expected Output & Evidence Format
- Precise test execution summary (Tests Run, Passed, Failed, Duration).
- Regression reports in `docs/upgrade/04-baseline-metrics.md` and `docs/upgrade/06-phase-1-execution.md`.

