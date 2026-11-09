---
name: cloud-infrastructure-hardening
description: Firebase project configuration hardening, security rules compiler validation, App Check integration, and cloud surface defense.
---

# Cloud Infrastructure Hardening Skill

## 1. Purpose
Secure cloud infrastructure components, Firebase Realtime Database rules configuration, Google Cloud Console API restrictions, and cloud credential management.

## 2. Activation Criteria
Activate this skill when:
- Updating `database.rules.json` or `firebase.rules`.
- Auditing Google Cloud API keys and restrictions.
- Configuring App Check / Play Integrity tokens for cloud endpoints.
- Reviewing cloud database indexes and rate-limiting controls.

## 3. Project Security Context
- **Cloud Backend**: Google Firebase (Project: `relief-distribution-2w5`).
- **Services Used**: Firebase Authentication, Firebase Realtime Database.
- **External APIs**: Google Maps Platform Android SDK.
- **Rules File**: `database.rules.json` (deployed to Firebase RTDB).

## 4. Engineering Standards
- **Zero Public Access**: Default deny on all database root paths; no node accessible without `auth != null`.
- **API Key Scoping**: Google Maps API key restricted to package `com.example.reliefdistribution` and release/debug SHA-1 certificate fingerprints.
- **Index Optimization**: Define `.indexOn` on all queried child properties to eliminate inefficient client-side scans.
- **Atomic Operations**: Enforce transactional integrity on inventory counters and status changes.

## 5. Step-by-Step Execution Procedure
1. **Security Rules Synchronization**:
   - Align `database.rules.json` with the exact domain models used by the client code (`Users`, `ReliefRequests`, `Inventory`, `Notifications`, `DisasterAlerts`).
   - Validate rules JSON syntax using local linters or test parsers.
2. **API Key Protection**:
   - Verify keys are sourced from `local.properties` via `manifestPlaceholders`.
   - Never commit `MAPS_API_KEY` to git.
3. **App Check Hardening**:
   - Establish App Check token validation to prevent unauthorized script bots from querying the RTDB directly.

## 6. Decision Tree for Ambiguous Cases
- *Is Google Maps API Key considered a confidential secret?*
  -> Client API keys in mobile apps cannot be kept completely secret from a local debugger, which is why API key restrictions (package name + SHA-1 fingerprint) are required in Google Cloud Console.
