package com.example.reliefdistribution;

import com.example.reliefdistribution.data.util.SecuritySanitizer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Security unit test suite verifying input sanitization, XSS mitigation,
 * coordinate boundary validation, and timestamp replay defense.
 */
public class SecuritySanitizerTest {

    @Test
    public void testSanitizeInput_stripsScriptTags() {
        String malicious = "<script>alert('XSS')</script>Emergency aid needed";
        String cleaned = SecuritySanitizer.sanitizeInput(malicious, 100);
        assertNotNull(cleaned);
        assertFalse(cleaned.contains("<script>"));
        assertFalse(cleaned.contains("alert"));
        assertTrue(cleaned.contains("Emergency aid needed"));
    }

    @Test
    public void testSanitizeInput_stripsHtmlTags() {
        String htmlInput = "<b>Urgent:</b> Clean water <img src='x' onerror='alert(1)'>";
        String cleaned = SecuritySanitizer.sanitizeInput(htmlInput, 100);
        assertNotNull(cleaned);
        assertFalse(cleaned.contains("<b>"));
        assertFalse(cleaned.contains("<img"));
        assertFalse(cleaned.contains("onerror"));
        assertTrue(cleaned.contains("Urgent: Clean water"));
    }

    @Test
    public void testSanitizeInput_enforcesLengthClamping() {
        String longText = "A".repeat(500);
        String clamped = SecuritySanitizer.sanitizeInput(longText, 50);
        assertNotNull(clamped);
        assertEquals(50, clamped.length());
    }

    @Test
    public void testSanitizeInput_handlesNullGracefully() {
        assertNull(SecuritySanitizer.sanitizeInput(null, 100));
    }

    @Test
    public void testSanitizeNotes_clampsToThousandChars() {
        String hugeNotes = "Emergency notes... ".repeat(100);
        String cleaned = SecuritySanitizer.sanitizeNotes(hugeNotes);
        assertNotNull(cleaned);
        assertTrue(cleaned.length() <= 1000);
    }

    @Test
    public void testIsValidEmail_acceptsValidEmails() {
        assertTrue(SecuritySanitizer.isValidEmail("survivor@relief.org"));
        assertTrue(SecuritySanitizer.isValidEmail("distributor.lead@un-aid.gov.bd"));
        assertTrue(SecuritySanitizer.isValidEmail("user+tag@example.com"));
    }

    @Test
    public void testIsValidEmail_rejectsMaliciousAndInvalidEmails() {
        assertFalse(SecuritySanitizer.isValidEmail(null));
        assertFalse(SecuritySanitizer.isValidEmail(""));
        assertFalse(SecuritySanitizer.isValidEmail("plainaddress"));
        assertFalse(SecuritySanitizer.isValidEmail("<script>@evil.com"));
        assertFalse(SecuritySanitizer.isValidEmail("user@domain..com"));
        assertFalse(SecuritySanitizer.isValidEmail("user@domain"));
        assertFalse(SecuritySanitizer.isValidEmail("a".repeat(250) + "@domain.com")); // exceeds 256 chars
    }

    @Test
    public void testIsValidPhone_acceptsValidPhoneNumbers() {
        assertTrue(SecuritySanitizer.isValidPhone("+8801712345678"));
        assertTrue(SecuritySanitizer.isValidPhone("01712345678"));
        assertTrue(SecuritySanitizer.isValidPhone("+1-555-123-4567"));
        assertTrue(SecuritySanitizer.isValidPhone("(02) 9876543"));
    }

    @Test
    public void testIsValidPhone_rejectsInvalidOrMaliciousPhones() {
        assertFalse(SecuritySanitizer.isValidPhone(null));
        assertFalse(SecuritySanitizer.isValidPhone(""));
        assertFalse(SecuritySanitizer.isValidPhone("123")); // too short (< 7 digits)
        assertFalse(SecuritySanitizer.isValidPhone("123456789012345678901")); // too long (> 20 chars)
        assertFalse(SecuritySanitizer.isValidPhone("0171234<script>"));
        assertFalse(SecuritySanitizer.isValidPhone("DROP TABLE users;"));
    }

    @Test
    public void testIsValidCoordinates_verifiesValidBounds() {
        assertTrue(SecuritySanitizer.isValidCoordinates(23.8103, 90.4125)); // Dhaka, Bangladesh
        assertTrue(SecuritySanitizer.isValidCoordinates(-90.0, -180.0));
        assertTrue(SecuritySanitizer.isValidCoordinates(90.0, 180.0));
        assertTrue(SecuritySanitizer.isValidCoordinates(0.0, 0.0));
    }

    @Test
    public void testIsValidCoordinates_rejectsOutOfBoundsAndSpecialValues() {
        assertFalse(SecuritySanitizer.isValidCoordinates(90.0001, 50.0)); // Latitude > 90
        assertFalse(SecuritySanitizer.isValidCoordinates(-90.0001, 50.0)); // Latitude < -90
        assertFalse(SecuritySanitizer.isValidCoordinates(23.0, 180.0001)); // Longitude > 180
        assertFalse(SecuritySanitizer.isValidCoordinates(23.0, -180.0001)); // Longitude < -180
        assertFalse(SecuritySanitizer.isValidCoordinates(Double.NaN, 50.0));
        assertFalse(SecuritySanitizer.isValidCoordinates(23.0, Double.POSITIVE_INFINITY));
    }

    @Test
    public void testIsFreshTimestamp_detectsReplayAndClockSpoofing() {
        long now = 1_700_000_000_000L;
        long maxAge = 86_400_000L; // 24 hours

        // Fresh current timestamp
        assertTrue(SecuritySanitizer.isFreshTimestamp(now - 1000, maxAge, now));

        // Timestamp 2 hours old (within 24 hours maxAge)
        assertTrue(SecuritySanitizer.isFreshTimestamp(now - (2 * 3600 * 1000), maxAge, now));

        // Stale timestamp (25 hours old -> replay attack detected)
        assertFalse(SecuritySanitizer.isFreshTimestamp(now - (25 * 3600 * 1000), maxAge, now));

        // Minor future skew within 1 minute (tolerance accepted)
        assertTrue(SecuritySanitizer.isFreshTimestamp(now + 30_000, maxAge, now));

        // Major future clock spoofing (5 minutes in future -> rejected)
        assertFalse(SecuritySanitizer.isFreshTimestamp(now + (5 * 60 * 1000), maxAge, now));

        // Non-positive timestamp
        assertFalse(SecuritySanitizer.isFreshTimestamp(0, maxAge, now));
        assertFalse(SecuritySanitizer.isFreshTimestamp(-1, maxAge, now));
    }

    @Test
    public void testIsValidQuantity_enforcesBounds() {
        assertTrue(SecuritySanitizer.isValidQuantity(1));
        assertTrue(SecuritySanitizer.isValidQuantity(50));
        assertTrue(SecuritySanitizer.isValidQuantity(10_000));

        assertFalse(SecuritySanitizer.isValidQuantity(0));
        assertFalse(SecuritySanitizer.isValidQuantity(-5));
        assertFalse(SecuritySanitizer.isValidQuantity(10_001));
    }
}
