# Phase 8 — Operational Runbooks & Standard Operating Procedures (SOPs)

## 1. Runbook Index

This document establishes operational procedures for on-call engineers, relief platform administrators, and release managers:
- **SOP-01**: Mitigating Cloud Datastore Outage & Failover
- **SOP-02**: Executing Disaster Recovery Point-in-Time Snapshot Restoration
- **SOP-03**: Emergency Release Rollback Procedure
- **SOP-04**: Distributor Fleet GPS Telemetry Degradation Triage

---

## 2. SOP-01: Mitigating Cloud Datastore Outage & Failover

### Trigger:
- P0 alert: 1-hour error budget burn rate >= 14.4 OR Firebase RTDB HTTP status 503 / unreachable for > 3 minutes.

### Procedure:
1. **Verify Client Offline Behavior**:
   - Confirm that active mobile clients are operating in degraded mode (`ReliefDataCache` serving cached requests and inventory; `OfflineTelemetryBuffer` queuing location breadcrumbs).
2. **Check Cloud Platform Status**:
   - Inspect Google Cloud Status Dashboard (`status.cloud.google.com`) for regional incidents in `us-central1`.
3. **Trigger Standby Redirection (If Regional Outage Confirmed > 15m)**:
   - Update `.firebaserc` default target to secondary standby instance (`relief-distribution-standby`).
   - Trigger emergency DNS / configuration switch via Firebase Remote Config or hotfix release.
4. **Post-Mitigation**:
   - Verify connection resumption and monitor queue replay rate to ensure database is not overwhelmed.

---

## 3. SOP-02: Executing Disaster Recovery Snapshot Restoration

### Trigger:
- Accidental data corruption, catastrophic administrator error, or database wipe.

### Procedure:
1. **Retrieve Verified Snapshot**:
   - Locate the most recent verified snapshot file in `backups/` or cloud cold storage (e.g. `relief_rtdb_snapshot_YYYYMMDD_HHMMSSZ.json`).
2. **Verify Checksum Attestation**:
   - Execute verification:
     ```powershell
     powershell -ExecutionPolicy Bypass -File scripts/Backup-FirebaseRtdb.ps1 -VerifyFile backups/relief_rtdb_snapshot_YYYYMMDD_HHMMSSZ.json
     ```
   - Ensure exit code 0 and `[SUCCESS]` output.
3. **Execute Import into Target Instance**:
   - Run declarative restore via Firebase CLI:
     ```bash
     firebase database:set / backups/relief_rtdb_snapshot_YYYYMMDD_HHMMSSZ.json --project relief-distribution-2w5 --confirm
     ```
4. **Deploy Security Rules & Indexes**:
   - Immediately re-deploy hardened rules:
     ```bash
     firebase deploy --only database:rules
     ```
5. **Operational Verification**:
   - Query `/users` and `/inventory` via test client to confirm data consistency.

---

## 4. SOP-03: Emergency Release Rollback Procedure

### Trigger:
- Crash-free sessions drop below 99.5% during progressive rollout OR critical security defect discovered in active binary.

### Procedure:
1. **Halt Google Play Staged Rollout**:
   - Navigate to Google Play Console -> Production / Open Testing -> Release Dashboard.
   - Click **Halt Rollout** immediately.
2. **Identify Faulty Commit**:
   - Inspect structured JSON logs in Crashlytics and filter by `traceId` and exception stack trace.
3. **Build Expedited Patch Release**:
   - Revert commit on `main` branch.
   - Increment `versionCode` in `app/build.gradle.kts`.
   - Run `./gradlew.bat testDebugUnitTest` and `./gradlew.bat lintDebug`.
4. **Promote Hotfix**:
   - Deploy new APK/AAB to Ring 0 (Internal) and expedite review for Ring 1 rollout.

---

## 5. SOP-04: Distributor Fleet GPS Telemetry Degradation Triage

### Trigger:
- P1 alert: Distributor location freshness latency > 60 seconds across > 10% of field units.

### Procedure:
1. **Inspect Network Connectivity**:
   - Verify cellular network coverage reports in the affected disaster zone.
2. **Check Compaction Statistics**:
   - Review edge compaction metrics in logs: confirm `OfflineTelemetryBuffer` is compacting stationary points and not evicting emergency points.
3. **Inspect RTDB Write Quota**:
   - Check Firebase console write operations per minute. If write ceiling (1,000/s) is approached, instruct field teams to enable high-efficiency battery mode (30s polling intervals).

