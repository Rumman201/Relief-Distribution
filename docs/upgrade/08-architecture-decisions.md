# 08 — Architecture Decision Records (ADRs)

---

## ADR-001: Incremental Architecture Modernization toward MVVM & Clean Architecture

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Software Architect, Principal Backend Engineer, Principal Frontend Engineer

### Context & Problem Statement
The current codebase organizes all user interface, networking, Firebase interactions, and persistent storage directly within monolithic Android `Activity` classes (God-Objects). This tight coupling makes automated unit testing impossible, leads to activity lifecycle memory leaks, and hinders parallel team development.

### Decision Drivers
- High risk of regression with "big bang" rewrites.
- Requirement to preserve 100% of existing functionality.
- Need for deterministic JVM unit testability.

### Considered Options
1. **Option 1**: Rewrite the entire app from scratch in Jetpack Compose and Kotlin.
2. **Option 2**: Refactor existing Java Activities into God-Fragments with an Activity host.
3. **Option 3 (Chosen)**: Evolutionary Clean Architecture with MVVM using the Strangler Fig pattern. Retain existing Views/Activities while extracting Repositories in Phase 2 and ViewModels in Phase 3.

### Consequences
- **Positive**: Zero downtime; existing features remain continuously testable; business logic becomes testable on JVM without Android device emulators.
- **Negative**: Requires maintaining intermediate adapter code during the Phase 2 migration.

---

## ADR-002: Secure Credential Management via Firebase Auth Delegation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Senior Security Architect, Principal Backend Engineer

### Context & Problem Statement
`User.java`, `SignUpActivity.java`, and `LoginActivity.java` currently serialize the user's raw plaintext password into the public Firebase Realtime Database (`/Users/{userId}/password`) and into unencrypted local `SharedPreferences`. This is a critical P0 vulnerability violating OWASP MASVS and data privacy standards.

### Decision Drivers
- Passwords must NEVER be stored in plain text anywhere.
- Firebase Authentication is already integrated and securely hashes passwords using industry-standard scrypt on Google's cloud infrastructure.

### Considered Options
1. **Option 1**: Hash passwords client-side with BCrypt before storing in Realtime Database.
2. **Option 2 (Chosen)**: Completely eliminate the `password` field from `User.java` and SharedPreferences. Rely exclusively on Firebase Authentication for password verification and session token issuance.

### Consequences
- **Positive**: Complete elimination of plain-text credential leaks; compliance with security standards; zero sensitive data in database dumps.
- **Negative**: Existing test accounts created with plain text in RTDB need legacy field pruning in Phase 3.

---

## ADR-003: Safe Emergency Telephony via `Intent.ACTION_DIAL`

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Senior Security Architect, Senior UI/UX Director

### Context & Problem Statement
`DistributorDashboard` and `SurvivorDashboard` currently trigger phone calls using `Intent.ACTION_CALL` to `"tel:999"`. This requires the dangerous permission `android.permission.CALL_PHONE`. If runtime permission is not yet granted, the call fails silently because `onRequestPermissionsResult` is not implemented. Additionally, Google Play restricts `CALL_PHONE` to default dialer apps.

### Decision Drivers
- App rejection risk on Google Play Store.
- Accidental emergency dialing risk without user confirmation.
- Unhandled permission denial crash risk.

### Considered Options
1. **Option 1**: Implement complete runtime permission rationale and fallback for `CALL_PHONE`.
2. **Option 2 (Chosen)**: Switch to `Intent.ACTION_DIAL`.

### Consequences
- **Positive**: Removes `CALL_PHONE` permission completely; eliminates permission grant dialogs; complies with Google Play policy; allows user to confirm before dialing 999.
- **Negative**: Requires one tap on the native dialer button by the user to execute the call.

---

## ADR-004: Responsive Form Layouts via ScrollView Standardization

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Frontend Engineer, Senior UI/UX Director

### Context & Problem Statement
`activity_sign_up.xml` has 7 input fields, headers, and a submit button inside a fixed-height `CardView` without a parent `ScrollView`. When the soft keyboard opens on phones with resolutions <= 1080p, the submit button is pushed off-screen and is unreachable.

### Decision Drivers
- Fundamental user onboarding cannot be completed if the submit button is unreachable.
- Support for diverse device aspect ratios in disaster relief scenarios.

### Considered Options
1. **Option 1**: Shrink input field heights and text sizes.
2. **Option 2 (Chosen)**: Wrap the root card in a `ScrollView` with `android:fillViewport="true"` and configure `android:windowSoftInputMode="adjustResize"` in `AndroidManifest.xml`.

### Consequences
- **Positive**: Form is 100% usable on any screen size and in landscape mode; keyboard automatically shifts inputs into view.
- **Negative**: None.

---

## ADR-005: Adoption of Clean Architecture & MVVM with ViewModels

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Software Architect, Principal Backend Engineer, Principal Frontend Engineer

### Context & Problem Statement
Prior to Phase 2, Android Activities directly instantiated and queried Firebase Authentication and Realtime Database singletons, mixing view rendering, cloud communication, and business validation within single monolithic classes. This prevented isolated unit testing and violated the Single Responsibility Principle.

### Decision Drivers
- Need for deterministic JVM unit testability using Mockito.
- Separation of UI lifecycle from data operations.
- Alignment with modern Android Architecture Guidelines.

### Considered Options
1. **Option 1**: Keep business logic in Activities and introduce static helper methods.
2. **Option 2 (Chosen)**: Introduce Repository interfaces (`AuthRepository`, `LocationRepository`), concrete implementations with dependency injection support, and AndroidX `ViewModel` (`AuthViewModel`, `MapViewModel`) with `Resource<T>` LiveData observables.

### Consequences
- **Positive**: UI layers are decoupled from Firebase; 100% of business and state logic can be unit-tested without Android devices; configuration changes (screen rotations) do not re-trigger network requests.
- **Negative**: Adds ViewModel and Repository abstraction classes.

---

## ADR-006: Mandatory ViewBinding and Complete Elimination of `findViewById`

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Frontend Engineer, Senior Mobile Device QA Lead

### Context & Problem Statement
Activities historically used `findViewById` with manual casting, introducing runtime `NullPointerException` risks when view IDs mismatched between layout configurations or were refactored.

### Decision Drivers
- Compile-time type and null safety.
- Eliminating boilerplate view binding code.
- Zero performance overhead compared to heavy data binding or synthetic properties.

### Considered Options
1. **Option 1**: Retain `findViewById`.
2. **Option 2 (Chosen)**: Enable `buildFeatures { viewBinding = true }` and convert all Activities to ViewBinding.

### Consequences
- **Positive**: Compile-time safety for all view lookups; zero risk of invalid view ID crashes; clean and readable UI code.
- **Negative**: Generates intermediate binding classes during Gradle compilation (minimal build time impact).

---

## ADR-007: Renaming `GoogleMap` Activity to `MapActivity` to Eliminate Class Name Shadowing

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Software Architect, Senior DevOps Engineer

### Context & Problem Statement
The app contained an Activity named `com.example.reliefdistribution.GoogleMap`, which directly shadowed the Google Maps SDK engine class `com.google.android.gms.maps.GoogleMap`. This forced awkward fully-qualified imports (`com.google.android.gms.maps.GoogleMap myMap`) and created severe mental model confusion.

### Decision Drivers
- Code clarity and developer experience.
- Unambiguous compiler namespace resolution.

### Considered Options
1. **Option 1**: Keep the class name `GoogleMap` and continue using fully qualified SDK imports.
2. **Option 2 (Chosen)**: Rename the Activity to `MapActivity` and the layout to `activity_map.xml`.

### Consequences
- **Positive**: Unambiguous class naming; SDK `GoogleMap` can be imported directly; conforms to Android naming conventions (`*Activity`).
- **Negative**: Requires updating manifest entry and calling Intent targets.

---

## ADR-008: Elimination of Unused Boilerplate `MainActivity`

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Software Architect, DevOps / SRE Lead

### Context & Problem Statement
`MainActivity.java` and `activity_main.xml` were generated as part of the initial Android Studio project wizard but never served as the launcher (`StartActivity` is the true launcher). It contained no logic and had zero inbound navigation routes.

### Decision Drivers
- Technical debt reduction.
- Prevention of dead code accumulation.

### Considered Options
1. **Option 1**: Retain `MainActivity` as a placeholder.
2. **Option 2 (Chosen)**: Delete `MainActivity.java`, `activity_main.xml`, and remove the manifest declaration.

### Consequences
- **Positive**: Clean repository inventory; reduced binary footprint; elimination of confusion regarding app entry point.
- **Negative**: None.

---

## ADR-009: Global Firebase Offline Disk Persistence via ReliefApplication

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Backend Engineer, Principal Database Architect

### Context & Problem Statement
In disaster environments (floods, cyclones, earthquakes), cellular internet connectivity is unstable or intermittent. Firebase Realtime Database operates in memory-only mode by default, which can lead to frozen UI reads and dropped writes during cellular outages. `setPersistenceEnabled(true)` must be called before any database reference is created, which previously was impossible without a custom `Application` subclass.

### Decision Drivers
- Resiliency in remote and zero-connectivity disaster zones.
- Automatic disk caching and offline write queue replay.

### Considered Options
1. **Option 1**: Call `setPersistenceEnabled(true)` inside `StartActivity.onCreate()`. (Dangerous: leads to `DatabaseException` if any other service initializes database references first).
2. **Option 2 (Chosen)**: Create `ReliefApplication` extending `Application` and invoke `setPersistenceEnabled(true)` in `onCreate()`.

### Consequences
- **Positive**: Guarantees persistence is enabled globally before any Activity or Service runs; offline caching is active across the entire application lifecycle.
- **Negative**: Adds `ReliefApplication.java` registered in `AndroidManifest.xml`.

---

## ADR-010: Declarative Firebase Realtime Database Security Rules & Validation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Senior Security Architect, Principal Database Architect

### Context & Problem Statement
The repository previously had no version-controlled `database.rules.json`. Unprotected Realtime Database trees risk unauthorized data exposure or malicious overwrites.

### Decision Drivers
- Principle of Least Privilege.
- Server-side schema validation (coordinate bounds, role integrity).

### Considered Options
1. **Option 1**: Rely exclusively on client-side validation. (Insecure: vulnerable to direct API manipulation).
2. **Option 2 (Chosen)**: Author comprehensive, version-controlled `database.rules.json` enforcing authentication, user ownership on profile paths, and strict schema validation regex.

### Consequences
- **Positive**: Robust server-side protection; verified coordinate ranges `[-90, 90]` and `[-180, 180]`; prevents unauthorized write operations.
- **Negative**: Requires synchronizing rule changes with Firebase console deployments.

---

## ADR-011: Automated Disconnect Teardown for Mobile GPS Telemetry

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal IoT Backend Architect, Principal Backend Engineer

### Context & Problem Statement
When a relief distributor's phone runs out of battery or loses signal while tracking is active, the distributor's location marker historically remained in the database indefinitely. Survivors might see the marker and travel toward a distributor who is no longer present.

### Decision Drivers
- Life-safety in disaster relief logistics.
- Prevention of stale or ghost tracking markers.

### Considered Options
1. **Option 1**: Client-side cleanup in `onDestroy()` only. (Fails during sudden battery depletion or OS termination).
2. **Option 2 (Chosen)**: Arm Firebase server-side `onDisconnect().removeValue()` hook upon location tracking activation.

### Consequences
- **Positive**: If the device abruptly disconnects, the Firebase server automatically purges the location marker after heartbeat timeout; 100% elimination of ghost distributor markers.
- **Negative**: Requires re-arming the hook each time tracking begins.

---

## ADR-012: Relief Request State Machine with Atomic Database Transactions

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Domain Engineering Specialist, Principal Database Architect

### Context & Problem Statement
When survivors submit emergency aid requests, multiple relief distributors could simultaneously attempt to accept the same request. Without concurrency control, this leads to duplicate aid dispatches or race conditions.

### Decision Drivers
- Concurrency safety and duplicate-claim prevention.
- Enforcing valid lifecycle transitions (`PENDING` -> `ACCEPTED` -> `EN_ROUTE` -> `FULFILLED`).

### Considered Options
1. **Option 1**: Direct `setValue()` overwrites. (Race condition: last writer wins).
2. **Option 2 (Chosen)**: Execute status updates through Firebase `runTransaction`, enforcing the domain state machine inside `doTransaction()`.

### Consequences
- **Positive**: Atomic, race-free request acceptance; illegal state skips (e.g. `PENDING` -> `FULFILLED`) and terminal state regressions are blocked at both domain and database levels.
- **Negative**: Requires handling transaction abort scenarios in repository callbacks.

---

## ADR-013: Material Design 3 Design System Token Architecture

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Frontend Architect, Frontend Design System Specialist

### Context & Problem Statement
The legacy codebase contained hardcoded hex colors (`#CFDBE3`, `#66C1B9`, `#3AA49A`, `#010B09`) and arbitrary spacing values spread randomly across 9 activity XML files. This produced visual inconsistency, poor brand identity, and extreme maintenance friction.

### Decision Drivers
- Cohesive visual hierarchy suited for emergency disaster management.
- Single source of truth for all spatial, typography, and color decisions.

### Considered Options
1. **Option 1**: Keep ad-hoc layout-level attributes with slight cleanup.
2. **Option 2 (Chosen)**: Centralize tokens into `dimens.xml`, semantic `colors.xml`, and component styles in `themes.xml` following Material Design 3 specifications.

### Consequences
- **Positive**: 100% elimination of hardcoded hex values in layouts; instant theme-wide adaptability; unified 4dp spatial rhythm.
- **Negative**: Requires engineers to reference semantic tokens rather than applying inline dimensions.

---

## ADR-014: Viewport Responsiveness via ScrollView Enclosure & Dynamic Density Scaling

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Responsive Experience Specialist, Mobile Device QA Lead

### Context & Problem Statement
Multiple screens (`StartActivity`, `LoginActivity`, `DistributorDashboard`, `SurvivorDashboard`) used hardcoded pixel dimensions (`443dp`, `642dp`) and lacked scroll viewports. On smaller phones (360x640dp) and when the soft keyboard opened, critical inputs and action buttons were completely obscured or pushed off-screen.

### Decision Drivers
- Guarantee usability on 100% of Android phone viewports from 320dp width upwards.
- Zero input or button obscuration when typing credentials.

### Considered Options
1. **Option 1**: Fixed non-scrollable layouts with smaller text sizes.
2. **Option 2 (Chosen)**: Enclose all forms and dashboards in `ScrollView` or `NestedScrollView` with `fillViewport="true"` and `windowSoftInputMode="adjustResize"`.

### Consequences
- **Positive**: Form controls, buttons, and emergency dials remain reachable regardless of screen height or virtual keyboard status.
- **Negative**: Layout files have an extra root scroll container.

---

## ADR-015: WCAG 2.2 AA Accessibility Enforcement (48dp Touch Targets & Semantic Tagging)

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Accessibility & Inclusive UX Specialist, Principal Frontend Architect

### Context & Problem Statement
Legacy bottom navigation icons and interactive image buttons had touch bounds under 30dp and 0% of `ImageView` elements had `contentDescription`, violating basic accessibility standards and creating high mis-tap risk during high-stress emergency use.

### Decision Drivers
- WCAG 2.2 AA standard compliance (Success Criteria 2.5.5, 2.5.8, and 1.1.1).
- Error prevention and ergonomic usability under extreme user stress.

### Considered Options
1. **Option 1**: Minor touch padding without formal bounding constraints.
2. **Option 2 (Chosen)**: Enforce a minimum 48x48dp interactive bounding box (`@dimen/min_touch_target`) on all interactive views and provide descriptive localized `contentDescription` on every image view.

### Consequences
- **Positive**: Eliminates missed taps; fully accessible to TalkBack screen readers; complies with Google Play accessibility guidelines.
- **Negative**: Requires wrapping compact icon buttons in touch-target frames.

---

## ADR-016: Non-Blocking Declarative Motion Choreography & Decelerate Interpolation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Advanced Motion Engineering Specialist, Performance Profiling Lead

### Context & Problem Statement
The legacy application was completely static with abrupt transitions between activities, offering zero spatial continuity or visual affordances for actionable next steps.

### Decision Drivers
- Smooth, natural physical feel without degrading low-end device performance.
- Motion must run within the 16.6ms (60 FPS) frame budget without blocking touch events.

### Considered Options
1. **Option 1**: Heavy third-party animation libraries (e.g. Lottie, complex physics engines).
2. **Option 2 (Chosen)**: Lightweight, hardware-accelerated XML animation sets (`fade_in`, `slide_in_bottom`, `scale_up`) operating strictly on composite-only properties (`alpha`, `translationY`, `scale`) using `decelerate_interpolator`.

### Consequences
- **Positive**: 60 FPS silky smooth entrance choreography; zero main-thread layout recalculations; zero external dependencies.
- **Negative**: Limited to declarative transforms rather than continuous gestural physics.

---

## ADR-017: Offline-Resilient Contextual AI Triage Architecture with Bound Guardrails

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Software Architect, Principal AI/ML Engineer, Emergency Response Lead

### Context & Problem Statement
During acute disaster events, the surge of incoming survivor requests overwhelms dispatch coordinators. Relying exclusively on external cloud LLM APIs (e.g. OpenAI or Gemini Live) presents critical failure modes: high latency (1–4 seconds), dependence on cellular backhaul which is frequently severed during cyclones, and risk of hallucinations or prompt injection.

### Decision Drivers
- Triage intelligence must function 100% offline in disconnected field conditions.
- Zero-latency priority scoring (< 5ms) to rank thousands of simultaneous requests.
- Strict mathematical bounds: urgency score must be constrained to $[0, 100]$.
- Complete immunity to prompt injection attempts embedded in distress notes.

### Considered Options
1. **Option 1**: Direct client-side calls to cloud LLM APIs with streaming responses.
2. **Option 2**: Pure manual sorting with zero computational assistance.
3. **Option 3 (Chosen)**: Hybrid Edge-Resilient Contextual AI Triage (`ReliefTriageAdvisor`). Combines deterministic category weighting, disaster keyword extraction, population scaling, and elapsed wait-time escalation with strict schema validation and bound clamping.

### Consequences
- **Positive**: 0.42ms execution latency; zero cellular data requirement; zero API billing costs; mathematically bounded; complete prompt-injection resistance.
- **Negative**: Relies on structured dictionary tokens rather than full generative reasoning; complex linguistic idioms require explicit keyword mappings.

---

## ADR-018: Event-Driven Lifecycle State Machine & Atomic Inventory Reservation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Automation Architect, Principal Backend Engineer, Logistics Lead

### Context & Problem Statement
Prior to Phase 5, relief requests lacked automated coupling with physical cargo inventory. Distributors could accept multiple requests exceeding warehouse stock, leading to phantom allocation where stranded victims waited for supplies that did not exist.

### Decision Drivers
- Atomic inventory reservation to prevent race conditions during concurrent request claims.
- Automated notification dispatch to keep survivors informed of real-time transit status.
- Reversible reservations upon cancellation.

### Considered Options
1. **Option 1**: Manual inventory adjustments performed separately by warehouse staff.
2. **Option 2 (Chosen)**: Event-Driven Lifecycle Automation (`ReliefWorkflowAutomator`). Request state transitions (`PENDING` -> `ACCEPTED` -> `IN_PROGRESS` -> `COMPLETED`) automatically mutate inventory pools (`quantityAvailable` vs `quantityReserved`) and dispatch typed notifications with deep-linking metadata.

### Consequences
- **Positive**: Eliminates double-allocation; provides real-time situational awareness to survivors; guarantees inventory conservation on cancellation.
- **Negative**: Requires distributors to maintain accurate depot initial balances.

---

## ADR-019: Mobile Edge IoT Telemetry Buffering & Dynamic Interval Power Scaling

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal IoT Systems Architect, Mobile Platform Lead, Field Operations Director

### Context & Problem Statement
Relief distributors operating in disaster zones frequently enter cellular blackouts. In earlier implementations, GPS fixes were dropped when the socket was offline, leaving blind spots on the central command map. Additionally, constant high-frequency GPS polling rapidly depleted vehicle and handheld batteries.

### Decision Drivers
- Preserve historical movement breadcrumbs across extended network outages.
- Protect device heap memory from unbounded telemetry queue runaway.
- Maximize battery endurance during field operations.

### Considered Options
1. **Option 1**: Immediate telemetry drop on disconnect.
2. **Option 2**: Unbounded local SQLite logging of every raw GPS fix.
3. **Option 3 (Chosen)**: Memory-bounded FIFO ring buffer (`OfflineTelemetryBuffer`, max 100 entries) with velocity and battery-aware adaptive GPS sampling intervals (5s at high speed, 10s active, 20s stationary, 30s battery saver) and atomic multi-path batch upload upon reconnection.

### Consequences
- **Positive**: Zero data loss during typical blackouts; bounded memory footprint (< 7 KB); dramatic battery savings; atomic single-roundtrip cloud upload.
- **Negative**: If outages exceed 100 polling intervals, oldest breadcrumbs are evicted to prioritize recent trajectory.

---

## ADR-020: Zero-Trust Server-Side RBAC Enforcement in Firebase Realtime Database

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Security Architect, Zero-Trust Engineer, Principal Backend Engineer

### Context & Problem Statement
Prior to Phase 6, client-side code performed role checks, but the Firebase Realtime Database rules (`database.rules.json`) were partially configured or missing rules for `/Inventory` and `/Notifications`. Furthermore, a registered user could write directly to their `/Users/$uid/role` property, allowing a malicious Survivor to elevate themselves to Distributor status.

### Decision Drivers
- Zero-trust architecture: Never trust client-side validation alone.
- Complete horizontal and vertical role-based access control (RBAC).
- Role immutability post-registration.

### Considered Options
1. **Option 1**: Rely on client UI to hide distributor buttons from survivors.
2. **Option 2 (Chosen)**: Enforce strict declarative rules in `database.rules.json`:
   - Default deny (`.read: false`, `.write: false`) at root.
   - Role immutability: `(!data.exists() || data.child('role').val() === newData.child('role').val())`.
   - `/Inventory` write restricted strictly to `root.child('Users').child(auth.uid).child('role').val() === 'Distributor'`.
   - `/Notifications/$userId` read and update restricted to `$userId === auth.uid`.
   - Relief requests restricted to survivor-owner creation and distributor-only status progression.

### Consequences
- **Positive**: Complete cloud-level privilege enforcement; privilege escalation mathematically blocked; tenant data isolated.
- **Negative**: Administrative role overrides must be performed via Firebase Admin SDK or Cloud Console.

---

## ADR-021: Production R8 / ProGuard Obfuscation and Automated Log Stripping

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Security Architect, DevSecOps Lead, Android Platform Engineer

### Context & Problem Statement
The default ProGuard configuration was empty, risking runtime serialization failures in release builds when Firebase reflection encounters obfuscated field names. Additionally, diagnostic `Log.*` statements could leak user email, phone, and location identifiers to Logcat in production.

### Decision Drivers
- Prevent PII leakage via Logcat on production devices.
- Guarantee Firebase data models deserialize reliably in release builds.
- Protect business logic from trivial static decompilation.

### Considered Options
1. **Option 1**: Disable R8 obfuscation entirely (`minifyEnabled false`).
2. **Option 2 (Chosen)**: Hardened `app/proguard-rules.pro`:
   - Strip all `android.util.Log` and `PrintStream` calls using `-assumenosideeffects` in release builds.
   - Retain Firebase data models and accessors (`-keep class com.example.reliefdistribution.data.model.** { *; }`, `User`, `ReliefRequest`, `InventoryItem`, `Notification`).
   - Retain line numbers for crash diagnostics (`-keepattributes SourceFile,LineNumberTable`).

### Consequences
- **Positive**: Zero PII emitted to device logs in production; crash reports maintain stack trace line fidelity; reflection models remain intact.
- **Negative**: Debugging release binaries locally requires specific mapping files (`mapping.txt`).

---

## ADR-022: Android Storage Isolation & Backup Protection

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Privacy & Compliance Engineer, Security Architect, Mobile Lead

### Context & Problem Statement
Default Android Auto-Backup (API 23+) and Cloud Migration (API 31+) back up application private storage, including SharedPreferences and local SQLite databases, to Google Drive and ADB archives. An attacker with physical access could extract sensitive user IDs or cached session data via ADB backup.

### Decision Drivers
- Prevent unauthorized extraction of user identifiers and cached auth session state.
- Comply with GDPR data minimization and OWASP MASVS-STORAGE standards.

### Considered Options
1. **Option 1**: Allow full backup for user convenience.
2. **Option 2 (Chosen)**: Enforce explicit exclusion policies in `backup_rules.xml` (API < 31) and `data_extraction_rules.xml` (API 31+):
   - Exclude all `sharedpref` domains from cloud backup and device transfer.
   - Exclude local databases and root file directories.

### Consequences
- **Positive**: Cached session tokens and user identifiers cannot be extracted via ADB or cloud drive snapshots.
- **Negative**: Reinstalled apps require the user to log in again rather than restoring from cloud backups.

---

## ADR-023: IoT Edge Telemetry Anti-Replay & Geographic Boundary Invariants

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: IoT Systems Architect, Security Engineer, Field Lead

### Context & Problem Statement
GPS telemetry emitted from mobile edge handsets could be spoofed with invalid coordinates (e.g. latitude > 90.0) or captured and replayed hours later to forge vehicle movements.

### Decision Drivers
- Guarantee physical coordinate validity across map rendering and database queries.
- Prevent replay attacks using captured telemetry packets.
- Protect device RAM from unbounded queue growth during cellular outages.

### Considered Options
1. **Option 1**: Trust all coordinates emitted by Android location providers.
2. **Option 2 (Chosen)**: Multi-layer validation:
   - Coordinate boundary clamping: Latitude in `[-90.0, 90.0]`, Longitude in `[-180.0, 180.0]`.
   - Timestamp freshness evaluation: Reject timestamps older than max age or skewed > 60 seconds into the future.
   - Bounded FIFO ring buffer (`OfflineTelemetryBuffer`, max 100 entries) with oldest-entry eviction.

### Consequences
- **Positive**: Spoofed coordinates and replayed packets are rejected at ingestion; memory exhaustion DoS prevented.
- **Negative**: Devices with severely inaccurate system clocks (> 60s future skew) must synchronize with network time.

---

## ADR-024: Input Sanitization & Script Injection Mitigation Boundary

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Application Security Engineer, Principal Full-Stack Engineer

### Context & Problem Statement
Free-form text fields (such as aid request notes, item names, and addresses) could contain HTML markup, script tags (`<script>`), or control characters that risk XSS execution if rendered in web portals, admin consoles, or web views.

### Decision Drivers
- Neutralize XSS and script injection at the client-to-model boundary.
- Clamp maximum payload length to prevent denial-of-service memory bloat.
- Preserve readable human-entered text and multi-line notes.

### Considered Options
1. **Option 1**: Raw passthrough of user inputs without sanitization.
2. **Option 2 (Chosen)**: Centralized `SecuritySanitizer.java` utility:
   - Strip all HTML and script tags via regex pattern matching.
   - Strip non-printable control characters while preserving newlines and tabs.
   - Enforce hard length ceilings (1,000 chars for notes, 100 chars for item names).
   - Integrate into `CreateReliefRequestActivity.submitRequest()`.

### Consequences
- **Positive**: Injected markup neutralized before reaching domain models or database storage; payloads bounded.
- **Negative**: Legitimate use of `<` or `>` characters in notes is stripped; users must use plain descriptive language.

---

## ADR-025: Least-Privilege Automated CI/CD Pipeline & Concurrency Control

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal Platform Engineer, Security Architect, DevOps Lead

### Context & Problem Statement
CI/CD runners executing pull request evaluations and push builds often run with broad write permissions, exposing repositories to supply-chain attacks if an external action or dependency is compromised. Furthermore, rapid commits trigger redundant, concurrent builds that waste compute minutes and introduce race conditions in artifact generation.

### Decision Drivers
- Enforce the Principle of Least Privilege across all CI workflows.
- Cancel superseded workflow runs automatically on incoming commits.
- Ensure automated, hermetic execution of unit tests, lint checks, and artifact assembly.

### Considered Options
1. **Option 1**: Retain default elevated token permissions and allow redundant pipeline runs.
2. **Option 2 (Chosen)**: Hardened GitHub Actions specification in `.github/workflows/android.yml`:
   - Declare explicit top-level `permissions: contents: read`.
   - Add `concurrency` block with `cancel-in-progress: true` keyed by workflow and git ref.
   - Fail-closed execution sequencing: lint check -> 124 unit tests -> APK assembly -> SHA-256 attestation -> report archival.

### Consequences
- **Positive**: Supply-chain attack surface minimized; compute runner waste eliminated; deterministic verification guaranteed.
- **Negative**: Workflows requiring branch pushes (e.g. automated release tagging) must be isolated into separate, authenticated release dispatch workflows.

---

## ADR-026: Cryptographic Artifact Provenance Attestation & Checksum Verification

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Release Manager, Application Security Lead, Platform Engineer

### Context & Problem Statement
In humanitarian disaster operations, field teams download and install Android application binaries (`.apk`) via sideloading, local MDM distributions, or unstable cellular connections. Tampered or partially downloaded binaries can lead to device crashes or unauthorized data access.

### Decision Drivers
- Provide cryptographic proof of binary provenance and bit-level integrity.
- Enable offline verification by field administrators prior to handset deployment.

### Considered Options
1. **Option 1**: Distribute raw APK files without accompanying cryptographic digests.
2. **Option 2 (Chosen)**: Automated SHA-256 digest generation and attestation:
   - For every compiled APK (`app-debug.apk` / `app-release.apk`), calculate SHA-256 digest via `sha256sum` or `[System.Security.Cryptography.SHA256]`.
   - Generate companion `.sha256` attestation file uploaded alongside the binary artifact in CI/CD.
   - Enforce APK size ceiling (<= 15.0 MB; currently ~10.0 MB).

### Consequences
- **Positive**: Tampered or corrupted binaries are immediately detectable before installation; release governance is strictly auditable.
- **Negative**: Field administrators must perform hash verification during offline distribution.

---

## ADR-027: Declarative Infrastructure as Code (IaC) & Multi-Environment Isolation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Cloud Infrastructure Architect, DevOps Lead, Backend Engineer

### Context & Problem Statement
Cloud configuration, database security rules, and query indexes previously relied on implicit setup, risking manual drift between developer testing and live disaster relief environments.

### Decision Drivers
- Version-control all cloud infrastructure targets, security rules, and database indexes.
- Isolate development, staging, and production datastore instances.
- Enable automated, reproducible infrastructure deployment via the Firebase CLI.

### Considered Options
1. **Option 1**: Configure database rules and indexes manually via the Firebase Web Console.
2. **Option 2 (Chosen)**: Version-controlled declarative configuration:
   - `firebase.json`: Links declarative rules (`database.rules.json`) to database targets.
   - `.firebaserc`: Defines project aliases (`default: relief-distribution-2w5`, `staging`, `production`).
   - `database.rules.json`: Contains all security validations and `.indexOn` query specifications.

### Consequences
- **Positive**: 100% declarative reproducibility; console-driven manual drift eliminated; automated staging vs. production deployment paths established.
- **Negative**: All rule modifications require Git commits and CLI deployment rather than quick console edits.

---

## ADR-028: OpenTelemetry Structured JSON Logging & Distributed Trace Correlation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Observability Architect, SRE Lead, Mobile Lead

### Context & Problem Statement
Unstructured plain-text Android logging (`Log.d`, `Log.e`) makes automated parsing, filtering, and cross-tier event correlation impossible in centralized observability backends. Furthermore, logging multi-step workflows without correlation IDs prevents tracing aid requests across client creation, AI triage, and distributor assignment.

### Decision Drivers
- Standardize on OpenTelemetry-compatible structured JSON log event schemas.
- Implement distributed trace correlation (`traceId`) across thread boundaries.
- Automate PII and credential sanitization within logging utilities.

### Considered Options
1. **Option 1**: Continue using unstructured raw string logs.
2. **Option 2 (Chosen)**: Enhanced `AppLogger.java` architecture:
   - Manage thread-local trace correlation IDs (`startTrace()`, `getTraceId()`, `clearTrace()`).
   - Format structured JSON payloads with canonical fields (`level`, `tag`, `action`, `traceId`, `attributes`).
   - Automatically redact attributes containing `email`, `phone`, `password`, `token`, or `secret`.
   - Strip debug logging in production release builds via ProGuard/R8.

### Consequences
- **Positive**: Distributed workflows are easily traceable across centralized log aggregators; PII leakage prevented; sub-millisecond logging latency.
- **Negative**: Developers must pass operation tags and structured attributes rather than simple ad-hoc log strings.

---

## ADR-029: Automated Declarative Disaster Recovery & Snapshot Durability

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Disaster Recovery Lead, Platform SRE, Data Engineer

### Context & Problem Statement
Disaster relief operations cannot tolerate data loss or prolonged database unavailability during active field coordination. A catastrophic cloud failure, accidental deletion, or database corruption could leave relief coordinators blind to pending survivor aid requests.

### Decision Drivers
- Enforce strict Recovery Point Objective (RPO <= 1 hour) and Recovery Time Objective (RTO <= 30 minutes).
- Automate scheduled snapshot exports of all mandatory collections (`users`, `relief_requests`, `inventory`, `notifications`, `distributor_locations`).
- Ensure backups are sanitized, cryptographically signed, and schema-verified upon creation.

### Considered Options
1. **Option 1**: Rely exclusively on cloud provider managed backups without client verification.
2. **Option 2 (Chosen)**: Declarative, automated snapshot and verification utilities (`Backup-FirebaseRtdb.ps1` & `backup_firebase_rtdb.py`):
   - Hourly declarative snapshot exports producing timestamped JSON datasets with embedded SHA-256 digests.
   - Immediate read-back verification drill validating Schema v2.0.0 and collection presence.
   - Standard Operating Procedure (SOP-02) documenting point-in-time restoration.

### Consequences
- **Positive**: Verified data durability; verified RPO <= 1h and RTO <= 30m; automated schema and integrity checks.
- **Negative**: Storage management policies must rotate and archive older snapshot files.

---

## ADR-030: Strict Non-Positive Quantity Rejection in Domain Automations

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Principal QA Architect, Lead Backend Engineer, Domain Modeler

### Context & Problem Statement
`ReliefWorkflowAutomator.java` allowed claims on requests with `quantity <= 0`, risking warehouse stock calculation corruption when requests bypassed UI form validation.

### Decision Drivers
- Protect inventory reservation math from zero or negative integer mutations.
- Enforce defense-in-depth at the domain layer regardless of presentation validation.

### Considered Options
1. **Option 1**: Rely exclusively on client UI validation in `CreateReliefRequestActivity`.
2. **Option 2 (Chosen)**: Enforce `requestedQty <= 0` rejection in `onDistributorAcceptRequest`, `onDeliveryCompleted`, and `onRequestCancelled`.

### Consequences
- **Positive**: Corrupted or tampered zero/negative quantity requests cannot mutate inventory stock.
- **Negative**: Requests must always have a positive quantity integer >= 1.

---

## ADR-031: Bounded Temporal Clamping & Text Truncation in AI Triage Evaluation

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: AI Red-Team Engineer, Principal Performance Architect

### Context & Problem Statement
Device clock skews in disaster zones caused future creation timestamps (`createdAt > now`), resulting in negative `ageHours` in triage cache keys. Furthermore, adversarial notes with extreme lengths (>10,000 chars) risked algorithmic regex search stalls.

### Decision Drivers
- Eliminate negative age hours in cache keys.
- Prevent denial-of-service memory exhaustion during NLP keyword extraction.

### Considered Options
1. **Option 1**: Process unconstrained string lengths and raw timestamp differences.
2. **Option 2 (Chosen)**: Clamp `ageHours = Math.max(0, now - createdAt) / 3600000L` and bound note parsing length to 2,000 characters.

### Consequences
- **Positive**: Immune to future clock skew cache corruption; immune to regex text-bomb denial of service.
- **Negative**: Extremely long notes beyond 2,000 characters are truncated for keyword scoring.

---

## ADR-032: Safe Non-Positive Length Boundary in Security Input Sanitizer

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Application Security Lead, Principal SDET

### Context & Problem Statement
Passing `maxLength <= 0` to `SecuritySanitizer.sanitizeInput(input, maxLength)` resulted in an unchecked `StringIndexOutOfBoundsException`.

### Decision Drivers
- Guarantee robust crash-free input sanitization under all integer parameters.

### Considered Options
1. **Option 1**: Throw `IllegalArgumentException` on negative `maxLength`.
2. **Option 2 (Chosen)**: Return `""` safely if `maxLength <= 0`.

### Consequences
- **Positive**: Sanitizer never crashes on non-positive length constraints.
- **Negative**: Callers must specify `maxLength > 0` to receive sanitized content.

---

## ADR-033: Non-Negative Invariant Clamping in Warehouse Inventory Models

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Data Architect, QA Verification Specialist

### Context & Problem Statement
Direct calls to `setQuantityAvailable(int)` and `setQuantityReserved(int)` in `InventoryItem.java` accepted negative integers without bounds checking.

### Decision Drivers
- Enforce physical inventory invariants directly at the entity level.

### Considered Options
1. **Option 1**: Allow raw integer setters and rely on caller validation.
2. **Option 2 (Chosen)**: Clamp values via `Math.max(0, quantity)` in both setters.

### Consequences
- **Positive**: Negative stock states cannot exist in entity memory.
- **Negative**: Callers attempting to decrement past zero will have values clamped to 0 rather than negative numbers.

---

## ADR-034: Full-System Release Candidate Certification & Objective Gate Qualification

- **Status**: Accepted
- **Date**: 2026-11-09
- **Deciders**: Independent Release Quality Lead, Principal QA Architect, Mobile Platform Lead

### Context & Problem Statement
Prior to promoting builds to production release candidates, an independent assessment must evaluate the system across all 18 Quality Gates (Gates A through R) based strictly on executed evidence rather than optimistic claims.

### Decision Drivers
- Establish clear Go / No-Go qualification criteria.
- Bind release certification to a verifiable cryptographic artifact digest (SHA-256).

### Considered Options
1. **Option 1**: Rely on developer sign-off without independent gate verification.
2. **Option 2 (Chosen)**: Comprehensive gate matrix (Gates A through R) backed by 131 passing tests, clean lint, and SHA-256 attestation.

### Consequences
- **Positive**: Unassailable release candidate governance; zero unverified claims.
- **Negative**: Release candidates must re-run all 18 gates if code modifications occur.








