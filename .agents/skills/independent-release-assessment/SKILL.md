---
name: independent-release-assessment
description: Independent adversarial quality evaluation, cross-phase verification, objective release challenge, and evidence-backed qualification for production release candidates.
---

# Independent Release Assessment Skill

## 1. Purpose
Provide objective, adversarial, and evidence-driven evaluation of candidate builds, operating independently from the implementation and remediation engineers to rigorously challenge fitness for production staging.

## 2. Activation Criteria
Activate this skill when:
- Conducting independent qualification of candidate builds (e.g. `1.0.0-rc2`).
- Evaluating cross-phase claims from architecture, security, performance, DevOps, and full-stack implementation.
- Auditing defect closures, regression risks, and unresolved issues against production standards.
- Challenging optimistic assessments and enforcing evidentiary verification.

## 3. Project Context
- **Candidate Release**: Relief Distribution System (`com.example.reliefdistribution`) v1.0.0-rc2.
- **Evaluation Paradigm**: Independent adversarial assessment (verify claims with reproducible commands, never accept unverified statements).
- **Core Verification Pillars**:
  1. Automated test suite execution (`testDebugUnitTest`).
  2. Static analysis and hygiene verification (`lintDebug`).
  3. Artifact integrity, size budget, and cryptographic signature (`assembleDebug`).
  4. Operational stability, zero plain-text secrets, and lifecycle safety.

## 4. Engineering Standards
- **Zero Fabrication**: Every finding, status, and verdict must be substantiated with verified execution logs, exit codes, and repository file links.
- **Skeptical Invariant Verification**: Actively search for regressions, unhandled edge cases, and runtime leaks.
- **Independence Principle**: Do not defend design shortcuts; report all gaps objectively in the defect register.

## 5. Execution Procedure
1. Verify candidate build immutability and Git commit state.
2. Re-run complete automated verification pipelines independently.
3. Challenge all feature workflows across roles (Survivor, Distributor, Admin).
4. Verify defect closures against regression test cases.
5. Record independent findings in Phase 10.2 release documentation suite (`docs/upgrade/155-181`).
