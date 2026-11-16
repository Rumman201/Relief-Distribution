# 131 — Phase 10.1: Authoritative Issue Backlog
**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: Phase 10.1  
**Date**: 2026-11-16  
**Status**: APPROVED

---

## 1. Executive Summary

This backlog serves as the canonical register of all identified engineering issues across the 10 transformation phases, detailing priority, root-cause domain, affected components, remediation status, and verification evidence.

---

## 2. Master Backlog Register

| Issue ID | Severity | Category | Description | Affected Components | Remediation Action | Status | Evidence |
| :--- | :---: | :--- | :--- | :--- | :--- | :---: | :--- |
| **ISS-001** | **P0** | Stability | `NullPointerException` on empty state rendering in Inventory & Requests | `InventoryManagementActivity`, `ManageRequestsActivity`, `MyReliefRequestsActivity` | Switched to ViewBinding with compile-time checked `llEmptyState` | **RESOLVED** | Compile & Activity inspection |
| **ISS-002** | **P0** | Usability / Workflow | Missing location picker routing and map destination marker rendering | `CreateReliefRequestActivity`, `MapActivity`, `GoogleMap` | Added map click listener returning coordinates; rendered aid target marker; delegated `GoogleMap` | **RESOLVED** | `MapActivity.java` & `CreateReliefRequestActivity.java` |
| **ISS-003** | **P1** | Architecture / Standards | 5 restored Activities and adapters used legacy `findViewById` | `CreateReliefRequestActivity`, `InventoryManagementActivity`, `ManageRequestsActivity`, etc. | 100% migrated to Android Jetpack ViewBinding | **RESOLVED** | Clean compilation exit code 0 |
| **ISS-004** | **P1** | Features / Inventory | Unfinished inventory replenishment; FAB and stock action buttons unwired | `InventoryManagementActivity`, `item_inventory.xml` | Built Material Dialogs for item creation, available quantity updates, and reservations | **RESOLVED** | `InventoryManagementActivity.java` |
| **ISS-005** | **P1** | Workflow / Survivor | Missing request cancellation workflow for pending aid requests | `MyReliefRequestsActivity`, `ReliefWorkflowAutomator` | Added confirmation dialog, status update to `CANCELLED`, and automator hook | **RESOLVED** | `Phase10SystemOverhaulTest` |
| **ISS-006** | **P1** | Logistics / Audit | Distributor ID omitted on request acceptance in Firebase RTDB | `ManageRequestsActivity.java` | Set `distributorId` atomically with status update to `ACCEPTED` | **RESOLVED** | `ManageRequestsActivity.java` |
| **ISS-007** | **P1** | Accessibility | Sub-48dp touch targets on list buttons (40dp and 44dp) | `item_inventory.xml`, `item_relief_request.xml`, dashboard layouts | Upgraded all list buttons and icons to 48dp height minimum | **RESOLVED** | WCAG 2.2 AA verified in XML |
| **ISS-008** | **P2** | AI / Guardrails | Adversarial text bomb & clock skew risk in triage engine | `ReliefTriageAdvisor.java` | Clamped `ageHours >= 0` and truncated notes to 2,000 characters | **RESOLVED** | `FullSystemQualityAssuranceTest` |
| **ISS-009** | **P2** | Security | Boundary crash on `maxLength <= 0` in input sanitization | `SecuritySanitizer.java` | Added guard clause `if (maxLength <= 0) return ""` | **RESOLVED** | `FullSystemQualityAssuranceTest` |
| **ISS-010** | **P2** | Data Integrity | In-memory negative physical stock mutation | `InventoryItem.java` | Enforced `Math.max(0, ...)` in available and reserved setters | **RESOLVED** | `Phase10SystemOverhaulTest` |
| **ISS-011** | **P3** | Architecture / Legacy | Class name collision and duplicate code in legacy `GoogleMap.java` | `GoogleMap.java` | Converted to transparent delegator forwarding to `MapActivity` | **RESOLVED** | `GoogleMap.java` |
