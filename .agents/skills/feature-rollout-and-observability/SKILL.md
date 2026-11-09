---
name: feature-rollout-and-observability
description: Feature flags, graceful degradation, circuit breakers, structured logging, crash containment, and release readiness verification.
---

# Feature Rollout and Observability Skill

## 1. Purpose
Provide operational safeguards, runtime feature switches, structured diagnostic logging, and progressive rollout controls for advanced features.

## 2. Activation Criteria
Activate this skill when:
- Deploying new high-impact features (AI triage, automated inventory reservation).
- Implementing circuit breakers or kill-switches for external API failures.
- Monitoring runtime system health, error rates, and feature adoption.

## 3. Engineering Invariants & Standards
1. **Zero Silent Crashes**: Wrap external API integrations and probabilistic AI calls in try-catch blocks with structured logging via `AppLogger`.
2. **Deterministic Kill Switches**: Maintain local feature flags allowing dangerous or experimental capabilities to be toggled off instantly.
3. **Audit Trail**: Operational state transitions must log timestamp, user ID, previous state, and target state.
4. **Rollback Simplicity**: Database schemas and API contracts must maintain backward compatibility to support immediate rollback.

## 4. Execution Procedure
1. **Define Feature Flag**: Define feature flags with default safe states in configuration.
2. **Instrument Structured Logs**: Use `AppLogger.d()`, `AppLogger.w()`, and `AppLogger.e()` with structured tags and sanitized payloads.
3. **Verify Health Metrics**: Confirm that critical user flows succeed and unhandled exceptions remain zero.
