---
name: production-ai-integration
description: Production-grade AI integration, model evaluation, structured generation, deterministic guardrails, fallback architectures, and disaster relief triage intelligence.
---

# Production AI Integration Skill

## 1. Purpose
Define architecture and reliability standards for integrating artificial intelligence and machine learning models into disaster relief workflows without compromising safety or data integrity.

## 2. Activation Criteria
Activate this skill when:
- Designing AI-assisted relief prioritization, triage scoring, or distress text classification.
- Generating structured outputs from unstructured disaster survivor requests.
- Defining guardrails, schema validation, and fallback mechanisms for probabilistic models.

## 3. Engineering Invariants & Standards
1. **Deterministic Authority**: AI outputs are probabilistic and untrusted; safety rules, permission checks, and physical dispatch authority remain deterministic.
2. **Strict Schema Validation**: All model outputs must conform to a strongly typed schema; invalid or malformed outputs trigger an immediate deterministic fallback.
3. **Data Minimization**: Never transmit PII, credentials, or private survivor phone numbers to external AI APIs unnecessarily.
4. **Offline Resilience**: When internet access is severed in disaster zones, the system must seamlessly fall back to local rule-based heuristic triage.

## 4. Execution Procedure
1. **Use-Case Validation**: Verify that AI solves a real ambiguity (e.g. natural language urgency extraction) that cannot be solved by a simple regex.
2. **Contract Definition**: Define strict input context schema and output JSON schema (e.g. `urgencyScore`, `priorityTier`, `rationale`, `extractedKeywords`).
3. **Prompt & Guardrail Engineering**: Constrain reasoning, forbid hallucinations, and enforce strict enum outputs.
4. **Fallback Implementation**: Implement a deterministic rule engine that produces equivalent schema outputs if the model fails, times out, or returns invalid data.
5. **Evaluation Suite**: Test against an evaluation dataset covering typical, edge-case, and adversarial inputs.
