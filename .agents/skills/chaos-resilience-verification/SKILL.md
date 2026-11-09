---
name: chaos-resilience-verification
description: Controlled fault injection, network partition simulation, circuit breaker tripping, high-concurrency stress, and recovery verification.
---

# Chaos & Resilience Verification Skill

## 1. Purpose
Design, execute, and evaluate controlled fault injection experiments, synthetic network outages, backend degradation drills, and concurrency stress tests to verify system recovery and user safety during humanitarian crises.

## 2. Activation Criteria
Activate this skill when:
- Conducting controlled fault injection drills against network interfaces, database clients, or local caches.
- Simulating high-concurrency multi-user load (500+ ops/sec) to detect race conditions or deadlocks.
- Validating client degradation states (serving cached data when cloud is unreachable).
- Measuring recovery time (MTTR) and verifying data consistency following simulated outages.

## 3. Project Context
- **Fault Injection Targets**:
  - Network transport: Simulating connection flapping, 100% packet loss, and high latency (5000ms+).
  - Firebase backend: Simulating 503 Service Unavailable and rate-limit responses.
  - Device battery: Simulating low-power states to verify GPS sampling reduction.

## 4. Engineering Standards
- **Safe Blast Radius**: Chaos testing must run in local JVM tests or disposable non-production environments only.
- **Fail-Safe Operation**: When cloud services fail, the client must display graceful offline indicators without freezing the UI.
- **Data Preservation**: Unsent survivor requests and distributor breadcrumbs must remain cached on device disk.

## 5. Step-by-Step Execution Procedure
1. **Hypothesis Formulation**: State expected system behavior when a specific component fails.
2. **Controlled Injection**: Intercept network or database calls to simulate failure conditions.
3. **Behavioral Inspection**: Verify circuit breaker trips, retries apply exponential backoff, and UI remains responsive.
4. **Recovery Attestation**: Restore dependency and measure time to full service restoration.

## 6. Decision Tree for Ambiguous Cases
- *Should chaos tests run against live production Firebase instances?*
  -> Absolutely not. All chaos experiments must run strictly against mock or local sandbox instances.

