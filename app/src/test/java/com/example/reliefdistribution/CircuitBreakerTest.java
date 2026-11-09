package com.example.reliefdistribution;

import com.example.reliefdistribution.data.resilience.NetworkResilienceCircuitBreaker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test suite verifying NetworkResilienceCircuitBreaker state transitions,
 * failure threshold tripping, fail-fast behavior, and timeout recovery.
 */
public class CircuitBreakerTest {

    @Test
    public void testInitialState_isClosedAndAllowsExecution() {
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(3, 1000L);
        assertEquals(NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());
        assertTrue(breaker.canExecute());
        assertEquals(0, breaker.getFailureCount());
    }

    @Test
    public void testTripsToOpen_afterReachingFailureThreshold() {
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(3, 1000L);

        breaker.recordFailure();
        assertEquals(1, breaker.getFailureCount());
        assertEquals(NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());
        assertTrue(breaker.canExecute());

        breaker.recordFailure();
        assertEquals(2, breaker.getFailureCount());
        assertEquals(NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());
        assertTrue(breaker.canExecute());

        // 3rd failure trips the breaker to OPEN
        breaker.recordFailure();
        assertEquals(3, breaker.getFailureCount());
        assertEquals(NetworkResilienceCircuitBreaker.State.OPEN, breaker.getState());

        // Fails fast immediately
        assertFalse(breaker.canExecute());
    }

    @Test
    public void testRecoveryToHalfOpenAndClosed() throws InterruptedException {
        // Fast reset timeout of 50ms for testing
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(2, 50L);

        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals(NetworkResilienceCircuitBreaker.State.OPEN, breaker.getState());
        assertFalse(breaker.canExecute());

        // Wait for reset timeout
        Thread.sleep(70L);

        // State advances to HALF_OPEN to allow trial call
        assertEquals(NetworkResilienceCircuitBreaker.State.HALF_OPEN, breaker.getState());
        assertTrue(breaker.canExecute());

        // Successful trial call resets breaker to CLOSED
        breaker.recordSuccess();
        assertEquals(NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());
        assertEquals(0, breaker.getFailureCount());
        assertTrue(breaker.canExecute());
    }

    @Test
    public void testHalfOpenFailsBackToOpen_onSubsequentFailure() throws InterruptedException {
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(2, 50L);

        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals(NetworkResilienceCircuitBreaker.State.OPEN, breaker.getState());

        Thread.sleep(70L);
        assertEquals(NetworkResilienceCircuitBreaker.State.HALF_OPEN, breaker.getState());

        // Trial call fails -> immediate return to OPEN
        breaker.recordFailure();
        assertEquals(NetworkResilienceCircuitBreaker.State.OPEN, breaker.getState());
        assertFalse(breaker.canExecute());
    }

    @Test
    public void testManualReset_restoresClosedState() {
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(2, 1000L);
        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals(NetworkResilienceCircuitBreaker.State.OPEN, breaker.getState());

        breaker.reset();
        assertEquals(NetworkResilienceCircuitBreaker.State.CLOSED, breaker.getState());
        assertEquals(0, breaker.getFailureCount());
        assertTrue(breaker.canExecute());
    }
}
