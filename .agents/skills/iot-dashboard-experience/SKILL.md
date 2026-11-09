---
name: iot-dashboard-experience
description: Real-time telemetry visualization, GPS tracking indicators, connection status banners, and emergency disaster maps.
---

# IoT Dashboard Experience

This skill governs real-time edge telemetry interfaces, GPS map overlays, live connectivity badges, and field operator dashboards.

## Core Directives

1. **Unambiguous Connection State**: The UI must clearly indicate whether the client is online and streaming live telemetry or operating in cached offline mode.
2. **Clear GPS Tracking Feedback**: When "Distribute Mode" is active, provide a prominent live indicator and switch, ensuring distributors are conscious of their active location broadcast.
3. **Safety-Critical Relief Markers**: Map pins must clearly distinguish between Active Relief Distributors, Pending Survivor Requests, and Relief Supply Depots.
4. **Resilient Data Displays**: Handle missing, delayed, or degraded GPS telemetry gracefully without showing blank screens or throwing exceptions.

## Verification Checklist

- [ ] Real-time connection badge reflects `ConnectionRepository` status.
- [ ] Active tracking mode visually highlighted with clear toggle controls.
- [ ] Map markers render reliably with fallback states.

