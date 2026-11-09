# Phase 9 — Screen Navigation & End-to-End User Journey Verification

## 1. Route & Screen Navigation Matrix

The Android client contains 11 registered activities representing the complete user surface for survivors and field distributors:

| Route / Activity | Associated Role | Navigation Entry Point | Critical User Actions |
| :--- | :--- | :--- | :--- |
| `StartActivity` | Public / Initializer | App launch (`android.intent.action.MAIN`) | Splash check, session token validation, redirect to Login or Dashboard |
| `WelcomeActivity` | Unauthenticated | Redirected from `StartActivity` on empty session | Route selection between Login and Sign Up |
| `LoginActivity` | Unauthenticated | `WelcomeActivity` -> Sign In button | Email/password validation, Firebase Auth sign-in, role resolution |
| `SignUpActivity` | Unauthenticated | `WelcomeActivity` -> Register button | Multi-field registration, password elimination, role attribution |
| `SurvivorDashboard` | Survivor (`recipient`) | Post-login redirect for survivors | Bottom navigation hub, emergency dial trigger, status summary |
| `DistributorDashboard` | Distributor | Post-login redirect for distributors | Bottom navigation hub, quick dispatch metrics, tracking toggle |
| `CreateReliefRequestActivity`| Survivor | Survivor Dashboard -> "Request Aid" | Form input, category selector, coordinate capture, AI triage |
| `MyReliefRequestsActivity` | Survivor | Survivor Dashboard -> "My Requests" | Request history list, real-time status chip, cancel action |
| `ManageRequestsActivity` | Distributor | Distributor Dashboard -> "Manage Requests" | AI urgency triage list, status tabs, accept claim, map route |
| `InventoryManagementActivity`| Distributor | Distributor Dashboard -> "Depot Stock" | Stock item catalog, category filter, threshold badges |
| `NotificationsActivity` | Shared | Both Dashboards -> Bell icon / Bottom Nav | Push alert history, deep-linking to affected request |
| `MapActivity` | Shared | Dashboards -> Live Map button | Google Map fragment, active distributor pins, GPS track |
| `Profile` | Shared | Bottom Navigation -> Profile tab | User details, computed avatar initials, logout session |
| `Informations` | Shared | Bottom Navigation -> Disaster Guides | Structured disaster survival playbooks (Flood, Cyclone, First Aid) |

---

## 2. Critical Journey Verification Evidence

1. **Journey 1: Survivor Emergency Aid Request**:
   - Verified form validation in `CreateReliefRequestActivity`: rejects empty inputs, validates phone format, sanitizes notes via `SecuritySanitizer.sanitizeNotes()`.
   - Verified automated AI triage evaluation: attaches urgency score and priority tier to the request before submitting to Firebase RTDB.
2. **Journey 2: Distributor Request Triage & Dispatch**:
   - Verified in `ManageRequestsActivity` and `FullSystemQualityAssuranceTest`: lists pending requests sorted by AI urgency score, claims request via atomic reservation, triggers notifications.
3. **Journey 3: Emergency Assistance Dialing**:
   - Verified: Invokes safe `Intent.ACTION_DIAL` (`tel:999`), eliminating privileged `CALL_PHONE` permission risks.

