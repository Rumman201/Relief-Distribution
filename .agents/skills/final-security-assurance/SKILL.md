---
name: final-security-assurance
description: Pre-release security qualification, OWASP MASVS compliance verification, zero-trust invariant auditing, and vulnerability regression defense.
---

# Final Security Assurance Skill

## 1. Purpose
Conduct comprehensive security verification of the candidate release, validating identity safeguards, permission boundaries, cryptographic hygiene, secret isolation, and database access control rules.

## 2. Activation Criteria
Activate this skill when:
- Performing pre-release security qualification and OWASP Mobile Application Security Verification Standard (MASVS) auditing.
- Verifying the absence of hardcoded API keys, keystores, and plain-text user credentials in source and version control.
- Auditing Firebase Realtime Database security rules for default-deny and role-scoped write enforcement.
- Validating Android permission hygiene and intent sanitization.

## 3. Project Context
- **Security Invariants**:
  - Zero Plaintext Passwords: Authentication strictly delegated to Firebase Authentication.
  - Safe Emergency Dialing: Use `Intent.ACTION_DIAL` (`tel:999`), never `Intent.ACTION_CALL`.
  - Minimal Permissions: Scoped strictly to `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `INTERNET`, `ACCESS_NETWORK_STATE`.
  - Rule Hardening: Firebase RTDB rules enforce role verification (`auth != null && auth.token.role == 'admin'`).

## 4. Engineering Standards
- **Zero Critical / High CVEs**: Candidate must have zero unresolved vulnerabilities.
- **Strict Data Minimization**: PII sanitized from all diagnostic logs (`SecuritySanitizer`).
- **Cryptographic Hygiene**: Secure random tokens, SHA-256 integrity digests, and TLS 1.3 transport security.

## 5. Execution Procedure
1. Scan repository for hardcoded secrets or sensitive credentials.
2. Verify Firebase rules syntax and security boundaries.
3. Audit AndroidManifest.xml for unexported activities and permission flags.
4. Review security regression test results.
5. Inscribe findings in `163-phase-10-2-security-assurance.md`.
