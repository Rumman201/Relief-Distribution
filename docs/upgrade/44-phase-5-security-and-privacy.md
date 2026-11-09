# Phase 5 — AI Security, Privacy & Trust Boundaries

**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: 5 of 10  
**Date**: 2026-11-09  
**Status**: ACTIVE  

---

## 1. Threat Modeling for Advanced Features & AI

Phase 5 introduced automated triage, state machines, and third-party location integrations. A systematic threat assessment was conducted across each new capability:

| Threat Vector | Affected Capability | Potential Impact | Implemented Mitigation |
| :--- | :--- | :--- | :--- |
| **Prompt Injection / Text Manipulation** | `ReliefTriageAdvisor` | Attacker embeds adversarial prompt text in notes attempting to lower priority or corrupt database | Deterministic NLP tokenization; prompt text is never executed as instruction; strict schema guardrails reject malformed outputs. |
| **Autonomous Action Abuse** | `ReliefWorkflowAutomator` | Agent or automated loop triggers unauthorized status transitions or stock deletion | State machine strictly enforces `isValidTransition()`; consequential actions (accepting/completing requests) require authenticated user action. |
| **PII Leakage in Telemetry** | `LocationTelemetry`, `OfflineTelemetryBuffer` | Field telemetry broadcasts expose personal phone numbers or user passwords | Telemetry payload is strictly bounded to `userId`, `latitude`, `longitude`, `timestamp`, `accuracy`, and `speed`. |
| **Privileged Telephony Exploitation** | Emergency Calling | Malicious or hijacked activity initiates unauthorized calls to premium numbers | Emergency calling is locked strictly to `Intent.ACTION_DIAL` (`tel:999`), requiring physical user confirmation on system dialer. |
| **API Key Exposure** | Google Maps SDK | Google Maps API key leaked in version control | Key loaded exclusively from `local.properties` via Gradle manifest placeholder; template provided in `local.properties.example`. |
| **Denial of Service via Telemetry Flood** | Mobile Edge Tracking | Continuous high-frequency GPS fixes exhaust battery and cloud quota | Adaptive GPS sampling intervals ($5\text{s}$ to $30\text{s}$) and local FIFO ring buffer ($100$ cap) prevent queue runaway. |

---

## 2. Instruction and Data Separation

The AI Triage Advisor explicitly enforces separation between system rules and user-supplied data:
- User notes and item descriptions are treated strictly as **untrusted passive text data**.
- Text is scanned for dictionary-bound distress keywords (e.g. `trapped`, `infant`, `injured`), which only modulate integer weights ($+15$, $+20$, $+25$, $+35$).
- No user-supplied text can override the base category weight, force a priority tier directly, or alter application logic.

---

## 3. Human-in-the-Loop Approval Safeguards

Consequential actions in the disaster response cycle are gated by explicit human authorization:
1. **Request Acceptance**: Only an authenticated distributor can claim a relief request.
2. **Delivery Confirmation**: Only the field distributor on site can mark a request as completed.
3. **Emergency Hotline**: The application pre-populates `999` in the Android dialer, but the citizen must physically press the call button.

---

## 4. Phase 6 Security Prerequisites

Phase 5 establishes a secure baseline for the upcoming **Phase 6: Ultimate Application Security, AI Safety, Zero-Trust Architecture & System Hardening**:
- Implement Firebase App Check to prevent unauthorized API requests from non-genuine APK instances.
- Author fine-grained Firebase Database Rules verifying role-based access control (RBAC) at the server level.
- Configure ProGuard/R8 obfuscation for all domain and repository classes.
