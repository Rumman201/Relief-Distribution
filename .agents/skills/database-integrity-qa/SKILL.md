---
name: database-integrity-qa
description: Firebase RTDB schema constraints, atomic transactions, concurrent reservations, data migrations, and multi-tenant isolation QA.
---

# Database Integrity & Transaction QA Skill

## 1. Purpose
Verify data integrity constraints, atomic multi-node mutations, concurrent stock reservations, schema migration resilience, and tenant isolation policies within the Firebase Realtime Database.

## 2. Activation Criteria
Activate this skill when:
- Testing atomic transactions (`runTransaction`) for aid request claims and inventory deduction.
- Verifying multi-tenant user data isolation (`/users/$uid`, `/notifications/$userId`).
- Auditing database query indexing (`.indexOn`) to eliminate unindexed table scans.
- Validating state machine transitions (Pending -> In Progress -> Completed / Cancelled).

## 3. Project Context
- **Cloud Datastore**: Firebase Realtime Database (`relief-distribution-2w5-default-rtdb`).
- **Critical Nodes**: `/users`, `/relief_requests`, `/inventory`, `/notifications`, `/distributor_locations`.
- **Integrity Rule**: A relief request can only be claimed by an authenticated distributor if its status is currently "Pending".

## 4. Engineering Standards
- **Atomic Operations**: Supply stock deduction and request status updates must be coupled atomically to prevent phantom inventory allocations.
- **Role Boundary Invariant**: User role (`role`) is immutable post-creation; survivor accounts cannot write to `/inventory`.
- **Index Optimization**: All queried child keys (`status`, `userId`, `recipientId`, `distributorId`, `category`) must be indexed.

## 5. Step-by-Step Execution Procedure
1. **Rule Constraint Testing**: Execute negative authorization tests simulating cross-tenant read/write attempts.
2. **Transaction Race Simulation**: Simulate two simultaneous distributors attempting to claim the same pending request.
3. **Data Lifecycle Verification**: Test create, read, update, and soft-delete states across relief requests and inventory.
4. **Index Verification**: Confirm queries execute without triggering index warnings in the Firebase console.

## 6. Decision Tree for Ambiguous Cases
- *Can a user delete another user's relief request?*
  -> No. Only the request creator or an administrator possesses delete or cancel privileges.

