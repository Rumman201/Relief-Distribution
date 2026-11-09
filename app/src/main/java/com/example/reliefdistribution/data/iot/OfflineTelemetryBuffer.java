package com.example.reliefdistribution.data.iot;

import androidx.annotation.NonNull;

import com.example.reliefdistribution.data.model.LocationTelemetry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mobile Edge IoT Telemetry Buffer.
 * Caches GPS telemetry in an in-memory bounded FIFO queue when field connectivity is lost.
 * Supports batch extraction for atomic synchronization upon network recovery and
 * adaptive power-aware sampling interval calculation.
 */
public class OfflineTelemetryBuffer {

    public static final int DEFAULT_MAX_CAPACITY = 100;
    public static final long INTERVAL_STATIONARY_MS = 20_000L;
    public static final long INTERVAL_ACTIVE_MS = 10_000L;
    public static final long INTERVAL_HIGH_SPEED_MS = 5_000L;
    public static final long INTERVAL_BATTERY_SAVER_MS = 30_000L;

    private final int maxCapacity;
    private final ArrayDeque<LocationTelemetry> buffer;

    public OfflineTelemetryBuffer() {
        this(DEFAULT_MAX_CAPACITY);
    }

    public OfflineTelemetryBuffer(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.maxCapacity = maxCapacity;
        this.buffer = new ArrayDeque<>(maxCapacity);
    }

    /**
     * Appends a telemetry fix. If queue is full, evicts oldest entry.
     */
    public synchronized boolean enqueue(@NonNull LocationTelemetry telemetry) {
        if (buffer.size() >= maxCapacity) {
            buffer.pollFirst(); // Discard oldest
        }
        return buffer.offerLast(telemetry);
    }

    /**
     * Drains all buffered telemetry points for batch transmission.
     */
    @NonNull
    public synchronized List<LocationTelemetry> drainAll() {
        List<LocationTelemetry> drained = new ArrayList<>(buffer);
        buffer.clear();
        return drained;
    }

    /**
     * Peeks all buffered telemetry points without removing them.
     */
    @NonNull
    public synchronized List<LocationTelemetry> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(buffer));
    }

    public synchronized int size() {
        return buffer.size();
    }

    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }

    public synchronized void clear() {
        buffer.clear();
    }

    /**
     * Converts drained telemetry list into Firebase multi-path atomic update map.
     */
    @NonNull
    public static Map<String, Object> toFirebaseBatchUpdate(@NonNull String userId,
                                                            @NonNull List<LocationTelemetry> telemetryList) {
        Map<String, Object> batch = new HashMap<>();
        for (LocationTelemetry loc : telemetryList) {
            String path = "Users/" + userId + "/locationHistory/" + loc.getTimestamp();
            batch.put(path, loc.toMap());
        }
        return batch;
    }

    /**
     * Calculates adaptive edge GPS polling interval based on speed and battery level.
     */
    public static long getRecommendedSamplingInterval(float speedMetersPerSec, boolean isBatteryLow) {
        if (isBatteryLow) {
            return INTERVAL_BATTERY_SAVER_MS;
        }
        if (speedMetersPerSec >= 10.0f) { // Over 36 km/h
            return INTERVAL_HIGH_SPEED_MS;
        }
        if (speedMetersPerSec < 0.5f) { // Stationary
            return INTERVAL_STATIONARY_MS;
        }
        return INTERVAL_ACTIVE_MS; // Standard walking / slow transit
    }
}
