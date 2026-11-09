---
name: disaster-recovery-engineering
description: Backup export automation, cold/warm recovery procedures, schema restoration, and recovery point/time objective (RPO/RTO) enforcement.
---

# Disaster Recovery Engineering Skill

## 1. Purpose
Formulate, automate, test, and document comprehensive disaster recovery strategies, automated cloud backups, data restoration workflows, and failover operations to guarantee data durability and rapid service restoration.

## 2. Activation Criteria
Activate this skill when:
- Designing automated backup scripts and point-in-time snapshot strategies for Firebase RTDB.
- Defining Recovery Time Objectives (RTO) and Recovery Point Objectives (RPO) for relief data.
- Authoring and validating restoration runbooks for catastrophic cloud datastore corruption or deletion.
- Conducting simulated disaster recovery drills and validating backup integrity.

## 3. Project Context
- **Primary Datastore**: Firebase Realtime Database (`relief-distribution-2w5-default-rtdb`).
- **Critical Data Entities**: Users (`/users`), Relief Requests (`/relief_requests`), Inventory Items (`/inventory`), Distributor Locations (`/distributor_locations`), Notifications (`/notifications`).
- **Recovery Targets**:
  - Maximum Acceptable RPO (Data Loss Window): <= 1 hour during active disaster operations.
  - Maximum Acceptable RTO (Service Downtime): <= 30 minutes for cloud failover/restoration.

## 4. Engineering Standards
- **Automated Declarative Backups**: Automated exports must run on scheduled cron intervals and output encrypted, timestamped JSON snapshots.
- **Restoration Verification**: Backups must be regularly restored into an isolated staging environment to verify schema integrity and data completeness.
- **Separation of Backup Storage**: Backups must be stored in physically isolated cloud storage buckets with lifecycle retention and immutability (WORM) policies.

## 5. Step-by-Step Execution Procedure
1. **Snapshot Generation**: Execute declarative backup script exporting database nodes with metadata.
2. **Integrity Check**: Verify SHA-256 checksum and validate JSON syntax and node schema completeness.
3. **Restoration Drill**: Simulate point-in-time recovery to staging instance and verify foreign keys and role mappings.
4. **Audit & Log**: Record snapshot size, execution duration, and verification status in disaster recovery logs.

## 6. Decision Tree for Ambiguous Cases
- *Should backup scripts include plain-text user passwords or tokens?*
  -> No. Passwords are never stored in the database; authentication tokens are ephemeral and excluded from backups.

