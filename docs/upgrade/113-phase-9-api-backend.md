# Phase 9 — Backend Services, APIs & Domain Contract QA

## 1. API Contract Verification

Data contracts between the Android mobile client and the Firebase Realtime Database are validated through model schemas and repository integration tests:

| Entity / Contract | Serialization Methods | Mandatory Attributes | Invariants Verified |
| :--- | :--- | :--- | :--- |
| `User` | `toMap()` / `fromMap()` | `userId`, `name`, `email`, `role`, `createdAt` | Zero password storage; role immutability |
| `ReliefRequest` | `toMap()` / `fromMap()` | `requestId`, `survivorId`, `status`, `quantity` | State machine forward-only transitions; non-negative quantity |
| `InventoryItem` | `toMap()` / `fromMap()` | `itemId`, `distributorId`, `quantityAvailable` | Quantity available/reserved >= 0 (DEF-004 fix); status calculation |
| `Notification` | `toMap()` / `fromMap()` | `notificationId`, `userId`, `title`, `type` | Recipient user isolation; data payload map |
| `LocationTelemetry`| `toMap()` / `fromMap()` | `userId`, `latitude`, `longitude`, `timestamp` | Coordinate boundary clamping `[-90, 90]`, `[-180, 180]` |

---

## 2. Error Semantics & Negative Testing

- **Malformed JSON Handling**: Deserialization tolerance verified in `fromMap()`—missing attributes default safely to null or default values rather than throwing `NullPointerException`.
- **Negative Value Clamping**: Submitting negative inventory quantities or negative relief items is clamped or rejected at the domain boundary.
- **Circuit Breaker Integration**: Failing network calls automatically trigger fast-failing via `NetworkResilienceCircuitBreaker`, protecting clients from thread starvation.

