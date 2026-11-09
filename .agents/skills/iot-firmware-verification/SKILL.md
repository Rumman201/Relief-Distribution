---
name: iot-firmware-verification
description: Distributor GPS edge tracker simulation, stationary telemetry compaction, offline buffer boundary QA, and disconnect teardown testing.
---

# IoT & Edge Telemetry Verification Skill

## 1. Purpose
Simulate, test, and verify mobile edge IoT location tracking, stationary coordinate compaction, velocity-aware battery scaling, offline ring buffer limits, and presence cleanup under degraded field connectivity.

## 2. Activation Criteria
Activate this skill when:
- Testing `LocationTrackingService.java` and `OfflineTelemetryBuffer.java`.
- Simulating edge location streams with varying speed, accuracy, and cellular connectivity states.
- Verifying stationary coordinate compaction (< 0.5 m/s) and emergency breadcrumb preservation.
- Auditing automatic disconnect cleanup (`onDisconnect().removeValue()`) in Firebase RTDB.

## 3. Project Context
- **Edge Subsystem**: Mobile handsets carried by relief distributors acting as IoT GPS trackers.
- **Telemetry Payload**: `LocationTelemetry` containing latitude, longitude, timestamp, speed, accuracy, and online status.
- **Buffer Ceiling**: 50 breadcrumbs in `OfflineTelemetryBuffer.java`.

## 4. Engineering Standards
- **Stationary Compaction**: When moving at < 0.5 m/s, sequential points within 10 meters must be compacted to save bandwidth and power.
- **FIFO Ring Buffer**: Buffer must never exceed 50 items; oldest non-emergency coordinates are evicted on overflow.
- **Coordinate Boundary Clamping**: Coordinates outside `[-90.0, 90.0]` latitude or `[-180.0, 180.0]` longitude must be rejected at ingestion.

## 5. Step-by-Step Execution Procedure
1. **Device Stream Simulation**: Generate synthetic GPS streams representing walking, transit, stationary, and erratic movement.
2. **Offline Buffer Stressing**: Queue 100+ telemetry points offline to verify bounded memory retention and FIFO eviction.
3. **Reconnection Flush Test**: Restore connectivity and verify all buffered points are transmitted in chronological batches.
4. **Disconnect Verification**: Sever connection and verify cloud node deletion within 5 seconds.

## 6. Decision Tree for Ambiguous Cases
- *Should an emergency distress coordinate be compacted or evicted?*
  -> Never. High-priority or emergency breadcrumbs must be preserved unconditionally.

