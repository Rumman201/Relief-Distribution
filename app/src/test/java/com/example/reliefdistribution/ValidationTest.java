package com.example.reliefdistribution;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationTest {

    @Test
    public void userEntity_omitsPasswordField() {
        User user = new User("uid123", "Tanvir", "Ahmed", "tanvir@example.com", "01700000000", "Distributor");
        assertEquals("uid123", user.getUserId());
        assertEquals("Tanvir", user.getFirstName());
        assertEquals("Ahmed", user.getLastName());
        assertEquals("tanvir@example.com", user.getEmail());
        assertEquals("01700000000", user.getPhone());
        assertEquals("Distributor", user.getRole());

        // Verify class reflection confirms NO password field exists
        boolean hasPasswordField = false;
        for (java.lang.reflect.Field field : User.class.getDeclaredFields()) {
            if ("password".equalsIgnoreCase(field.getName())) {
                hasPasswordField = true;
                break;
            }
        }
        assertFalse("Security Invariant: User class must not contain a password field", hasPasswordField);
    }

    @Test
    public void roleValidation_distinguishesDistributorAndSurvivor() {
        String roleDistributor = "Distributor";
        String roleSurvivor = "Survivor";
        String roleInvalid = "Admin";

        assertTrue(isValidRole(roleDistributor));
        assertTrue(isValidRole(roleSurvivor));
        assertFalse(isValidRole(roleInvalid));
        assertFalse(isValidRole(null));
    }

    @Test
    public void passwordMatchValidation_detectsMismatches() {
        String pass1 = "SecurePass123";
        String pass2 = "SecurePass123";
        String passMismatch = "DifferentPass";

        assertTrue(passwordsMatch(pass1, pass2));
        assertFalse(passwordsMatch(pass1, passMismatch));
        assertFalse(passwordsMatch(pass1, null));
    }

    @Test
    public void passwordLength_enforcesMinimumLength() {
        assertTrue(isPasswordLengthValid("123456"));
        assertTrue(isPasswordLengthValid("password123"));
        assertFalse(isPasswordLengthValid("12345"));
        assertFalse(isPasswordLengthValid(""));
        assertFalse(isPasswordLengthValid(null));
    }

    @Test
    public void locationCoordinates_validateLatitudeLongitudeBounds() {
        // Dhaka coordinates
        assertTrue(isValidCoordinate(23.8103, 90.4125));
        // Boundary coordinates
        assertTrue(isValidCoordinate(90.0, 180.0));
        assertTrue(isValidCoordinate(-90.0, -180.0));
        // Invalid coordinates
        assertFalse(isValidCoordinate(90.1, 90.0));
        assertFalse(isValidCoordinate(-90.1, 90.0));
        assertFalse(isValidCoordinate(0.0, 180.1));
        assertFalse(isValidCoordinate(0.0, -180.1));
    }

    // Helper validation methods representing domain rules
    private boolean isValidRole(String role) {
        return "Distributor".equals(role) || "Survivor".equals(role);
    }

    private boolean passwordsMatch(String p1, String p2) {
        return p1 != null && p1.equals(p2);
    }

    private boolean isPasswordLengthValid(String password) {
        return password != null && password.length() >= 6;
    }

    private boolean isValidCoordinate(double lat, double lng) {
        return lat >= -90.0 && lat <= 90.0 && lng >= -180.0 && lng <= 180.0;
    }
}

