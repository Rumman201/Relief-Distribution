---
name: intelligent-automation-engineering
description: Event-driven automation, lifecycle state machines, inventory reservation triggers, push notifications, and operational retry pipelines.
---

# Intelligent Automation Engineering Skill

## 1. Purpose
Define resilient, event-driven background automation patterns, trigger systems, notification dispatch pipelines, and inventory synchronization rules for disaster relief logistics.

## 2. Activation Criteria
Activate this skill when:
- Automating state transitions upon external or internal events (e.g. relief request claimed -> reserve inventory).
- Generating automated notifications upon request status modifications.
- Implementing low-stock warning triggers and background sync workers.

## 3. Engineering Invariants & Standards
1. **Trigger Deduplication**: Automation triggers must handle duplicate delivery safely via idempotency checks.
2. **Atomic Inventory Mutation**: Stock reservations and deductions must use atomic database transactions to eliminate negative inventory or race conditions.
3. **Reliable Notification Delivery**: Notifications must be stored durably in `/Notifications/{id}` with recipient UID, unread status, and deep linking payload.
4. **State Machine Invariants**: Only valid lifecycle transitions (`isValidTransition()`) can trigger downstream automated actions.

## 4. Execution Procedure
1. **Model Event Flow**: Define trigger event, precondition verification, action execution, and failure recovery.
2. **State Machine Hooking**: Intercept state changes (e.g. `PENDING` -> `ACCEPTED`) to trigger parallel side-effects (e.g., inventory reservation + notification creation).
3. **Error & Retry Handling**: If notification creation or inventory reservation fails, rollback or log to an unhandled queue with retry backoff.
4. **Testing**: Validate trigger accuracy, duplicate event tolerance, and state consistency under simulated concurrent operations.
