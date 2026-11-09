# Phase 8 — Containerization, Build Emulation & Kubernetes Operations

## 1. Overview & Toolchain Containerization

To guarantee deterministic, clean-room compilation and eliminate workstation-specific build variations, build and test environments for the Relief Distribution System are defined as containerized runners.

### Dockerfile Specification for Hermetic Android CI:
```dockerfile
FROM eclipse-temurin:17-jdk-jammy

ENV ANDROID_SDK_ROOT="/opt/android-sdk"
ENV PATH="${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools"

RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    git \
    unzip \
    libncurses5 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

# Install Android Command-Line Tools
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    curl -o /tmp/tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q /tmp/tools.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools && \
    mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest && \
    rm /tmp/tools.zip

# Accept Licenses and Install SDK Platform 34
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

USER 1001
CMD ["./gradlew", "testDebugUnitTest", "assembleDebug"]
```

---

## 2. Kubernetes Ephemeral Runners

- **Ephemeral Worker Pods**: GitHub Actions runner pods deployed dynamically on Kubernetes clusters to execute pipeline jobs in hermetically sealed pods.
- **Rootless Security**: Container processes run under non-root UID 1001 with restricted capability sets (`drop: ["ALL"]`).
- **Resource Limits**: Configured with 4 CPU cores and 8 GiB RAM to prevent container thrashing during Gradle compilation and lint analysis.

