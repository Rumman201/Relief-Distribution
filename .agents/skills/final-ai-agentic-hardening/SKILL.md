---
name: final-ai-agentic-hardening
description: AI triage model quality, prompt-injection defense, deterministic guardrails, structured output validation, approval gates, and operational boundaries.
---

# Final AI & Agentic Hardening Skill

## 1. Purpose & Scope
Hardens the decision-making and triage models in disaster relief coordination, preventing prompt injection, adversarial text-bombs, temporal clock skew manipulation, and non-deterministic tool use.

## 2. Invariants & Guardrails
- **Deterministic Bounds**: Triage scores strictly bounded to $[0, 100]$.
- **Adversarial Resilience**: Input notes truncated to 2,000 characters to prevent algorithmic regex starvation.
- **Clock Skew Defense**: Time delta clamped to non-negative zero (`Math.max(0, now - createdAt)`).
- **Human Approval**: High-consequence operations (e.g. bulk stock zeroing, priority escalation overrides) require explicit authenticated distributor action.
- **Privacy Enforcement**: Scrub PII (names, phone numbers, national IDs) prior to inference caching.

## 3. Step-by-Step Procedure
1. Verify `ReliefTriageAdvisor` scoring weights and risk-keyword extraction.
2. Verify caching memoization logic in `ReliefTriageAdvisor` using tenant-scoped keys.
3. Validate adversarial test payloads in `FullSystemQualityAssuranceTest` and `AiInferenceEfficiencyTest`.
4. Enforce strict type safety and non-null guarantees in triage result objects.
