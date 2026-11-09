---
name: mlops-llmops-engineering
description: Triage model versioning, inference latency tracking, fallback circuit breakers, and offline prompt/model evaluation pipelines.
---

# MLOps and LLMOps Engineering Skill

## 1. Purpose
Operationalize, monitor, version, and evaluate machine learning, triage scoring, and artificial intelligence models deployed within the Relief Distribution System client and backend services.

## 2. Activation Criteria
Activate this skill when:
- Deploying, updating, or versioning heuristic and algorithmic triage models (`ReliefTriageAdvisor.java`).
- Monitoring AI inference latency, memory footprint, cache hit rates, and prediction drift.
- Configuring model fallbacks, circuit breakers, and degradation policies during high-load emergencies.
- Establishing offline evaluation pipelines and automated regression test suites for AI scoring accuracy.

## 3. Project Context
- **AI Engine**: `ReliefTriageAdvisor.java` multi-factor urgency assessment with LRU memoization caching.
- **Scoring Invariants**: Deterministic score range [0, 100], priority categorization (CRITICAL, HIGH, MEDIUM, LOW), and sub-millisecond memoized inference latency (< 0.05ms).
- **Fallback Guarantee**: In case of internal scoring errors or timeout, fallback to deterministic rule-based priority mapping.

## 4. Engineering Standards
- **Model Version Tracking**: Every inference assessment must carry the model version tag (e.g., `model_version: "v2.1-lru"`).
- **Latency Monitoring**: Measure and report p50, p95, and p99 evaluation latencies to ensure AI processing never stutters the main UI thread.
- **Drift Detection**: Monitor distribution of priority classifications over time to detect shifts in scoring behavior.

## 5. Step-by-Step Execution Procedure
1. **Model Validation**: Execute `AiInferenceEfficiencyTest` to verify latency bounds and memoization efficiency.
2. **Benchmark Comparison**: Compare cold vs. warm inference times across varying batch sizes.
3. **Fallback Testing**: Verify deterministic behavior when model inputs contain boundary or missing attributes.
4. **Telemetry Logging**: Record structured scoring events with correlation IDs for operational observability.

## 6. Decision Tree for Ambiguous Cases
- *What should happen if the triage model throws an unhandled exception?*
  -> The system must catch the exception, log the diagnostic error, and safely return the fallback priority without crashing the caller.

