package com.example.reliefdistribution;

import com.example.reliefdistribution.data.cache.InMemoryLruCache;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Performance & reliability test suite for InMemoryLruCache.
 * Verifies LRU eviction, TTL expiration, hit/miss metrics,
 * tenant isolation prefix invalidation, and multi-thread concurrency.
 */
public class InMemoryLruCacheTest {

    @Test
    public void testLruEviction_discardsLeastRecentlyUsed() {
        InMemoryLruCache<String, String> cache = new InMemoryLruCache<>(3, 60_000L);

        cache.put("k1", "v1");
        cache.put("k2", "v2");
        cache.put("k3", "v3");

        assertEquals(3, cache.size());

        // Access k1 so k2 becomes least recently used
        assertEquals("v1", cache.get("k1"));

        // Put k4 -> should evict k2
        cache.put("k4", "v4");

        assertEquals(3, cache.size());
        assertNotNull(cache.get("k1"));
        assertNull(cache.get("k2")); // k2 evicted
        assertNotNull(cache.get("k3"));
        assertNotNull(cache.get("k4"));
        assertEquals(1, cache.getEvictionCount());
    }

    @Test
    public void testTtlExpiration_returnsNullAfterTimeout() throws InterruptedException {
        // Short TTL of 50ms
        InMemoryLruCache<String, String> cache = new InMemoryLruCache<>(10, 50L);

        cache.put("tempKey", "tempValue");
        assertEquals("tempValue", cache.get("tempKey"));

        // Wait for TTL to expire
        Thread.sleep(70L);

        assertNull(cache.get("tempKey"));
        assertFalse(cache.containsKey("tempKey"));
    }

    @Test
    public void testHitRatioStatistics_tracksAccurately() {
        InMemoryLruCache<String, Integer> cache = new InMemoryLruCache<>(10, 60_000L);

        cache.put("a", 100);
        cache.put("b", 200);

        // 2 Hits
        assertEquals(Integer.valueOf(100), cache.get("a"));
        assertEquals(Integer.valueOf(200), cache.get("b"));

        // 2 Misses
        assertNull(cache.get("missing_1"));
        assertNull(cache.get("missing_2"));

        assertEquals(2, cache.getHitCount());
        assertEquals(2, cache.getMissCount());
        assertEquals(0.50, cache.getHitRatio(), 0.001);
    }

    @Test
    public void testTenantScopingAndPrefixInvalidation() {
        InMemoryLruCache<String, String> cache = new InMemoryLruCache<>(50, 60_000L);

        String u1Key = InMemoryLruCache.buildScopedKey("user_001", "profile");
        String u2Key = InMemoryLruCache.buildScopedKey("user_002", "profile");

        cache.put(u1Key, "Profile Data 1");
        cache.put(u2Key, "Profile Data 2");

        assertEquals("Profile Data 1", cache.get(u1Key));
        assertEquals("Profile Data 2", cache.get(u2Key));

        // Invalidate user_001 only
        int removed = cache.invalidatePrefix("user_001::");
        assertEquals(1, removed);

        assertNull(cache.get(u1Key));
        assertNotNull(cache.get(u2Key)); // user_002 remains intact
    }

    @Test
    public void testConcurrentMultiThreadedAccess_isThreadSafe() throws InterruptedException {
        int threadCount = 8;
        int operationsPerThread = 500;
        InMemoryLruCache<String, Integer> cache = new InMemoryLruCache<>(100, 60_000L);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        String key = "key_" + (i % 20);
                        cache.put(key, threadId * 1000 + i);
                        cache.get(key);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();

        assertTrue(cache.size() <= 100);
        assertTrue(cache.getHitCount() > 0);
    }
}
