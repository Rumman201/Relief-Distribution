---
name: container-kubernetes-operations
description: Containerized builds, ephemeral test runners, hermetic Android emulation, and pipeline scaling.
---

# Container and Kubernetes Operations Skill

## 1. Purpose
Define, optimize, and maintain containerized build environments, ephemeral CI/CD runners, and containerized operational tooling for reproducible compilation and testing of the Relief Distribution System.

## 2. Activation Criteria
Activate this skill when:
- Designing or maintaining Dockerfiles for Android build and test environments.
- Configuring ephemeral container runners in CI/CD platforms or Kubernetes clusters.
- Standardizing build toolchain versions (Android SDK, JDK, Gradle) inside container images.
- Establishing isolated sandbox environments for security testing or disaster recovery drills.

## 3. Project Context
- **Base Environment**: Linux/Ubuntu container environments with OpenJDK 17, Android SDK Command-Line Tools, and Google Play Services libraries.
- **Role in Platform**: Provides identical, reproducible, and clean-room build execution to prevent "works on my machine" discrepancies across developer workstations and CI runners.

## 4. Engineering Standards
- **Minimal Image Footprint**: Use multi-stage builds and clean up package manager caches to keep images lightweight.
- **Rootless Execution**: Build containers must run under unprivileged non-root user accounts.
- **Deterministic Pinning**: Pin base images and package dependencies to specific immutable tags or digests.

## 5. Step-by-Step Execution Procedure
1. **Container Specification**: Author Dockerfile defining exact Android SDK, cmdline-tools, and platform versions.
2. **Layer Optimization**: Order instructions to leverage caching for Gradle caches and Android SDK components.
3. **Runner Integration**: Configure container execution within CI/CD pipelines.
4. **Security Scanning**: Scan container base images for known vulnerabilities before deployment.

## 6. Decision Tree for Ambiguous Cases
- *Should container builds download dependencies dynamically on every run?*
  -> No. Dependency directories and Gradle caches should be mounted or restored from shared persistent caches.

