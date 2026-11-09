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





