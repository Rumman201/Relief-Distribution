# Phase 6 — Application & API Security Hardening

**Document ID**: `54-phase-6-application-api-security`  
**Phase**: Phase 6 of 10 — Comprehensive Security Transformation  
**Program**: Relief Distribution System Modernization (`com.example.reliefdistribution`)  
**Standard**: OWASP MASVS-PLATFORM / OWASP MASVS-CODE / OWASP MASVS-NETWORK  

---

## 1. Executive Summary

This document establishes the client application hardening, Intent security, component isolation, API transport security, and R8/ProGuard obfuscation controls implemented in Phase 6.

---

## 2. Android Component Exposure & Intent Security

### 2.1 Component Isolation Analysis (`AndroidManifest.xml`)
Android components (Activities, Services, Receivers) with `android:exported="true"` can be initiated by any application installed on the device. An attacker app could forge Intents to bypass authentication or access restricted functionality.

| Component Name | Type | Exported Status | Intent Filter / Action | Security Evaluation |
| :--- | :--- | :---: | :--- | :--- |
| `StartActivity` | Activity | **true** | `android.intent.action.MAIN`, `LAUNCHER` | **Permitted**: Single entry point. Sanitizes auth state on boot. |
| `LoginActivity` | Activity | **false** | None | **Secure**: Cannot be launched directly by external apps. |
| `SignUpActivity` | Activity | **false** | None | **Secure**: Cannot be launched directly by external apps. |
| `WelcomeActivity` | Activity | **false** | None | **Secure**: Internal routing only. |
| `SurvivorDashboard` | Activity | **false** | None | **Secure**: Internal routing only. |
| `DistributorDashboard` | Activity | **false** | None | **Secure**: Internal routing only. |
| `CreateReliefRequestActivity`| Activity | **false** | None | **Secure**: Internal routing only. |
| `ManageRequestsActivity` | Activity | **false** | None | **Secure**: Internal routing only. |
| `InventoryManagementActivity`| Activity | **false** | None | **Secure**: Internal routing only. |
| `NotificationsActivity` | Activity | **false** | None | **Secure**: Internal routing only. |
| `Profile` | Activity | **false** | None | **Secure**: Internal routing only. |
| `GoogleMap` | Activity | **false** | None | **Secure**: Internal routing only. |
| `MapActivity` | Activity | **false** | None | **Secure**: Internal routing only. |
| `Informations` | Activity | **false** | None | **Secure**: Internal routing only. |
| `MyReliefRequestsActivity` | Activity | **false** | None | **Secure**: Internal routing only. |

**Component Exposure Ratio**: 1/15 (6.7%) — only the standard Android launcher is exported.

### 2.2 Intent Safety Invariant: Emergency Dialing
- **Vulnerability**: Historical code triggered automated phone calls using `Intent.ACTION_CALL` requiring dangerous `CALL_PHONE` permission.
- **Remediation**: Replaced with `Intent.ACTION_DIAL` (`Uri.parse("tel:999")`), handing safe control to the user's default system dialer with zero special dangerous permissions required.

---

## 3. Network Transport & API Security

### 3.1 Network Security Configuration (`network_security_config.xml`)
Cleartext HTTP traffic is blocked across all domains:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <debug-overrides>
        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </debug-overrides>
</network-security-config>
```

### 3.2 Key Protection: Google Maps Platform SDK
- **Storage**: Key resides in `local.properties` (`MAPS_API_KEY=...`) which is untracked in version control.
- **Injection**: Injected into `AndroidManifest.xml` via Gradle `manifestPlaceholders["MAPS_API_KEY"]`.
- **Cloud Restrictions**: Key restricted in Google Cloud Console by Android package name (`com.example.reliefdistribution`) and signing certificate SHA-1 fingerprint.

---

## 4. Code Protection & Reverse-Engineering Defense (R8 / ProGuard)

`app/proguard-rules.pro` was hardened to achieve:
1. **Model Field Preservation**: Keeps all data model attributes (`User`, `ReliefRequest`, `InventoryItem`, `Notification`) to preserve Firebase reflection deserialization.
2. **Log Stripping**: Strips all `android.util.Log` and `System.out.println` calls in release builds via `-assumenosideeffects` rules.
3. **Symbol Renaming**: Renames internal algorithms and ViewModels to prevent easy static decompilation.
4. **Crash Reporting Integrity**: Preserves line numbers via `-keepattributes SourceFile,LineNumberTable`.
