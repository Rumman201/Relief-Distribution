---
name: iot-cloud-backend-engineering
description: Manage mobile edge-to-cloud GPS telemetry, real-time location streaming, disconnect cleanup, and disaster zone communication reliability.
---

# IoT Cloud Backend Engineering (Mobile Edge Telemetry)

This skill governs device-to-cloud telemetry pipelines where mobile devices in disaster relief zones operate as edge tracking nodes broadcasting real-time GPS coordinates, connection heartbeats, and status signals.

## Core Directives

1. **Telemetry Schema Integrity**: Edge GPS telemetry payloads must validate coordinate boundaries (latitude [-90, 90], longitude [-180, 180]), timestamps (> 0), and accuracy metrics.
2. **Bandwidth and Battery Optimization**: Telemetry streaming must employ rate limiting and adaptive intervals to conserve cellular bandwidth and device battery during disasters.
3. **Automated Session Cleanup on Disconnect**: When edge nodes lose connectivity or experience power failure, the cloud backend must automatically clean up active broadcast markers via `onDisconnect()` handlers.
4. **Offline Edge Buffering**: When cellular towers fail, edge telemetry must not crash the device; local state must be buffered safely until reconnection.

## Verification Checklist

- [ ] Telemetry payload serialization and deserialization verified.
- [ ] Coordinate validation and boundary enforcement tested.
- [ ] `onDisconnect` handlers configured on location streams.

