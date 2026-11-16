---
name: final-architecture-and-risk-review
description: Architecture integrity, technical debt risk assessment, security invariant preservation, operational impact analysis, and Architecture Decision Record governance.
---

# Final Architecture & Risk Review Skill

## 1. Purpose & Scope
Provides governance for comprehensive system-level architectural reviews, dependency direction audits, technical debt categorization, and risk register management across all 10 transformation phases.

## 2. Invariants & Responsibilities
- **Clean Architecture Preservation**: No business logic leaks into ViewBinding presentation layers; data sources remain isolated behind repositories.
- **Risk Classification**: Classify residual risks strictly by Severity (P0-P3) and Risk Impact, documenting mitigation strategies in `docs/upgrade/07-risk-register.md` and `docs/upgrade/150-phase-10-1-risk-register.md`.
- **ADR Governance**: Author comprehensive Architecture Decision Records (ADR-001 through ADR-035+) capturing Context, Decision, and Consequences.
- **Zero Technical Debt Regressions**: Remove obsolete abstractions, dead code, and fragile imports without introducing breaking changes.

## 3. Step-by-Step Procedure
1. Scan project packages: `com.example.reliefdistribution.{data, ui, util, resilience, cache, concurrency, ai, automation, iot}`.
2. Review dependency graph for circular references or leaky abstractions.
3. Validate residual risks against mitigation evidence.
4. Record formal architectural decisions in ADR logs.
