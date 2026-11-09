---
name: iot-firmware-security
description: Edge telemetry anti-spoofing, replay attack mitigation, timestamp freshness validation, and disconnect cleanup for mobile GPS IoT services.
---

# IoT & Firmware Security Skill

## 1. Purpose
Secure mobile edge IoT telemetry, GPS sensor tracking, offline telemetry buffers, and cloud synchronization against location spoofing, timestamp replay attacks, buffer overflow, and rogue beacon updates.

## 2. Activation Criteria
Activate this skill when:
- Modifying `OfflineTelemetryBuffer`, `FirebaseLocationRepository`, or `LocationTelemetry`.
- Reviewing GPS collection rates, battery-aware throttling, and telemetry validation.
- Implementing anti-replay protection or cryptographic timestamp checks.
- Designing disconnect and teardown security routines.

## 3. Project Security Context
- **Edge Device**: Android mobile handset operating as a field sensor node.
- **Telemetry Data**: `LocationTelemetry` (userId, latitude, longitude, timestamp, speed, accuracy, isEmergency).
- **Buffer**: `OfflineTelemetryBuffer` (stores up to 100 points in FIFO buffer when offline).
- **Threats**:
  - GPS Coordinate Spoofing: Submitting invalid coordinates (outside [-90, 90] / [-180, 180]) to crash maps or mislead distributors.
  - Telemetry Replay Attack: Replaying captured offline telemetry packets with stale timestamps to simulate fake vehicle movement.
  - Buffer Flooding / Exhaustion: Flooding the offline buffer to exhaust mobile device RAM.

## 4. Engineering Standards
- **Range Invariants**: Latitude must be within `[-90.0, 90.0]`; Longitude must be within `[-180.0, 180.0]`.
- **Timestamp Freshness**: Timestamps must not be in the future (`timestamp <= now + 60000ms`) and replayed buffered packets must be monotonically increasing.
- **Buffer Capacity Bounds**: FIFO queue must enforce a hard cap (100 items max) to prevent memory exhaustion.
- **Disconnect Teardown**: Unregister callbacks and utilize `onDisconnect().remove()` on sensitive live tracking nodes.

## 5. Step-by-Step Execution Procedure
1. **Validation Checks**:
   - Verify coordinate range checks in `LocationTelemetry` constructor.
   - Implement timestamp sanity checks (rejecting negative or absurd future timestamps).
2. **Buffer Guarding**:
   - Ensure `OfflineTelemetryBuffer` evicts oldest entries gracefully when full, without crashing or allocating unbounded memory.
3. **Database Rule Invariant**:
   - Verify `database.rules.json` enforces numeric range constraints on `latitude` and `longitude`.

## 6. Decision Tree for Ambiguous Cases
- *What if GPS reports inaccurate or mock location?*
  -> Check `location.isFromMockProvider()` on supported Android versions; flag or reject mock locations in production mode.
