package com.example.reliefdistribution.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model representing real-time GPS telemetry broadcasted from mobile devices.
 * Contains domain invariant validations for geographic coordinates and timing.
 */
@IgnoreExtraProperties
public class LocationTelemetry {

    private String userId;
    private double latitude;
    private double longitude;
    private long timestamp;
    private float accuracy;
    private float speed;
    private boolean isOnline;

    /**
     * Default constructor required for Firebase Realtime Database deserialization.
     */
    public LocationTelemetry() {
        this.isOnline = true;
    }

    /**
     * Domain constructor with invariant assertions.
     */
    public LocationTelemetry(String userId, double latitude, double longitude, long timestamp,
                             float accuracy, float speed, boolean isOnline) {
        setUserId(userId);
        setCoordinates(latitude, longitude);
        setTimestamp(timestamp);
        this.accuracy = accuracy;
        this.speed = speed;
        this.isOnline = isOnline;
    }

    public static boolean isValidLatitude(double lat) {
        return !Double.isNaN(lat) && lat >= -90.0 && lat <= 90.0;
    }

    public static boolean isValidLongitude(double lng) {
        return !Double.isNaN(lng) && lng >= -180.0 && lng <= 180.0;
    }

    public static boolean isValidTimestamp(long timestamp) {
        return timestamp > 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId != null ? userId.trim() : null;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (!isValidLatitude(latitude)) {
            throw new IllegalArgumentException("Latitude out of range [-90.0, 90.0]: " + latitude);
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (!isValidLongitude(longitude)) {
            throw new IllegalArgumentException("Longitude out of range [-180.0, 180.0]: " + longitude);
        }
        this.longitude = longitude;
    }

    public void setCoordinates(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        if (!isValidTimestamp(timestamp)) {
            throw new IllegalArgumentException("Timestamp must be positive: " + timestamp);
        }
        this.timestamp = timestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    /**
     * Converts telemetry data into a Map for Firebase atomic path writes.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("timestamp", timestamp);
        map.put("accuracy", accuracy);
        map.put("speed", speed);
        map.put("isOnline", isOnline);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationTelemetry that = (LocationTelemetry) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                timestamp == that.timestamp &&
                Float.compare(that.accuracy, accuracy) == 0 &&
                Float.compare(that.speed, speed) == 0 &&
                isOnline == that.isOnline &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, latitude, longitude, timestamp, accuracy, speed, isOnline);
    }
}

