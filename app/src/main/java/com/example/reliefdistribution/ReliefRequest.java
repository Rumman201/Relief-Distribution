package com.example.reliefdistribution;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ReliefRequest {
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    public static final String PRIORITY_LOW = "low";
    public static final String PRIORITY_MEDIUM = "medium";
    public static final String PRIORITY_HIGH = "high";
    public static final String PRIORITY_URGENT = "urgent";

    private String requestId;
    private String survivorId;
    private String survivorName;
    private String survivorPhone;
    private double survivorLatitude;
    private double survivorLongitude;
    private String survivorAddress;
    private String itemName;
    private String itemCategory;
    private int quantity;
    private String priority;
    private String status;
    private String distributorId;
    private String distributorName;
    private long timestamp;
    private long updatedAt;
    private long createdAt;
    private String notes;
    private String unit;
    private String locationAddress;
    private double latitude;
    private double longitude;
    private String category;

    public ReliefRequest() {
        // Default constructor required for Firebase
    }

    public ReliefRequest(String survivorId, String survivorName, String survivorPhone,
                         double survivorLatitude, double survivorLongitude, String survivorAddress,
                         String itemName, String itemCategory, int quantity, String priority, String notes) {
        this.survivorId = survivorId;
        this.survivorName = survivorName;
        this.survivorPhone = survivorPhone;
        this.survivorLatitude = survivorLatitude;
        this.survivorLongitude = survivorLongitude;
        this.survivorAddress = survivorAddress;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.quantity = quantity;
        this.priority = priority;
        this.status = STATUS_PENDING;
        this.timestamp = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.notes = notes;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSurvivorId() {
        return survivorId;
    }

    public void setSurvivorId(String survivorId) {
        this.survivorId = survivorId;
    }

    public String getSurvivorName() {
        return survivorName;
    }

    public void setSurvivorName(String survivorName) {
        this.survivorName = survivorName;
    }

    public String getSurvivorPhone() {
        return survivorPhone;
    }

    public void setSurvivorPhone(String survivorPhone) {
        this.survivorPhone = survivorPhone;
    }

    public double getSurvivorLatitude() {
        return survivorLatitude;
    }

    public void setSurvivorLatitude(double survivorLatitude) {
        this.survivorLatitude = survivorLatitude;
    }

    public double getSurvivorLongitude() {
        return survivorLongitude;
    }

    public void setSurvivorLongitude(double survivorLongitude) {
        this.survivorLongitude = survivorLongitude;
    }

    public String getSurvivorAddress() {
        return survivorAddress;
    }

    public void setSurvivorAddress(String survivorAddress) {
        this.survivorAddress = survivorAddress;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("survivorId", survivorId);
        result.put("survivorName", survivorName);
        result.put("survivorPhone", survivorPhone);
        result.put("survivorLatitude", survivorLatitude);
        result.put("survivorLongitude", survivorLongitude);
        result.put("survivorAddress", survivorAddress);
        result.put("itemName", itemName);
        result.put("itemCategory", itemCategory);
        result.put("quantity", quantity);
        result.put("priority", priority);
        result.put("status", status);
        result.put("distributorId", distributorId);
        result.put("distributorName", distributorName);
        result.put("timestamp", timestamp);
        result.put("updatedAt", updatedAt);
        result.put("createdAt", createdAt);
        result.put("notes", notes);
        result.put("unit", unit);
        result.put("locationAddress", locationAddress);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("category", category);
        return result;
    }

    @Exclude
    public static ReliefRequest fromMap(Map<String, Object> map) {
        ReliefRequest request = new ReliefRequest();
        request.setRequestId((String) map.get("requestId"));
        request.setSurvivorId((String) map.get("survivorId"));
        request.setSurvivorName((String) map.get("survivorName"));
        request.setSurvivorPhone((String) map.get("survivorPhone"));
        request.setSurvivorLatitude(((Number) map.get("survivorLatitude")).doubleValue());
        request.setSurvivorLongitude(((Number) map.get("survivorLongitude")).doubleValue());
        request.setSurvivorAddress((String) map.get("survivorAddress"));
        request.setItemName((String) map.get("itemName"));
        request.setItemCategory((String) map.get("itemCategory"));
        request.setQuantity(((Number) map.get("quantity")).intValue());
        request.setPriority((String) map.get("priority"));
        request.setStatus((String) map.get("status"));
        request.setDistributorId((String) map.get("distributorId"));
        request.setDistributorName((String) map.get("distributorName"));
        request.setTimestamp(((Number) map.get("timestamp")).longValue());
        request.setUpdatedAt(((Number) map.get("updatedAt")).longValue());
        if (map.get("createdAt") != null) {
            request.setCreatedAt(((Number) map.get("createdAt")).longValue());
        }
        request.setNotes((String) map.get("notes"));
        request.setUnit((String) map.get("unit"));
        request.setLocationAddress((String) map.get("locationAddress"));
        if (map.get("latitude") != null) {
            request.setLatitude(((Number) map.get("latitude")).doubleValue());
        }
        if (map.get("longitude") != null) {
            request.setLongitude(((Number) map.get("longitude")).doubleValue());
        }
        request.setCategory((String) map.get("category"));
        return request;
    }
}