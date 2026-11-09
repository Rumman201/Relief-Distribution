---
name: zero-trust-access-engineering
description: Role-based access control (RBAC), least-privilege scoping, microsegmentation, and zero-trust verification for mobile clients and cloud datastores.
---

# Zero-Trust Access Engineering Skill

## 1. Purpose
Enforce zero-trust architecture principles across the Relief Distribution System, ensuring that every request, transaction, and state change is authenticated, authorized, and verified regardless of device provenance or network environment.

## 2. Activation Criteria
Activate this skill when:
- Defining or refining database access rules (`database.rules.json`).
- Designing role-based access control (Survivor vs Distributor vs Admin).
- Implementing per-user data isolation and tenant boundary enforcement.
- Hardening session verification and token revocation handling.

## 3. Project Security Context
- **Roles**:
  - `Survivor`: Can read own profile, submit new relief requests, view own requests, cancel own pending requests, read received notifications. Cannot read or write other users' requests, cannot mutate inventory, cannot accept/complete relief requests.
  - `Distributor`: Can read own profile, view all pending/active relief requests, accept requests, update request lifecycle status (`accepted`, `in_progress`, `completed`), manage inventory (`Inventory`), broadcast alerts, send notifications to survivors. Cannot alter survivor personal data or notes.
- **Microsegmentation**:
  - `/Users/$uid`: Read/write isolated to `$uid === auth.uid`.
  - `/ReliefRequests/$requestId`: Writes strictly segmented by role and lifecycle state.
  - `/Inventory/$itemId`: Read/write restricted strictly to verified distributors.
  - `/Notifications/$userId`: Read/update restricted strictly to recipient `$userId`.

## 4. Engineering Standards
- **Explicit Deny Default**: Any node or operation without an explicit `allow` rule must evaluate to `false`.
- **Server-Side Token Validation**: Firebase security rules must verify `auth != null` and check role predicates against `/Users/$uid/role`.
- **State Machine Invariants**: Transitions must enforce linear progression (`pending` -> `accepted` -> `in_progress` -> `completed`).

## 5. Step-by-Step Execution Procedure
1. **Rule Structure Design**:
   - Write declarative Firebase Security Rules enforcing `$userId === auth.uid` for private data.
   - Restrict role escalation: Prevent users from changing their own `role` field after registration.
2. **Path Segmentation**:
   - Nest personal notifications under `/Notifications/$userId/` to ensure path-level security.
   - Ensure query indices (`.indexOn`) support filtered queries without requiring broad table-scan read permissions.
3. **Automated Verification**:
   - Execute unit test simulations verifying that unauthorized read/write attempts are rejected.

## 6. Decision Tree for Ambiguous Cases
- *Should distributors be allowed to modify survivor request details (notes, items)?*
  -> No. Distributors can only modify status, distributorId, distributorName, and updatedAt timestamps. Request content is immutable by distributors.
