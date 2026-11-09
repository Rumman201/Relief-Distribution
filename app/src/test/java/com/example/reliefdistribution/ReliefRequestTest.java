package com.example.reliefdistribution;

import com.example.reliefdistribution.data.model.ReliefRequest;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReliefRequestTest {

    @Test
    public void newRequest_hasPendingStatus() {
        ReliefRequest request = new ReliefRequest("req-1", "user-1", "Rahim", "01700000000", "Food & Water", 4, 23.8, 90.4);
        assertEquals(ReliefRequest.STATUS_PENDING, request.getStatus());
        assertEquals("req-1", request.getRequestId());
        assertEquals(4, request.getRationCount());
    }

    @Test
    public void validLifecycleTransitions_succeed() {
        ReliefRequest request = new ReliefRequest("req-1", "user-1", "Rahim", "01700000000", "Food", 2, 23.8, 90.4);

        // PENDING -> ACCEPTED
        request.transitionTo(ReliefRequest.STATUS_ACCEPTED, "distributor-99");
        assertEquals(ReliefRequest.STATUS_ACCEPTED, request.getStatus());
        assertEquals("distributor-99", request.getAssignedDistributorId());

        // ACCEPTED -> EN_ROUTE
        request.transitionTo(ReliefRequest.STATUS_EN_ROUTE, "distributor-99");
        assertEquals(ReliefRequest.STATUS_EN_ROUTE, request.getStatus());

        // EN_ROUTE -> FULFILLED
        request.transitionTo(ReliefRequest.STATUS_FULFILLED, "distributor-99");
        assertEquals(ReliefRequest.STATUS_FULFILLED, request.getStatus());
    }

    @Test
    public void cancellationTransitions_succeed() {
        ReliefRequest req1 = new ReliefRequest("r1", "u1", "A", "01700000000", "Food", 1, 23.0, 90.0);
        req1.transitionTo(ReliefRequest.STATUS_CANCELLED, null);
        assertEquals(ReliefRequest.STATUS_CANCELLED, req1.getStatus());

        ReliefRequest req2 = new ReliefRequest("r2", "u1", "A", "01700000000", "Food", 1, 23.0, 90.0);
        req2.transitionTo(ReliefRequest.STATUS_ACCEPTED, "dist-1");
        req2.transitionTo(ReliefRequest.STATUS_CANCELLED, null);
        assertEquals(ReliefRequest.STATUS_CANCELLED, req2.getStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void invalidTransition_pendingToFulfilled_throwsException() {
        ReliefRequest request = new ReliefRequest("req-1", "user-1", "Rahim", "01700000000", "Food", 2, 23.8, 90.4);
        request.transitionTo(ReliefRequest.STATUS_FULFILLED, "dist-1");
    }

    @Test(expected = IllegalStateException.class)
    public void invalidTransition_terminalStateCannotRevert() {
        ReliefRequest request = new ReliefRequest("req-1", "user-1", "Rahim", "01700000000", "Food", 2, 23.8, 90.4);
        request.transitionTo(ReliefRequest.STATUS_ACCEPTED, "dist-1");
        request.transitionTo(ReliefRequest.STATUS_EN_ROUTE, "dist-1");
        request.transitionTo(ReliefRequest.STATUS_FULFILLED, "dist-1");

        // Attempting to revert terminal state
        request.transitionTo(ReliefRequest.STATUS_PENDING, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRationCount_throwsException() {
        new ReliefRequest("req-1", "user-1", "Rahim", "01700000000", "Food", 0, 23.8, 90.4);
    }

    @Test
    public void toMap_serializesCorrectly() {
        ReliefRequest request = new ReliefRequest("req-10", "user-5", "Karim", "01800000000", "Medical", 3, 22.5, 89.5);
        Map<String, Object> map = request.toMap();

        assertEquals("req-10", map.get("requestId"));
        assertEquals("user-5", map.get("userId"));
        assertEquals("Karim", map.get("survivorName"));
        assertEquals("01800000000", map.get("phone"));
        assertEquals("Medical", map.get("emergencyType"));
        assertEquals(3, map.get("rationCount"));
        assertEquals(ReliefRequest.STATUS_PENDING, map.get("status"));
    }
}

