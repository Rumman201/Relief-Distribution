---
name: integration-contract-engineering
description: Architect, test, and maintain external integrations with Firebase Auth, Google Play Services, Google Maps API, and telecommunications adapters.
---

# Integration Contract Engineering

This skill defines engineering practices for external cloud services, third-party mobile SDKs, and platform APIs.

## Core Directives

1. **Adapter Isolation**: All direct invocations of third-party SDKs must reside behind domain-oriented repository or adapter interfaces.
2. **Defensive Error Mapping**: External SDK exceptions must be caught and translated into strongly typed domain error representations.
3. **API Key Protection**: Secrets and API keys must never be committed to source control; they must be provided via local properties or secure environment configuration.
4. **Platform Contract Compliance**: Adhere to Android platform requirements (e.g. `ACTION_DIAL` instead of `ACTION_CALL`).

## Verification Checklist

- [ ] External SDK calls wrapped in repository abstractions.
- [ ] Failure paths and timeout mappings verified through unit test mocks.
- [ ] No secrets committed to git.

