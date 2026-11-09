package com.example.reliefdistribution.data.resilience;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Production Circuit Breaker pattern implementation for mobile disaster networks.
 * Prevents continuous socket thrashing during cellular blackouts by failing fast
 * when consecutive failures cross the configured threshold.
 */
public class NetworkResilienceCircuitBreaker {

    public enum State {
        CLOSED,     // Normal operations: Calls are permitted through
        OPEN,       // Tripped: Calls fail fast to prevent resource exhaustion
        HALF_OPEN   // Probing: Permitting a trial call to test network recovery
    }

    public static final int DEFAULT_FAILURE_THRESHOLD = 5;
    public static final long DEFAULT_RESET_TIMEOUT_MS = 30_000L; // 30 seconds

    private final int failureThreshold;
    private final long resetTimeoutMs;

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);

    public NetworkResilienceCircuitBreaker() {
        this(DEFAULT_FAILURE_THRESHOLD, DEFAULT_RESET_TIMEOUT_MS);
    }

    public NetworkResilienceCircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        if (failureThreshold <= 0) {
            throw new IllegalArgumentException("Failure threshold must be positive");
        }
        if (resetTimeoutMs <= 0) {
            throw new IllegalArgumentException("Reset timeout must be positive");
        }
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
    }

    /**
     * Determines whether a network or backend operation is permitted to execute.
     */
    public boolean canExecute() {
        State current = state.get();
        if (current == State.CLOSED) {
            return true;
        }

        long now = System.currentTimeMillis();
        if (current == State.OPEN) {
            if (now - lastFailureTime.get() >= resetTimeoutMs) {
                // Trial transition to HALF_OPEN to probe connection recovery
                if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                    return true;
                }
            }
            return false; // Still within cool-down, fail fast
        }

        // HALF_OPEN permits execution
        return true;
    }

    /**
     * Records a successful operation, resetting the circuit breaker to CLOSED.
     */
    public void recordSuccess() {
        successCount.incrementAndGet();
        failureCount.set(0);
        state.set(State.CLOSED);
    }

    /**
     * Records an operation failure, advancing the failure counter and tripping
     * the circuit to OPEN if the threshold is crossed.
     */
    public void recordFailure() {
        lastFailureTime.set(System.currentTimeMillis());
        int currentFailures = failureCount.incrementAndGet();

        if (state.get() == State.HALF_OPEN || currentFailures >= failureThreshold) {
            state.set(State.OPEN);
        }
    }

    /**
     * Manually resets the breaker to CLOSED state with cleared counters.
     */
    public void reset() {
        state.set(State.CLOSED);
        failureCount.set(0);
        lastFailureTime.set(0);
    }

    public State getState() {
        // Evaluate dynamic transition if timeout expired
        if (state.get() == State.OPEN && (System.currentTimeMillis() - lastFailureTime.get() >= resetTimeoutMs)) {
            state.compareAndSet(State.OPEN, State.HALF_OPEN);
        }
        return state.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public int getFailureThreshold() {
        return failureThreshold;
    }

    public long getResetTimeoutMs() {
        return resetTimeoutMs;
    }
}
