---
name: autonomous-defect-remediation
description: Automated bug reproduction, root-cause analysis, minimal corrective changes, regression test creation, and defect closure.
---

# Autonomous Defect Remediation Skill

## 1. Purpose
Reproduce confirmed defects, perform rigorous root-cause analysis, author minimal behavior-preserving code corrections, create dedicated automated regression tests, and verify defect resolution without introducing unintended regressions.

## 2. Activation Criteria
Activate this skill when:
- A test failure, lint defect, or edge-case vulnerability is confirmed.
- Developing reproduction unit test cases that capture the failing condition.
- Authoring focused, targeted code patches to resolve defects.
- Retesting the entire suite to verify that fixes do not compromise neighboring subsystems.

## 3. Project Context
- **Codebase**: Android Jetpack architecture, ViewBinding, Firebase client repositories, multi-tier caches, and utility sanitizers.
- **Remediation Protocol**: Reproduce -> Isolate Root Cause -> Author Minimal Fix -> Add Regression Test -> Full Suite Retest -> Defect Closure.

## 4. Engineering Standards
- **Reproduction First**: Every high-priority defect must have a reproducible test case demonstrating the failure prior to implementing the fix.
- **Minimal Code Changes**: Avoid broad refactorings or speculative restructuring; apply the minimal effective patch.
- **Mandatory Regression Protection**: The fix must be permanently protected by an automated unit or integration test.

## 5. Step-by-Step Execution Procedure
1. **Defect Characterization**: Document exact steps to reproduce, expected result, and actual error.
2. **Regression Test Authoring**: Write a test that fails due to the defect.
3. **Targeted Code Correction**: Implement the fix adhering to existing design patterns and Clean Architecture boundaries.
4. **Verification & Retest**: Execute the reproduction test (must pass) followed by the complete test suite.

## 6. Decision Tree for Ambiguous Cases
- *Should production code be changed if a test assertion is incorrect?*
  -> No. If the test assertion expects flawed behavior, correct the test with documented justification.

