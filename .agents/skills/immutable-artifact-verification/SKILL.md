---
name: immutable-artifact-verification
description: Cryptographic digest calculation, binary provenance attestation, hermetic artifact verification, and build immutability enforcement.
---

# Immutable Artifact Verification Skill

## 1. Purpose
Ensure binary artifacts staged for release candidates are cryptographically attested, immutable, reproducible, and verifiable against exact Git commit boundaries and metadata hashes.

## 2. Activation Criteria
Activate this skill when:
- Verifying the integrity of APK/AAB candidate artifacts (`app-debug.apk`, `app-release.apk`).
- Computing cryptographic digests (SHA-256, MD5) and binary byte measurements.
- Enforcing hermetic build provenance and checking against uncommitted modifications.
- Validating Android package metadata (Package Name, VersionCode, VersionName, MinSdk, TargetSdk).

## 3. Project Context
- **Target Application**: `com.example.reliefdistribution`
- **Candidate Release**: v1.0.0-rc2
- **Artifact Path**: `app/build/outputs/apk/debug/app-debug.apk`
- **Key Hash Algorithm**: SHA-256
- **Size Budget**: <= 15.0 MB (Actual candidate ~10.0 MB)

## 4. Engineering Standards
- **Cryptographic Rigor**: Checksums must be computed directly via `Get-FileHash` or `sha256sum` on actual binaries.
- **Commit Pinning**: Artifacts must map uniquely to a documented Git commit SHA.
- **Tamper Evidence**: Any alteration to bytecode or resources invalidates the certification.

## 5. Execution Procedure
1. Verify clean Git tree or precisely documented modifications.
2. Build binary via Gradle tasks (`assembleDebug` or `assembleRelease`).
3. Compute SHA-256 digest and byte size of the resulting APK.
4. Verify AndroidManifest parameters match release configuration.
5. Inscribe results in the candidate manifest (`156-phase-10-2-candidate-manifest.md`).
