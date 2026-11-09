---
name: iot-edge-performance
description: Mobile edge IoT telemetry throughput, adaptive battery-aware GPS polling throttling, offline buffer optimization, and low-power field connectivity.
---

# IoT & Edge Performance Skill

## 1. Purpose
Optimize mobile edge IoT telemetry pipelines, FusedLocationProvider polling intervals, battery-aware power scaling, and offline telemetry buffer management to maximize device battery endurance during disaster blackouts while maintaining high-fidelity fleet visibility.

## 2. Activation Criteria
Activate this skill when:
- Profiling battery consumption, CPU wake locks, or GPS polling rates in `GoogleMap` or `OfflineTelemetryBuffer`.
- Tuning adaptive sampling intervals based on device velocity or battery state.
- Optimizing in-memory telemetry queue size, garbage collection churn, or serialization overhead.
- Benchmarking batch sync throughput upon network reconnect.

## 3. Project Context
- **Edge Device**: Android smartphones operated by field distributors and survivors in disaster zones.
- **Power Constraints**: Power grid often collapsed in flood/cyclone areas; devices rely on limited battery packs.
- **Adaptive Velocity Profiles**:
  - `HIGH_SPEED` (> 25 km/h, vehicle transit): 5-second polling interval.
  - `ACTIVE` (5-25 km/h, walking/patrol): 10-second polling interval.
  - `STATIONARY` (< 5 km/h, stopped at depot/victim site): 30-second polling interval (reduces GPS radio awake time by 66%).
  - `BATTERY_SAVER` (Battery < 20%): 45-second polling interval with reduced accuracy.

## 4. Engineering Standards
- **Battery-First Philosophy**: Minimize active GPS radio runtime by throttling polling when stationary.
- **Lossless Queue Eviction**: Enforce bounded queue ($100$ items) that sheds non-critical stationary breadcrumbs first during extended blackouts.
- **Single-Roundtrip Sync**: Batch drain buffered points in a single network transmission rather than individual point-by-point calls.

## 5. Step-by-Step Execution Procedure
1. **Dynamic Interval Calculation**:
   - Compute sampling interval from current speed and battery percentage.
2. **Buffer Compaction**:
   - Deduplicate consecutive stationary fixes before enqueuing to conserve queue slots.
3. **Benchmark Verification**:
   - Benchmark batch enqueue and drain operations verifying sub-millisecond execution.

## 6. Decision Tree for Ambiguous Cases
- *Should we completely disable GPS when battery is below 15%?*
  -> No. Maintain heartbeat telemetry at extended intervals (e.g. 60s) so rescue teams know the distributor's last known location.
