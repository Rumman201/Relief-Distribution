---
name: slo-alerting-and-incident-response
description: Multi-window burn-rate alerting, automated pager triggers, severity classification, and operational incident playbooks.
---

# SLO Alerting and Incident Response Skill

## 1. Purpose
Define, configure, and maintain operational alerting rules, multi-window error budget burn rate calculations, severity triage matrices, and standard incident response protocols for humanitarian relief operations.

## 2. Activation Criteria
Activate this skill when:
- Establishing automated alerts based on SLI/SLO breaches and error budget consumption rates.
- Classifying operational incidents (P0 Critical, P1 Major, P2 Moderate, P3 Low).
- Designing on-call escalation policies, communication cadences, and responder runbooks.
- Conducting post-incident retrospectives and authoring root cause analysis (RCA) reports.

## 3. Project Context
- **System SLA**: 99.5% operational availability for critical disaster relief coordination workflows.
- **Critical Paths**:
  - P0: Complete cloud outage, database unreachable, or aid submission failure affecting >5% of users.
  - P1: AI triage degradation, distributor GPS tracking latency > 60s, or partial auth failures.
  - P2: Non-critical UI glitch, slow inventory filtering, or delayed push notifications.
  - P3: Minor cosmetic defect or non-blocking documentation issue.

## 4. Engineering Standards
- **Burn-Rate Alerting**: Alert on rate of error budget consumption (e.g., 14.4x burn rate over 1 hour consumes 2% of budget in 1 hour) to eliminate alert fatigue.
- **Actionable Notification**: Every alert must include direct links to relevant runbooks, diagnostic dashboards, and rollback triggers.
- **Blameless Retrospectives**: Post-incident reviews must focus on systemic improvements, guardrails, and architectural defenses rather than human fault.

## 5. Step-by-Step Execution Procedure
1. **Budget Calculation**: Determine monthly error budget based on agreed SLO targets.
2. **Alert Rule Authoring**: Configure Prometheus/Cloud Monitoring multi-window multi-burn-rate alerts.
3. **Escalation Path Definition**: Map incident severity levels to primary and secondary on-call responders.
4. **Runbook Execution**: Follow step-by-step verification, mitigation, and post-incident documentation procedures.

## 6. Decision Tree for Ambiguous Cases
- *When should an incident be declared P0?*
  -> When relief survivors cannot submit requests or coordinators cannot dispatch aid across multiple disaster zones.

