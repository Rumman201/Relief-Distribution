# Phase 9 — Defect Register & Root Cause Analysis

## 1. Defect Register

Every confirmed defect discovered during Phase 9 was recorded, isolated, and tracked through structured remediation:

| Defect ID | Subsystem | Severity | Description | Root Cause | Remediated In | Verified By Test | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **DEF-001** | Domain / Inventory | **HIGH** | `ReliefWorkflowAutomator.onDistributorAcceptRequest` accepted non-positive quantities (`qty <= 0`) | Missing validation check `if (requestedQty <= 0)` before mutating stock | `ReliefWorkflowAutomator.java` | `FullSystemQualityAssuranceTest.testDefect001` | **REMEDIATED** |
| **DEF-002** | AI Triage | **MEDIUM** | Future timestamp clock skew produced negative `ageHours` in cache key; huge notes risked regex stall | Missing `Math.max(0, ...)` guard on timestamp difference; unbounded string search | `ReliefTriageAdvisor.java` | `FullSystemQualityAssuranceTest.testDefect002` | **REMEDIATED** |
| **DEF-003** | Security / Utility | **MEDIUM** | `SecuritySanitizer.sanitizeInput` threw `StringIndexOutOfBoundsException` when `maxLength <= 0` | Missing boundary check on `maxLength <= 0` before calling `substring` | `SecuritySanitizer.java` | `FullSystemQualityAssuranceTest.testDefect003` | **REMEDIATED** |
| **DEF-004** | Data Model | **MEDIUM** | `InventoryItem` permitted setting negative stock quantities (`quantityAvailable < 0`) | Direct assignment without `Math.max(0, ...)` clamping in setters | `InventoryItem.java` | `FullSystemQualityAssuranceTest.testDefect004` | **REMEDIATED** |

---

## 2. Root Cause & Systemic Impact Analysis

1. **DEF-001 (Non-Positive Quantity Exploitation)**:
   - *Impact*: In previous phases, it was assumed that form validation in `CreateReliefRequestActivity` would always supply `quantity >= 1`. However, direct repository mutations or tampered database payloads could bypass the UI and inject `quantity = 0` or negative values, corrupting warehouse depot tallies. Adding domain-level enforcement in `ReliefWorkflowAutomator` ensures defense-in-depth.
2. **DEF-002 (AI Triage Clock Skew & Text Flooding)**:
   - *Impact*: In disaster zones, field handset device clocks can skew hours into the future. Computing `(now - createdAt)` yielded negative numbers, polluting LRU cache keys with negative hours (`|-1|`). Clamping `ageHours` to `>= 0` and bounding notes length to 2,000 characters neutralizes algorithmic denial-of-service.
3. **DEF-003 (Sanitizer Negative Bounds Exception)**:
   - *Impact*: Passing negative or zero length to `SecuritySanitizer.sanitizeInput(text, -1)` threw an unchecked runtime exception. Adding `if (maxLength <= 0) return ""` prevents crashes.
4. **DEF-004 (Negative Stock Clamping)**:
   - *Impact*: Ensures physical inventory invariants are mathematically enforced directly in entity setters.

