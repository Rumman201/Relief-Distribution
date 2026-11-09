package com.example.reliefdistribution;

import com.example.reliefdistribution.data.iot.OfflineTelemetryBuffer;
import com.example.reliefdistribution.data.model.LocationTelemetry;
import com.example.reliefdistribution.data.util.SecuritySanitizer;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Security unit test suite verifying IoT edge telemetry defenses,
 * memory exhaustion mitigation, timestamp anti-replay validation,
 * and coordinate boundary invariants.
 */
public class TelemetryReplaySecurityTest {

    @Test
    public void testBufferCapacity_preventsMemoryExhaustionDoS() {
        OfflineTelemetryBuffer buffer = new OfflineTelemetryBuffer(10);

        // Enqueue 25 items into capacity-10 buffer
        for (int i = 0; i < 25; i++) {
            LocationTelemetry t = new LocationTelemetry(
                    "dist_1", 23.8 + (i * 0.001), 90.4, 1000L + i, 10.0f, 5.0f, false
            );
            buffer.enqueue(t);
        }

        // Buffer must clamp strictly at max capacity (10), evicting oldest
        assertEquals(10, buffer.size());
        List<LocationTelemetry> drained = buffer.drainAll();
        assertEquals(10, drained.size());

        // First remaining item should be index 15 (timestamp 1015L)
        assertEquals(1015L, drained.get(0).getTimestamp());
        assertEquals(1024L, drained.get(9).getTimestamp());
    }

    @Test
    public void testCoordinateSanitization_rejectsSpoofedLocations() {
        // Valid Dhaka coordinates
        assertTrue(SecuritySanitizer.isValidCoordinates(23.8103, 90.4125));

        // Invalid latitude spoofing attempts
        assertFalse(SecuritySanitizer.isValidCoordinates(90.1, 90.4125));
        assertFalse(SecuritySanitizer.isValidCoordinates(-91.0, 90.4125));

        // Invalid longitude spoofing attempts
        assertFalse(SecuritySanitizer.isValidCoordinates(23.8103, 180.1));
        assertFalse(SecuritySanitizer.isValidCoordinates(23.8103, -181.0));
    }

    @Test
    public void testTelemetryReplayDetection_rejectsStaleOrOutOfOrderTelemetry() {
        long baseTime = 1_700_000_000_000L;
        long maxAge = 3_600_000L; // 1 hour max age for live telemetry

        // Fresh telemetry at T0
        long t0 = baseTime;
        assertTrue(SecuritySanitizer.isFreshTimestamp(t0, maxAge, baseTime));

        // Progressive telemetry at T0 + 10s
        long t1 = baseTime + 10_000L;
        assertTrue(SecuritySanitizer.isFreshTimestamp(t1, maxAge, baseTime + 10_000L));

        // Replay attack: Attacker captures t0 packet and replays it 2 hours later
        long replayedAt = baseTime + (2 * 3600 * 1000L);
        assertFalse("Replayed packet must be rejected as stale",
                SecuritySanitizer.isFreshTimestamp(t0, maxAge, replayedAt));

        // Clock spoofing: Telemetry claiming to be 10 minutes in the future
        long futureTime = baseTime + (10 * 60 * 1000L);
        assertFalse("Future spoofed packet must be rejected",
                SecuritySanitizer.isFreshTimestamp(futureTime, maxAge, baseTime));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationTelemetry_enforcesValidLatitudeInDomain() {
        new LocationTelemetry("user_1", 95.0, 90.0, 1000L, 0.0f, 0.0f, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationTelemetry_enforcesValidLongitudeInDomain() {
        new LocationTelemetry("user_1", 23.0, 195.0, 1000L, 0.0f, 0.0f, false);
    }
}
