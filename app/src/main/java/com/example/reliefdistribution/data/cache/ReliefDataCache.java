package com.example.reliefdistribution.data.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reliefdistribution.InventoryItem;
import com.example.reliefdistribution.User;
import com.example.reliefdistribution.data.model.DisasterAlert;

import java.util.List;

/**
 * Production caching hub providing strongly-typed, tenant-isolated in-memory caches
 * for User profiles, Depot Inventory, and Disaster Alerts.
 */
public class ReliefDataCache {

    private static volatile ReliefDataCache sInstance;

    // User profile cache (Key: userId, TTL: 15 minutes, Max: 100)
    private final InMemoryLruCache<String, User> userCache;

    // Inventory item cache (Key: itemId, TTL: 5 minutes, Max: 200)
    private final InMemoryLruCache<String, InventoryItem> inventoryCache;

    // Disaster alerts list cache (Key: "active_alerts", TTL: 2 minutes, Max: 10)
    private final InMemoryLruCache<String, List<DisasterAlert>> alertCache;

    private ReliefDataCache() {
        this.userCache = new InMemoryLruCache<>(100, 15 * 60 * 1000L);
        this.inventoryCache = new InMemoryLruCache<>(200, 5 * 60 * 1000L);
        this.alertCache = new InMemoryLruCache<>(10, 2 * 60 * 1000L);
    }

    public static ReliefDataCache getInstance() {
        if (sInstance == null) {
            synchronized (ReliefDataCache.class) {
                if (sInstance == null) {
                    sInstance = new ReliefDataCache();
                }
            }
        }
        return sInstance;
    }

    // --- User Cache Methods ---

    public void putUser(@NonNull String userId, @NonNull User user) {
        userCache.put(userId, user);
    }

    @Nullable
    public User getUser(@NonNull String userId) {
        return userCache.get(userId);
    }

    public void invalidateUser(@NonNull String userId) {
        userCache.remove(userId);
    }

    // --- Inventory Cache Methods ---

    public void putInventoryItem(@NonNull String itemId, @NonNull InventoryItem item) {
        inventoryCache.put(itemId, item);
    }

    @Nullable
    public InventoryItem getInventoryItem(@NonNull String itemId) {
        return inventoryCache.get(itemId);
    }

    public void invalidateInventoryItem(@NonNull String itemId) {
        inventoryCache.remove(itemId);
    }

    public void invalidateDistributorInventory(@NonNull String distributorId) {
        inventoryCache.invalidatePrefix(distributorId);
    }

    // --- Alert Cache Methods ---

    public void putActiveAlerts(@NonNull List<DisasterAlert> alerts) {
        alertCache.put("active_alerts", alerts);
    }

    @Nullable
    public List<DisasterAlert> getActiveAlerts() {
        return alertCache.get("active_alerts");
    }

    public void invalidateAlerts() {
        alertCache.remove("active_alerts");
    }

    /**
     * Purges all cache pools on user sign-out to prevent cross-session data leakage.
     */
    public void clearAll() {
        userCache.clear();
        inventoryCache.clear();
        alertCache.clear();
    }

    public InMemoryLruCache<String, User> getUserCache() {
        return userCache;
    }

    public InMemoryLruCache<String, InventoryItem> getInventoryCache() {
        return inventoryCache;
    }

    public InMemoryLruCache<String, List<DisasterAlert>> getAlertCache() {
        return alertCache;
    }
}
