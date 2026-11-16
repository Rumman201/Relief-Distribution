---
name: production-readiness-review
description: Comprehensive production readiness assessment, non-functional requirement validation, scalability inspection, and release prerequisite qualification.
---

# Production Readiness Review Skill

## 1. Purpose
Evaluate the full-stack system against rigorous production deployment prerequisites, inspecting architecture fitness, infrastructure scalability, disaster recovery plans, operational telemetry, and deployment risk boundaries.

## 2. Activation Criteria
Activate this skill when:
- Conducting final operational and production readiness reviews.
- Auditing Firebase service configurations, index definitions, and rate limits.
- Evaluating memory budgets, CPU consumption, thread bounding, and battery impact.
- Assessing incident response procedures, observability dashboards, and alert policies.

## 3. Project Context
- **System Stack**: Android (Java 17 / SDK 34), Firebase Auth, Firebase Realtime Database, Google Play Services Location & Maps.
- **Architectural Constraints**: Clean Architecture / MVVM, ViewBinding, Repository pattern, `AppExecutors` thread pooling, `ReliefDataCache` LRU caching.
- **Operational Baselines**: P95 UI thread latency < 16ms (60 FPS), background GPS duty cycle throttling, cold start < 1.2s.

## 4. Engineering Standards
- **Exhaustive Review**: Cover all non-functional requirements (security, reliability, performance, privacy, observability).
- **Failure Anticipation**: Validate behavior under network degradation, Firebase outages, and memory pressure.
- **Clear Governance**: Document operational readiness criteria in `178-phase-10-2-readiness-verdict.md`.

## 5. Execution Procedure
1. Inspect Firebase Realtime Database indexes and security rules.
2. Review thread pool bounds, LRU cache eviction policies, and memory leak safeguards.
3. Validate telemetry and crash reporting hooks.
4. Verify deployment runbooks and rollback procedures.
