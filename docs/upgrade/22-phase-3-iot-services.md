# Phase 3 IoT Services & Mobile Edge Telemetry

**Project**: Relief Distribution Android Application  
**Package**: `com.example.reliefdistribution`  
**Phase**: Phase 3 of 10  
**Status**: ACTIVE  
**Author**: Principal IoT Backend Architect  

---

## 1. Edge-to-Cloud Telemetry Pipeline

In disaster field operations, mobile devices carried by relief distributors and stranded survivors function as distributed edge IoT tracking nodes.

```mermaid
sequenceDiagram
    participant MobileEdge as Mobile Device (Edge Node)
    participant FusedLoc as FusedLocationProviderClient
    participant TelemetryModel as LocationTelemetry Domain
    participant CloudDB as Firebase Realtime Database
    participant SurvivorUI as Survivor Map Client

    Note over MobileEdge,CloudDB: Session Initialization & Disconnect Arming
    MobileEdge->>CloudDB: onDisconnect().removeValue() armed
    
    Note over MobileEdge,CloudDB: Streaming Edge Telemetry (10s intervals)
    loop Every 10 Seconds
        FusedLoc->>MobileEdge: GPS Coordinate Sample (lat, lng, acc, speed)
        MobileEdge->>TelemetryModel: Invariant Validation (Bounds, Time)
        alt Validation Passed
            TelemetryModel->>CloudDB: Atomic update to /Users/{userId}/location
            CloudDB->>SurvivorUI: Real-time map marker update
        else Validation Failed
            TelemetryModel--xMobileEdge: Log warning, drop malformed telemetry
        end
    end

    Note over MobileEdge,CloudDB: Abrupt Edge Power Failure / Signal Loss
    MobileEdge-xCloudDB: Connection lost (Heartbeat timeout)
    CloudDB->>CloudDB: Auto-executes armed onDisconnect().removeValue()
    CloudDB->>SurvivorUI: Marker removed from survivor map
```

---

## 2. Telemetry Invariant Verification

Edge telemetry streams are governed by [`LocationTelemetry.java`](file:///H:/Projects/Relief-Distribution-App/app/src/main/java/com/example/reliefdistribution/data/model/LocationTelemetry.java):
1. **Latitude Validation**: $-90.0 \le \text{latitude} \le 90.0$.
2. **Longitude Validation**: $-180.0 \le \text{longitude} \le 180.0$.
3. **Timestamp Integrity**: $\text{timestamp} > 0$.
4. **Edge Battery Throttling**: GPS intervals are set to 10-second updates (`setInterval(10000)`) with 5-second minimum thresholds (`setMinUpdateIntervalMillis(5000)`) using `Priority.PRIORITY_HIGH_ACCURACY`. This balances real-time accuracy with battery preservation in disaster areas where charging points are unavailable.

