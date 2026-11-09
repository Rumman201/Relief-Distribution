---
name: workflow-distributed-qa
description: Event-driven automation QA, lifecycle state transitions, idempotency verification, queue recovery, and retry backoff testing.
---

# Workflow & Distributed Systems QA Skill

## 1. Purpose
Validate event-driven lifecycle automations, state machine integrity, queue recovery, retry backoff algorithms, and idempotent mutations across the asynchronous distributed architecture of the Relief Distribution System.

## 2. Activation Criteria
Activate this skill when:
- Testing `ReliefWorkflowAutomator.java` lifecycle triggers (request acceptance -> inventory reservation -> notification dispatch).
- Verifying retry policies (`RetryPolicy.java`) with exponential backoff and full randomized jitter.
- Testing circuit breaker state transitions (`NetworkResilienceCircuitBreaker.java` CLOSED -> OPEN -> HALF-OPEN).
- Auditing idempotent event handling under duplicate network transmissions.

## 3. Project Context
- **Workflow Engine**: `ReliefWorkflowAutomator.java` coordinating request status transitions.
- **Resilience Layer**: `NetworkResilienceCircuitBreaker.java` protecting backend calls with a 5-failure trip threshold and 30s cool-down.
- **Backoff Engine**: `RetryPolicy.java` with randomized full jitter between 50% and 100% of exponential delay.

## 4. Engineering Standards
- **Strict Idempotency**: Re-executing an already completed workflow trigger must produce zero duplicate side effects.
- **Forward-Only Transitions**: Request state transitions must strictly obey the directed graph (`Pending -> In Progress -> Completed/Cancelled`).
- **Bounded Queues**: Work queues must reject or evict excess requests gracefully rather than exhausting device memory.

## 5. Step-by-Step Execution Procedure
1. **State Machine Fuzzing**: Attempt invalid state transitions (e.g. `Completed -> Pending`).
2. **Duplicate Injection**: Deliver duplicate status update events to test idempotency guards.
3. **Flapping Network Simulation**: Trigger repeated network timeouts to verify circuit breaker trip and recovery cycles.
4. **Queue Capacity Verification**: Saturate thread pool queues to confirm bounded rejection and backpressure.

## 6. Decision Tree for Ambiguous Cases
- *Can a cancelled relief request be marked as Completed?*
  -> No. Terminal states (`Completed`, `Cancelled`) cannot transition to any other status.

