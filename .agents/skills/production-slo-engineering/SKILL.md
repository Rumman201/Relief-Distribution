---
name: production-slo-engineering
description: User-centric SLIs, SLO design, error budget policies, latency percentiles, data freshness metrics, and operational performance monitoring.
---

# Production SLO & Reliability Engineering Skill

## 1. Purpose
Define, monitor, and enforce Service Level Indicators (SLIs), Service Level Objectives (SLOs), and Error Budget policies to ensure the Relief Distribution System provides reliable, resilient, and responsive service to field operations and survivors.

## 2. Activation Criteria
Activate this skill when:
- Establishing user-centric SLIs and SLOs for critical user journeys.
- Defining latency budgets, error rate thresholds, and data freshness targets.
- Designing operational monitoring metrics, telemetry dashboards, and alert thresholds.
- Calculating error budget burn rates and determining deployment readiness.

## 3. Project Context
- **Target Subsystems**:
  - Survivor Request Journey: SLI = Aid request submitted and acknowledged. SLO = 99.5% within 1500ms.
  - Distributor Location Broadcast: SLI = Fresh location recorded in RTDB. SLO = 99.0% freshness <= 10s.
  - Triage Evaluation: SLI = Urgency score computed. SLO = 99.9% within 10ms.
  - Inventory Claiming: SLI = Atomic reservation executed. SLO = 99.5% within 500ms.

## 4. Engineering Standards
- **Realistic Objectives**: Never set unattainable 100% availability SLOs; design for 99.0% to 99.9% targets with explicit error budgets.
- **Percentile-Based Latency**: Evaluate latency at p50, p95, and p99 percentiles rather than arithmetic averages.
- **Actionable Alerts**: Only trigger alerts when error budget consumption threatens SLO failure within a defined operational window.

## 5. Step-by-Step Execution Procedure
1. **SLI Formulation**:
   - Define precise ratio formulas: $\text{SLI} = \frac{\text{Good Events}}{\text{Total Valid Events}} \times 100\%$.
2. **SLO Target Setting**:
   - Align target percentages with real disaster operational requirements and battery/network constraints.
3. **Error Budget Policy**:
   - Define operational protocols when error budget is exhausted (freeze non-critical rollouts, prioritize reliability fixes).

## 6. Decision Tree for Ambiguous Cases
- *Is a client timeout considered an SLO failure?*
  -> Yes, if the timeout impacts an end-user workflow and prevents completion of the requested operation.
