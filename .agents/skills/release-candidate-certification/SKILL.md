---
name: release-candidate-certification
description: Release candidate qualification, cryptographic checksum attestation, quality gate sign-off, and objective go/no-go recommendation.
---

# Release Candidate Certification Skill

## 1. Purpose
Conduct independent, evidence-backed evaluation of release candidates, assess compliance across Quality Gates A through R, verify cryptographic artifact digests, review residual risks, and author the definitive Go/No-Go release certification.

## 2. Activation Criteria
Activate this skill when:
- Evaluating a candidate build for promotion to staging, Google Play Internal Testing, or production.
- Auditing all automated test results, lint reports, and security scans for release-blocking defects.
- Calculating cryptographic digests (SHA-256) and artifact byte counts for candidate APKs/AABs.
- Formulating the final objective Go / Conditional Go / No-Go recommendation.

## 3. Project Context
- **Candidate Artifact**: `app-debug.apk` and `app-release.apk` compiled from target Git commit.
- **Mandatory Release Gates**:
  - Gate A to Gate R: 100% test pass rate, 0 lint errors, valid SHA-256 digest, APK <= 15 MB, 0 unresolved P0/Critical defects.

## 4. Engineering Standards
- **Zero Hallucinated Approvals**: Every quality gate must be substantiated with verified command logs and exit code 0.
- **Strict Go/No-Go Criteria**: A candidate with an open Blocker or Critical bug is immediately disqualified with a **NO-GO** ruling.
- **Non-Authorization Rule**: A **GO** recommendation certifies technical fitness but does NOT constitute authorization to deploy live to production.

## 5. Step-by-Step Execution Procedure
1. **Candidate Identity Inscription**: Record commit SHA, branch, version code, artifact size, and SHA-256 checksum.
2. **Quality Gate Auditing**: Audit evidence across Gates A through R.
3. **Residual Risk Assessment**: Document any low-priority residual concerns with mitigation plans.
4. **Certification Publishing**: Publish the final Release Candidate Assessment in `126-phase-9-release-candidate.md`.

## 6. Decision Tree for Ambiguous Cases
- *Can a candidate with 1 low-priority cosmetic defect receive a GO recommendation?*
  -> Yes, provided all P0 and Critical gates pass and the minor defect is tracked in the backlog for Phase 10.

