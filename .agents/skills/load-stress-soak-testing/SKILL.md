---
name: load-stress-soak-testing
description: Controlled load testing, benchmark reproducibility, stress testing, soak testing for memory leaks, and performance regression prevention.
---

# Load, Stress & Soak Testing Skill

## 1. Purpose
Design, automate, and execute controlled load tests, microbenchmarks, stress tests, and soak tests to evaluate system throughput, tail latencies, memory stability, and recovery behavior under heavy workloads.

## 2. Activation Criteria
Activate this skill when:
- Designing or executing performance microbenchmarks and simulated workload generators.
- Performing stress testing to identify capacity breaking points.
- Running soak tests to detect memory leaks, connection leaks, or queue backlog runaway.
- Measuring p50, p95, and p99 latencies under concurrent operations.

## 3. Project Context
- **Workload Scenarios**:
  - High-Volume Telemetry Ingestion: 100+ concurrent GPS fixes per second into buffer.
  - Concurrent Relief Request Processing: 50+ concurrent requests evaluated by AI triage and automator.
  - Heavy Cache Access: 1,000+ concurrent reads/writes with active eviction.
- **Testing Guardrails**:
  - Hermetic testing within local JVM unit test harnesses; zero unauthorized production cloud flooding.

## 4. Engineering Standards
- **Zero Fabricated Benchmarks**: All throughput, latency, and resource metrics must be empirically measured and verified.
- **Statistical Rigor**: Collect multiple samples (minimum 100 iterations) to filter out JVM JIT warmup noise.
- **Leak Detection**: Soak tests must measure heap memory before and after sustained execution to verify zero uncollected objects.

## 5. Step-by-Step Execution Procedure
1. **Scenario Definition**:
   - Model arrival rates, payload distributions, and concurrency levels.
2. **Benchmark Execution**:
   - Run warmup passes, followed by timed measurement passes capturing execution times and memory deltas.
3. **Evidence Recording**:
   - Document commands, iterations, percentiles (p50, p95, p99), and throughput in test evidence records.

## 6. Decision Tree for Ambiguous Cases
- *Should microbenchmarks include JVM JIT warmup iterations?*
  -> Yes. Execute warmup iterations prior to recording timing metrics to avoid skew from classloading and bytecode interpretation.
