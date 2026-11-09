# Phase 6 — Data Privacy, Storage Security & PII Protection

**Document ID**: `55-phase-6-data-privacy-security`  
**Phase**: Phase 6 of 10 — Comprehensive Security Transformation  
**Program**: Relief Distribution System Modernization (`com.example.reliefdistribution`)  
**Standard**: GDPR Data Minimization / OWASP MASVS-STORAGE  

---

## 1. Executive Summary

This document establishes the data privacy safeguards, PII protection mechanisms, and storage isolation controls implemented in Phase 6. In humanitarian disaster scenarios, protecting victim identities and physical locations is critical to prevent exploitation, stalking, or discriminatory withholding of aid.

---

## 2. PII Inventory & Data Minimization

| Data Field | Purpose | Sensitivity Level | Minimization Rule | Redaction / Masking Standard |
| :--- | :--- | :--- | :--- | :--- |
| **Email Address** | Account Authentication & Alerting | High PII | Kept in Auth provider; masked in logs | `AppLogger.maskEmail` (e.g. `j***e@relief.org`) |
| **Phone Number** | Direct Crisis Coordination | High PII | Dialed via user dialer only | `AppLogger.maskPhone` (e.g. `017****5678`) |
| **GPS Coordinates** | Emergency Delivery Dispatch | Critical PII | Ephemeral in RAM buffer; bounded ranges | Stored to 4 decimal places (~11m resolution) |
| **Survivor Full Name**| Aid Recipient Identification | Medium PII | Displayed to assigned distributor only | Stripped of control characters |
| **Password** | N/A (Handled by Firebase Auth) | Extreme Secret | **NEVER stored in DB or SharedPreferences** | Stripped from `User.java` and SharedPreferences |

---

## 3. Storage Isolation & Android Backup Protection

### 3.1 Android Backup Exclusion Policy
By default, Android Auto-Backup (API 23+) and Cloud Transfer (API 31+) back up application SharedPreferences and databases to Google Drive or local ADB archives. If unconfigured, this permits unauthorized extraction of session tokens and user identifiers.

#### Android 11 and Below (`app/src/main/res/xml/backup_rules.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <exclude domain="sharedpref" path="." />
    <exclude domain="root" path="databases" />
    <exclude domain="file" path="." />
</full-backup-content>
```

#### Android 12+ / API 31+ (`app/src/main/res/xml/data_extraction_rules.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
    <cloud-backup>
        <exclude domain="sharedpref" path="." />
        <exclude domain="root" path="databases" />
        <exclude domain="file" path="." />
    </cloud-backup>
    <device-transfer>
        <exclude domain="sharedpref" path="." />
        <exclude domain="root" path="databases" />
        <exclude domain="file" path="." />
    </device-transfer>
</data-extraction-rules>
```

### 3.2 Client In-Memory Hygiene
- Sensitive in-memory objects (such as `OfflineTelemetryBuffer`) are purged upon user logout.
- Android system clipboard is never automatically populated with user tokens or credentials.
- SharedPreferences only caches `registeredUserId` and `registeredRole`; all passwords and tokens are handled exclusively by the Firebase Authentication SDK.

---

## 4. Privacy Verification

- **Log Audits**: Zero instances of unmasked email or phone numbers in application Logcat.
- **Backup Verification**: `backup_rules.xml` and `data_extraction_rules.xml` explicitly disallow extraction of sensitive data directories.
- **Password Invariant**: `User.java` has zero password fields, preventing serialization into cloud or disk datastores.
