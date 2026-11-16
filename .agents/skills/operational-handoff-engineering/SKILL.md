---
name: operational-handoff-engineering
description: Production operational readiness transfer, deployment runbook authoring, rollback procedures, monitoring alert configuration, and operations team enablement.
---

# Operational Handoff Engineering Skill

## 1. Purpose
Formulate comprehensive operational handoff packages, deployment playbooks, rollback procedures, configuration guides, and monitoring playbooks to ensure production engineering and operations teams can smoothly operate, maintain, and support the application.

## 2. Activation Criteria
Activate this skill when:
- Preparing the production handoff dossier for release candidates.
- Authoring release cutover runbooks, deployment checklists, and staged rollout plans.
- Defining emergency rollback triggers, database snapshot restoration, and traffic diversion procedures.
- Documenting telemetry metrics, alert thresholds, and incident escalation protocols.

## 3. Project Context
- **Deployment Platform**: Google Play Console (Internal Testing -> Closed Beta -> Phased Production Rollout 10% / 25% / 50% / 100%).
- **Backend Services**: Firebase Realtime Database (`us-central1`), Firebase Authentication, Google Play Services.
- **Rollback SLA**: < 15 minutes to revert traffic or halt phased staged rollout in Google Play Console.
- **Support Documentation**: `180-phase-10-2-operations-handoff.md`, `170-phase-10-2-rollback-recovery.md`.

## 4. Engineering Standards
- **Exhaustive Runbooks**: Every deployment step must have an explicit verification check and rollback trigger.
- **Zero Ambiguity Contacts**: Clear escalation paths, role responsibilities, and emergency contact lists.
- **Continuous Observability**: Operational dashboards and alert playbooks defined prior to release.

## 5. Execution Procedure
1. Define pre-deployment verification checklist.
2. Draft step-by-step phased rollout runbook with halt/abort triggers.
3. Establish emergency database restore and rollback protocols.
4. Document operational monitoring and alerting thresholds.
5. Inscribe handoff records in `180-phase-10-2-operations-handoff.md`.
