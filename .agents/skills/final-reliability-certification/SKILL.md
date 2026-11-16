---
name: final-reliability-certification
description: System resilience validation, offline mode auditing, chaos fault recovery, and battery/lifecycle reliability certification.
---

# Final Reliability Certification Skill

## 1. Purpose
Certify system reliability under extreme operational stress, network partitions, intermittent connectivity, device restarts, and resource exhaustion, ensuring critical relief coordination is never interrupted.

## 2. Activation Criteria
Activate this skill when:
- Validating resilience patterns (exponential backoff with jitter, offline request queuing, circuit breakers).
- Auditing Android Activity lifecycle state restoration and configuration change survival.
- Verifying listener teardown and memory leak prevention (`onStop` / `onDestroy`).
- Measuring IoT telemetry buffer compaction and edge tracking survivability during network loss.

## 3. Project Context
- **Edge Tracker Buffer**: Ring buffer capped at 100 points with stationary compaction (25:1 ratio).
- **Network Resilience**: Firebase Realtime Database disk persistence enabled (`setPersistenceEnabled(true)`).
- **Concurrency Safeguards**: `AppExecutors` with bounded thread pools (Disk I/O, Network, Main Thread).
- **Lifecycle Cleanup**: Unregister all `ValueEventListener` and `LocationCallback` instances.

## 4. Engineering Standards
- **Zero Memory Leaks**: All async callbacks must be safely cancelled or decoupled on activity teardown.
- **Data Survivability**: Zero data loss for pending offline relief requests or emergency dispatches.
- **Fail-Safe Operation**: Graceful error UI when remote services fail or encounter rate limits.

## 5. Execution Procedure
1. Verify offline data queuing and disk persistence configurations.
2. Review automated chaos and fault-injection test reports.
3. Audit memory profiles and lifecycle listener unregistration across all 13 activities.
4. Document reliability certification in `168-phase-10-2-resilience.md`.
