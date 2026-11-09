---
name: domain-logic-verification
description: Discover, enforce, test, and guarantee business invariants, role workflows, and lifecycle state machines in disaster relief operations.
---

# Domain Logic Verification

This skill defines engineering procedures to discover, model, and rigorously verify domain invariants and state transitions in relief distribution workflows.

## Core Directives

1. **Explicit Invariant Modeling**: Business constraints (e.g. valid coordinate ranges, non-empty roles, valid relief status progressions) must be modeled as pure domain invariants.
2. **State Machine Integrity**: Status transitions (e.g. `PENDING` -> `ACCEPTED` -> `EN_ROUTE` -> `FULFILLED`) must strictly forbid illegal state skips or retroactive mutations.
3. **Role Boundary Enforcement**: Actions reserved for Distributors must never be executed by Survivors, and vice-versa, verified at both client ViewModel and server database rule boundaries.
4. **Boundary and Boundary-Adjacent Testing**: Test every boundary value (e.g., latitude `90.0`, `-90.0`, `90.0001`, empty strings, null values).

## Verification Checklist

- [ ] Domain models reject invalid states upon construction or setter calls.
- [ ] Valid state transitions succeed deterministically; invalid state transitions throw or emit structured errors.
- [ ] 100% test coverage on pure domain validation logic.

