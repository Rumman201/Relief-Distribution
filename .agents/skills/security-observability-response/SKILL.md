---
name: security-observability-response
description: Security incident detection, PII-sanitized audit logging, abnormal event monitoring, and incident response runbooks for relief platforms.
---

# Security Observability & Response Skill

## 1. Purpose
Establish structured security telemetry, detect suspicious behaviors (e.g. brute-force login attempts, rapid request spam, unauthorized role modification attempts), and provide actionable incident response runbooks.

## 2. Activation Criteria
Activate this skill when:
- Designing security audit logs or error telemetry.
- Defining alert thresholds for suspicious authentication or authorization failures.
- Drafting incident response plans, key compromise procedures, or vulnerability response playbooks.
- Reviewing diagnostic outputs for security monitoring readiness.

## 3. Project Security Context
- **Audit Logging**: `AppLogger` provides centralized, PII-masked logging.
- **Security Events**:
  - Auth failure / bad credentials spike.
  - Database permission denied (`DatabaseError.PERMISSION_DENIED`).
  - Input validation rejection (detected malicious markup or extreme values).
  - Out-of-order or future timestamp telemetry rejection.

## 4. Engineering Standards
- **Zero Sensitive Data in Logs**: No tokens, passwords, or full plaintext PII in log records.
- **Actionable Diagnostic Context**: Include error codes, anonymized entity IDs, and component tags.
- **Incident Response Runbooks**: Provide documented procedures for API key rotation, compromised user revocation, and emergency maintenance.

## 5. Step-by-Step Execution Procedure
1. **Security Event Logging**:
   - Ensure critical security events (authorization failures, validation rejections) emit tagged diagnostic warnings.
2. **Alert Thresholds**:
   - Establish guidelines for detecting anomalous traffic patterns in Firebase Cloud console.
3. **Incident Playbook**:
   - Maintain runbooks for credential compromise, database breach containment, and malicious client revocation.

## 6. Decision Tree for Ambiguous Cases
- *Should security exceptions expose detailed stack traces to the user UI?*
  -> Never. Expose user-friendly generic messages on screen; log detailed diagnostics internally for authorized operators.
