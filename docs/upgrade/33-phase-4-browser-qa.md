# Phase 4 — Visual Regression & Layout QA Matrix

**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: 4 of 10  
**Discipline**: Mobile Device QA & Visual Inspection  
**Status**: Completed  
**Date**: 2026-11-09  

---

## 1. Test Device & Viewport Matrix

The transformed layout resources and activities were evaluated against the following canonical Android device configurations:

| Device Profile | Screen Resolution | Density | Viewport Dimensions | Aspect Ratio | Verification Result |
|---|---|---|---|---|---|
| **Compact Budget Phone** | 720 x 1280 px | hdpi (240 dpi) | 360 x 640 dp | 16:9 | **PASS** — Scroll viewports prevent clipping; all buttons reachable. |
| **Standard Modern Phone** | 1080 x 2400 px | xxhdpi (400 dpi) | 392 x 872 dp | 20:9 | **PASS** — Crisp card layouts, comfortable thumb reachability. |
| **Large Flagship Phone** | 1440 x 3120 px | xxxhdpi (560 dpi) | 411 x 891 dp | 19.5:9 | **PASS** — Flawless vector icon scaling and typography contrast. |
| **7" Field Tablet** | 800 x 1280 px | mdpi (160 dpi) | 600 x 960 dp | 16:10 | **PASS** — Card containers remain centered and legible without distortion. |

---

## 2. Interactive Journey QA Scenarios

| Test Case ID | User Journey | Expected Visual & Behavioral Result | Execution Status |
|---|---|---|---|
| **QA-P4-01** | First launch on cold boot | `StartActivity` displays hero branding card with 4:3 aspect ratio, "GET STARTED" button slides in smoothly. | **PASS** |
| **QA-P4-02** | Welcome screen navigation | `WelcomeActivity` presents centered branding badge; clicking "Sign In" routes to `LoginActivity`, "Create Account" routes to `SignUpActivity`. | **PASS** |
| **QA-P4-03** | Login keyboard interaction | Entering text in `etEmail` and `etPassword` causes viewport to resize gracefully via `adjustResize`; Login button remains reachable via scroll. | **PASS** |
| **QA-P4-04** | Password toggle visibility | Tapping eye icon on password input toggles plaintext visibility without destroying layout structure. | **PASS** |
| **QA-P4-05** | Distributor live status chip | `DistributorDashboard` displays green "System Online" pill when Firebase connected; updates to amber "Offline Mode" upon network severance. | **PASS** |
| **QA-P4-06** | Survivor 999 dialing | Tapping emergency card opens system dialer with `tel:999` prefilled; zero security exceptions or crash events. | **PASS** |
| **QA-P4-07** | Map tracking badge toggle | When launched with `distributeMode=true`, top badge shows green "Broadcasting Live GPS"; when `false`, shows teal "Viewing Relief Points". | **PASS** |
| **QA-P4-08** | Dynamic Profile population | `Profile` dynamically renders user initials circle avatar, formatted full name, role badge, email, and UID from SharedPreferences. | **PASS** |
| **QA-P4-09** | Disaster advisory cards | `Informations` displays 4 detailed Material cards (Cyclone, Flood, Earthquake, First Aid) with clean typography and spacing. | **PASS** |
| **QA-P4-10** | Touch target verification | All bottom navigation icons provide minimum 48x48dp interactive bounding boxes with visual ripple feedback. | **PASS** |

