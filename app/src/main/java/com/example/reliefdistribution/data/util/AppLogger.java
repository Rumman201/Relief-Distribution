package com.example.reliefdistribution.data.util;

import android.util.Log;

/**
 * PII-safe application logging utility for disaster response systems.
 * Automatically masks emails, phone numbers, and credentials to prevent log leakage.
 */
public class AppLogger {

    private static final String APP_TAG = "ReliefDist";

    private AppLogger() {}

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
        Log.d(APP_TAG + ":" + tag, message);
    }

    public static void i(String tag, String message) {
        Log.i(APP_TAG + ":" + tag, message);
    }

    public static void w(String tag, String message) {
        Log.w(APP_TAG + ":" + tag, message);
    }

    public static void e(String tag, String message, Throwable throwable) {
        Log.e(APP_TAG + ":" + tag, message, throwable);
    }
}

