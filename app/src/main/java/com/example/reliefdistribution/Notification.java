package com.example.reliefdistribution;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    public static final String TYPE_REQUEST_CREATED = "request_created";
    public static final String TYPE_REQUEST_ACCEPTED = "request_accepted";
    public static final String TYPE_REQUEST_IN_PROGRESS = "request_in_progress";
    public static final String TYPE_REQUEST_COMPLETED = "request_completed";
    public static final String TYPE_REQUEST_CANCELLED = "request_cancelled";
    public static final String TYPE_INVENTORY_LOW = "inventory_low";
    public static final String TYPE_GENERAL = "general";

    private String notificationId;
    private String userId; // recipient user ID
    private String title;
    private String message;
    private String type;
    private String relatedRequestId;
    private String relatedItemId;
    private long timestamp;
    private boolean isRead;
    private Map<String, String> data; // Additional data payload

    public Notification() {
        // Default constructor required for Firebase
    }

    public Notification(String userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.data = new HashMap<>();
    }

    public Notification(String userId, String title, String message, String type,
                        String relatedRequestId, String relatedItemId) {
        this(userId, title, message, type);
        this.relatedRequestId = relatedRequestId;
        this.relatedItemId = relatedItemId;
    }

    // Getters and Setters
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedRequestId() {
        return relatedRequestId;
    }

    public void setRelatedRequestId(String relatedRequestId) {
        this.relatedRequestId = relatedRequestId;
    }

    public String getRelatedItemId() {
        return relatedItemId;
    }

    public void setRelatedItemId(String relatedItemId) {
        this.relatedItemId = relatedItemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public void addData(String key, String value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("userId", userId);
        result.put("title", title);
        result.put("message", message);
        result.put("type", type);
        result.put("relatedRequestId", relatedRequestId);
        result.put("relatedItemId", relatedItemId);
        result.put("timestamp", timestamp);
        result.put("isRead", isRead);
        result.put("data", data);
        return result;
    }

    @Exclude
    public static Notification fromMap(Map<String, Object> map) {
        Notification notification = new Notification();
        notification.setNotificationId((String) map.get("notificationId"));
        notification.setUserId((String) map.get("userId"));
        notification.setTitle((String) map.get("title"));
        notification.setMessage((String) map.get("message"));
        notification.setType((String) map.get("type"));
        notification.setRelatedRequestId((String) map.get("relatedRequestId"));
        notification.setRelatedItemId((String) map.get("relatedItemId"));
        notification.setTimestamp(((Number) map.get("timestamp")).longValue());
        notification.setRead((Boolean) map.get("isRead"));
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) map.get("data");
        notification.setData(data);
        return notification;
    }
}