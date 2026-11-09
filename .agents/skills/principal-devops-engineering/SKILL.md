---
name: principal-devops-engineering
description: Platform architecture, operational reliability, release automation, CI/CD governance, and production readiness for disaster relief.
---

# Principal DevOps Engineering Skill

## 1. Purpose
Architect, govern, and continuously enhance the platform operations, automated delivery lifecycles, and environment infrastructure of the Relief Distribution System to guarantee uninterrupted mission-critical availability in humanitarian disaster relief zones.

## 2. Activation Criteria
Activate this skill when:
- Designing or auditing end-to-end platform architecture and CI/CD pipelines.
- Establishing deployment governance, automated release gates, and environment parity.
- Evaluating operational readiness, system resilience, and infrastructure security.
- Standardizing DevOps practices across build, packaging, testing, and deployment.

## 3. Project Context
- **Target Application**: Android Disaster Relief Client (`com.example.reliefdistribution`) + Firebase Cloud Infrastructure.
- **Build Infrastructure**: Gradle 8.9 (Kotlin DSL), Android Gradle Plugin 8.7.3, Java 17/11 compilation target.
- **Continuous Integration**: GitHub Actions (`.github/workflows/android.yml`) running hermetic unit, security, concurrency, and performance validation suites.
- **Operational Boundaries**: Zero tolerance for unverified production deployments; strict separation of development, staging, and production configurations.

## 4. Engineering Standards
- **Hermetic Reproducibility**: Builds and tests must produce deterministic results regardless of host system configuration.
- **Automated Verification Gates**: No binary or release candidate may proceed without 100% test passage across unit, security, and performance test suites.
- **Zero Secrets in Code**: Secrets and keys must be injected strictly via environment variables or encrypted secrets management.
- **Traceable Provenance**: Every release artifact must be traceable to a specific Git commit hash, build run, and cryptographic digest.

## 5. Step-by-Step Execution Procedure
1. **Pipeline Assessment**: Audit existing workflow definitions, dependencies, and caching policies.
2. **Security & Quality Gating**: Enforce static analysis, unit test suites, and lint verification prior to packaging.
3. **Artifact Hardening**: Configure release obfuscation, symbol file retention, and deterministic checksum generation.
4. **Operational Sign-Off**: Validate readiness against production SLAs, monitoring hooks, and disaster recovery runbooks.

## 6. Decision Tree for Ambiguous Cases
- *Should test failures ever be ignored during CI builds?*
  -> Never. Any test failure halts the pipeline immediately and blocks artifact promotion.

