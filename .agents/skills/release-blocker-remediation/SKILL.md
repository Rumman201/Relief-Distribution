---
name: release-blocker-remediation
description: Defect verification, root-cause analysis, severity classification, minimal safe patch design, regression test creation, and closure evidence for release-blocking defects.
---

# Release Blocker Remediation Skill

## 1. Purpose & Scope
Provides an evidence-based protocol for identifying, analyzing, fixing, and verifying P0/P1 defects that prevent release candidate promotion.

## 2. Remediation Protocol
1. **Reproduce**: Write a deterministic failing unit test proving the defect exists.
2. **Root-Cause Analysis**: Identify the precise boundary, off-by-one, type-mismatch, or concurrency fault causing the failure.
3. **Minimal Safe Fix**: Implement the smallest possible, backward-compatible code modification that cures the root cause without side effects.
4. **Targeted Verification**: Run the unit test to verify exit code 0 and green assertion.
5. **Neighboring Impact**: Test adjacent modules and regression suites to ensure no secondary regressions.
6. **Closure Evidence**: Document in the remediation register with file paths, commit hashes, and test execution timings.

## 3. Severity Criteria
- **P0 Release Blocker**: Security breach, data loss, stock corruption, crash on main user path.
- **P1 High Impact**: Core workflow failure, broken accessibility, unhandled network exception.
- **P2 Medium Impact**: Edge case boundary, performance sub-optimal, visual glitch.
- **P3 Low Impact**: Code style, minor logging, non-functional cosmetic issue.
