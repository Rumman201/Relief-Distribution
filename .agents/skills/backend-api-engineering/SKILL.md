---
name: backend-api-engineering
description: Design, validate, test, and document robust backend API contracts, request validation rules, and error response semantics.
---

# Backend API Engineering

This skill defines engineering protocols for designing, specifying, validating, and testing backend API contracts across mobile and cloud services.

## Core Directives

1. **Explicit Validation at Trust Boundaries**: Every input from mobile clients, external webhooks, or third-party APIs must be strictly validated before entering the domain layer.
2. **Deterministic Error Contracts**: Error responses must be structured, predictable, and avoid leaking internal stack traces or database error details.
3. **Backward Compatibility**: Any contract changes must adhere to expand-and-contract patterns to maintain compatibility with distributed mobile clients in the field.
4. **Contract Testing**: Every endpoint or repository operation must have contract tests asserting positive flows, missing field rejections, and permission failures.

## Verification Checklist

- [ ] All required fields have nullity and format assertions.
- [ ] Error envelopes contain standard code, user-friendly message, and optional field-level details.
- [ ] Contract tests executed against mock or local integration endpoints.

