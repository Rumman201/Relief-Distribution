---
name: security-hardening
description: Android security standards, secrets management, OWASP Mobile Top 10 mitigation, secure credential storage, and permission boundaries.
---

# Security Hardening Skill

## 1. Purpose
Identify, prioritize, and eliminate security vulnerabilities in the Android client application and backend interfaces, following OWASP Mobile Application Security Verification Standard (MASVS).

## 2. Activation Criteria
Activate this skill when:
- Reviewing authentication, token storage, credential management, or data persistence.
- Inspecting Android permissions, exported components, intent handling, or broadcast receivers.
- Handling API keys (Google Maps, Firebase, third-party APIs).
- Protecting sensitive survivor or distributor data in transit and at rest.

## 3. Project Context
- **Critical Vulnerabilities Uncovered in Reconnaissance**:
  1. **P0 - Plain-Text Google Maps API Key**: Committed directly in `app/src/main/res/values/strings.xml` (`AIzaSyCigtRRj8Lc7PdEPDgWCnjwh837kynOKzk`) and baked into `AndroidManifest.xml`.
  2. **P0 - Plain-Text Password Storage**: `User.java` and `SignUpActivity.java` save user passwords in cleartext to both the public Firebase Realtime Database and unencrypted `SharedPreferences` (`editor.putString("registeredPassword", password)`).
  3. **P1 - Unrestricted Calling Permission**: App requests `CALL_PHONE` permission and triggers direct automated dialing (`Intent.ACTION_CALL`), which violates Google Play Store policies and allows arbitrary phone calls. Standard safe pattern is `Intent.ACTION_DIAL`.
  4. **P1 - Insecure SharedPreferences**: Session tokens and user profiles stored in plain XML SharedPreferences without encryption.
  5. **P2 - Missing Certificate Pinning / Weak Network Security Config**: App lacks a network security configuration file.

## 4. Specialist Engineering Standards
- **Zero Secrets in Source Control**: API keys, signing configs, and cloud credentials must be loaded from `local.properties`, environment variables, or Gradle `secrets-gradle-plugin`.
- **Zero Plain-Text Passwords**: Passwords must never be saved in client databases, models, or SharedPreferences. Authentication should rely solely on Firebase Auth UIDs and secure session tokens.
- **Principle of Least Privilege**:
  - Replace `CALL_PHONE` with `ACTION_DIAL` (which requires NO special dangerous permission and hands control safely to the user's phone dialer).
  - Declare `android:exported="false"` on all Activities not intended for external launching.
- **Secure Storage**: Use `EncryptedSharedPreferences` (Jetpack Security `androidx.security:security-crypto`) for sensitive user preferences.
- **Input Sanitization**: Validate all inputs against regex patterns for email (`Patterns.EMAIL_ADDRESS`), phone, and password length.

## 5. Step-by-Step Execution Procedure
1. **Secrets Remediation**:
   - Move raw API keys from `strings.xml` to `local.properties`.
   - Inject key via Gradle `manifestPlaceholders` or `buildConfigField`.
   - Restrict the Google Maps API Key in Google Cloud Console by Android package name (`com.example.reliefdistribution`) and SHA-1 certificate fingerprint.
2. **Credential Sanitization**:
   - Strip the `password` field from `User.java`.
   - Remove `registeredPassword` writes in `SignUpActivity` and `LoginActivity`.
3. **Permission Hardening**:
   - Switch emergency call actions to `Intent.ACTION_DIAL` (`Uri.parse("tel:999")`).
   - Remove `android.permission.CALL_PHONE` from `AndroidManifest.xml`.
4. **Export Boundary Audit**: Ensure only launcher activities have `android:exported="true"`.

## 6. Decision Tree for Ambiguous Cases
- *Store API key in strings.xml or BuildConfig/ManifestPlaceholder?*
  -> Never in `strings.xml`. Use `local.properties` + `buildConfigField` or `manifestPlaceholders`. For release builds, load from CI secret environment variables.
- *ACTION_CALL vs ACTION_DIAL?*
  -> Always prefer `ACTION_DIAL`. It does not require dangerous `android.permission.CALL_PHONE`, passes Google Play Store review without telephony declaration exemptions, and gives users ultimate control before initiating a 999 call.

## 7. Tool Instructions
- Search for secrets in codebase: `git log -S "AIzaSy"`, `grep_search`.
- Inspect compiled APK manifests: `apkanalyzer manifest print app-debug.apk`.

## 8. Safety Constraints
- Never commit actual production API keys or Google Services OAuth client secrets to version control.
- Never log user locations, phone numbers, or passwords to Logcat.

## 9. Verification Steps
- Verify `strings.xml` no longer contains the hardcoded API key.
- Verify `User.java` and Firebase database payload do not serialize a password field.
- Verify `assembleDebug` successfully compiles and injects keys from local configuration.

## 10. Expected Output & Evidence Format
- Vulnerability remediation reports in `docs/upgrade/03-engineering-audit.md` and `docs/upgrade/07-risk-register.md`.
- Diff showing complete elimination of plain-text passwords and credentials.

