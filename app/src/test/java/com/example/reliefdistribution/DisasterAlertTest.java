package com.example.reliefdistribution;

import com.example.reliefdistribution.data.model.DisasterAlert;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DisasterAlertTest {

    @Test
    public void validAlert_creation() {
        DisasterAlert alert = new DisasterAlert("alt-1", "Cyclone Warning", "Category 4 approaching", DisasterAlert.SEVERITY_CRITICAL, "Chittagong Coastal Zone", 1700000000L);
        assertEquals("alt-1", alert.getAlertId());
        assertEquals("Cyclone Warning", alert.getTitle());
        assertEquals(DisasterAlert.SEVERITY_CRITICAL, alert.getSeverity());
        assertEquals("Chittagong Coastal Zone", alert.getAffectedZone());
        assertTrue(alert.isActive());
    }

    @Test
    public void invalidSeverity_fallsBackToModerate() {
        DisasterAlert alert = new DisasterAlert("alt-2", "Flood Watch", "River rising", "UNKNOWN_SEVERITY", "Sylhet", 1700000000L);
        assertEquals(DisasterAlert.SEVERITY_MODERATE, alert.getSeverity());
    }

    @Test
    public void toMap_serializesCorrectly() {
        DisasterAlert alert = new DisasterAlert("alt-3", "Tsunami Watch", "Wave height 3m", DisasterAlert.SEVERITY_SEVERE, "Cox's Bazar", 1800000000L);
        Map<String, Object> map = alert.toMap();

        assertEquals("alt-3", map.get("alertId"));
        assertEquals("Tsunami Watch", map.get("title"));
        assertEquals(DisasterAlert.SEVERITY_SEVERE, map.get("severity"));
        assertEquals("Cox's Bazar", map.get("affectedZone"));
        assertEquals(true, map.get("isActive"));
    }
}

