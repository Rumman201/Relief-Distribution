package com.example.reliefdistribution.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Domain model representing survivor emergency aid requests.
 * Enforces state machine transitions and input validation rules.
 */
@IgnoreExtraProperties
public class ReliefRequest {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_EN_ROUTE = "EN_ROUTE";
    public static final String STATUS_FULFILLED = "FULFILLED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            STATUS_PENDING, STATUS_ACCEPTED, STATUS_EN_ROUTE, STATUS_FULFILLED, STATUS_CANCELLED
    ));

    private String requestId;
    private String userId;
    private String survivorName;
    private String phone;
    private String emergencyType;
    private int rationCount;
    private double latitude;
    private double longitude;
    private String status;
    private String assignedDistributorId;
    private long createdAt;
    private long updatedAt;

    public ReliefRequest() {
        this.status = STATUS_PENDING;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public ReliefRequest(String requestId, String userId, String survivorName, String phone,
                         String emergencyType, int rationCount, double latitude, double longitude) {
        setRequestId(requestId);
        setUserId(userId);
        setSurvivorName(survivorName);
        setPhone(phone);
        this.emergencyType = emergencyType != null ? emergencyType : "General Aid";
        setRationCount(rationCount);
        setCoordinates(latitude, longitude);
        this.status = STATUS_PENDING;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public static boolean isValidStatus(String status) {
        return status != null && VALID_STATUSES.contains(status);
    }

    /**
     * Asserts whether a status transition is permitted by the state machine.
     */
    public static boolean isValidTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) return false;
        if (currentStatus.equals(targetStatus)) return true;

        switch (currentStatus) {
            case STATUS_PENDING:
                return STATUS_ACCEPTED.equals(targetStatus) || STATUS_CANCELLED.equals(targetStatus);
            case STATUS_ACCEPTED:
                return STATUS_EN_ROUTE.equals(targetStatus) || STATUS_CANCELLED.equals(targetStatus);
            case STATUS_EN_ROUTE:
                return STATUS_FULFILLED.equals(targetStatus) || STATUS_CANCELLED.equals(targetStatus);
            case STATUS_FULFILLED:
            case STATUS_CANCELLED:
                // Terminal states
                return false;
            default:
                return false;
        }
    }

    public void transitionTo(String newStatus, String distributorId) {
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Unknown status: " + newStatus);
        }
        if (!isValidTransition(this.status, newStatus)) {
            throw new IllegalStateException("Illegal state transition from " + this.status + " to " + newStatus);
        }
        this.status = newStatus;
        if (distributorId != null) {
            this.assignedDistributorId = distributorId;
        }
        this.updatedAt = System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID must not be blank");
        }
        this.requestId = requestId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID must not be blank");
        }
        this.userId = userId.trim();
    }

    public String getSurvivorName() {
        return survivorName;
    }

    public void setSurvivorName(String survivorName) {
        if (survivorName == null || survivorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Survivor name must not be blank");
        }
        this.survivorName = survivorName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone must not be blank");
        }
        this.phone = phone.trim();
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public int getRationCount() {
        return rationCount;
    }

    public void setRationCount(int rationCount) {
        if (rationCount < 1) {
            throw new IllegalArgumentException("Ration count must be at least 1");
        }
        this.rationCount = rationCount;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (!LocationTelemetry.isValidLatitude(latitude)) {
            throw new IllegalArgumentException("Invalid latitude: " + latitude);
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (!LocationTelemetry.isValidLongitude(longitude)) {
            throw new IllegalArgumentException("Invalid longitude: " + longitude);
        }
        this.longitude = longitude;
    }

    public void setCoordinates(double lat, double lng) {
        setLatitude(lat);
        setLongitude(lng);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        this.status = status;
    }

    public String getAssignedDistributorId() {
        return assignedDistributorId;
    }

    public void setAssignedDistributorId(String assignedDistributorId) {
        this.assignedDistributorId = assignedDistributorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", requestId);
        map.put("userId", userId);
        map.put("survivorName", survivorName);
        map.put("phone", phone);
        map.put("emergencyType", emergencyType);
        map.put("rationCount", rationCount);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("status", status);
        map.put("assignedDistributorId", assignedDistributorId);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReliefRequest that = (ReliefRequest) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}

