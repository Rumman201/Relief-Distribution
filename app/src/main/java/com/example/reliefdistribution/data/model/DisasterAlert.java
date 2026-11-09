package com.example.reliefdistribution.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model representing official disaster bulletins and emergency advisories.
 */
@IgnoreExtraProperties
public class DisasterAlert {

    public static final String SEVERITY_LOW = "LOW";
    public static final String SEVERITY_MODERATE = "MODERATE";
    public static final String SEVERITY_SEVERE = "SEVERE";
    public static final String SEVERITY_CRITICAL = "CRITICAL";

    private String alertId;
    private String title;
    private String description;
    private String severity;
    private String affectedZone;
    private long issuedAt;
    private long expiresAt;
    private boolean isActive;

    public DisasterAlert() {
        this.isActive = true;
        this.issuedAt = System.currentTimeMillis();
    }

    public DisasterAlert(String alertId, String title, String description, String severity,
                         String affectedZone, long expiresAt) {
        setAlertId(alertId);
        setTitle(title);
        setDescription(description);
        setSeverity(severity);
        this.affectedZone = affectedZone != null ? affectedZone.trim() : "All Zones";
        this.issuedAt = System.currentTimeMillis();
        this.expiresAt = expiresAt;
        this.isActive = true;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        if (alertId == null || alertId.trim().isEmpty()) {
            throw new IllegalArgumentException("Alert ID must not be blank");
        }
        this.alertId = alertId.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        if (severity == null || (!severity.equals(SEVERITY_LOW) && !severity.equals(SEVERITY_MODERATE) &&
                !severity.equals(SEVERITY_SEVERE) && !severity.equals(SEVERITY_CRITICAL))) {
            this.severity = SEVERITY_MODERATE;
        } else {
            this.severity = severity;
        }
    }

    public String getAffectedZone() {
        return affectedZone;
    }

    public void setAffectedZone(String affectedZone) {
        this.affectedZone = affectedZone;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("alertId", alertId);
        map.put("title", title);
        map.put("description", description);
        map.put("severity", severity);
        map.put("affectedZone", affectedZone);
        map.put("issuedAt", issuedAt);
        map.put("expiresAt", expiresAt);
        map.put("isActive", isActive);
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisasterAlert that = (DisasterAlert) o;
        return Objects.equals(alertId, that.alertId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId);
    }
}

