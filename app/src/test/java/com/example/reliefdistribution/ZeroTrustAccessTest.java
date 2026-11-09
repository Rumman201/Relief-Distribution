package com.example.reliefdistribution;

import com.example.reliefdistribution.data.model.ReliefRequest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Security unit test suite verifying Zero-Trust state machine boundaries,
 * transition validation, and prevention of illegal state tampering.
 */
public class ZeroTrustAccessTest {

    private ReliefRequest request;

    @Before
    public void setUp() {
        request = new ReliefRequest("req_001", "survivor_1", "Fatima Begum", "01712345678",
                "MEDICAL", 3, 23.8103, 90.4125);
    }

    @Test
    public void testInitialState_isPending() {
        assertEquals(ReliefRequest.STATUS_PENDING, request.getStatus());
    }

    @Test
    public void testValidStateProgression_succeeds() {
        assertTrue(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_ACCEPTED));
        request.transitionTo(ReliefRequest.STATUS_ACCEPTED, "dist_001");
        assertEquals(ReliefRequest.STATUS_ACCEPTED, request.getStatus());

        assertTrue(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_EN_ROUTE));
        request.transitionTo(ReliefRequest.STATUS_EN_ROUTE, "dist_001");
        assertEquals(ReliefRequest.STATUS_EN_ROUTE, request.getStatus());

        assertTrue(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_FULFILLED));
        request.transitionTo(ReliefRequest.STATUS_FULFILLED, "dist_001");
        assertEquals(ReliefRequest.STATUS_FULFILLED, request.getStatus());
    }

    @Test
    public void testCancellationFromPendingOrAccepted_succeeds() {
        assertTrue(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_CANCELLED));
        request.transitionTo(ReliefRequest.STATUS_CANCELLED, null);
        assertEquals(ReliefRequest.STATUS_CANCELLED, request.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalTransition_skippingToFulfilled_throwsException() {
        // Attacker attempting to skip ACCEPTED and EN_ROUTE directly to FULFILLED
        assertFalse(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_FULFILLED));
        request.transitionTo(ReliefRequest.STATUS_FULFILLED, "dist_001");
    }

    @Test(expected = IllegalStateException.class)
    public void testTerminalState_cannotTransitionOutFromFulfilled() {
        request.transitionTo(ReliefRequest.STATUS_ACCEPTED, "dist_001");
        request.transitionTo(ReliefRequest.STATUS_EN_ROUTE, "dist_001");
        request.transitionTo(ReliefRequest.STATUS_FULFILLED, "dist_001");

        // Attempting to reopen fulfilled request
        assertFalse(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_PENDING));
        request.transitionTo(ReliefRequest.STATUS_PENDING, "dist_001");
    }

    @Test(expected = IllegalStateException.class)
    public void testTerminalState_cannotTransitionOutFromCancelled() {
        request.transitionTo(ReliefRequest.STATUS_CANCELLED, null);

        // Attempting to revive cancelled request
        assertFalse(ReliefRequest.isValidTransition(request.getStatus(), ReliefRequest.STATUS_ACCEPTED));
        request.transitionTo(ReliefRequest.STATUS_ACCEPTED, "dist_001");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStatus_rejectsUnrecognizedState() {
        request.transitionTo("HACKED_STATUS", "dist_001");
    }
}
