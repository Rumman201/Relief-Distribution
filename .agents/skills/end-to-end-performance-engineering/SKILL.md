---
name: end-to-end-performance-engineering
description: Workload discovery, baseline collection, performance profiling, bottleneck analysis, optimization ranking, benchmark comparison, and regression prevention.
---

# End-to-End Performance Engineering Skill

## 1. Purpose
Provide principal-level performance engineering leadership, end-to-end bottleneck analysis, critical path profiling, workload modeling, and systematic benchmark verification across Android frontend, cloud database, edge IoT, and AI subsystems.

## 2. Activation Criteria
Activate this skill when:
- Establishing reproducible performance baselines or identifying system bottlenecks.
- Profiling end-to-end latency across user journeys (startup, request dispatch, inventory updates).
- Ranking performance optimizations in the bottleneck register.
- Comparing before-and-after benchmarks under controlled, equivalent test conditions.

## 3. Project Context
- **Application**: Relief Distribution System (`com.example.reliefdistribution`).
- **Critical Workflows**:
  - Survivor Emergency Aid Request: Input -> Validation -> AI Triage -> Cloud Persistence -> Notification.
  - Distributor Fleet Tracking: GPS Fix -> Range Clamping -> Bounded Buffer -> Firebase WebSocket Sync.
  - Relief Inventory Management: Stock Query -> Threshold Evaluation -> Atomic Reservation -> Lifecycle Update.
- **Constraints**:
  - Never compromise Phase 6 security controls (Zero plaintext passwords, strict RBAC, input sanitization, TLS 1.3).
  - All benchmarks must be empirical with zero fabricated gains.

## 4. Engineering Standards
- **Baseline Integrity**: Always measure baseline under identical test hardware, dataset, and network configuration.
- **Tail Latency Focus**: Always track p95 and p99 latencies alongside p50 (median) values.
- **Resource Discipline**: Faster computation that exhausts device battery or memory is unacceptable in disaster relief.

## 5. Step-by-Step Execution Procedure
1. **Critical Path Instrumentation**:
   - Trace latency spans across UI, local validation, network transport, and database commits.
2. **Bottleneck Identification**:
   - Differentiate CPU-bound, I/O-bound, lock contention, and thread starvation issues.
3. **Hypothesis & Implementation**:
   - Formulate targeted optimization hypothesis, implement focused code changes, and benchmark.
4. **Verification**:
   - Validate that functional correctness and security regression tests pass with exit code 0.

## 6. Decision Tree for Ambiguous Cases
- *Should we optimize microsecond utility functions before addressing multi-second database waits?*
  -> No. Always optimize the critical path and high-impact bottlenecks first.
