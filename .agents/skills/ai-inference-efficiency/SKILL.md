---
name: ai-inference-efficiency
description: Model request profiling, triage heuristic optimization, assessment memoization, prompt and context reduction, and sub-millisecond AI triage latency.
---

# AI Inference Efficiency Skill

## 1. Purpose
Optimize AI triage inference pipelines, heuristic evaluation algorithms, assessment memoization caching, and context payload efficiency, achieving sub-millisecond evaluation latency without sacrificing triage accuracy, groundedness, or safety.

## 2. Activation Criteria
Activate this skill when:
- Profiling `ReliefTriageAdvisor` evaluation latency, CPU consumption, or memory overhead.
- Implementing memoization caches for identical aid requests and emergency categories.
- Pruning redundant tokens, stopwords, or regex evaluations in natural language triage parsing.
- Verifying that speed optimizations preserve 100% of Phase 5 quality benchmarks and safety guardrails.

## 3. Project Context
- **AI Engine**: `ReliefTriageAdvisor` (computes `aiUrgencyScore` $[0, 100]$ and `aiPriorityTier`).
- **Baseline Latency**: ~0.05ms to 0.5ms per heuristic evaluation on mobile CPU.
- **Optimization Opportunities**:
  - Pre-compiled keyword regex patterns (eliminating on-the-fly regex compilation).
  - Assessment memoization: Caching triage results for frequent identical category/item combinations.
  - Early-exit critical phrase matching: Instantly assigning critical priority when life-danger tokens ("trapped", "bleeding") are detected.

## 4. Engineering Standards
- **Quality Invariance**: Performance optimizations must produce identical urgency scores and priority tiers as baseline benchmarks.
- **Deterministic Bounds**: Bounded clamping to $[0, 100]$ must remain non-negotiable.
- **Sub-Millisecond Target**: Heuristic evaluation must complete in under 1 millisecond on standard mobile processors.

## 5. Step-by-Step Execution Procedure
1. **Algorithm Profiling**:
   - Measure execution time across a batch of diverse disaster relief requests.
2. **Regex Pre-compilation**:
   - Ensure all keyword patterns are static pre-compiled `Pattern` constants.
3. **Memoization Integration**:
   - Introduce bounded LRU assessment cache with capacity for 256 frequent emergency patterns.
4. **Benchmark Verification**:
   - Verify throughput exceeds 10,000 evaluations/second on JVM test harnesses.

## 6. Decision Tree for Ambiguous Cases
- *Can we skip text notes analysis to achieve faster triage?*
  -> No. Emergency notes contain critical life-safety details ("trapped under rubble"). Optimization must accelerate analysis, never discard essential signals.
