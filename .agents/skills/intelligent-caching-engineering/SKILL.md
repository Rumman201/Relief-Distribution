---
name: intelligent-caching-engineering
description: Multi-tier LRU caching, tenant-isolated cache key design, TTL expiration policies, event-driven cache invalidation, stampede prevention, and cache failure resilience.
---

# Intelligent Caching Engineering Skill

## 1. Purpose
Design, implement, and benchmark intelligent in-memory and local persistent caching architectures that dramatically reduce origin database load and network latency while guaranteeing data freshness, tenant isolation, and security.

## 2. Activation Criteria
Activate this skill when:
- Designing or maintaining in-memory caches (`InMemoryLruCache`, `ReliefDataCache`).
- Defining cache key structures, TTL windows, and eviction policies.
- Implementing cache invalidation on write/update mutations.
- Preventing cache stampedes and cross-tenant data contamination.

## 3. Project Context
- **Cache Targets**:
  - User Profiles (`User`): Low volatility; cache TTL 15 minutes, invalidated on profile edit.
  - Depot Inventory Catalog: Moderate volatility; cache TTL 5 minutes, invalidated on stock transactions.
  - Active Disaster Alerts (`DisasterAlert`): High fan-out, read-heavy; cache TTL 2 minutes.
  - AI Triage Assessments: Memoization cache keyed by category + itemName + distress keywords.
- **Security Constraints**:
  - Invariant: Cache keys for private data MUST include user/tenant ID (`userId + ":" + key`).
  - Never cache raw credentials, passwords, or authentication secrets.

## 4. Engineering Standards
- **Thread Safety**: All cache implementations must use thread-safe data structures (`ConcurrentHashMap`, synchronized LRU locks).
- **Graceful Degradation**: Cache failures must fall back cleanly to origin database fetch without crashing.
- **Bounded Memory**: All caches must enforce hard capacity caps with LRU eviction to prevent OutOfMemoryErrors.

## 5. Step-by-Step Execution Procedure
1. **Cache Key Design**:
   - Construct typed, deterministic keys incorporating tenant isolation.
2. **TTL & Invalidation Design**:
   - Establish appropriate time-to-live and wire event listeners to invalidate stale cache entries upon write operations.
3. **Cache Verification**:
   - Write unit tests verifying hit ratios, eviction of oldest entries upon reaching capacity, and zero cross-user leakage.

## 6. Decision Tree for Ambiguous Cases
- *Should we cache relief requests across all distributors?*
  -> Only if the distributor role permits viewing them, and status updates immediately invalidate or update the cached entry.
