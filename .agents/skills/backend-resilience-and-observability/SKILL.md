---
name: backend-resilience-and-observability
description: Establish structured logging, connection state monitoring, timeout protection, error diagnostics, and graceful recovery for mobile backend systems.
---

# Backend Resilience and Observability

This skill establishes logging, diagnostics, connection observability, and graceful fault recovery for Android mobile clients interacting with backend infrastructure.

## Core Directives

1. **PII-Safe Structured Logging**: Logs must record contextual operational details (component, operation, latency, status code) while masking or redacting personally identifiable information (PII) such as plaintext emails, passwords, and phone numbers.
2. **Realtime Connection Monitoring**: The system must listen to network and cloud connectivity states (`.info/connected`) and provide real-time status to the application layer.
3. **Explicit Timeouts**: Asynchronous operations must not hang indefinitely; explicit timeout durations must govern remote interactions.
4. **Graceful Degradation**: Loss of internet connectivity must degrade smoothly into cached/offline read modes rather than throwing unhandled runtime exceptions.

## Verification Checklist

- [ ] Connection state changes (`CONNECTED`, `CONNECTING`, `DISCONNECTED`) correctly propagated to subscribers.
- [ ] No plaintext passwords or credentials written to logcat or persistent storage.
- [ ] Network timeout and recovery behaviors verified in test suites.

