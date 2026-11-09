---
name: project-intelligence-audit
description: Comprehensive reconnaissance, repository exploration, dependency mapping, feature inventory, and risk assessment for the Relief Distribution Android project.
---

# Project Intelligence Audit Skill

## 1. Purpose
This skill provides a systematic protocol for inspecting the codebase, identifying dependencies, cataloging features, auditing architecture, and uncovering hidden regressions, misconfigurations, and technical debt in native Android (Java/Kotlin, Gradle, Firebase) applications.

## 2. Activation Criteria
Activate this skill whenever:
- Entering a new codebase or beginning a modernization phase.
- Conducting a system-wide structural or architectural assessment.
- Auditing dependencies, build configurations, and external SDK integrations.
- Formulating a comprehensive feature inventory or impact analysis.

## 3. Project Context
- **Application**: Relief Distribution System (Android native client for disaster relief coordination).
- **Package**: `com.example.reliefdistribution`
- **Core Technology Stack**: Android SDK (minSdk 24, targetSdk 34, compileSdk 34), Java 11/17, Gradle 8.9 (Kotlin DSL), AGP 8.7.3, Firebase Authentication, Firebase Realtime Database, Google Play Services Maps & Location.
- **Key Personas**: Disaster Survivors (requesting aid, finding points, viewing relief updates) and Relief Distributors (sharing live GPS location, managing distribution status).

## 4. Specialist Engineering Standards
- Never assume an architecture from directory names alone; verify with AST or file content inspection.
- Inspect both manifest declarations (`AndroidManifest.xml`) and actual runtime usage (`Activity`, `Intent`, `Service`, `BroadcastReceiver`).
- Identify hardcoded configuration values, plain-text credentials, and shadow dependencies.
- Map data flows from user input down to persistence and network layers.

## 5. Step-by-Step Execution Procedure
1. **Workspace Discovery**: Identify repository structure, build manifests (`settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`), and version control state.
2. **Configuration Audit**: Inspect `local.properties`, `google-services.json`, ProGuard rules, and XML values (`strings.xml`, `colors.xml`, `themes.xml`).
3. **Component Inventory**: Map all declared Android activities, intent filters, permissions, and exported flags in `AndroidManifest.xml`.
4. **Codebase Exploration**: Read and categorize all source files (`app/src/main/java/**`), tracing execution from the launcher activity.
5. **Data Model & Backend Mapping**: Audit data entities (e.g. `User.java`), database references (`FirebaseDatabase`), and external services.
6. **Risk Analysis**: Document defects, security vulnerabilities, deprecations, and race conditions in a structured register.

## 6. Decision Tree for Ambiguous Cases
- *Is a component exported if `android:exported` is omitted?*
  -> On API 31+, `android:exported` is required if intent filters are present. If missing, flag as build blocker.
- *Is an unreferenced Java class dead code?*
  -> Check AndroidManifest, reflection, layout XML references, and dynamic instantiation. If unreferenced anywhere, mark as candidate for cleanup in Phase 2.

## 7. Tool Instructions
- Use `run_command` to execute Gradle inspection: `.\gradlew.bat tasks`, `.\gradlew.bat dependencies`.
- Use `read_file` to inspect source code and resources directly.

## 8. Safety Constraints
- Never delete untracked user code without explicit confirmation.
- Never commit secret API keys or credentials to public or shared version control.
- Never run destructive Git commands (`git reset --hard`, `git clean -fdx`).

## 9. Verification Steps
- Cross-check every identified issue against real file paths and line numbers.
- Verify that every listed feature corresponds to implemented code or documented stub.

## 10. Expected Output & Evidence Format
Deliver findings in standard Markdown tables with:
- Issue ID (`AUD-001`, etc.)
- Component / File Link
- Severity (P0, P1, P2, P3)
- Root Cause & Description
- Evidence & Verification Criteria

