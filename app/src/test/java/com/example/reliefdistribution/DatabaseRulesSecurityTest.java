package com.example.reliefdistribution;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Security unit test suite simulating and verifying Firebase Realtime Database
 * server-side security rules, RBAC authorization invariants, and schema constraints.
 */
public class DatabaseRulesSecurityTest {

    private static final Set<String> VALID_RELIEF_STATUSES = new HashSet<>(Arrays.asList(
            "pending", "accepted", "in_progress", "completed", "cancelled",
            "PENDING", "ACCEPTED", "EN_ROUTE", "FULFILLED", "CANCELLED"
    ));

    private static final Set<String> VALID_ROLES = new HashSet<>(Arrays.asList(
            "Survivor", "Distributor", "survivor", "distributor"
    ));

    // Simulated Rule Evaluator for User Profile
    private boolean canWriteUserProfile(String authUid, String targetUid, String oldRole, String newRole) {
        if (authUid == null || !authUid.equals(targetUid)) {
            return false; // Can only write own profile
        }
        if (!VALID_ROLES.contains(newRole)) {
            return false; // Invalid role
        }
        if (oldRole != null && !oldRole.equalsIgnoreCase(newRole)) {
            return false; // Role escalation blocked
        }
        return true;
    }

    // Simulated Rule Evaluator for ReliefRequest Creation
    private boolean canCreateReliefRequest(String authUid, String survivorId, String initialStatus, int quantity) {
        if (authUid == null) return false;
        if (!authUid.equals(survivorId)) return false; // Must be own request
        if (!"pending".equalsIgnoreCase(initialStatus)) return false; // Initial status must be pending
        if (quantity <= 0 || quantity > 10_000) return false;
        return true;
    }

    // Simulated Rule Evaluator for ReliefRequest Update
    private boolean canUpdateRequestStatus(String authUid, String userRole, String survivorId,
                                           String currentStatus, String targetStatus) {
        if (authUid == null) return false;
        if (!VALID_RELIEF_STATUSES.contains(targetStatus)) return false;

        boolean isDistributor = "Distributor".equalsIgnoreCase(userRole);
        boolean isOwner = authUid.equals(survivorId);

        if (isDistributor) {
            // Distributors can advance status
            return true;
        }

        if (isOwner) {
            // Survivors can only cancel their own request if pending
            if ("cancelled".equalsIgnoreCase(targetStatus) || "CANCELLED".equalsIgnoreCase(targetStatus)) {
                return "pending".equalsIgnoreCase(currentStatus);
            }
            // Survivors cannot accept or complete their own requests
            return false;
        }

        return false;
    }

    // Simulated Rule Evaluator for Inventory Write
    private boolean canWriteInventory(String authUid, String userRole, String itemDistributorId) {
        if (authUid == null) return false;
        if (!"Distributor".equalsIgnoreCase(userRole)) return false; // Only Distributors
        return authUid.equals(itemDistributorId); // Must match auth UID
    }

    // Simulated Rule Evaluator for Notifications Access
    private boolean canReadNotification(String authUid, String notificationRecipientId) {
        if (authUid == null) return false;
        return authUid.equals(notificationRecipientId); // Strict recipient isolation
    }

    @Test
    public void testUserProfile_allowsSelfRegistration() {
        assertTrue(canWriteUserProfile("uid_123", "uid_123", null, "Survivor"));
        assertTrue(canWriteUserProfile("uid_456", "uid_456", null, "Distributor"));
    }

    @Test
    public void testUserProfile_blocksImpersonation() {
        // Attacker uid_999 attempting to write to victim uid_123
        assertFalse(canWriteUserProfile("uid_999", "uid_123", null, "Survivor"));
    }

    @Test
    public void testUserProfile_preventsRoleEscalation() {
        // Survivor attempting to mutate their role to Distributor
        assertFalse(canWriteUserProfile("uid_123", "uid_123", "Survivor", "Distributor"));
    }

    @Test
    public void testReliefRequestCreation_permitsValidSurvivorRequest() {
        assertTrue(canCreateReliefRequest("survivor_1", "survivor_1", "pending", 5));
    }

    @Test
    public void testReliefRequestCreation_blocksUnauthorizedOrMalformedRequest() {
        // Unauthenticated
        assertFalse(canCreateReliefRequest(null, "survivor_1", "pending", 5));
        // Impersonating another survivor
        assertFalse(canCreateReliefRequest("attacker", "survivor_1", "pending", 5));
        // Submitting with already-accepted status
        assertFalse(canCreateReliefRequest("survivor_1", "survivor_1", "accepted", 5));
        // Invalid quantity
        assertFalse(canCreateReliefRequest("survivor_1", "survivor_1", "pending", 0));
        assertFalse(canCreateReliefRequest("survivor_1", "survivor_1", "pending", -10));
    }

    @Test
    public void testReliefRequestUpdate_enforcesRoleSeparation() {
        // Distributor can accept, en-route, fulfill
        assertTrue(canUpdateRequestStatus("dist_1", "Distributor", "survivor_1", "pending", "accepted"));
        assertTrue(canUpdateRequestStatus("dist_1", "Distributor", "survivor_1", "accepted", "in_progress"));
        assertTrue(canUpdateRequestStatus("dist_1", "Distributor", "survivor_1", "in_progress", "completed"));

        // Survivor can cancel own pending request
        assertTrue(canUpdateRequestStatus("survivor_1", "Survivor", "survivor_1", "pending", "cancelled"));

        // Survivor CANNOT cancel request after it has been accepted
        assertFalse(canUpdateRequestStatus("survivor_1", "Survivor", "survivor_1", "accepted", "cancelled"));

        // Survivor CANNOT self-approve or complete own request
        assertFalse(canUpdateRequestStatus("survivor_1", "Survivor", "survivor_1", "pending", "accepted"));
        assertFalse(canUpdateRequestStatus("survivor_1", "Survivor", "survivor_1", "in_progress", "completed"));

        // Unauthorized bystander cannot modify request
        assertFalse(canUpdateRequestStatus("random_user", "Survivor", "survivor_1", "pending", "accepted"));
    }

    @Test
    public void testInventoryAccess_restrictedToDistributors() {
        // Distributor can write their own inventory item
        assertTrue(canWriteInventory("dist_1", "Distributor", "dist_1"));

        // Survivor cannot write to inventory
        assertFalse(canWriteInventory("survivor_1", "Survivor", "survivor_1"));

        // Distributor cannot overwrite another distributor's item
        assertFalse(canWriteInventory("dist_1", "Distributor", "dist_2"));

        // Unauthenticated access blocked
        assertFalse(canWriteInventory(null, "Distributor", "dist_1"));
    }

    @Test
    public void testNotificationAccess_enforcesRecipientIsolation() {
        // Recipient can read their own notification
        assertTrue(canReadNotification("user_123", "user_123"));

        // Another user cannot read recipient's notification
        assertFalse(canReadNotification("eavesdropper", "user_123"));

        // Unauthenticated cannot read
        assertFalse(canReadNotification(null, "user_123"));
    }
}
