---
name: agentic-workflow-engineering
description: Bounded tool-using agent architectures, approval boundaries, relief dispatch orchestration, tool permissions, and idempotent execution.
---

# Agentic Workflow Engineering Skill

## 1. Purpose
Govern the design, permissions, safety boundaries, and orchestration of autonomous and semi-autonomous tool-using agents in emergency management systems.

## 2. Activation Criteria
Activate this skill when:
- Implementing multi-step automated disaster workflows (e.g. aid allocation, route proposal, inventory reservation).
- Exposing application tools to LLMs or autonomous worker loops.
- Enforcing human-in-the-loop approval gates for consequential operations.

## 3. Engineering Invariants & Standards
1. **Bounded Autonomy**: Agents may propose, calculate, and rank relief distribution plans, but irreversible or consequential actions (dispatching supplies, deleting records, broadcasting emergency alerts) require explicit human approval.
2. **Least Privilege**: Agent tools must only receive the minimum necessary database and device permissions.
3. **Idempotency**: All tool actions must accept idempotency keys to ensure duplicate delivery or retries cause zero side effects.
4. **Observable Execution**: Every agent step, tool invocation, input parameters, and return status must be logged in an audit trail.

## 4. Execution Procedure
1. **Define Tool Schemas**: Explicitly specify tool name, arguments, types, error codes, and side-effect classification.
2. **Implement Approval Gates**: Flag any consequential tool call with an `AWAITING_APPROVAL` status before execution.
3. **Execution Limits**: Enforce maximum steps, step timeouts, and token caps to prevent unbounded loops.
4. **Failure Recovery**: Persist state across tool failures and support clean cancellation without orphan state.
