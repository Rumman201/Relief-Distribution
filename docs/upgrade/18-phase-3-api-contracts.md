# Phase 3 API Contracts & Boundary Validation

**Project**: Relief Distribution Android Application  
**Package**: `com.example.reliefdistribution`  
**Phase**: Phase 3 of 10  
**Status**: ACTIVE  
**Author**: Principal API Architect  

---

## 1. Firebase Realtime Database API Contracts

### 1.1 Endpoint: `/Users/{userId}`
- **Protocol**: Firebase Realtime WebSocket / REST SDK
- **Authentication**: Required (`auth != null`)
- **Authorization**:
  - Read: All authenticated users (`auth != null`) to discover distributors and relief roles.
  - Write: Restricted strictly to resource owner (`auth.uid === $userId`).
- **Validation Contract**:
  - `firstName`: String, non-empty.
  - `lastName`: String, non-empty.
  - `email`: String, valid RFC format.
  - `phone`: String, 7–15 digits.
  - `role`: Enum matching `^(Distributor|Survivor)$`.
  - `password`: **FORBIDDEN** (never accepted in database tree).

### 1.2 Endpoint: `/Users/{userId}/location`
- **Protocol**: Real-time GPS Telemetry Stream
- **Authentication**: Required (`auth != null`)
- **Authorization**: Owner only (`auth.uid === $userId`).
- **Validation Contract**:
  - `latitude`: Number in `[-90.0, 90.0]`.
  - `longitude`: Number in `[-180.0, 180.0]`.
  - `timestamp`: Integer `> 0`.
  - `accuracy`: Optional float (meters).
  - `speed`: Optional float (m/s).
  - `isOnline`: Boolean.

### 1.3 Endpoint: `/ReliefRequests/{requestId}`
- **Protocol**: Firebase Realtime Database
- **Authentication**: Required (`auth != null`)
- **Authorization**:
  - Read: All authenticated users.
  - Write:
    - Creation: Survivor (`data.val() == null && newData.child('userId').val() === auth.uid`).
    - Edit: Owning survivor OR verified distributor (`root.child('Users').child(auth.uid).child('role').val() === 'Distributor'`).
- **Validation Contract**:
  - `requestId`: String, non-empty unique key.
  - `userId`: String, matches authenticated creator.
  - `survivorName`: String, non-empty.
  - `phone`: String, non-empty.
  - `emergencyType`: String.
  - `rationCount`: Number `>= 1`.
  - `status`: Enum matching `^(PENDING|ACCEPTED|EN_ROUTE|FULFILLED|CANCELLED)$`.

### 1.4 Endpoint: `/DisasterAlerts/{alertId}`
- **Protocol**: Real-time Broadcast Channel
- **Authentication**: Required (`auth != null`)
- **Authorization**:
  - Read: All authenticated users.
  - Write: Distributor role only (`root.child('Users').child(auth.uid).child('role').val() === 'Distributor'`).
- **Validation Contract**:
  - `alertId`: String, non-empty.
  - `title`: String, non-empty.
  - `severity`: Enum matching `^(LOW|MODERATE|SEVERE|CRITICAL)$`.

---

## 2. Network Security Configuration (`network_security_config.xml`)

All external network communication is strictly enforced over TLS 1.3/HTTPS:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <debug-overrides>
        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```
Cleartext HTTP traffic is permanently forbidden across production configurations.

