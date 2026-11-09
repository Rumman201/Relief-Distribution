---
name: progressive-delivery-engineering
description: Phased release rings, staged rollout strategies, rollback automation, and feature flag containment.
---

# Progressive Delivery Engineering Skill

## 1. Purpose
Design, execute, and monitor staged deployment rings, canary rollouts, automated rollback criteria, and safe release mechanisms for Android application updates and cloud database schema transitions.

## 2. Activation Criteria
Activate this skill when:
- Defining release staging tracks (Internal Testing -> Closed Beta -> 5% Canary -> 20% -> 50% -> 100% General Availability).
- Establishing automated rollback criteria and health gates during staged rollout.
- Implementing runtime feature flags or configuration toggles for operational containment.
- Auditing client version skew and backward-compatible protocol migrations.

## 3. Project Context
- **Distribution Channel**: Google Play Console staged rollouts and direct enterprise/field APK distribution.
- **Rollout Rings**:
  - Ring 0 (Internal / Field Engineers): 100% immediate deployment for dogfooding.
  - Ring 1 (Canary Pilot Zones): 5% rollout to select relief coordinators.
  - Ring 2 (Broader Relief Operations): 25% staged release.
  - Ring 3 (General Availability): 100% full rollout.

## 4. Engineering Standards
- **Automated Rollback Gates**: An automated pause/halt must trigger if crash-free sessions drop below 99.5% or P0 incident is reported during canary.
- **Strict Backward Compatibility**: New app releases must gracefully handle existing database schemas, and old app releases must not crash on new schema fields.
- **Explicit Versioning**: Use semantic versioning (`MAJOR.MINOR.PATCH`) with monotonic integer `versionCode` for every release candidate.

## 5. Step-by-Step Execution Procedure
1. **Candidate Verification**: Confirm 100% test pass rate, APK signature, and ProGuard mapping retention.
2. **Ring Promotion**: Deploy binary to targeted release ring with monitoring active.
3. **Health Telemetry**: Continuously evaluate crash rates, connection stability, and user error rates.
4. **Halt / Rollback Protocol**: Immediately freeze or rollback distribution upon detecting anomalous failure rates.

## 6. Decision Tree for Ambiguous Cases
- *Can a staged rollout proceed if crash rates increase by 0.2% in the canary cohort?*
  -> No. Any statistically significant elevation in crash rates mandates an immediate release pause and root cause investigation.

