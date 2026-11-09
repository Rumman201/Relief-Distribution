---
name: backend-api-contract-qa
description: Firebase backend API contracts, schema serialization, network error semantics, negative testing, and contract regression prevention.
---

# Backend API Contract QA Skill

## 1. Purpose
Validate, test, and protect backend data exchange contracts, serialization schemas, Firebase Realtime Database payload envelopes, and network error semantics across all client and cloud integration seams.

## 2. Activation Criteria
Activate this skill when:
- Testing Firebase RTDB query contracts and snapshot deserialization (`snapshot.getValue(Class)`).
- Validating negative API scenarios (unauthenticated requests, malformed payloads, rate limiting).
- Testing schema backward compatibility between client releases and stored cloud nodes.
- Auditing error translation and user-facing network error messages.

## 3. Project Context
- **Backend Infrastructure**: Firebase Realtime Database + Firebase Authentication.
- **Data Entities**: `User`, `ReliefRequest`, `InventoryItem`, `Notification`, `LocationTelemetry`.
- **Contract Boundary**: Models must provide default zero-argument constructors and standard getters/setters for reflection deserialization without triggering ProGuard R8 stripping.

## 4. Engineering Standards
- **Strict Null Safety**: Deserialized models must tolerate missing fields without throwing `NullPointerException`.
- **Negative Testing Coverage**: Verify that unauthorized writes, oversized payloads, or invalid types fail with descriptive error events.
- **Contract Preservation**: Field renames must be accompanied by `@PropertyName` annotations to prevent database drift.

## 5. Step-by-Step Execution Procedure
1. **Schema Mapping**: Inventory all model fields against `database.rules.json` validation criteria.
2. **Serialization Testing**: Test round-trip JSON serialization and deserialization across all entities.
3. **Negative Contract Testing**: Simulate network drops, HTTP 403 Forbidden, and corrupted JSON snapshots.
4. **Regression Gate**: Verify 100% passage across all repository and data contract tests.

## 6. Decision Tree for Ambiguous Cases
- *Should a missing field in an older database record crash the new app?*
  -> No. The data model must initialize default values (e.g. `status = "Pending"`, `timestamp = System.currentTimeMillis()`).

