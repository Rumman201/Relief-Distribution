---
name: principal-qa-orchestration
description: Risk-based test strategy, verification matrices, quality gates, defect prioritization, and full-system QA orchestration for disaster relief.
---

# Principal QA Orchestration Skill

## 1. Purpose
Orchestrate end-to-end full-system quality assurance, establish risk-based test coverage matrices, enforce rigorous release gates, and coordinate independent specialist QA verification across mobile presentation, domain logic, backend APIs, cloud datastores, AI models, and IoT telemetry.

## 2. Activation Criteria
Activate this skill when:
- Establishing or updating the master Quality Assurance strategy and test matrices.
- Prioritizing defects by severity (Blocker, Critical, High, Medium, Low) and blast radius.
- Defining release gates and acceptance criteria across functional and nonfunctional dimensions.
- Coordinating multi-agent verification and synthesizing cross-tier test evidence.

## 3. Project Context
- **Target Application**: Android Disaster Relief Platform (`com.example.reliefdistribution`).
- **Critical Journeys**:
  - Survivor Aid Request: Form input -> GPS capture -> AI triage scoring -> Firebase RTDB submission.
  - Distributor Dispatch: Live request list -> Status filtering -> Map navigation -> Atomic claim transaction.
  - Depot Inventory: Stock deduction -> Low-stock alerting -> Category filtering -> Concurrency safety.
  - Edge IoT: Background GPS tracking -> Stationary compaction -> Offline ring buffering -> Presence disconnect cleanup.

## 4. Engineering Standards
- **Zero Fabricated Passes**: Every test claim must be validated with an observed exit code 0 from `./gradlew.bat testDebugUnitTest` or instrumented test runners.
- **Fail-Closed Release Gates**: A candidate with an unresolved Blocker or Critical defect cannot receive a GO recommendation.
- **Independent Verification**: Implementation agent claims must be independently challenged with edge cases, invalid inputs, and stress conditions.

## 5. Step-by-Step Execution Procedure
1. **Quality Baseline Analysis**: Map out all functional routes, background services, and data models.
2. **Risk-Based Prioritization**: Classify test scenarios into P0 (Release Blocking), P1 (High), P2 (Medium), P3 (Low).
3. **Execution Coordination**: Trigger unit, integration, visual, accessibility, security, and resilience test suites.
4. **Gate Evaluation**: Assess Gates A through R using empirical metrics, logs, and artifacts.

## 6. Decision Tree for Ambiguous Cases
- *Can a release be approved if an edge-case test is failing intermittently?*
  -> No. Intermittent failure indicates non-deterministic state or a race condition that must be isolated and remediated.

