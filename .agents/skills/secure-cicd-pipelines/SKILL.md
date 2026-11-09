---
name: secure-cicd-pipelines
description: Secure GitHub Actions workflows, branch protection, hermetic test execution, artifact provenance, and secrets isolation.
---

# Secure CI/CD Pipelines Skill

## 1. Purpose
Design, harden, and maintain automated Continuous Integration and Continuous Delivery (CI/CD) pipelines with strict security controls, cryptographic artifact verification, and least-privilege execution permissions.

## 2. Activation Criteria
Activate this skill when:
- Authoring or modifying GitHub Actions workflow definitions (`.github/workflows/*.yml`).
- Securing pipeline secrets, environment variables, and build credentials.
- Implementing artifact integrity verification, software bill of materials (SBOM), or checksums.
- Designing branch protection rules, pull request gates, and automated security checks.

## 3. Project Context
- **Platform**: GitHub Actions Ubuntu runners (`ubuntu-latest`).
- **Target Workflows**: Android unit test suite execution (122 tests), lint analysis, release compilation, and artifact archiving.
- **Security Posture**: Least-privilege `GITHUB_TOKEN` permissions (`contents: read`), hashed cache keys, and hermetic execution without dynamic untrusted code downloads.

## 4. Engineering Standards
- **Least Privilege Tokens**: Workflows must explicitly declare minimal required permissions (`permissions: contents: read`).
- **Deterministic Action Pinning**: Third-party GitHub actions must use verified version tags or SHA commits.
- **No In-Flight Secret Echoing**: Secrets must never be printed to stdout, logged in test outputs, or baked into unencrypted artifacts.
- **Fail-Closed Verification**: Build and test steps must terminate the job immediately on non-zero exit codes.

## 5. Step-by-Step Execution Procedure
1. **Workflow Hardening**: Declare top-level and job-level security contexts and permissions.
2. **Caching Strategy**: Configure dependency cache keys using hash sums of Gradle wrapper and build scripts.
3. **Execution Verification**: Run `./gradlew testDebugUnitTest` and static analysis checks.
4. **Artifact Signing & Attestation**: Generate SHA-256 hashes for generated APK binaries and upload securely.

## 6. Decision Tree for Ambiguous Cases
- *Can untrusted pull requests access production deployment credentials?*
  -> No. Pull request workflows run in isolated environments without access to deployment secrets.

