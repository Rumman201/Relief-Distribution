---
name: iot-feature-engineering
description: Mobile edge IoT telemetry, offline breadcrumb queuing, GPS lifecycle management, battery-aware sampling throttling, and connection resilience.
---

# IoT Feature Engineering Skill

## 1. Purpose
Define engineering standards for using mobile devices as distributed edge IoT tracking beacons, providing reliable location telemetry, offline breadcrumb caching, and power conservation during disaster relief operations.

## 2. Activation Criteria
Activate this skill when:
- Tracking distributor positions in real time.
- Buffering location telemetry when cellular connection drops.
- Synchronizing batch breadcrumbs upon network restoration.
- Optimizing GPS sampling intervals for battery life.

## 3. Engineering Invariants & Standards
1. **Edge Disconnect Arming**: Always arm `onDisconnect().removeValue()` on live location nodes so stale markers vanish if a distributor's device shuts down or loses signal.
2. **Offline Ring Buffer**: When disconnected, cache GPS fixes locally in a bounded FIFO queue (maximum 100 points) to prevent OutOfMemory errors.
3. **Power-Aware Throttling**: Adapt location sampling interval (10s active, 30s power-save) based on movement speed and battery level.
4. **Coordinate Invariant Validation**: Only persist valid coordinates ($-90 \le \text{lat} \le 90$, $-180 \le \text{lng} \le 180$, $\text{timestamp} > 0$).

## 4. Execution Procedure
1. **Lifecycle Binding**: Register `LocationCallback` in `onResume()` / `onStart()` and unregister in `onPause()` / `onStop()` / `onDestroy()`.
2. **Buffer Management**: Check `ConnectionRepository.isConnected()`. If false, append `LocationTelemetry` to `OfflineTelemetryBuffer`.
3. **Flush on Reconnect**: On connectivity restoration, flush all buffered breadcrumbs as a batch update to Firebase RTDB.
4. **Verification**: Write unit tests simulating connection drop, queue overflow, and flush operations.
