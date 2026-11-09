---
name: third-party-integration-engineering
description: Google Maps API, Location Services, Geocoding, External Navigation, Emergency Telecom, and secure adapter architectures.
---

# Third-Party Integration Engineering Skill

## 1. Purpose
Provide architectural discipline, error isolation, credential security, and contract testing for third-party mobile integrations (Google Maps, FusedLocationProviderClient, System Telecom Dialer).

## 2. Activation Criteria
Activate this skill when:
- Integrating Google Maps SDK or Location Services.
- Launching external navigation intents for aid distributors.
- Interfacing with telecommunications services (e.g. `Intent.ACTION_DIAL`).
- Managing API keys and provider-specific error semantics.

## 3. Engineering Invariants & Standards
1. **Isolated Adapters**: External SDK details must never leak into the domain layer; wrap SDK calls in dedicated adapters or repositories.
2. **Key Security**: API keys must be loaded from `local.properties` or environment variables via Gradle manifest placeholders, never hardcoded.
3. **Intent Safety**: Use safe intent actions (`Intent.ACTION_DIAL`, `Intent.ACTION_VIEW` with geo URIs) that do not require dangerous privileged runtime permissions.
4. **Fallback Handling**: If Google Play Services is missing or outdated, gracefully degrade with clear user guidance.

## 4. Execution Procedure
1. **Verify Manifest & Permissions**: Ensure only declared permissions (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `INTERNET`) are used.
2. **Implement Adapter**: Provide a clean wrapper that checks provider availability and handles exceptions.
3. **Intent Construction**: For navigation, build standard `geo:latitude,longitude?q=latitude,longitude(Label)` URIs.
4. **Contract Verification**: Validate adapter behavior with unit tests using mocks for SDK components.
