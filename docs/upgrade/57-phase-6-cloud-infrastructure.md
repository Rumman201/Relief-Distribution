# Phase 6 — Cloud Infrastructure & Database Security

**Document ID**: `57-phase-6-cloud-infrastructure`  
**Phase**: Phase 6 of 10 — Comprehensive Security Transformation  
**Program**: Relief Distribution System Modernization (`com.example.reliefdistribution`)  
**Standard**: CIS Google Cloud Platform Foundations Benchmark / OWASP Cloud Security  

---

## 1. Executive Summary

This document establishes the cloud infrastructure hardening, Firebase Realtime Database security rules configuration, and Google Cloud Platform API protection mechanisms implemented in Phase 6.

---

## 2. Firebase Cloud Topology & Trust Boundaries

- **GCP Project ID**: `relief-distribution-2w5`
- **Primary Datastore**: Firebase Realtime Database (`https://relief-distribution-2w5-default-rtdb.firebaseio.com`)
- **Authentication Engine**: Firebase Authentication (Email/Password)
- **External API Services**: Google Maps Platform Android SDK

---

## 3. Realtime Database Security Rules Architecture (`database.rules.json`)

The cloud datastore enforces a **Default-Deny** posture at root:
```json
{
  "rules": {
    ".read": false,
    ".write": false,
    ...
  }
}
```

### 3.1 Node-Level Access Control Specifications

#### `/Users/$userId`
- **Read**: Authenticated users (`auth != null`).
- **Write**: `$userId === auth.uid` (Self-write only).
- **Validation**: Requires `firstName`, `lastName`, `email`, `phone`, `role`.
- **Anti-Escalation**: Role cannot be altered once created: `(!data.exists() || data.child('role').val() === newData.child('role').val())`.
- **Location Sub-Node**: Coordinates bounded to `[-90, 90]` (latitude) and `[-180, 180]` (longitude).

#### `/ReliefRequests/$requestId`
- **Read**: Authenticated users (`auth != null`).
- **IndexOn**: `["survivorId", "userId", "status", "priority", "timestamp", "createdAt"]`.
- **Write Rules**:
  - *Create*: Permitted only if `survivorId === auth.uid` or `userId === auth.uid`.
  - *Cancel*: Permitted by survivor only while status is `pending` / `PENDING`.
  - *Status Progression*: Permitted only by authenticated Distributors (`Users/$uid/role === 'Distributor'`).
- **Validation**: Strict regex matching on status: `/^(pending|accepted|in_progress|completed|cancelled|PENDING|ACCEPTED|EN_ROUTE|FULFILLED|CANCELLED)$/`. Coordinates bounded to valid geographic ranges.

#### `/Inventory/$itemId`
- **Read**: Authenticated users (`auth != null`).
- **IndexOn**: `["distributorId", "category", "quantityAvailable", "lastUpdated"]`.
- **Write**: Restricted strictly to users possessing the `Distributor` role in `/Users/$uid/role`.
- **Validation**: Requires valid category, positive numbers for quantities, and `distributorId === auth.uid`.

#### `/Notifications/$userId/$notificationId`
- **Read**: Restricted strictly to `$userId === auth.uid`.
- **IndexOn**: `["timestamp", "read", "isRead"]`.
- **Write**: Allowed by the recipient or by Distributors sending system notifications.

#### `/DisasterAlerts/$alertId`
- **Read**: Authenticated users (`auth != null`).
- **IndexOn**: `["isActive", "issuedAt", "severity"]`.
- **Write**: Restricted strictly to Distributors. Severity must match `/^(LOW|MEDIUM|HIGH|CRITICAL)$/`.

---

## 4. Google Cloud Console API Protection

- **Google Maps Android API**:
  - Bound to application package name: `com.example.reliefdistribution`.
  - Bound to developer and release SHA-1 certificate fingerprints.
  - Quota throttles configured to prevent billing exhaustion from automated scraping.
