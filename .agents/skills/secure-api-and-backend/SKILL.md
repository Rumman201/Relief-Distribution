---
name: secure-api-and-backend
description: Secure backend integration, API payload validation, anti-tampering, rate-limiting strategies, and transport layer security (TLS 1.3).
---

# Secure API & Backend Skill

## 1. Purpose
Secure client-to-cloud communications and backend API boundaries, enforcing strict transport encryption, payload validation, schema conformance, and anti-tampering protections.

## 2. Activation Criteria
Activate this skill when:
- Defining or updating network communication channels or endpoints.
- Reviewing `network_security_config.xml` and transport security.
- Implementing payload sanitization and schema validation before persistence.
- Securing third-party HTTP/REST integrations and webhooks.

## 3. Project Security Context
- **Transport**: HTTPS/TLS over port 443 with Firebase Realtime Database WebSockets and REST APIs.
- **Cleartext Traffic Policy**: Explicitly disabled across all domains via `network_security_config.xml`.
- **Certificates**: System CA certificates trusted in production; User CA trusted only in `debug-overrides`.
- **Integrations**: Google Maps SDK, Firebase SDKs.

## 4. Engineering Standards
- **TLS Enforcement**: Minimum TLS 1.2, preferred TLS 1.3. Cleartext (`http://`) strictly forbidden.
- **Input Validation**: All incoming and outgoing fields must pass type, regex, length, and range checks before write operations.
- **Payload Limits**: Enforce maximum payload sizes (e.g. notes <= 1000 chars, strings <= 256 chars) to prevent DoS.

## 5. Step-by-Step Execution Procedure
1. **Network Configuration**:
   - Verify `res/xml/network_security_config.xml` contains `<base-config cleartextTrafficPermitted="false">`.
   - Ensure `AndroidManifest.xml` links `android:networkSecurityConfig="@xml/network_security_config"`.
2. **Payload Sanitizer**:
   - Enforce input sanitization on all user-submitted text fields (strip HTML tags, control characters, SQL/script injection markers).
3. **Database Validation Rules**:
   - Write comprehensive `.validate` expressions in Firebase rules matching the sanitizer rules.

## 6. Decision Tree for Ambiguous Cases
- *Should client trust user certificates in release builds?*
  -> Absolutely not. User certificates enable proxy interception (mitmproxy/Burp Suite) and must be restricted to debug builds only.
