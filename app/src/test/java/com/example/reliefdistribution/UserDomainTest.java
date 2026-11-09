package com.example.reliefdistribution;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class UserDomainTest {

    @Test
    public void validRoles_accepted() {
        assertTrue(User.isValidRole("Distributor"));
        assertTrue(User.isValidRole("Survivor"));
        assertFalse(User.isValidRole("Admin"));
        assertFalse(User.isValidRole("Guest"));
        assertFalse(User.isValidRole(null));
    }

    @Test
    public void validEmails_accepted() {
        assertTrue(User.isValidEmail("tanvir@example.com"));
        assertTrue(User.isValidEmail("relief.worker+aid@domain.org"));
        assertFalse(User.isValidEmail("invalid-email"));
        assertFalse(User.isValidEmail("missing@domain"));
        assertFalse(User.isValidEmail(null));
        assertFalse(User.isValidEmail(""));
    }

    @Test
    public void validPhones_accepted() {
        assertTrue(User.isValidPhone("01712345678"));
        assertTrue(User.isValidPhone("+8801712345678"));
        assertFalse(User.isValidPhone("12345")); // Too short
        assertFalse(User.isValidPhone(null));
        assertFalse(User.isValidPhone(""));
    }

    @Test
    public void equalityAndHashCode_basedOnIdEmailRole() {
        User u1 = new User("uid1", "Rahim", "Khan", "r@test.com", "017", "Distributor");
        User u2 = new User("uid1", "Rahim", "Khan", "r@test.com", "017", "Distributor");
        User u3 = new User("uid2", "Karim", "Khan", "k@test.com", "018", "Survivor");

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotEquals(u1, u3);
    }
}

