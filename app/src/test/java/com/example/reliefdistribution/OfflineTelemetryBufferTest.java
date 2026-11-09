package com.example.reliefdistribution;

import com.example.reliefdistribution.data.iot.OfflineTelemetryBuffer;
import com.example.reliefdistribution.data.model.LocationTelemetry;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit Test Suite for OfflineTelemetryBuffer.
 * Verifies mobile edge telemetry buffering, ring-buffer FIFO eviction,
 * atomic Firebase batch compilation, and adaptive power throttling.
 */
public class OfflineTelemetryBufferTest {

    @Test
    public void enqueueAndDrain_preservesOrderAndClearsBuffer() {
        OfflineTelemetryBuffer buffer = new OfflineTelemetryBuffer(5);
        assertTrue(buffer.isEmpty());

        LocationTelemetry fix1 = new LocationTelemetry("dist-1", 23.81, 90.41, 1000L, 5.0f, 1.2f, true);
        LocationTelemetry fix2 = new LocationTelemetry("dist-1", 23.82, 90.42, 2000L, 4.0f, 1.5f, true);

        buffer.enqueue(fix1);
        buffer.enqueue(fix2);

        assertEquals(2, buffer.size());
        assertFalse(buffer.isEmpty());

        List<LocationTelemetry> drained = buffer.drainAll();
        assertEquals(2, drained.size());
        assertEquals(1000L, drained.get(0).getTimestamp());
        assertEquals(2000L, drained.get(1).getTimestamp());

        assertTrue("Buffer must be empty after drainAll()", buffer.isEmpty());
    }

    @Test
    public void ringBufferEviction_dropsOldestWhenCapacityExceeded() {
        OfflineTelemetryBuffer smallBuffer = new OfflineTelemetryBuffer(3);

        smallBuffer.enqueue(new LocationTelemetry("dist-1", 23.01, 90.01, 100L, 5.0f, 1.0f, true));
        smallBuffer.enqueue(new LocationTelemetry("dist-1", 23.02, 90.02, 200L, 5.0f, 1.0f, true));
        smallBuffer.enqueue(new LocationTelemetry("dist-1", 23.03, 90.03, 300L, 5.0f, 1.0f, true));
        assertEquals(3, smallBuffer.size());

        // 4th fix should evict 100L
        smallBuffer.enqueue(new LocationTelemetry("dist-1", 23.04, 90.04, 400L, 5.0f, 1.0f, true));
        assertEquals(3, smallBuffer.size());

        List<LocationTelemetry> snapshot = smallBuffer.snapshot();
        assertEquals(200L, snapshot.get(0).getTimestamp());
        assertEquals(300L, snapshot.get(1).getTimestamp());
        assertEquals(400L, snapshot.get(2).getTimestamp());
    }

    @Test
    public void toFirebaseBatchUpdate_producesCorrectAtomicPaths() {
        OfflineTelemetryBuffer buffer = new OfflineTelemetryBuffer(10);
        buffer.enqueue(new LocationTelemetry("dist-user-42", 23.75, 90.38, 1700000000000L, 3.5f, 0.8f, true));
        buffer.enqueue(new LocationTelemetry("dist-user-42", 23.76, 90.39, 1700000010000L, 4.0f, 1.2f, true));

        Map<String, Object> batchMap = OfflineTelemetryBuffer.toFirebaseBatchUpdate("dist-user-42", buffer.drainAll());
        assertEquals(2, batchMap.size());
        assertTrue(batchMap.containsKey("Users/dist-user-42/locationHistory/1700000000000"));
        assertTrue(batchMap.containsKey("Users/dist-user-42/locationHistory/1700000010000"));
    }

    @Test
    public void getRecommendedSamplingInterval_adaptsToSpeedAndBattery() {
        // Battery Low: throttles to 30s
        assertEquals(OfflineTelemetryBuffer.INTERVAL_BATTERY_SAVER_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(15.0f, true));

        // High speed vehicle (12 m/s = 43.2 km/h): fast 5s updates
        assertEquals(OfflineTelemetryBuffer.INTERVAL_HIGH_SPEED_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(12.0f, false));

        // Stationary (0.1 m/s): power save 20s updates
        assertEquals(OfflineTelemetryBuffer.INTERVAL_STATIONARY_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(0.1f, false));

        // Normal walking/slow vehicle (2.0 m/s = 7.2 km/h): standard 10s updates
        assertEquals(OfflineTelemetryBuffer.INTERVAL_ACTIVE_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(2.0f, false));
    }
}
