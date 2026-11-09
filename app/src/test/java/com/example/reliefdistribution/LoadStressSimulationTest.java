package com.example.reliefdistribution;

import com.example.reliefdistribution.data.ai.ReliefTriageAdvisor;
import com.example.reliefdistribution.data.cache.InMemoryLruCache;
import com.example.reliefdistribution.data.resilience.NetworkResilienceCircuitBreaker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Load, stress & concurrency test suite simulating multi-threaded high-throughput operations.
 * Evaluates throughput, p50/p95/p99 latencies, and thread safety under concurrent load.
 */
public class LoadStressSimulationTest {

    @Test
    public void testHighThroughputConcurrentLoad_evaluatesLatencyPercentiles() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 50; // Total 500 concurrent operations
        int totalOperations = threadCount * operationsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        ReliefTriageAdvisor triageAdvisor = new ReliefTriageAdvisor();
        InMemoryLruCache<String, String> cache = new InMemoryLruCache<>(200, 60_000L);
        NetworkResilienceCircuitBreaker breaker = new NetworkResilienceCircuitBreaker(10, 5000L);

        List<Long> latenciesNanos = Collections.synchronizedList(new ArrayList<>(totalOperations));
        AtomicInteger successCounter = new AtomicInteger(0);

        long overallStart = System.currentTimeMillis();

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        long opStart = System.nanoTime();

                        // 1. Concurrent Caching operation
                        String key = "cache_" + (i % 25);
                        cache.put(key, "data_" + threadId + "_" + i);
                        cache.get(key);

                        // 2. Concurrent AI Triage evaluation
                        String category = (i % 2 == 0) ? "Medicine" : "Food";
                        triageAdvisor.evaluate(category, "Aid Package", i + 1, "Urgent assistance required", opStart);

                        // 3. Concurrent Circuit Breaker probe
                        if (breaker.canExecute()) {
                            breaker.recordSuccess();
                        }

                        long opDuration = System.nanoTime() - opStart;
                        latenciesNanos.add(opDuration);
                        successCounter.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue("All operations must complete within timeout", latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        long overallDurationMs = System.currentTimeMillis() - overallStart;
        assertEquals(totalOperations, successCounter.get());
        assertEquals(totalOperations, latenciesNanos.size());

        // Sort latencies to compute percentiles
        List<Long> sortedLatencies = new ArrayList<>(latenciesNanos);
        Collections.sort(sortedLatencies);

        long p50Nanos = sortedLatencies.get((int) (totalOperations * 0.50));
        long p95Nanos = sortedLatencies.get((int) (totalOperations * 0.95));
        long p99Nanos = sortedLatencies.get((int) (totalOperations * 0.99));

        double p50Ms = p50Nanos / 1_000_000.0;
        double p95Ms = p95Nanos / 1_000_000.0;
        double p99Ms = p99Nanos / 1_000_000.0;
        double throughputOpsPerSec = (totalOperations / (double) overallDurationMs) * 1000.0;

        // Invariants: p50 should be sub-5ms, p99 under 50ms, throughput > 100 ops/sec
        assertTrue("p50 latency should be under 5ms, was: " + p50Ms + "ms", p50Ms < 5.0);
        assertTrue("p99 latency should be under 50ms, was: " + p99Ms + "ms", p99Ms < 50.0);
        assertTrue("Throughput should exceed 100 ops/sec, was: " + throughputOpsPerSec, throughputOpsPerSec > 100.0);
    }
}
