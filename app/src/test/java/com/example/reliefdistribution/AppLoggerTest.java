package com.example.reliefdistribution;

import com.example.reliefdistribution.data.util.AppLogger;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AppLoggerTest {

    @After
    public void tearDown() {
        AppLogger.clearTrace();
    }

    @Test
    public void maskEmail_masksUsernameCorrectly() {
        assertEquals("j***e@example.com", AppLogger.maskEmail("john.doe@example.com"));
        assertEquals("t***r@gmail.com", AppLogger.maskEmail("tanvir@gmail.com"));
        assertEquals("***", AppLogger.maskEmail(null));
        assertEquals("***", AppLogger.maskEmail("invalid-email"));
    }

    @Test
    public void maskPhone_masksMiddleDigits() {
        assertEquals("017****5678", AppLogger.maskPhone("01712345678"));
        assertEquals("018****4321", AppLogger.maskPhone("01898764321"));
        assertEquals("***", AppLogger.maskPhone(null));
        assertEquals("***", AppLogger.maskPhone("12345"));
    }

    @Test
    public void traceCorrelation_managesThreadLocalLifecycle() {
        assertEquals("trc-none", AppLogger.getTraceId());

        String generatedTrace = AppLogger.startTrace();
        assertNotNull(generatedTrace);
        assertTrue(generatedTrace.startsWith("trc-"));
        assertEquals(generatedTrace, AppLogger.getTraceId());

        AppLogger.startTrace("custom-trace-1234");
        assertEquals("custom-trace-1234", AppLogger.getTraceId());

        AppLogger.clearTrace();
        assertEquals("trc-none", AppLogger.getTraceId());
    }

    @Test
    public void formatStructuredJson_formatsOpenTelemetryLogAttributes() {
        AppLogger.startTrace("trc-ops-test-99");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("operation", "AID_DISPATCH");
        attributes.put("itemsDispatched", 42);
        attributes.put("success", true);
        attributes.put("contactEmail", "distributor.alpha@relief.org");
        attributes.put("contactPhone", "01712345678");
        attributes.put("authToken", "super-secret-jwt-token");

        String json = AppLogger.formatStructuredJson("INFO", "DisasterOps", "DISPATCH_COMMITTED", null, attributes);

        assertTrue(json.contains("\"level\":\"INFO\""));
        assertTrue(json.contains("\"tag\":\"DisasterOps\""));
        assertTrue(json.contains("\"action\":\"DISPATCH_COMMITTED\""));
        assertTrue(json.contains("\"traceId\":\"trc-ops-test-99\""));
        assertTrue(json.contains("\"itemsDispatched\":42"));
        assertTrue(json.contains("\"success\":true"));

        // Verify PII & credential redactions
        assertTrue(json.contains("\"contactEmail\":\"d***a@relief.org\""));
        assertTrue(json.contains("\"contactPhone\":\"017****5678\""));
        assertTrue(json.contains("\"authToken\":\"***REDACTED***\""));
    }
}
