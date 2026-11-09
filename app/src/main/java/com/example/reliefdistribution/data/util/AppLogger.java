package com.example.reliefdistribution.data.util;

import android.util.Log;

import java.util.Map;
import java.util.UUID;

/**
 * PII-safe application logging utility for disaster response systems.
 * Automatically masks emails, phone numbers, and credentials to prevent log leakage.
 * Provides OpenTelemetry-compatible structured event logging and trace correlation.
 */
public class AppLogger {

    private static final String APP_TAG = "ReliefDist";
    private static final ThreadLocal<String> CURRENT_TRACE_ID = new ThreadLocal<>();

    private AppLogger() {}

    /**
     * Initializes a unique distributed trace identifier for the calling thread.
     */
    public static String startTrace() {
        String traceId = "trc-" + UUID.randomUUID().toString().substring(0, 8);
        CURRENT_TRACE_ID.set(traceId);
        return traceId;
    }

    /**
     * Sets an explicit correlation trace ID for the calling thread.
     */
    public static String startTrace(String customTraceId) {
        if (customTraceId == null || customTraceId.trim().isEmpty()) {
            return startTrace();
        }
        CURRENT_TRACE_ID.set(customTraceId);
        return customTraceId;
    }

    /**
     * Retrieves the current correlation trace ID, or "trc-none" if uninitialized.
     */
    public static String getTraceId() {
        String id = CURRENT_TRACE_ID.get();
        return id != null ? id : "trc-none";
    }

    /**
     * Clears the correlation trace ID from the calling thread to prevent thread pool leakage.
     */
    public static void clearTrace() {
        CURRENT_TRACE_ID.remove();
    }

    /**
     * Formats an OpenTelemetry-compatible structured JSON log string.
     */
    public static String formatStructuredJson(String level, String tag, String action, String traceId, Map<String, Object> attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"level\":\"").append(escapeJson(level != null ? level : "INFO")).append("\",");
        sb.append("\"tag\":\"").append(escapeJson(tag != null ? tag : "APP")).append("\",");
        sb.append("\"action\":\"").append(escapeJson(action != null ? action : "EVENT")).append("\",");
        sb.append("\"traceId\":\"").append(escapeJson(traceId != null ? traceId : getTraceId())).append("\"");

        if (attributes != null && !attributes.isEmpty()) {
            sb.append(",\"attributes\":{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if (!first) sb.append(",");
                String key = entry.getKey();
                Object val = entry.getValue();
                sb.append("\"").append(escapeJson(key)).append("\":");

                if (val == null) {
                    sb.append("null");
                } else if (val instanceof Number || val instanceof Boolean) {
                    sb.append(val);
                } else {
                    String strVal = val.toString();
                    String lowerKey = key.toLowerCase();
                    if (lowerKey.contains("email")) {
                        strVal = maskEmail(strVal);
                    } else if (lowerKey.contains("phone")) {
                        strVal = maskPhone(strVal);
                    } else if (lowerKey.contains("password") || lowerKey.contains("token") || lowerKey.contains("secret")) {
                        strVal = "***REDACTED***";
                    }
                    sb.append("\"").append(escapeJson(strVal)).append("\"");
                }
                first = false;
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Masks an email address (e.g., "john.doe@example.com" -> "j***e@example.com").
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return "*".repeat(username.length()) + domain;
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + domain;
    }

    /**
     * Masks a phone number (e.g., "01712345678" -> "017****5678").
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return "***";
        }
        int len = phone.length();
        return phone.substring(0, 3) + "****" + phone.substring(len - 4);
    }

    public static void d(String tag, String message) {
        try {
            Log.d(APP_TAG + ":" + tag, message);
        } catch (Throwable ignored) {
            // JVM Unit test environment fallback
        }
    }

    public static void i(String tag, String message) {
        try {
            Log.i(APP_TAG + ":" + tag, message);
        } catch (Throwable ignored) {
            // JVM Unit test environment fallback
        }
    }

    public static void w(String tag, String message) {
        try {
            Log.w(APP_TAG + ":" + tag, message);
        } catch (Throwable ignored) {
            // JVM Unit test environment fallback
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        try {
            Log.e(APP_TAG + ":" + tag, message, throwable);
        } catch (Throwable ignored) {
            // JVM Unit test environment fallback
        }
    }
}
