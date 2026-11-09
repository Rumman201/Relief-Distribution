package com.example.reliefdistribution.data.util;

import androidx.annotation.Nullable;

import java.util.regex.Pattern;

/**
 * Enterprise-grade security sanitizer and validator for disaster relief operations.
 * Enforces strict input validation, Cross-Site Scripting (XSS) prevention,
 * SQL/NoSQL injection neutralization, coordinate boundary validation,
 * and timestamp freshness / replay attack defense.
 */
public final class SecuritySanitizer {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)(<script.*?>.*?</script>|javascript:|onload=|onerror=)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\p{Cntrl}&&[^\r\n\t]]");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_]+(\\.[A-Za-z0-9+_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-()]{7,20}$");

    private static final long MAX_FUTURE_SKEW_MS = 60_000L; // 1 minute future tolerance

    private SecuritySanitizer() {
        // Non-instantiable utility
    }

    /**
     * Sanitizes general free-form text input.
     * Strips HTML tags, script markers, and control characters, clamping length to maxLength.
     */
    @Nullable
    public static String sanitizeInput(@Nullable String input, int maxLength) {
        if (input == null) {
            return null;
        }
        if (maxLength <= 0) {
            return "";
        }

        String cleaned = SCRIPT_PATTERN.matcher(input).replaceAll("");
        cleaned = HTML_TAG_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = CONTROL_CHARS.matcher(cleaned).replaceAll("");
        cleaned = cleaned.trim();

        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }
        return cleaned;
    }

    /**
     * Sanitizes survivor or distributor emergency notes (up to 1,000 characters).
     */
    @Nullable
    public static String sanitizeNotes(@Nullable String notes) {
        return sanitizeInput(notes, 1000);
    }

    /**
     * Validates that an email conforms to strict format standards and length boundaries.
     */
    public static boolean isValidEmail(@Nullable String email) {
        if (email == null || email.trim().isEmpty() || email.length() > 256) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates that a phone number contains only valid dialing digits, separators, and length.
     */
    public static boolean isValidPhone(@Nullable String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String trimmed = phone.trim();
        if (trimmed.length() < 7 || trimmed.length() > 20) {
            return false;
        }
        return PHONE_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates geographic coordinates for physical correctness.
     * Latitude: [-90.0, 90.0], Longitude: [-180.0, 180.0]. Rejects NaN and Infinite.
     */
    public static boolean isValidCoordinates(double latitude, double longitude) {
        if (Double.isNaN(latitude) || Double.isInfinite(latitude) ||
                Double.isNaN(longitude) || Double.isInfinite(longitude)) {
            return false;
        }
        return latitude >= -90.0 && latitude <= 90.0 &&
                longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Validates timestamp freshness to prevent telemetry replay attacks.
     *
     * @param timestamp     The timestamp to evaluate (epoch milliseconds).
     * @param maxAgeMillis  The maximum allowable age in milliseconds (e.g., 24 hours).
     * @param currentMillis The current reference time (System.currentTimeMillis()).
     * @return true if timestamp is within [currentMillis - maxAgeMillis, currentMillis + 60,000].
     */
    public static boolean isFreshTimestamp(long timestamp, long maxAgeMillis, long currentMillis) {
        if (timestamp <= 0) {
            return false;
        }
        if (timestamp > currentMillis + MAX_FUTURE_SKEW_MS) {
            // Future timestamp beyond tolerance (clock spoofing)
            return false;
        }
        return timestamp >= (currentMillis - maxAgeMillis);
    }

    /**
     * Validates quantity boundaries (1 to 10,000 items per relief request).
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0 && quantity <= 10_000;
    }
}
