---
name: production-observability-engineering
description: OpenTelemetry instrumentation, structured JSON diagnostic logging, trace correlation, and client-side error telemetry.
---

# Production Observability Engineering Skill

## 1. Purpose
Implement, maintain, and standardize structured diagnostic logging, correlation tracing, metrics capture, and error observability across the mobile client and cloud backend of the Relief Distribution System.

## 2. Activation Criteria
Activate this skill when:
- Designing or modifying application logging infrastructure (`AppLogger.java` or telemetry collectors).
- Implementing structured log schemas, trace IDs, and correlation identifiers for distributed events.
- Monitoring real-time client performance, error rates, and connection states.
- Auditing log sanitization to prevent sensitive PII (credentials, exact survivor addresses) from reaching operational logs.

## 3. Project Context
- **Client Logging**: `AppLogger.java` provides unified diagnostic logging with environment-aware filtering (stripped in release builds via ProGuard/R8).
- **Structured Fields**: Timestamp, Log Level, Component Tag, Trace Correlation ID, Operation Name, and Sanitized Context Attributes.
- **Observability Invariants**: Zero plain-text credentials, passwords, or raw phone numbers in log entries.

## 4. Engineering Standards
- **Structured Output**: Log events must follow standard key-value or JSON formatting for automated ingestion and indexing.
- **Correlation Propagation**: Multi-step operations (e.g., request creation -> AI triage -> distribution dispatch) must share a common `traceId`.
- **Performance Budget**: Logging calls must incur sub-millisecond execution overhead (< 0.1ms per call) and avoid heap allocations on critical rendering loops.

## 5. Step-by-Step Execution Procedure
1. **Schema Formulation**: Define canonical log attributes (level, timestamp, traceId, component, message).
2. **Sanitization Filter**: Pass all dynamic attributes through `SecuritySanitizer` before emitting logs.
3. **Trace Context Management**: Generate or propagate UUID-based correlation identifiers across asynchronous workflows.
4. **Log Retention & Routing**: Route debug logs to Logcat and structured diagnostic logs to operational telemetry sinks.

## 6. Decision Tree for Ambiguous Cases
- *Can user IDs be logged in production telemetry?*
  -> User IDs (Firebase UIDs) may be logged as non-identifying operational pseudonyms, but email addresses, passwords, and survivor names must be masked.

