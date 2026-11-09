---
name: principal-security-audit
description: Threat modeling, attack-surface enumeration, trust boundary analysis, and vulnerability prioritization for disaster relief mobile architectures.
---

# Principal Security Audit Skill

## 1. Purpose
Provide principal-level cybersecurity review, trust boundary mapping, STRIDE threat modeling, and prioritized vulnerability auditing across the Android application, Firebase backend, IoT telemetry, and AI pipelines.

## 2. Activation Criteria
Activate this skill when:
- Conducting comprehensive security reviews of architecture, endpoints, or data models.
- Identifying attack surfaces, threat actors, and attack vectors.
- Classifying vulnerabilities according to OWASP MASVS and CVSS v3.1 scoring.
- Evaluating residual security risk before major release milestones.

## 3. Project Security Context
- **System**: Relief Distribution System (`com.example.reliefdistribution`).
- **Trust Boundaries**:
  - Boundary 1: Android Client Runtime <-> Android OS / IPC / Shared Storage.
  - Boundary 2: Android Client <-> Firebase Authentication & Realtime Database (Cloud).
  - Boundary 3: Survivor Role <-> Distributor Role (Horizontal/Vertical Privilege Separation).
  - Boundary 4: Client GPS Sensor <-> Edge Telemetry Buffer <-> Cloud Fleet Tracking.
  - Boundary 5: AI Triage Input <-> Heuristic Scorer <-> Automated Dispatch.
- **Threat Actors**: Malicious survivor, rogue distributor, local network eavesdropper, compromised device owner (rooted/tampered), automated bot/script.

## 4. Engineering Standards
- **Zero-Trust Baseline**: Never trust client-side validation alone; cloud rules and backend validators must independently enforce all business rules and RBAC.
- **OWASP MASVS Compliance**: Map all findings against MASVS-STORAGE, MASVS-CRYPTO, MASVS-AUTH, MASVS-NETWORK, MASVS-PLATFORM, MASVS-CODE, and MASVS-RESILIENCE.
- **Evidence-Based Severity**: Score vulnerabilities using CVSS v3.1 metrics (AV, AC, PR, UI, S, C, I, A).

## 5. Step-by-Step Execution Procedure
1. **Attack Surface Mapping**:
   - Inspect `AndroidManifest.xml` for exported components, intent filters, and permission declarations.
   - Inspect all Firebase RTDB references (`Users`, `ReliefRequests`, `Inventory`, `Notifications`, `DisasterAlerts`).
2. **Threat Modeling (STRIDE)**:
   - Spoofing: Identity forgery, GPS location spoofing, fake request submission.
   - Tampering: Request status manipulation, inventory quantity alteration, offline cache tampering.
   - Repudiation: Denying dispatched aid delivery, unlogged status transitions.
   - Information Disclosure: PII leakage in logs, unencrypted backups, broad RTDB read permissions.
   - Denial of Service: Flood requests, resource exhaustion via oversized payloads.
   - Elevation of Privilege: Survivor acquiring distributor capabilities, bypass of auth rules.
3. **Vulnerability Prioritization**: Group by CVSS v3.1 score and draft remediation tasks with explicit verification steps.

## 6. Decision Tree for Ambiguous Cases
- *Is a client-side check sufficient for authorization?*
  -> No. Client-side checks are UX-only; server-side database rules and backend validators are mandatory.
