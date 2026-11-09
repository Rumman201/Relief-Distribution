---
name: authentication-authorization-hardening
description: Authentication flow hardening, session lifecycle security, biometric and credential safeguards, Play Integrity verification, and privilege enforcement.
---

# Authentication & Authorization Hardening Skill

## 1. Purpose
Harden user authentication and authorization workflows, preventing credential theft, session hijacking, unauthorized role escalation, and counterfeit client connections.

## 2. Activation Criteria
Activate this skill when:
- Modifying `FirebaseAuthRepository`, `LoginActivity`, `SignUpActivity`, or auth view models.
- Evaluating password policies, multi-factor readiness, or credential handling.
- Reviewing session persistence, token refresh lifecycles, and sign-out invalidation.
- Integrating Google Play Integrity or Firebase App Check.

## 3. Project Security Context
- **Identity Provider**: Firebase Authentication (Email/Password).
- **Session Tokens**: Handled via Firebase Auth SDK ID tokens with short-lived access (1 hour) and automatic secure refresh.
- **Client Identity Cache**: SharedPreferences stores `registeredUserId` for fast routing; must never store raw passwords or tokens.
- **Risks**:
  - Offline session tampering.
  - Bruteforce dictionary attacks on login.
  - Role escalation during registration.

## 4. Engineering Standards
- **Zero Plaintext Passwords**: Invariant 1 - Passwords must never touch Realtime Database, logs, or unencrypted storage.
- **Client Integrity**: Prepare App Check attestation hooks with Play Integrity provider for release builds.
- **Password Strength**: Minimum 8 characters, requiring mixed alphanumeric characters.
- **Session Revocation**: Complete local teardown on sign-out (clear preferences, cancel listeners, flush caches).

## 5. Step-by-Step Execution Procedure
1. **Auth Repository Hardening**:
   - Verify that `register()` does not write password to the database record.
   - Enforce explicit error message sanitization: Return generic auth failure messages to the user to prevent user enumeration.
2. **Session Cleanup**:
   - Ensure `signOut()` clears local caches, resets ViewModels, and unbinds location services.
3. **App Check Architecture**:
   - Provide clean integration hooks for `FirebaseAppCheck` with `PlayIntegrityAppCheckProviderFactory` for release builds.

## 6. Decision Tree for Ambiguous Cases
- *Should the app remember user password for quick login?*
  -> Never. Use Firebase Auth persistent tokens or Android Biometric Prompt with Keystore-backed secrets.
