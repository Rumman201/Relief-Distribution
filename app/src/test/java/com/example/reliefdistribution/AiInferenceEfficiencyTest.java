package com.example.reliefdistribution;

import com.example.reliefdistribution.data.ai.ReliefTriageAdvisor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Performance test suite verifying AI triage inference efficiency,
 * memoization caching, sub-millisecond throughput, and output quality preservation.
 */
public class AiInferenceEfficiencyTest {

    private ReliefTriageAdvisor triageAdvisor;

    @Before
    public void setUp() {
        triageAdvisor = new ReliefTriageAdvisor();
        triageAdvisor.clearCache();
    }

    @Test
    public void testMemoizationCache_servesIdenticalAssessmentInstantly() {
        long timestamp = System.currentTimeMillis();

        // Initial evaluation (Cache Miss)
        ReliefTriageAdvisor.TriageAssessment coldResult = triageAdvisor.evaluate(
                "Medicine", "Insulin vials", 5, "Diabetic patient trapped in flood water", timestamp
        );
        assertNotNull(coldResult);
        assertEquals(0, triageAdvisor.getCacheHitCount());
        assertEquals(1, triageAdvisor.getCacheMissCount());

        // Subsequent evaluation with identical parameters (Cache Hit)
        ReliefTriageAdvisor.TriageAssessment cachedResult = triageAdvisor.evaluate(
                "Medicine", "Insulin vials", 5, "Diabetic patient trapped in flood water", timestamp
        );
        assertNotNull(cachedResult);
        assertEquals(1, triageAdvisor.getCacheHitCount());

        // Verify identical output quality
        assertEquals(coldResult.getUrgencyScore(), cachedResult.getUrgencyScore());
        assertEquals(coldResult.getPriorityTier(), cachedResult.getPriorityTier());
        assertEquals(coldResult.getDetectedRiskFactors(), cachedResult.getDetectedRiskFactors());
    }

    @Test
    public void testInferenceThroughput_benchmarksSubMillisecondLatency() {
        long timestamp = System.currentTimeMillis();
        int iterations = 1000;

        // Warmup pass
        for (int i = 0; i < 50; i++) {
            triageAdvisor.evaluate("Food", "Rice bags", 20, "Family stranded on roof", timestamp);
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            triageAdvisor.evaluate("Food", "Rice bags", 20, "Family stranded on roof", timestamp);
        }
        long totalDurationNanos = System.nanoTime() - startTime;
        double avgDurationMs = (totalDurationNanos / (double) iterations) / 1_000_000.0;

        // Average inference time per evaluation should be sub-millisecond (< 0.1ms with cache)
        assertTrue("Average inference latency must be sub-millisecond, was: " + avgDurationMs + "ms",
                avgDurationMs < 1.0);
        assertTrue(triageAdvisor.getCacheHitRatio() > 0.90);
    }
}
