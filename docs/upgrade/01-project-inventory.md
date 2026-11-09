# 01 — Project Inventory & Technology Stack

## 1. Project Overview
- **Project Name**: Relief Distribution App (GPS Guided Relief Distribution System)
- **Application ID / Package**: `com.example.reliefdistribution`
- **Purpose**: Mobile application coordinating disaster relief operations in Bangladesh and vulnerable regions. Connects Disaster Survivors (needing emergency aid, locating relief stations, and requesting help) with Relief Distributors (broadcasting live GPS tracking, coordinating supply drops, and monitoring emergency requests).

## 2. Technology Stack & Runtimes
| Category | Technology | Version | Notes / Location |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 (Target/Source 11) | OpenJDK Temurin 17.0.21+9 installed |
| **Android SDK** | Android SDK Platform 34 | Compile: 34, Target: 34, Min: 24 | Android 7.0 (Nougat) to 14 (UpsideDownCake) |
| **Build Tools** | Android SDK Build-Tools | 34.0.0 | Platform-tools v37.0.1 |
| **Build Automation** | Gradle & AGP | Gradle 8.9, AGP 8.7.3 | Configured with Kotlin DSL (`build.gradle.kts`) |
| **Cloud Backend** | Google Firebase | BoM 33.8.0 | Project: `relief-distribution-2w5` |
| **Authentication** | Firebase Auth | 23.1.0 | Email / Password authentication |
| **Database** | Firebase Realtime Database | 21.0.0 | `https://relief-distribution-2w5-default-rtdb.firebaseio.com` |
| **Maps & Geospatial** | Google Play Services Maps | 19.0.0 | Google Map Fragment, camera tracking |
| **Location Services** | Google Play Services Location | 21.3.0 | `FusedLocationProviderClient` |
| **UI Components** | Google Material Design & AndroidX | Material 1.12.0, AppCompat 1.7.0, ConstraintLayout 2.2.0 | Material 3 DayNight theme |
| **Testing** | JUnit 4, AndroidX Ext JUnit, Espresso | JUnit 4.13.2, Espresso 3.6.1 | JVM unit tests + instrumented runner |

## 3. Repository Structure & Boundary Map
```
Relief-Distribution-App/
├── .agents/
│   ├── rules/
│   │   ├── android-quality-rules.md
│   │   └── security-rules.md
│   └── skills/
│       ├── backend-engineering/
│       ├── database-engineering/
│       ├── devops-reliability/
│       ├── frontend-experience-engineering/
│       ├── mobile-device-qa/
│       ├── performance-optimization/
│       ├── project-intelligence-audit/
│       ├── qa-verification/
│       ├── security-hardening/
│       ├── system-architecture-review/
│       └── technical-documentation/
├── app/
│   ├── build.gradle.kts                   # App module build dependencies
│   ├── google-services.json               # Firebase configuration
│   ├── proguard-rules.pro                 # R8 / ProGuard obfuscation rules
│   └── src/
│       ├── androidTest/java/com/example/reliefdistribution/
│       │   └── ExampleInstrumentedTest.java
│       ├── test/java/com/example/reliefdistribution/
│       │   └── ExampleUnitTest.java
│       └── main/
│           ├── AndroidManifest.xml        # Permissions & Activity declarations
│           ├── java/com/example/reliefdistribution/
│           │   ├── DistributorDashboard.java # Distributor operational control panel
│           │   ├── GoogleMap.java            # Live GPS tracking map activity
│           │   ├── Informations.java         # Information hub screen
│           │   ├── LoginActivity.java        # Email/password authentication & role check
│           │   ├── MainActivity.java         # Unused boilerplate activity
│           │   ├── Profile.java              # User profile & logout screen
│           │   ├── SignUpActivity.java       # User registration screen
│           │   ├── StartActivity.java        # Launcher & splash routing activity
│           │   ├── SurvivorDashboard.java    # Survivor emergency & relief dashboard
│           │   ├── User.java                 # User data entity model
│           │   └── WelcomeActivity.java      # Welcome gateway (Login / Register)
│           └── res/
│               ├── drawable/                 # 24 vector and raster graphics
│               ├── layout/                   # 10 Activity XML layouts
│               ├── values/                   # colors.xml, strings.xml, themes.xml
│               ├── values-night/             # Dark theme overrides
│               └── xml/                      # backup_rules.xml, data_extraction_rules.xml
├── docs/upgrade/                          # Transformation Control Center documentation
├── gradle/
│   ├── libs.versions.toml                 # Version catalog
│   └── wrapper/                           # Gradle wrapper jar & properties
├── AGENTS.md                              # Persistent agent guidelines
├── build.gradle.kts                       # Root project build script
├── gradle.properties                      # JVM memory and build settings
├── gradlew / gradlew.bat                  # Gradle execution wrappers
├── local.properties                       # Machine-specific SDK directory
└── settings.gradle.kts                    # Root project settings
```

## 4. Feature Inventory
| Feature ID | Feature Name | Description | Source File | Status | Technical Debt / Defects |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **FEAT-001** | Splash Routing & Auto-Login | Checks cached Firebase Auth user; routes to Distributor or Survivor dashboard | `StartActivity.java` | Implemented | Race condition: user can click Get Started while async check runs; NPE risk |
| **FEAT-002** | Welcome Gateway | Entry screen presenting Login and Register buttons | `WelcomeActivity.java` | Implemented | Fixed 300dp top margin causes clipping on small screens |
| **FEAT-003** | User Registration | Registers new account in Firebase Auth and saves profile in Realtime Database | `SignUpActivity.java` | Implemented | **P0 Security Defect**: Stores plain password in DB & SharedPreferences; form overflows without ScrollView |
| **FEAT-004** | User Authentication | Logs user in and validates chosen role against DB profile | `LoginActivity.java` | Implemented | Stores plain password in SharedPreferences; leaves user authenticated even on role mismatch |
| **FEAT-005** | Distributor Dashboard | Distributor toggles Distribute Mode and opens tracking map | `DistributorDashboard.java` | Partially Implemented | Dead click listener on `ivHomeIcon`; features/settings buttons unhandled; dangerous `CALL_PHONE` |
| **FEAT-006** | Survivor Dashboard | Survivor access to Emergency, Informations, and Aid points | `SurvivorDashboard.java` | Partially Implemented | Unnamed layout variables (`linearLayout1..6`); Ask for Help button unhandled |
| **FEAT-007** | Live GPS Tracking Map | Displays Google Map and uploads live GPS coordinates to Firebase | `GoogleMap.java` | Implemented | Class name shadows SDK GoogleMap; deprecated Location APIs; toast spam every 2s; no error handling |
| **FEAT-008** | Emergency 999 Hotline | Triggers telephone call to 999 emergency services | `DistributorDashboard`, `SurvivorDashboard` | Defective | Uses `ACTION_CALL` without handling permission result; should use `ACTION_DIAL` |
| **FEAT-009** | Information Hub | Displays relief guidelines and disaster advisories | `Informations.java` | Stub | Blank TextView `tvInformations` with 677dp height and no content |
| **FEAT-010** | User Profile & Logout | Clears session data and signs out of Firebase Auth | `Profile.java` | Implemented | Profile details (name, email, phone, role) are not rendered |
| **FEAT-011** | Main Activity | Template activity created by Android Studio | `MainActivity.java` | Dead Code | Never launched by manifest; empty layout |

