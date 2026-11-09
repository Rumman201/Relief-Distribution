package com.example.reliefdistribution;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class InventoryItem {
    public static final String CATEGORY_FOOD = "food";
    public static final String CATEGORY_WATER = "water";
    public static final String CATEGORY_MEDICINE = "medicine";
    public static final String CATEGORY_CLOTHING = "clothing";
    public static final String CATEGORY_SHELTER = "shelter";
    public static final String CATEGORY_HYGIENE = "hygiene";
    public static final String CATEGORY_OTHER = "other";

    public static final String STATUS_IN_STOCK = "in_stock";
    public static final String STATUS_LOW_STOCK = "low_stock";
    public static final String STATUS_OUT_OF_STOCK = "out_of_stock";

    private String itemId;
    private String distributorId;
    private String itemName;
    private String category;
    private int quantityAvailable;
    private int quantityReserved;
    private String unit; // e.g., "pieces", "kg", "liters", "boxes"
    private String description;
    private String location;
    private long expiryDate;
    private int threshold = 10;
    private long lastUpdated;
    private boolean isActive;

    public InventoryItem() {
        // Default constructor required for Firebase
    }

    public InventoryItem(String distributorId, String itemName, String category,
                         int quantityAvailable, String unit, String description) {
        this.distributorId = distributorId;
        this.itemName = itemName;
        this.category = category;
        this.quantityAvailable = quantityAvailable;
        this.quantityReserved = 0;
        this.unit = unit;
        this.description = description;
        this.location = "Central Depot";
        this.threshold = 10;
        this.lastUpdated = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getQuantityReserved() {
        return quantityReserved;
    }

    public void setQuantityReserved(int quantityReserved) {
        this.quantityReserved = quantityReserved;
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getQuantityTotal() {
        return quantityAvailable + quantityReserved;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStatus() {
        if (quantityAvailable <= 0) {
            return STATUS_OUT_OF_STOCK;
        } else if (quantityAvailable <= threshold) {
            return STATUS_LOW_STOCK;
        } else {
            return STATUS_IN_STOCK;
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemId", itemId);
        result.put("distributorId", distributorId);
        result.put("itemName", itemName);
        result.put("category", category);
        result.put("quantityAvailable", quantityAvailable);
        result.put("quantityReserved", quantityReserved);
        result.put("unit", unit);
        result.put("description", description);
        result.put("location", location);
        result.put("expiryDate", expiryDate);
        result.put("threshold", threshold);
        result.put("lastUpdated", lastUpdated);
        result.put("isActive", isActive);
        return result;
    }

    @Exclude
    public static InventoryItem fromMap(Map<String, Object> map) {
        InventoryItem item = new InventoryItem();
        item.setItemId((String) map.get("itemId"));
        item.setDistributorId((String) map.get("distributorId"));
        item.setItemName((String) map.get("itemName"));
        item.setCategory((String) map.get("category"));
        if (map.get("quantityAvailable") != null) {
            item.setQuantityAvailable(((Number) map.get("quantityAvailable")).intValue());
        }
        if (map.get("quantityReserved") != null) {
            item.setQuantityReserved(((Number) map.get("quantityReserved")).intValue());
        }
        item.setUnit((String) map.get("unit"));
        item.setDescription((String) map.get("description"));
        item.setLocation((String) map.get("location"));
        if (map.get("expiryDate") != null) {
            item.setExpiryDate(((Number) map.get("expiryDate")).longValue());
        }
        if (map.get("threshold") != null) {
            item.setThreshold(((Number) map.get("threshold")).intValue());
        }
        if (map.get("lastUpdated") != null) {
            item.setLastUpdated(((Number) map.get("lastUpdated")).longValue());
        }
        if (map.get("isActive") != null) {
            item.setActive((Boolean) map.get("isActive"));
        }
        return item;
    }
}
