---
name: ai-agentic-security
description: Security guardrails, prompt-injection defense, deterministic bounds, adversarial robustness, and lifecycle safety for disaster AI triage models.
---

# AI & Agentic Security Skill

## 1. Purpose
Protect AI triage models (`ReliefTriageAdvisor`), automated state machines (`ReliefWorkflowAutomator`), and autonomous decision pipelines from prompt injection, adversarial manipulation, urgency escalation attacks, and unsafe automated actions.

## 2. Activation Criteria
Activate this skill when:
- Evaluating or updating `ReliefTriageAdvisor` heuristic algorithms.
- Reviewing workflow automation rules and auto-transition state machines.
- Adding LLM or cloud AI integration boundaries.
- Hardening inputs passed into AI scorers or classifiers.

## 3. Project Security Context
- **AI Engine**: `ReliefTriageAdvisor` (determines `aiUrgencyScore` [0-100] and `aiPriorityTier`).
- **Automation Engine**: `ReliefWorkflowAutomator` (processes inventory reservations and lifecycle triggers).
- **Threats**:
  - Triage Inflation: Malicious actors fabricating life-threatening phrases ("trapped under rubble") to artificially obtain maximum priority for non-essential items.
  - Denial of Service / Triage Starvation: Low-priority requests consuming critical resources by manipulating scoring weights.
  - State Hijacking: Triggering unauthorized automated status transitions via malformed events.

## 4. Engineering Standards
- **Deterministic Hard Bounds**: AI output must be bounded within strict ranges (e.g., score bounded between 0 and 100, valid tiers only).
- **Human-in-the-Loop Override**: Automated triage provides advisory scoring only; human distributors have ultimate dispatch authority.
- **Input Sanitization**: Strip non-text tokens, control characters, and injection markers before processing notes and descriptions.
- **Auditable Scoring**: Every triage assessment must output explicit rationale factors and timestamped score components.

## 5. Step-by-Step Execution Procedure
1. **Adversarial Robustness Review**:
   - Test `ReliefTriageAdvisor` against boundary inputs (extreme numbers, negative quantities, null strings, adversarial text).
   - Ensure scoring logic clamps output within `[0, 100]` under all circumstances.
2. **State Machine Invariant Testing**:
   - Ensure `ReliefWorkflowAutomator` rejects invalid state transitions (e.g., `COMPLETED` -> `PENDING`).
3. **Automated Regression Suite**:
   - Include adversarial and negative test cases in `app/src/test/java/`.

## 6. Decision Tree for Ambiguous Cases
- *Can the AI advisor automatically dispatch inventory without distributor confirmation?*
  -> No. AI triage is strictly advisory; physical dispatch requires human distributor approval.
