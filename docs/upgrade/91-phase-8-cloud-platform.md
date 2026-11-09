# Phase 8 — Cloud Platform Architecture & High Availability

## 1. Cloud Infrastructure Overview

The cloud backend supporting the Relief Distribution System utilizes Google Cloud Platform and Firebase managed serverless infrastructure. This architecture provides high availability, elastic scalability, and zero compute node maintenance overhead for emergency humanitarian operations.

| Dimension | Specification | Operational Constraint |
| :--- | :--- | :--- |
| **Datastore Tier** | Firebase Realtime Database | Single-region high-availability cluster in `us-central1` |
| **Concurrent Connections** | 200,000 simultaneous WebSockets | Automatically handles disaster demand surges |
| **Throughput Capacity** | 1,000 write ops/sec per database | Scalable across tree branches via index optimization |
| **Identity Service** | Firebase Authentication | Global Google Identity infrastructure with 99.95% SLA |
| **Network Security** | TLS 1.3 with Certificate Pinning | Strict HTTPS/WSS transport encryption |

---

## 2. Multi-Region Resilience & Disaster Strategy

1. **Active Realtime Ingestion**: Primary ingestion node accepts location telemetry and aid requests in `us-central1`.
2. **Scheduled Cold Snapshots**: Automated snapshot exports are stored in geographically distinct cloud storage buckets (e.g., `asia-east1` or `europe-west1`).
3. **Failover Execution**: In the event of a catastrophic regional failure in `us-central1`, the disaster recovery runbook guides immediate project redirection and data restoration into the standby region within the 30-minute RTO window.

