---
name: database-privacy-security
description: Data privacy, PII minimization, backup exclusion policies, encrypted storage, and Firebase Realtime Database rule hardening.
---

# Database & Privacy Security Skill

## 1. Purpose
Protect user privacy, eliminate Personally Identifiable Information (PII) leakage, enforce strict Android backup isolation, and guarantee field-level validation and privacy across database collections.

## 2. Activation Criteria
Activate this skill when:
- Reviewing data persistence schemas and data retention policies.
- Modifying `backup_rules.xml` or `data_extraction_rules.xml`.
- Auditing log statements, analytics events, or crash reports for PII leakage.
- Enforcing field-level masking (e.g. `AppLogger.maskEmail`, `AppLogger.maskPhone`).

## 3. Project Security Context
- **PII Data**: Full names, email addresses, phone numbers, GPS coordinates, home/shelter addresses.
- **Risks**:
  - Leakage of survivor physical locations to unauthorized actors.
  - Exposure of phone numbers and emails in diagnostic logs or crash traces.
  - Extraction of plaintext user profile data via unauthorized ADB backups.
- **Compliance Alignment**: GDPR minimization principles, OWASP MASVS-STORAGE.

## 4. Engineering Standards
- **Data Minimization**: Collect only what is necessary for relief dispatch.
- **PII Masking in Logs**: Always log sanitized strings via `AppLogger.maskEmail()` and `AppLogger.maskPhone()`.
- **Backup Isolation**: Exclude sensitive SharedPreferences and cache databases from automated Android backups.
- **Data Erasure**: Provide mechanisms for users to delete their account data upon request.

## 5. Step-by-Step Execution Procedure
1. **Backup Configuration Hardening**:
   - Update `app/src/main/res/xml/backup_rules.xml` to exclude sensitive SharedPreferences files.
   - Update `app/src/main/res/xml/data_extraction_rules.xml` with `<exclude domain="sharedpref" path="."/>` for cloud and device transfers.
2. **Database Field Minimization**:
   - Ensure `/Users/$uid/` only exposes non-sensitive profile info to authorized distributors.
3. **Log Sanitization Audit**:
   - Ensure zero raw passwords, full tokens, or unmasked phone numbers appear in Logcat.

## 6. Decision Tree for Ambiguous Cases
- *Should GPS coordinates be retained indefinitely in user profile?*
  -> No. Location data should be ephemeral or cleared on session disconnect (`onDisconnect().remove()`).
