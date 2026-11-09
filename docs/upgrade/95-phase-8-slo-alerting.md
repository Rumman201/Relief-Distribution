# Phase 8 — Multi-Window SLO Monitoring, Error Budgets & Alerting Rules

## 1. Service Level Indicators & Objectives (SLIs/SLOs)

Operational reliability targets are defined around four critical humanitarian journeys:

| Journey / Critical Path | Service Level Indicator (SLI) Formula | Monthly Target (SLO) | Monthly Error Budget |
| :--- | :--- | :--- | :--- |
| **Survivor Aid Submission** | $\frac{\text{Successful Requests}}{\text{Total Valid Requests}} \times 100\%$ | **99.5%** | 0.5% (approx. 216 min/month) |
| **Distributor GPS Stream** | $\frac{\text{Telemetry Packets Ingested} \le 10\text{s}}{\text{Total Telemetry Packets}} \times 100\%$ | **99.0%** | 1.0% |
| **Inventory Atomic Reservation**| $\frac{\text{Stock Claims Executed} \le 500\text{ms}}{\text{Total Stock Mutations}} \times 100\%$ | **99.5%** | 0.5% |
| **AI Triage Prioritization** | $\frac{\text{Triage Evaluations} \le 10\text{ms}}{\text{Total Triage Inferences}} \times 100\%$ | **99.9%** | 0.1% |

---

## 2. Multi-Window Multi-Burn-Rate Alerting Strategy

Rather than relying on noisy single-threshold alerts, alerting rules evaluate the rate of error budget consumption across short and long time windows (Google SRE standard):

```mermaid
graph TD
    MON["Error Budget Monitor"] -->|14.4x Burn (1h / 5m window)| PAGER["PagerDuty P0: Pager Alert (2% budget consumed in 1h)"]
    MON -->|6x Burn (6h / 30m window)| TICKET["Jira/Slack P1: Ticket Alert (5% budget consumed in 6h)"]
    MON -->|1x Burn (3d / 3h window)| NOTICE["Daily Digest P2: Warning (10% budget consumed in 3d)"]
```

### Alerting Rules Matrix:
1. **Critical P0 Alert (Immediate Page)**:
   - **Condition**: 1-hour burn rate >= 14.4 AND 5-minute burn rate >= 14.4.
   - **Impact**: System will exhaust 100% of error budget in ~50 hours if unaddressed.
   - **Action**: Immediate on-call engineer dispatch; initiate incident triage within 15 minutes.
2. **Major P1 Alert (High Priority)**:
   - **Condition**: 6-hour burn rate >= 6.0 AND 30-minute burn rate >= 6.0.
   - **Impact**: System will exhaust error budget in 5 days.
   - **Action**: Response required within 1 business hour.

