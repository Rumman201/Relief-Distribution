package com.example.reliefdistribution.data.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * High-performance, thread-safe In-Memory LRU Cache with configurable TTL,
 * tenant-scoped key isolation, and hit/miss statistical telemetry.
 *
 * @param <K> Key type (typically String)
 * @param <V> Value type
 */
public class InMemoryLruCache<K, V> {

    public static final long DEFAULT_TTL_MS = 10 * 60 * 1000L; // 10 minutes default
    public static final int DEFAULT_MAX_CAPACITY = 200;

    private final int maxCapacity;
    private final long defaultTtlMs;
    private final LinkedHashMap<K, CacheEntry<V>> map;

    private final AtomicLong hitCount = new AtomicLong(0);
    private final AtomicLong missCount = new AtomicLong(0);
    private final AtomicLong evictionCount = new AtomicLong(0);

    public static class CacheEntry<V> {
        final V value;
        final long createdAt;
        final long ttlMs;

        public CacheEntry(V value, long ttlMs) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
            this.ttlMs = ttlMs;
        }

        public boolean isExpired() {
            if (ttlMs <= 0) return false; // Infinite TTL
            return (System.currentTimeMillis() - createdAt) > ttlMs;
        }

        public V getValue() {
            return value;
        }
    }

    public InMemoryLruCache() {
        this(DEFAULT_MAX_CAPACITY, DEFAULT_TTL_MS);
    }

    public InMemoryLruCache(int maxCapacity) {
        this(maxCapacity, DEFAULT_TTL_MS);
    }

    public InMemoryLruCache(int maxCapacity, long defaultTtlMs) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be strictly positive");
        }
        this.maxCapacity = maxCapacity;
        this.defaultTtlMs = defaultTtlMs;

        // accessOrder = true for Least-Recently-Used eviction
        this.map = new LinkedHashMap<K, CacheEntry<V>>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                if (size() > InMemoryLruCache.this.maxCapacity) {
                    evictionCount.incrementAndGet();
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Constructs a tenant-scoped cache key to prevent cross-user/tenant data leakage.
     */
    public static String buildScopedKey(@NonNull String tenantOrUserId, @NonNull String resourceKey) {
        return tenantOrUserId.trim() + "::" + resourceKey.trim();
    }

    /**
     * Stores an item with the default TTL.
     */
    public synchronized void put(@NonNull K key, @NonNull V value) {
        put(key, value, defaultTtlMs);
    }

    /**
     * Stores an item with a custom TTL.
     */
    public synchronized void put(@NonNull K key, @NonNull V value, long ttlMs) {
        map.put(key, new CacheEntry<>(value, ttlMs));
    }

    /**
     * Retrieves an item if present and not expired.
     */
    @Nullable
    public synchronized V get(@NonNull K key) {
        CacheEntry<V> entry = map.get(key);
        if (entry == null) {
            missCount.incrementAndGet();
            return null;
        }

        if (entry.isExpired()) {
            map.remove(key);
            missCount.incrementAndGet();
            return null;
        }

        hitCount.incrementAndGet();
        return entry.getValue();
    }

    /**
     * Removes an item by key.
     */
    public synchronized boolean remove(@NonNull K key) {
        return map.remove(key) != null;
    }

    /**
     * Invalidates all keys starting with a specific prefix (e.g. all entries for a user).
     */
    public synchronized int invalidatePrefix(@NonNull String prefix) {
        List<K> toRemove = new ArrayList<>();
        for (K key : map.keySet()) {
            if (key.toString().startsWith(prefix)) {
                toRemove.add(key);
            }
        }
        for (K key : toRemove) {
            map.remove(key);
        }
        return toRemove.size();
    }

    /**
     * Clears all cached entries and resets statistics.
     */
    public synchronized void clear() {
        map.clear();
        hitCount.set(0);
        missCount.set(0);
        evictionCount.set(0);
    }

    public synchronized int size() {
        return map.size();
    }

    public synchronized boolean containsKey(@NonNull K key) {
        CacheEntry<V> entry = map.get(key);
        if (entry == null) return false;
        if (entry.isExpired()) {
            map.remove(key);
            return false;
        }
        return true;
    }

    // --- Statistics & Observability ---

    public long getHitCount() {
        return hitCount.get();
    }

    public long getMissCount() {
        return missCount.get();
    }

    public long getEvictionCount() {
        return evictionCount.get();
    }

    public double getHitRatio() {
        long hits = hitCount.get();
        long total = hits + missCount.get();
        if (total == 0) return 0.0;
        return (double) hits / total;
    }
}
