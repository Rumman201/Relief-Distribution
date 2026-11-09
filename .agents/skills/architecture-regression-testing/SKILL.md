---
name: architecture-regression-testing
description: Architecture invariant validation, structural fitness testing, ViewModel characterization tests, and regression prevention.
---

# Architecture Regression Testing Skill

## 1. Purpose
Define automated testing patterns to verify that architectural boundaries are respected, ViewModels correctly manage UI state, and refactored components preserve all existing functional behavior.

## 2. Activation Criteria
Activate this skill when:
- Writing unit tests for ViewModels, Repositories, and domain use cases.
- Mocking asynchronous callback dependencies with Mockito.
- Verifying LiveData emissions using `InstantTaskExecutorRule`.

## 3. Engineering Standards
- **Host JVM Execution**: Tests must run on the local development JVM without needing an Android emulator or device (`testDebugUnitTest`).
- **Synchronous LiveData Testing**: Use `androidx.arch.core.executor.testing.InstantTaskExecutorRule` so LiveData updates occur synchronously during test execution.
- **Mock Verification**: Verify that Repositories invoke expected Firebase methods and correctly handle success and error responses.

