---
name: distributed-processing-reliability
description: Ensure asynchronous background operations, event synchronization, queue processing, and retry mechanisms are safe, idempotent, and resilient.
---

# Distributed Processing Reliability

This skill guides the design and testing of asynchronous background processing, network retry mechanisms, and event synchronization.

## Core Directives

1. **Idempotent Mutations**: All retryable network and database mutations must be idempotent. Repeating an operation must produce the identical system state without side-effect proliferation.
2. **Exponential Backoff with Jitter**: Network retries must employ exponential backoff with randomized jitter to prevent thundering herd spikes against cloud infrastructure.
3. **Bounded Queues**: Work queues must have finite bounds to prevent out-of-memory errors on constrained mobile hardware.
4. **Disconnection Cleanup**: Asynchronous real-time sessions must declare disconnection handlers (`onDisconnect()`) to prevent orphaned resources.

## Verification Checklist

- [ ] Duplicate event delivery handled safely without creating duplicate database records.
- [ ] Retries bounded by maximum attempt count and timeouts.
- [ ] Session disconnect handlers tested.

