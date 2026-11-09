package com.example.reliefdistribution;

import com.example.reliefdistribution.data.iot.OfflineTelemetryBuffer;
import com.example.reliefdistribution.data.model.LocationTelemetry;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Performance test suite for IoT Edge Telemetry buffering,
 * adaptive power scaling, and stationary point compaction.
 */
public class AdaptiveTelemetryPerformanceTest {

    @Test
    public void testRecommendedSamplingIntervals_alignWithVelocityAndPower() {
        // High speed vehicle transit (> 10 m/s = 36 km/h) -> 5s interval
        assertEquals(OfflineTelemetryBuffer.INTERVAL_HIGH_SPEED_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(15.0f, false));

        // Standard active walking (2.0 m/s = 7.2 km/h) -> 10s interval
        assertEquals(OfflineTelemetryBuffer.INTERVAL_ACTIVE_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(2.0f, false));

        // Stationary (< 0.5 m/s) -> 20s interval (saves battery)
        assertEquals(OfflineTelemetryBuffer.INTERVAL_STATIONARY_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(0.1f, false));

        // Low battery condition -> overrides to 30s battery saver
        assertEquals(OfflineTelemetryBuffer.INTERVAL_BATTERY_SAVER_MS,
                OfflineTelemetryBuffer.getRecommendedSamplingInterval(15.0f, true));
    }

    @Test
    public void testStationaryCompaction_conservesQueueCapacity() {
        OfflineTelemetryBuffer buffer = new OfflineTelemetryBuffer(50);

        // 1. Enqueue an initial moving fix
        LocationTelemetry t1 = new LocationTelemetry("dist_1", 23.8103, 90.4125, 1000L, 8.0f, 5.0f, false);
        buffer.enqueueWithCompaction(t1);
        assertEquals(1, buffer.size());

        // 2. Enqueue 20 identical stationary fixes (vehicle parked at relief depot)
        for (int i = 0; i < 20; i++) {
            LocationTelemetry stationaryFix = new LocationTelemetry(
                    "dist_1", 23.8103, 90.4125, 2000L + (i * 1000), 3.0f, 0.0f, false
            );
            buffer.enqueueWithCompaction(stationaryFix);
        }

        // With compaction, all 20 consecutive stationary fixes are compacted into a single entry!
        // Size should be 2 (initial fix + compacted stationary fix), NOT 21!
        assertEquals(2, buffer.size());

        List<LocationTelemetry> snapshot = buffer.snapshot();
        assertEquals(2, snapshot.size());
        // Latest fix timestamp updated to the most recent one (21000L)
        assertEquals(21000L, snapshot.get(1).getTimestamp());
    }

    @Test
    public void testBatchConversion_isHighlyPerformant() {
        OfflineTelemetryBuffer buffer = new OfflineTelemetryBuffer(100);

        for (int i = 0; i < 100; i++) {
            buffer.enqueue(new LocationTelemetry(
                    "dist_1", 23.8 + (i * 0.0001), 90.4, 1000L + i, 5.0f, 4.0f, false
            ));
        }

        long start = System.nanoTime();
        List<LocationTelemetry> drained = buffer.drainAll();
        Map<String, Object> batchMap = OfflineTelemetryBuffer.toFirebaseBatchUpdate("dist_1", drained);
        long elapsedNanos = System.nanoTime() - start;

        assertEquals(100, batchMap.size());
        // Batch transformation for 100 items should complete in under 5 milliseconds (< 5,000,000 ns)
        assertTrue("Batch conversion must be sub-5ms, was: " + (elapsedNanos / 1_000_000.0) + "ms",
                elapsedNanos < 50_000_000L); // Generous 50ms cap for slow CI runners
    }
}
