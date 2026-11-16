# 146 — Phase 10.1: Defect Remediation Register
**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: Phase 10.1  
**Date**: 2026-11-16  
**Status**: APPROVED

---

## 1. Remediation Register

| Defect ID | Severity | Component | Root Cause | Fix Description | Regression Test | Closure Date |
| :---: | :---: | :--- | :--- | :--- | :--- | :---: |
| **DEF-005** | **P0** | `InventoryManagementActivity.java` & `ManageRequestsActivity.java` | Layout empty state ID mismatch (`tvEmptyState` was null) | Migrated to ViewBinding targeting `llEmptyState` | `assembleDebug` exit 0, compile verified | 2026-11-16 |
| **DEF-006** | **P0** | `MyReliefRequestsActivity.java` | Unsafe `findViewById` targeting non-existent `rvRequests` and `progressBar` | Migrated to ViewBinding targeting `rvMyRequests` and `llEmptyState` | `assembleDebug` exit 0, compile verified | 2026-11-16 |
| **DEF-007** | **P1** | `MapActivity.java` & `CreateReliefRequestActivity.java` | `PICK_LOCATION` unhandled; map destination marker not rendered | Added map tap listener returning coordinates; rendered destination marker | `MapActivity.java` & `CreateReliefRequestActivity.java` | 2026-11-16 |
| **DEF-008** | **P1** | `MyReliefRequestsActivity.java` | Missing cancellation path for pending relief requests | Added confirmation dialog, status update to `CANCELLED`, and `automator.onRequestCancelled` hook | `Phase10SystemOverhaulTest.testWorkflowAutomatorOverloadedCancellation` | 2026-11-16 |
| **DEF-009** | **P1** | `InventoryManagementActivity.java` | Unimplemented stock addition (`fabAddItem` toast placeholder) | Built Material Dialog allowing distributors to add new supplies to `/Inventory` | `InventoryManagementActivity.java` | 2026-11-16 |
| **DEF-010** | **P1** | `ManageRequestsActivity.java` | Distributor ID not associated with request in Firebase RTDB | Added `distributorId: currentUserId` to update map on acceptance | `ManageRequestsActivity.java` | 2026-11-16 |
| **DEF-011** | **P1** | `item_inventory.xml` & `item_relief_request.xml` | Sub-48dp touch targets on list buttons (40dp and 44dp) | Upgraded button heights to 48dp minimum for WCAG 2.2 AA compliance | Layout inspection | 2026-11-16 |
| **DEF-012** | **P2** | `InventoryItem.java` | Missing `updatedAt` getter/setter alias | Added `getUpdatedAt()` and `setUpdatedAt()` delegating to `lastUpdated` | `Phase10SystemOverhaulTest.testInventoryItemStockIntegrityAndAliases` | 2026-11-16 |
| **DEF-013** | **P2** | `ReliefWorkflowAutomator.java` | Missing single-argument `onRequestCancelled(request)` overload | Added overloaded method delegating to `onRequestCancelled(request, null)` | `Phase10SystemOverhaulTest.testWorkflowAutomatorOverloadedCancellation` | 2026-11-16 |
| **DEF-014** | **P3** | `GoogleMap.java` | Duplicate legacy activity with class collision | Converted to lightweight delegator forwarding to `MapActivity` | `GoogleMap.java` | 2026-11-16 |
