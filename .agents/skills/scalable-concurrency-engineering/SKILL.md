---
name: scalable-concurrency-engineering
description: Bounded concurrency control, thread pool management, queue backpressure, race condition mitigation, and deadlock-free asynchronous execution.
---

# Scalable Concurrency Engineering Skill

## 1. Purpose
Architect, manage, and benchmark thread concurrency, asynchronous execution queues, and backpressure mechanisms across the Android client application to prevent thread starvation, UI freezing, race conditions, and memory leaks.

## 2. Activation Criteria
Activate this skill when:
- Managing background tasks, executor services, and asynchronous worker pools.
- Enforcing bounded parallelism on I/O operations and telemetry queues.
- Investigating race conditions, deadlocks, or concurrent modification exceptions.
- Designing backpressure mechanisms to prevent high-frequency producers from overwhelming consumers.

## 3. Project Context
- **Concurrency Hub**: `AppExecutors` / `ReliefDispatchers`.
- **Worker Pools**:
  - Main Thread: UI rendering, user interaction handling (Zero blocking I/O permitted).
  - Disk / Database Executor: Single-thread or bounded pool for serializing cache and preference writes.
  - Network / Cloud Executor: Bounded thread pool (Core: 2, Max: 4) for API operations.
  - Telemetry Processor: Bounded queue with backpressure for location breadcrumbs.

## 4. Engineering Standards
- **Zero Unbounded Pools**: `Executors.newCachedThreadPool()` is strictly forbidden; always use `ThreadPoolExecutor` with fixed core/max bounds and bounded `ArrayBlockingQueue`.
- **Deterministic Synchronization**: Guard mutable shared state with fine-grained locks or concurrent collections (`ConcurrentHashMap`, `AtomicInteger`).
- **Graceful Rejection Policy**: Configure sensible rejection handlers (`DiscardOldestPolicy` or caller-runs) when queues fill up.

## 5. Step-by-Step Execution Procedure
1. **Executor Architecture**:
   - Provide centralized access to structured executors with lifecycle termination hooks.
2. **Backpressure Implementation**:
   - Clamp task submission rates when queues exceed saturation thresholds.
3. **Concurrency Testing**:
   - Write multi-threaded stress tests verifying absence of race conditions and thread leaks.

## 6. Decision Tree for Ambiguous Cases
- *What should happen when the background telemetry queue is completely saturated?*
  -> Evict the oldest non-emergency telemetry point to prioritize recent data while preventing heap exhaustion.
