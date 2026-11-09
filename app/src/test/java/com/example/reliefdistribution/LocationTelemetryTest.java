package com.example.reliefdistribution;

import com.example.reliefdistribution.data.model.LocationTelemetry;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationTelemetryTest {

    @Test
    public void validCoordinates_accepted() {
        LocationTelemetry telemetry = new LocationTelemetry("user-1", 23.8103, 90.4125, 1000L, 5.0f, 1.2f, true);
        assertEquals(23.8103, telemetry.getLatitude(), 0.0001);
        assertEquals(90.4125, telemetry.getLongitude(), 0.0001);
        assertEquals(1000L, telemetry.getTimestamp());
        assertTrue(telemetry.isOnline());
    }

    @Test
    public void boundaryCoordinates_accepted() {
        assertTrue(LocationTelemetry.isValidLatitude(90.0));
        assertTrue(LocationTelemetry.isValidLatitude(-90.0));
        assertTrue(LocationTelemetry.isValidLatitude(0.0));

        assertTrue(LocationTelemetry.isValidLongitude(180.0));
        assertTrue(LocationTelemetry.isValidLongitude(-180.0));
        assertTrue(LocationTelemetry.isValidLongitude(0.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void latitudeOverLimit_throwsException() {
        new LocationTelemetry("user-1", 90.001, 90.0, 1000L, 0.0f, 0.0f, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void latitudeUnderLimit_throwsException() {
        new LocationTelemetry("user-1", -90.001, 90.0, 1000L, 0.0f, 0.0f, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void longitudeOverLimit_throwsException() {
        new LocationTelemetry("user-1", 20.0, 180.001, 1000L, 0.0f, 0.0f, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void longitudeUnderLimit_throwsException() {
        new LocationTelemetry("user-1", 20.0, -180.001, 1000L, 0.0f, 0.0f, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeTimestamp_throwsException() {
        new LocationTelemetry("user-1", 23.0, 90.0, -1L, 0.0f, 0.0f, true);
    }

    @Test
    public void toMap_containsAllFields() {
        LocationTelemetry telemetry = new LocationTelemetry("user-1", 23.8, 90.4, 5000L, 10.0f, 2.5f, true);
        Map<String, Object> map = telemetry.toMap();

        assertEquals("user-1", map.get("userId"));
        assertEquals(23.8, (Double) map.get("latitude"), 0.001);
        assertEquals(90.4, (Double) map.get("longitude"), 0.001);
        assertEquals(5000L, map.get("timestamp"));
        assertEquals(10.0f, (Float) map.get("accuracy"), 0.01);
        assertEquals(2.5f, (Float) map.get("speed"), 0.01);
        assertEquals(true, map.get("isOnline"));
    }
}

