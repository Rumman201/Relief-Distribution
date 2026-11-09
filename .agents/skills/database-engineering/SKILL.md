---
name: database-engineering
description: Firebase Realtime Database schema design, indexing, data consistency, security rules, and query optimization for disaster relief coordination.
---

# Database Engineering Skill

## 1. Purpose
Design, structure, index, and optimize persistent data models and schemas in Firebase Realtime Database and local caches to ensure high throughput, zero data corruption, and scalable real-time location streaming.

## 2. Activation Criteria
Activate this skill when:
- Designing or modifying database schemas (`Users`, `ReliefPoints`, `HelpRequests`, `Distributions`).
- Defining Firebase indexing rules (`.indexOn`) for query performance.
- Writing Firebase Security Rules (`rules.json`).
- Implementing local data caching, SQLite / Room persistence, or data synchronization.

## 3. Project Context
- **Database Engine**: Firebase Realtime Database (`https://relief-distribution-2w5-default-rtdb.firebaseio.com`).
- **Current Tree Structure**:
  ```
  /Users
    /{userId}
      /firstName: String
      /lastName: String
      /email: String
      /phone: String
      /role: "Distributor" | "Survivor"
      /password: String  <-- SECURITY DEFECT
      /location
        /latitude: Double
        /longitude: Double
        /timestamp: Long
  ```
- **Deficiencies Identified**:
  - Deep nesting of locations under User accounts prevents efficient bulk querying of active distributors.
  - Missing `.indexOn` for role, location, or active status.
  - Missing relief inventory, relief points, and emergency help requests in the data model.
  - Lack of atomic transactions for multi-user status updates.

## 4. Specialist Engineering Standards
- **Flat Schema Architecture**: Avoid deep nesting. Denormalize data for efficient querying:
  - `/users/{userId}`: Static profile data.
  - `/distributor_locations/{userId}`: High-frequency live GPS coordinates (`lat`, `lng`, `updatedAt`, `active`).
  - `/relief_points/{pointId}`: Coordinates, inventory count, distributor in charge.
  - `/help_requests/{requestId}`: Survivor ID, timestamp, urgency level, rations requested.
- **Index Definitions**: Index keys queried by clients:
  ```json
  "distributor_locations": {
    ".indexOn": ["active", "updatedAt"]
  }
  ```
- **Data Integrity & Schema Validation**:
  - Enforce non-null fields and string length boundaries in Firebase Security Rules.
  - Implement idempotent writes using server timestamps (`ServerValue.TIMESTAMP`).

## 5. Step-by-Step Execution Procedure
1. **Schema Design**: Plan normalized/denormalized trees ensuring read and write separation.
2. **Model Implementation**: Create POJO/JavaBean classes with `@IgnoreExtraProperties` and proper type annotations.
3. **Index Optimization**: Write and maintain `database.rules.json` specifying `.read`, `.write`, and `.indexOn`.
4. **Local Cache Integration**: In Phase 3, introduce Room database for persistent offline caching of relief stations and offline emergency contacts.

## 6. Decision Tree for Ambiguous Cases
- *Should GPS locations be stored inside `/Users/{userId}` or in a separate root `/locations`?*
  -> Separate root `/distributor_locations/{userId}`. Storing coordinates inside `/Users` triggers unnecessary full user profile downloads whenever location updates occur, increasing bandwidth usage by 10x-50x.

## 7. Tool Instructions
- Test rules and schema validation via Firebase CLI or emulator suite when available.
- Inspect JSON serialization via unit tests with Jackson or Gson.

## 8. Safety Constraints
- Never execute destructive data wiping operations against the live production database.
- Never write data models without explicit model versioning or backwards-compatible deserialization.

## 9. Verification Steps
- Verify model serialization/deserialization with JUnit unit tests.
- Test that missing fields in older records do not cause crash on newer client versions.

## 10. Expected Output & Evidence Format
- Documented schema specifications in `docs/upgrade/02-architecture-assessment.md`.
- `database.rules.json` with complete security and indexing definitions.

