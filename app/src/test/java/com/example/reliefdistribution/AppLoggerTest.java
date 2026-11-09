package com.example.reliefdistribution;

import com.example.reliefdistribution.data.util.AppLogger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppLoggerTest {

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
}

