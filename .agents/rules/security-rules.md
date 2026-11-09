---
description: Critical security and data protection rules for Android development
---

# Persistent Security Rules

1. **No Plaintext Passwords**:
   - `User` entity must never include a `password` field.
   - Do not write passwords to `SharedPreferences` or Firebase Realtime Database.
   - All authentication is delegated to `FirebaseAuth.getInstance().signInWithEmailAndPassword()` or `createUserWithEmailAndPassword()`.

2. **No Hardcoded API Keys**:
   - Do not place Google Maps API keys or Firebase secrets in `strings.xml`.
   - Store local keys in `local.properties` (which is in `.gitignore`) and inject via Gradle `buildConfigField` or `manifestPlaceholders`.

3. **No Unsafe Permissions**:
   - Do not use `android.permission.CALL_PHONE` or `Intent.ACTION_CALL`. Use `Intent.ACTION_DIAL` with `Uri.parse("tel:999")` to let the user confirm emergency calls.
   - Only declare and request runtime permissions needed for active features.

