---
name: backend-throughput-optimization
description: Backend service integration tuning, async execution optimization, serialization efficiency, connection management, and throughput maximization.
---

# Backend Throughput Optimization Skill

## 1. Purpose
Optimize client-to-cloud backend communication pipelines, asynchronous I/O execution, serialization overhead, and thread pool efficiency, minimizing request latency and maximizing throughput.

## 2. Activation Criteria
Activate this skill when:
- Profiling network and data layer calls (`FirebaseAuthRepository`, `FirebaseReliefRequestRepository`, `FirebaseLocationRepository`).
- Tuning thread pool executors, background dispatchers, and async callbacks.
- Optimizing data serialization (`toMap()`, `fromMap()`) and payload compaction.
- Addressing connection timeouts or network thread contention.

## 3. Project Context
- **Backend**: Firebase Realtime Database (WebSocket multiplexing) and Firebase Auth.
- **Data Serialization**: Reflection and explicit `Map<String, Object>` serialization for domain models (`ReliefRequest`, `InventoryItem`, `LocationTelemetry`).
- **Threads**: Main UI thread must never perform heavy I/O or JSON parsing; background workers must be bounded.

## 4. Engineering Standards
- **Bounded Concurrency**: Never spawn unbounded raw threads (`new Thread().start()`); always use managed, bounded executors.
- **Contract Integrity**: Throughput optimizations must preserve existing API schemas and domain models without breaking data contracts.
- **Non-Blocking Execution**: Ensure all network and heavy computation tasks run off the UI main thread.

## 5. Step-by-Step Execution Procedure
1. **Thread Pool Architecture**:
   - Introduce centralized `ReliefDispatchers` / `AppExecutors` with bounded core/max pool sizes.
2. **Payload Compaction**:
   - Prune empty/null entries from data maps to minimize byte transfer over low-bandwidth disaster networks.
3. **Connection Efficiency**:
   - Utilize single persistent WebSocket connections for multiple Firebase operations.

## 6. Decision Tree for Ambiguous Cases
- *Should we increase thread pool size indefinitely to handle concurrent spikes?*
  -> No. Excessive threads lead to context-switching overhead and memory thrashing. Use bounded pools with bounded work queues.
