package com.example.reliefdistribution;

import com.example.reliefdistribution.data.resilience.RetryPolicy;

import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test suite verifying RetryPolicy exponential backoff calculation,
 * jitter bounds, delay clamping, and transient error classification.
 */
public class RetryPolicyTest {

    @Test
    public void testCalculateDelay_increasesWithAttempts() {
        RetryPolicy policy = new RetryPolicy(5, 1000L, 16_000L);

        long d1 = policy.calculateDelay(1); // ~1000 * rand(0.5, 1.0)
        long d2 = policy.calculateDelay(2); // ~2000 * rand(0.5, 1.0)
        long d3 = policy.calculateDelay(3); // ~4000 * rand(0.5, 1.0)

        // Verifies bounded ranges with jitter
        assertTrue(d1 >= 500L && d1 <= 1000L);
        assertTrue(d2 >= 1000L && d2 <= 2000L);
        assertTrue(d3 >= 2000L && d3 <= 4000L);
    }

    @Test
    public void testCalculateDelay_clampsAtMaxDelay() {
        RetryPolicy policy = new RetryPolicy(5, 1000L, 4000L);

        // Attempt 5 raw would be 16,000, but capped at 4000L * rand(0.5, 1.0)
        long d5 = policy.calculateDelay(5);
        assertTrue(d5 <= 4000L);
        assertTrue(d5 >= 2000L);
    }

    @Test
    public void testIsTransientError_classifiesCorrectly() {
        assertTrue(RetryPolicy.isTransientError(new SocketTimeoutException("Read timed out")));
        assertTrue(RetryPolicy.isTransientError(new UnknownHostException("Unable to resolve host")));
        assertTrue(RetryPolicy.isTransientError(new IOException("Connection reset by peer")));
        assertTrue(RetryPolicy.isTransientError(new Exception("Database service unavailable")));

        // Non-transient errors
        assertFalse(RetryPolicy.isTransientError(new IllegalArgumentException("Invalid password")));
        assertFalse(RetryPolicy.isTransientError(new NullPointerException("Null reference")));
        assertFalse(RetryPolicy.isTransientError(null));
    }
}
