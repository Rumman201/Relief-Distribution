---
name: iot-fleet-operations
description: Distributor GPS tracker fleet management, battery-aware OTA firmware simulation, stale telemetry pruning, and edge health monitoring.
---

# IoT Fleet Operations Skill

## 1. Purpose
Manage, monitor, optimize, and maintain operational reliability for the fleet of mobile IoT location trackers carried by field aid distributors across active disaster zones.

## 2. Activation Criteria
Activate this skill when:
- Monitoring distributor GPS telemetry streams, ingestion rates, and packet drop ratios.
- Tuning adaptive sampling policies based on distributor movement (stationary vs. transit) and battery state.
- Managing offline telemetry queue capacities, compaction algorithms, and network retransmission policies.
- Configuring telemetry cleanup, stale tracker disconnection, and presence indicators in Firebase RTDB.

## 3. Project Context
- **Edge Subsystem**: `LocationTrackingService.java` + `OfflineTelemetryBuffer.java`.
- **Telemetry Payloads**: Latitude, longitude, timestamp, speed, accuracy, and online status.
- **Fleet Constraints**: Variable cellular coverage, battery conservation in remote regions, bursty network connectivity.

## 4. Engineering Standards
- **Adaptive Ingestion**: Enforce stationary coordinate compaction to preserve mobile battery and transmission bandwidth.
- **Automatic Disconnect Handling**: Leverage Firebase `.info/connected` and `onDisconnect()` handlers to immediately clear stale location markers upon device disconnection.
- **Freshness SLA**: 99.0% of active distributor location records must be updated within 10 seconds of device transmission.

## 5. Step-by-Step Execution Procedure
1. **Fleet Health Audit**: Inspect connected distributor counts, update frequencies, and buffer drop counters.
2. **Buffer Efficiency Check**: Run `AdaptiveTelemetryPerformanceTest` to verify stationary compaction and queue bounds.
3. **Stale Node Pruning**: Verify automated cloud cleanup of inactive distributor sessions older than 5 minutes.
4. **Alert Configuration**: Alert operations when distributor telemetry latency exceeds 30 seconds.

## 6. Decision Tree for Ambiguous Cases
- *Should telemetry points be discarded if the buffer overflows?*
  -> Yes, the oldest non-emergency telemetry point is dropped to preserve bounded memory footprint while retaining recent movement.

