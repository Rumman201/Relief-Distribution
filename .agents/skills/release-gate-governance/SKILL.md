---
name: release-gate-governance
description: Formal quality gate evaluation, release blocker tracking, decision rubric execution, and authoritative Go/No-Go release governance.
---

# Release Gate Governance Skill

## 1. Purpose
Govern the evaluation of the 30 Master Release Gates (Gates 01 through 30), adjudicating candidate qualification using empirical test data, static analysis, security audits, and operational criteria.

## 2. Activation Criteria
Activate this skill when:
- Evaluating release readiness against formal gate rubrics.
- Reviewing blocker registers, critical defects, and technical debt items.
- Rendering formal Go / Conditional Go / No-Go verdicts.
- Preparing release candidate sign-off documentation for stakeholders.

## 3. Project Context
- **Master Release Gates**: 30 quantitative and qualitative gates covering:
  - Architecture & Code Quality (Gates 01–06)
  - Testing & Verification (Gates 07–12)
  - Security, Privacy & Compliance (Gates 13–18)
  - Performance, Reliability & Scalability (Gates 19–24)
  - DevOps, Release & Operations (Gates 25–30)
- **Authority Scope**: Independent QA & Release Governance Organization.

## 4. Engineering Standards
- **Zero Ambiguity**: Each gate is strictly evaluated as `PASSED`, `FAILED`, or `WAIVED` with attached artifact proof.
- **Strict Disqualification**: Any unmitigated P0/Blocker defect mandates a `NO-GO` or `CONDITIONAL GO`.
- **Traceability**: All gate decisions reference concrete test outputs, git commits, and code files.

## 5. Execution Procedure
1. Aggregate evidence from all verification runs (unit tests, lint, security scans, APK digests).
2. Score candidate against all 30 Master Release Gates.
3. Record detailed evidence matrix in `177-phase-10-2-release-gate-matrix.md`.
4. Issue final readiness verdict in `178-phase-10-2-readiness-verdict.md`.
