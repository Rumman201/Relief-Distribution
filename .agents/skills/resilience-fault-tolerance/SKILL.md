---
name: resilience-fault-tolerance
description: Failure modeling, circuit breakers, exponential backoff with full jitter, bulkheads, graceful degradation, and disaster-recovery fault simulation.
---

# Resilience & Fault Tolerance Skill

## 1. Purpose
Design, implement, and verify fault tolerance, circuit breakers, exponential backoff with jitter, graceful degradation, and recovery strategies to ensure the system survives intermittent network failures, cloud outages, and degraded mobile conditions without cascading failures.

## 2. Activation Criteria
Activate this skill when:
- Implementing or tuning retry policies, timeouts, and circuit breakers (`NetworkResilienceCircuitBreaker`).
- Designing fallback strategies for network blackouts or cloud datastore unavailability.
- Simulating transient failure modes, socket resets, and API rate limiting.
- Preserving critical operational capabilities under extreme disaster stress.

## 3. Project Context
- **Failure Modes**:
  - Cell tower blackout (Complete network disconnect).
  - Flapping connectivity (Intermittent socket resets, partial packet loss).
  - Firebase rate limiting / quota exhaustion.
  - GPS hardware timeout / satellite signal loss.
- **Circuit Breaker States**:
  - `CLOSED`: Normal operation, calls pass through.
  - `OPEN`: Failures exceeded threshold, calls fail fast to fallback.
  - `HALF-OPEN`: Trial state allowing limited probes to test recovery.

## 4. Engineering Standards
- **Exponential Backoff with Full Jitter**: Never retry immediately in a tight loop. Always apply exponential backoff multiplied by a random jitter factor to prevent synchronized retry storms.
- **Fail Fast & Graceful Fallback**: When cloud services are offline, fail fast to cached local data with clear UI state indicators instead of hanging indefinitely.
- **Security Invariance**: Fault recovery must never bypass authentication or authorization checks.

## 5. Step-by-Step Execution Procedure
1. **Circuit Breaker Implementation**:
   - Provide clean stateful wrapper tracking consecutive failure counts, failure threshold (e.g. 5 failures), and reset timeout (e.g. 30 seconds).
2. **Retry Strategy**:
   - Limit retries to maximum 3 attempts with formula: $T_{wait} = \min(T_{max}, T_{base} \times 2^{attempt}) \times \text{rand}(0.5, 1.0)$.
3. **Fault Simulation Testing**:
   - Test circuit transition from CLOSED -> OPEN upon threshold breach, and OPEN -> HALF-OPEN -> CLOSED upon recovery.

## 6. Decision Tree for Ambiguous Cases
- *Should we retry a request that failed with 401 Unauthorized or 403 Forbidden?*
  -> No. Auth/permission errors are non-transient; retrying them wastes battery and saturates networks. Only retry transient I/O and 5xx errors.
