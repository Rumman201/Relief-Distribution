---
name: cloud-platform-architecture
description: Multi-region failover, cloud resource boundaries, serverless datastores, and operational continuity.
---

# Cloud Platform Architecture Skill

## 1. Purpose
Design, evaluate, and scale cloud infrastructure topology, high-availability datastores, tenant isolation, and multi-region resilience for the Relief Distribution System cloud ecosystem.

## 2. Activation Criteria
Activate this skill when:
- Evaluating cloud hosting topologies, serverless datastores, or network boundaries.
- Designing high-availability architectures and failover strategies for cloud outages.
- Auditing cloud resource limits, concurrent connection ceilings, and quotas.
- Structuring cloud telemetry, audit logging, and platform security boundaries.

## 3. Project Context
- **Cloud Backend**: Google Cloud Platform / Firebase Serverless ecosystem.
- **Datastore Topology**: Firebase Realtime Database in US-Central region with WebSocket persistent connections.
- **Identity Provider**: Firebase Authentication (Google Identity Toolkit) with email/password and Google Sign-In.
- **Platform Invariants**: 200k concurrent WebSocket connection capacity, zero compute maintenance overhead, automatic managed SSL/TLS 1.3 termination.

## 4. Engineering Standards
- **Elastic Scalability**: Cloud topology must automatically absorb 10x traffic spikes during sudden disaster events without manual node provisioning.
- **Region Resilience**: Cloud dependencies must document cold standby and disaster recovery options across secondary regions.
- **Strict Boundary Isolation**: Staging and production cloud environments must reside in completely isolated projects or database instances.

## 5. Step-by-Step Execution Procedure
1. **Capacity & Quota Review**: Check concurrent connection limits, bandwidth throughput, and rate limits.
2. **Topology Verification**: Validate secure HTTPS/WSS endpoints and TLS certificate status.
3. **Failover Architecture**: Define point-in-time recovery and snapshot replication strategies.
4. **Resilience Assessment**: Test client failover and reconnect behaviors under simulated network drops.

## 6. Decision Tree for Ambiguous Cases
- *Can staging test data be stored in the production Firebase instance?*
  -> No. Staging data must never share a database instance with live humanitarian relief operations.

