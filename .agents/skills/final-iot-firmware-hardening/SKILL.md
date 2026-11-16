---
name: final-iot-firmware-hardening
description: Edge telemetry anti-spoofing, stationary coordinate compaction, offline ring buffer management, disconnect cleanup, and low-power GPS lifecycle safety.
---

# Final IoT & Firmware Hardening Skill

## 1. Purpose & Scope
Governs mobile edge GPS tracking, telemetry transmission, offline ring buffer resilience, coordinate validation, and server-side disconnect teardown for disaster relief distribution vehicles.

## 2. Invariants & Guardrails
- **Coordinate Integrity**: Latitude strictly within $[-90.0, 90.0]$, Longitude within $[-180.0, 180.0]$, Non-zero values required.
- **Anti-Replay Freshness**: GPS fix timestamp within $\pm 5$ minutes of current server clock.
- **Stationary Compaction**: When ground speed $< 0.5$ m/s, sequential stationary telemetry fixes are compacted to eliminate redundant database writes and conserve device battery.
- **Ring Buffer Bounds**: Offline buffer capped at 100 entries with FIFO eviction to prevent heap exhaustion during cellular blackouts.
- **Server Disconnect Hook**: Armed `onDisconnect().removeValue()` hook ensures ghost vehicles are scrubbed from survivor emergency maps immediately upon cellular drops.

## 3. Step-by-Step Procedure
1. Audit `OfflineTelemetryBuffer` enqueue, compaction, and batch drain operations.
2. Verify `SecuritySanitizer.isValidCoordinates` and `isFreshTimestamp` methods.
3. Validate mock tracking sessions in `AdaptiveTelemetryPerformanceTest` and `FullSystemQualityAssuranceTest`.
4. Ensure location listeners disconnect gracefully in `MapActivity.onStop()` / `onDestroy()`.
