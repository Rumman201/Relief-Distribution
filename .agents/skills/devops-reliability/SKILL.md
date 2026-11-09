---
name: devops-reliability
description: Build automation, Gradle dependency management, ProGuard/R8 configurations, GitHub Actions CI/CD pipelines, and environment configuration.
---

# DevOps & SRE Reliability Skill

## 1. Purpose
Establish reproducible, deterministic build pipelines, continuous integration, dependency security, release packaging, and crash observability for the Relief Distribution Android project.

## 2. Activation Criteria
Activate this skill when:
- Configuring Gradle build scripts (`build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`).
- Managing Android SDK/NDK requirements, wrapper versions, and environment variables.
- Setting up GitHub Actions CI workflows for automated pull request checks and artifact releases.
- Configuring crash reporting (Firebase Crashlytics) and logging.

## 3. Project Context
- **Build System**: Gradle 8.9 with Gradle Wrapper, Android Gradle Plugin (AGP) 8.7.3.
- **Java Runtime**: OpenJDK 17.
- **Configuration Deficiencies Discovered**:
  - Missing Android CI workflow (`.github/workflows/ci.yml`).
  - `.gitignore` was configured for Python instead of standard Android development, missing `.idea`, `*.iml`, `app/build`, and local configuration ignores.
  - Hardcoded absolute SDK directory in `local.properties` belonging to a different machine/user (`C:\Users\DELL\...`).
  - Duplicate dependencies in `app/build.gradle.kts`.

## 4. Specialist Engineering Standards
- **Hermetic & Portable Builds**: A newly cloned repository must build cleanly on any developer machine or CI runner without manual configuration hacks.
- **Automated Quality Gates**: CI pipeline must execute on every push and PR:
  1. Gradle wrapper validation.
  2. Lint checks (`./gradlew lint`).
  3. Unit test execution (`./gradlew testDebugUnitTest`).
  4. Release assemble check (`./gradlew assembleRelease`).
- **Secret Isolation**: CI builds must inject keys via GitHub Secrets into `local.properties` or environment variables; no credentials committed to git.

## 5. Step-by-Step Execution Procedure
1. **Repository Sanitization**: Fix root `.gitignore` to cover standard Android, JetBrains, and Gradle artifacts.
2. **Environment Standardization**: Set up user environment variables (`JAVA_HOME`, `ANDROID_HOME`) and configure `gradle.properties` for memory allocation (`org.gradle.jvmargs=-Xmx2048m`).
3. **CI Pipeline Setup**: Create `.github/workflows/android.yml` with steps to setup Java 17, cache Gradle dependencies, and run build + tests.
4. **ProGuard Hardening**: Configure `proguard-rules.pro` to keep Firebase models while obfuscating internal business logic.

## 6. Decision Tree for Ambiguous Cases
- *Should local.properties be committed to Git?*
  -> Never. `local.properties` is machine-specific and contains local SDK paths and local secrets. It must always be in `.gitignore`. Provide a `local.properties.example` template instead.

## 7. Tool Instructions
- Validate Gradle wrapper: `.\gradlew.bat --version`.
- Run full build: `.\gradlew.bat assembleDebug`.
- Check dependency updates: `.\gradlew.bat dependencyUpdates` (if plugin configured).

## 8. Safety Constraints
- Never configure CI pipelines that upload signed release keystores to unsecured endpoints.
- Never use `--stacktrace` or debug logs in public CI logs when secrets are in environment variables.

## 9. Verification Steps
- Verify local clean build: `.\gradlew.bat clean assembleDebug`.
- Verify Git status is clean of generated build artifacts.

## 10. Expected Output & Evidence Format
- Clean `.gitignore` and CI configuration files.
- Documented build steps in `docs/upgrade/01-project-inventory.md`.

