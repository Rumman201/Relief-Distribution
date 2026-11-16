---
name: final-system-reconciliation
description: Cross-phase findings reconciliation, conflicting decision resolution, unfinished work discovery, requirements traceability, and canonical issue backlog governance for Relief Distribution System.
---

# Final System Reconciliation Skill

## 1. Purpose & Scope
This skill provides comprehensive procedures to reconcile all engineering findings, architectural decisions, defect registers, performance baselines, security postures, and release blockers from Phases 1 through 9. It creates an authoritative single source of truth for Phase 10.1 and 10.2.

## 2. Operating Principles
- **Cross-Phase Traceability**: Trace every defect, invariant, and architecture decision to its originating phase.
- **Evidence-Based Grounding**: No claim is considered true without verifiable implementation and test evidence.
- **Deduplication**: Aggregate identical root causes across different symptom descriptions into a single authoritative issue ID.
- **Strict Invariant Preservation**: Enforce Phase 6 security, Phase 7 performance, and Phase 8 operational guarantees.

## 3. Step-by-Step Procedure
1. **Inventory Handoffs**: Ingest handoffs `docs/upgrade/09`, `16`, `25`, `35`, `49`, `66`, `85`, `106`, and `129`.
2. **Compile Master Issue Register**: Collect open defects, unverified claims, and partial implementations.
3. **Validate in Codebase**: Cross-reference against current source code and verify whether issues exist or were resolved.
4. **Prioritize Impact**: Assign strict priorities: P0 (Release Blocker), P1 (High Impact), P2 (Medium), P3 (Low).
5. **Assign Ownership**: Assign each issue to a principal engineering domain (Backend, DB, Security, Mobile, AI/IoT).
6. **Formulate Verification Matrix**: Define acceptance criteria and regression test mandates for every issue.

## 4. Decision Tree
- *If* a defect affects physical stock counts, user authentication, or security boundaries -> **P0 Release Blocker**.
- *If* an issue causes UI truncation, layout clipping, or talkback failure -> **P1 High Impact**.
- *If* an issue is cosmetic or minor technical debt -> **P3 Low Impact**.
