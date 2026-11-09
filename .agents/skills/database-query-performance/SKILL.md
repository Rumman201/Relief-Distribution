---
name: database-query-performance
description: Database query optimization, index tuning, transaction latency minimization, query plan inspection, and data access efficiency for Firebase Realtime Database and mobile SQLite.
---

# Database Query Performance Skill

## 1. Purpose
Optimize cloud datastore and local database access patterns, query performance, indexing strategies, and transactional throughput while maintaining strict ACID/data consistency invariants.

## 2. Activation Criteria
Activate this skill when:
- Reviewing or updating database indexing rules (`.indexOn` in `database.rules.json`).
- Profiling query latency for filtered collections (`ReliefRequests`, `Inventory`, `Notifications`).
- Optimizing atomic transactions (`runTransaction`) in request claiming or stock reservations.
- Addressing slow query warnings or excessive data transfer over WebSockets.

## 3. Project Context
- **Cloud Datastore**: Firebase Realtime Database.
- **Key Indexed Nodes**:
  - `/ReliefRequests`: Indexed on `["survivorId", "userId", "status", "priority", "timestamp", "createdAt"]`.
  - `/Inventory`: Indexed on `["distributorId", "category", "quantityAvailable", "lastUpdated"]`.
  - `/Notifications/$userId`: Indexed on `["timestamp", "read", "isRead"]`.
  - `/DisasterAlerts`: Indexed on `["isActive", "issuedAt", "severity"]`.

## 4. Engineering Standards
- **Zero Full-Tree Scans**: Every client query with `.orderByChild().equalTo()` must be backed by a corresponding server-side `.indexOn` definition.
- **Transaction Scope Minimization**: Keep transaction mutate functions pure and minimal; avoid performing I/O or logging inside `Transaction.Handler.doTransaction()`.
- **Atomic Consistency**: Never replace atomic transactions with unsynchronized read-then-write calls merely to reduce latency.

## 5. Step-by-Step Execution Procedure
1. **Query Pattern Audit**:
   - Verify all `.orderByChild()` calls in Activity/Repository code match declared `.indexOn` keys.
2. **Shallow vs Deep Retrieval**:
   - Avoid pulling entire collection sub-trees when only metadata or filtered subsets are required.
3. **Transaction Benchmarking**:
   - Benchmark concurrent transaction contention in simulated multi-user request claim tests.

## 6. Decision Tree for Ambiguous Cases
- *Can we bypass transactions on inventory deduction to make it faster?*
  -> Never. Bypassing transactions causes race conditions and phantom allocation of disaster supplies.
