---
name: infrastructure-as-code-engineering
description: Declarative configuration of cloud datastores, Firebase rules, indexes, and automated infrastructure reproducibility.
---

# Infrastructure as Code Engineering Skill

## 1. Purpose
Define, version-control, validate, and manage all cloud resources, datastore rules, security policies, and database indexes using declarative Infrastructure as Code (IaC) configuration files.

## 2. Activation Criteria
Activate this skill when:
- Authoring or modifying Firebase declarative configurations (`firebase.json`, `.firebaserc`, `database.rules.json`).
- Managing cloud datastore indexes, security rules, and project targets.
- Establishing repeatable environment provisioning for development, staging, and production.
- Auditing infrastructure drifts or reconciling state between cloud consoles and code repositories.

## 3. Project Context
- **Datastore**: Firebase Realtime Database (`relief-distribution-2w5-default-rtdb.firebaseio.com`).
- **Declarative Artifacts**:
  - `firebase.json`: Top-level Firebase project and RTDB rule bindings.
  - `database.rules.json`: Role-based security rules and `.indexOn` query specifications.
  - `.firebaserc`: Multi-project alias configurations (default, staging, production).

## 4. Engineering Standards
- **Versioned Declarations**: Cloud configuration must be maintained in version control alongside application code.
- **Dry-Run Validation**: Rule modifications must be validated using the Firebase CLI rule compiler (`firebase database:rules:canary` or simulator) prior to release.
- **Zero Manual Drift**: Console-based manual configuration adjustments are prohibited; all changes must flow through code commits.

## 5. Step-by-Step Execution Procedure
1. **Schema & Rule Modeling**: Define security and indexing rules in `database.rules.json`.
2. **Project Binding**: Configure project targets in `.firebaserc` and deployment targets in `firebase.json`.
3. **Automated Validation**: Verify syntactic correctness and rule invariants using automated checks.
4. **Declarative Deployment**: Execute deterministic rule deployments via CI/CD pipelines.

## 6. Decision Tree for Ambiguous Cases
- *Should database indexes be added manually in the Firebase console?*
  -> No. All indexing rules (`.indexOn`) must be defined in `database.rules.json` to prevent drift.

