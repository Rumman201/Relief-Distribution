# Specialist Engineering Skill Registry

This registry documents all active Antigravity engineering skills created and configured for the Relief Distribution System transformation program.

| Skill Name | Purpose | Activation Criteria | Relevant Area | Reusability in Phases 2–10 |
| :--- | :--- | :--- | :--- | :--- |
| `project-intelligence-audit` | Complete codebase exploration, dependency mapping, risk discovery | Starting any phase or auditing unknown code | Workspace & Architecture | Reused in Phase 2, 5, 10 for re-audits |
| `system-architecture-review` | Clean Architecture, MVVM/MVI design, modularization, ADRs | Refactoring God-Activities to ViewModels | Architecture & Refactoring | Core skill for Phase 2 (Architecture Modernization) |
| `backend-engineering` | Firebase Auth, Realtime Database, cloud resilience, offline data | Backend integration, data transactions | Backend & Cloud | Core skill for Phase 3 (Backend & Data Engineering) |
| `frontend-experience-engineering` | Material Design 3, ViewBinding, responsive layouts, accessibility | Designing or editing XML layouts, styles, themes | Presentation / UI | Core skill for Phase 4 (Frontend Transformation) |
| `security-hardening` | Secrets protection, OWASP MASVS, permission boundaries | Handling API keys, auth tokens, permissions | Security & Privacy | Core skill for Phase 6 (Security Hardening) |
| `database-engineering` | Schema design, indexing, data consistency, caching | Modifying data trees, indexing, SQLite/Room | Persistence & Caching | Core skill for Phase 3 & 7 (Data & Performance) |
| `performance-optimization` | Battery optimization, GPS throttling, memory leaks, R8 | Profiling CPU, battery, RAM, APK size | Performance & Runtime | Core skill for Phase 7 (Performance & Scalability) |
| `qa-verification` | Unit testing, Mockito, Espresso, quality gates | Writing/running tests, verifying bug fixes | Testing & QA | Core skill for all phases (Phase 1–10) |
| `devops-reliability` | Gradle automation, CI/CD pipelines, reproducible builds | Modifying build scripts, GitHub Actions | DevOps & CI/CD | Core skill for Phase 8 (DevOps & Reliability) |
| `technical-documentation` | Architecture documentation, ADRs, handoffs | Updating docs/upgrade, authoring ADRs | Documentation & DX | Reused continuously across all phases |
| `mobile-device-qa` | Multi-screen responsiveness, touch testing, permissions | Verifying UI on small/large screens, keyboard | Mobile UX & QA | Reused in Phase 4, 9, 10 |
| `architecture-modernization` | Boundary definition, Clean Architecture/MVVM design | Refactoring monoliths and God-Activities | Architecture & Modernization | Core skill for Phase 2 |
| `safe-large-scale-refactoring` | Strangler Fig pattern, characterization testing, risk isolation | Restructuring and renaming core components | Refactoring & Safety | Core skill for Phase 2–4 |
| `modular-codebase-engineering` | Package structure design, encapsulation, dependency validation | Organizing packages, modules, and boundaries | Code Structure | Core skill for Phase 2–3 |
| `type-safety-and-contracts` | Type safety enforcement, ViewBinding, contract validation | Replacing unsafe casts and view lookups | Type Safety | Core skill for Phase 2 |
| `technical-debt-reduction` | Systematic debt reduction, dead-code removal, de-duplication | Pruning unused files, resolving shadowing | Technical Debt | Core skill for Phase 2 |
| `architecture-regression-testing` | Invariant checks, ViewModel unit testing, Mockito | Testing ViewModels and architectural seams | Testing & Verification | Core skill for Phase 2–10 |
| `backend-api-engineering` | API contract discovery, endpoint validation, structured errors | Designing and testing backend interfaces | API Engineering | Core skill for Phase 3 |
| `database-integrity-engineering` | Schema constraints, Firebase security rules, transactions | Authoring security rules, offline persistence | Database & Security | Core skill for Phase 3 |
| `domain-logic-verification` | Business invariant testing, state transitions, domain rules | Validating relief operations and user workflows | Domain Modeling | Core skill for Phase 3 |
| `distributed-processing-reliability` | Idempotent mutations, backoff retries, disconnect cleanup | Asynchronous jobs, queue processing | Distributed Systems | Core skill for Phase 3 |
| `backend-resilience-and-observability` | PII-safe logging, connection monitoring, health diagnostics | Monitoring connectivity and error recovery | Reliability & SRE | Core skill for Phase 3 |
| `integration-contract-engineering` | External SDK encapsulation, error translation, key protection | Integrating third-party platforms safely | Integration & Cloud | Core skill for Phase 3 |
| `iot-cloud-backend-engineering` | Edge GPS telemetry, rate limiting, disconnect teardown | Mobile edge tracking in disaster zones | IoT & Edge Telemetry | Core skill for Phase 3 |
| `creative-frontend-direction` | Art direction, visual hierarchy, layout composition, typography | Designing and directing mobile UI aesthetics | Creative Direction | Core skill for Phase 4 |
| `advanced-motion-engineering` | Motion architecture, interpolators, ripple feedback, transitions | Adding fluid, performance-aware animations | Motion Engineering | Core skill for Phase 4 |
| `frontend-design-system` | Three-layer design tokens, color palette, typography scales | Building systematic, reusable UI foundations | Design Systems | Core skill for Phase 4 |
| `responsive-experience-engineering` | Multi-screen adaptation, ScrollView containment, soft keyboard | Ensuring UI adapts across device sizes | Responsive UX | Core skill for Phase 4 |
| `accessibility-and-inclusive-ux` | 48dp touch targets, contentDescription, WCAG 2.2 AA contrast | Ensuring inclusive, accessible interactions | Accessibility | Core skill for Phase 4 |
| `premium-interaction-design` | Tactile feedback, floating labels, state indicators, dialogs | Crafting high-quality interactive components | Interaction Design | Core skill for Phase 4 |
| `frontend-performance-profiling` | Hierarchy flattening, overdraw elimination, ViewBinding speed | Optimizing layout rendering and frame rates | UI Performance | Core skill for Phase 4 |
| `visual-regression-and-browser-qa` | Multi-device layout verification, resource linting, UI QA | Auditing visual rendering and regression safety | Visual QA | Core skill for Phase 4 |
| `iot-dashboard-experience` | Telemetry visualization, GPS tracking state, live badges | Building operational maps and edge dashboards | IoT Experience | Core skill for Phase 4 |
| `advanced-feature-engineering` | End-to-end feature discovery, value assessment, vertical slice execution | Building user journeys for Survivors & Distributors | Feature Engineering | Core skill for Phase 5 |
| `production-ai-integration` | AI triage architecture, model evaluation, structured schemas, guardrails | Designing relief prioritization & urgency inference | Artificial Intelligence | Core skill for Phase 5 |
| `agentic-workflow-engineering` | Bounded tool-using agent systems, approval boundaries, dispatch loops | Multi-step aid routing and dispatch orchestration | Agentic Workflows | Core skill for Phase 5 |
| `intelligent-automation-engineering` | Event-driven automation, lifecycle state triggers, stock reservation | Automating request status and notification dispatch | Automation & Workflows | Core skill for Phase 5 |
| `ai-evaluation-and-guardrails` | Quality evaluation datasets, adversarial test cases, latency benchmarks | Validating AI outputs, groundedness & prompt safety | AI Evaluation & QA | Core skill for Phase 5 |
| `third-party-integration-engineering` | Google Maps API, external navigation intents, telecom dialer contracts | Integrating external mapping, geocoding and routing | Integrations & Telephony | Core skill for Phase 5 |
| `iot-feature-engineering` | Mobile edge IoT telemetry, offline breadcrumbs, GPS power throttling | Real-time tracking and offline breadcrumb store | IoT & Edge Telemetry | Core skill for Phase 5 |
| `feature-rollout-and-observability` | Feature flags, circuit breakers, structured logging, rollback controls | Safeguarding runtime feature releases | Observability & SRE | Core skill for Phase 5 |
| `full-stack-feature-qa` | End-to-end testing, domain test coverage, AI quality benchmarks | Comprehensive testing across UI, domain, data tiers | Full-Stack QA | Core skill for Phase 5 |
| `principal-security-audit` | Threat modeling, attack-surface enumeration, trust boundary analysis | Reviewing architecture, threat actors, and CVSS scoring | Security Architecture | Core skill for Phase 6 |
| `zero-trust-access-engineering` | Role-based access control (RBAC), least privilege, microsegmentation | Defining security rules, user isolation, role boundaries | Zero Trust & Auth | Core skill for Phase 6 |
| `authentication-authorization-hardening` | Auth flow hardening, session security, Play Integrity verification | Reviewing login/signup, session lifecycle, token safety | Identity & Auth | Core skill for Phase 6 |
| `secure-api-and-backend` | Secure backend integration, payload validation, TLS 1.3 | Reviewing network config, input validation, transport safety | API & Network Security | Core skill for Phase 6 |
| `ai-agentic-security` | AI guardrails, prompt-injection defense, deterministic bounds | Testing AI triage models, state machine invariants | AI Safety & Security | Core skill for Phase 6 |
| `database-privacy-security` | Data privacy, PII minimization, backup exclusion, rule hardening | Auditing PII in logs, backup rules, RTDB validation | Privacy & Data Security | Core skill for Phase 6 |
| `cloud-infrastructure-hardening` | Firebase rules hardening, API key scoping, App Check integration | Hardening cloud configuration, RTDB rules, API restrictions | Cloud Security | Core skill for Phase 6 |
| `software-supply-chain-security` | Dependency risks, R8/ProGuard code hardening, CI/CD pipeline | Reviewing dependencies, R8 rules, build integrity | Supply Chain & DevSecOps | Core skill for Phase 6 |
| `authorized-security-testing` | Security test automation, boundary fuzzing, negative auth tests | Writing/running security regression tests, negative tests | Security QA & Testing | Core skill for Phase 6 |
| `iot-firmware-security` | Edge telemetry anti-spoofing, replay mitigation, disconnect cleanup | Validating GPS coordinates, buffer bounds, freshness | IoT & Edge Security | Core skill for Phase 6 |
| `security-observability-response` | Security telemetry, PII-safe audit logging, incident response | Designing security audit logs, incident playbooks | SecOps & Observability | Core skill for Phase 6 |
| `security-regression-engineering` | Continuous security verification, invariant testing, fitness gates | Enforcing security invariants in CI/CD and refactorings | Security Verification | Core skill for Phase 6 |

All skills reside in `.agents/skills/<skill-name>/SKILL.md` with complete YAML frontmatter and step-by-step engineering protocols. Total registered specialist skills: 54.
