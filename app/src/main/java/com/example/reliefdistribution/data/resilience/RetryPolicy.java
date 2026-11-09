package com.example.reliefdistribution.data.resilience;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Exponential backoff with full jitter policy for resilient disaster network recovery.
 * Prevents network storming during cellular tower restoration.
 */
public class RetryPolicy {

    public static final int DEFAULT_MAX_ATTEMPTS = 3;
    public static final long DEFAULT_BASE_DELAY_MS = 1_000L; // 1 second
    public static final long DEFAULT_MAX_DELAY_MS = 16_000L;  // 16 seconds

    private final int maxAttempts;
    private final long baseDelayMs;
    private final long maxDelayMs;
    private final Random random;

    public RetryPolicy() {
        this(DEFAULT_MAX_ATTEMPTS, DEFAULT_BASE_DELAY_MS, DEFAULT_MAX_DELAY_MS);
    }

    public RetryPolicy(int maxAttempts, long baseDelayMs, long maxDelayMs) {
        if (maxAttempts <= 0) throw new IllegalArgumentException("Max attempts must be positive");
        if (baseDelayMs <= 0) throw new IllegalArgumentException("Base delay must be positive");
        if (maxDelayMs < baseDelayMs) throw new IllegalArgumentException("Max delay cannot be less than base delay");

        this.maxAttempts = maxAttempts;
        this.baseDelayMs = baseDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.random = new Random();
    }

    /**
     * Computes exponential backoff with full jitter for a given attempt.
     * Formula: t = min(maxDelay, baseDelay * 2^(attempt - 1)) * rand(0.5, 1.0)
     *
     * @param attempt 1-indexed attempt number (1, 2, 3...)
     * @return delay in milliseconds
     */
    public long calculateDelay(int attempt) {
        if (attempt <= 0) {
            return baseDelayMs;
        }

        // 2^(attempt - 1) with overflow protection
        long exponentialMultiplier = 1L << Math.min(attempt - 1, 30);
        long rawDelay = baseDelayMs * exponentialMultiplier;
        long cappedDelay = Math.min(rawDelay, maxDelayMs);

        // Full jitter between 50% and 100% of capped delay
        double jitterMultiplier = 0.5 + (random.nextDouble() * 0.5);
        return Math.max(baseDelayMs, (long) (cappedDelay * jitterMultiplier));
    }

    /**
     * Determines whether an error is transient and eligible for retry.
     */
    public static boolean isTransientError(@Nullable Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        if (throwable instanceof SocketTimeoutException ||
                throwable instanceof UnknownHostException ||
                throwable instanceof SocketException ||
                throwable instanceof IOException) {
            return true;
        }

        String msg = throwable.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            return lower.contains("timeout") ||
                    lower.contains("network") ||
                    lower.contains("connection reset") ||
                    lower.contains("unavailable") ||
                    lower.contains("temporarily unavailable");
        }

        return false;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getBaseDelayMs() {
        return baseDelayMs;
    }

    public long getMaxDelayMs() {
        return maxDelayMs;
    }
}
