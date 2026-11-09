---
name: backend-engineering
description: Backend service integration, Firebase Authentication, Firebase Realtime Database rules, error resilience, retry strategies, and cloud integration.
---

# Backend Engineering Skill

## 1. Purpose
Define engineering standards for cloud backend services, Firebase Authentication workflows, Realtime Database transactions, offline persistence, network resilience, and error handling.

## 2. Activation Criteria
Activate this skill when:
- Implementing or refactoring authentication flows (Email/Password, Google Sign-In, Phone Auth).
- Interacting with Firebase Realtime Database (CRUD, live listeners, geospatial queries).
- Handling network disconnects, offline synchronization, or token expiration.
- Writing Firebase Security Rules or Cloud Functions.

## 3. Project Context
- **Backend Service**: Google Firebase (Project ID: `relief-distribution-2w5`).
- **Database**: Firebase Realtime Database (`https://relief-distribution-2w5-default-rtdb.firebaseio.com`).
- **Auth Service**: Firebase Authentication (Email/Password).
- **Core Entities**:
  - `/Users/{userId}`: User profiles (`userId`, `firstName`, `lastName`, `email`, `phone`, `role`, `location`).
  - `/ReliefPoints/{pointId}`: Relief distribution centers and aid hubs.
  - `/HelpRequests/{requestId}`: Survivor distress calls and assistance requirements.

## 4. Specialist Engineering Standards
- **Zero Plain-Text Credentials**: Passwords must NEVER be transmitted to or stored in Realtime Database. Firebase Auth securely hashes and manages authentication credentials.
- **Fail Gracefully**: Always handle Firebase callback failures (`onCancelled`, `addOnFailureListener`) with informative user feedback, retry logic, and zero app crashes.
- **Offline First**: Enable Firebase offline persistence (`FirebaseDatabase.getInstance().setPersistenceEnabled(true)`) so the app works during disaster scenarios with poor or zero connectivity.
- **Connection Awareness**: Monitor `/.info/connected` to adapt UI state based on online/offline status.

## 5. Step-by-Step Execution Procedure
1. **Model Audit**: Verify that data transfer models (e.g., `User`, `ReliefLocation`, `HelpRequest`) follow JavaBean conventions with no-arg constructors and omit sensitive fields (`@Exclude`).
2. **Repository Abstraction**: Encapsulate Firebase references behind clean interfaces:
   - `AuthRepository`: `login()`, `register()`, `logout()`, `getCurrentUser()`.
   - `UserRepository`: `getUserProfile()`, `updateLocation()`, `observeDistributorLocations()`.
3. **Transaction Safety**: Use Firebase `runTransaction()` for atomic updates (e.g. claiming relief rations or updating inventory counts) to prevent race conditions.
4. **Listener Lifecycle Management**: Always remove active `ValueEventListener` or `ChildEventListener` instances in `onStop()` / `onDestroy()` / ViewModel `onCleared()` to prevent memory leaks and background battery drain.

## 6. Decision Tree for Ambiguous Cases
- *Realtime Database vs Cloud Firestore?*
  -> The existing project uses Realtime Database. Maintain RTDB for low-latency live GPS location streaming. Consider Firestore in Phase 5 only if complex hierarchical querying or compound indexing is strictly necessary.
- *Synchronous vs Asynchronous Firebase operations?*
  -> All Firebase calls in Android must remain asynchronous. Use `Task<T>` with `addOnSuccessListener`/`addOnFailureListener` or Kotlin Coroutines `await()`.

## 7. Tool Instructions
- Inspect Firebase JSON configuration: `app/google-services.json`.
- Monitor network requests with Android Studio Network Profiler or debug logging.

## 8. Safety Constraints
- Never log user credentials, auth tokens, or exact GPS coordinates in plain text logs (`Log.d`, `Log.e`).
- Never delete or overwrite database root nodes (`database.setValue(null)`).
- Enforce strict server-side Firebase Security Rules so users cannot read or write arbitrary nodes.

## 9. Verification Steps
- Verify offline behavior by toggling airplane mode during operations.
- Test authentication token expiration and automatic refresh.
- Verify that Firebase listeners detach properly when activities finish.

## 10. Expected Output & Evidence Format
- Complete implementation code with proper annotations (`@Exclude`, `@PropertyName`).
- Integration test logs and error handling verification.

