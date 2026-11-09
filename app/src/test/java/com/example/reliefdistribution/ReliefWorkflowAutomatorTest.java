package com.example.reliefdistribution;

import com.example.reliefdistribution.data.automation.ReliefWorkflowAutomator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit & Integration Test Suite for ReliefWorkflowAutomator.
 * Verifies request state machines, atomic inventory reservations, and automated notifications.
 */
public class ReliefWorkflowAutomatorTest {

    private ReliefWorkflowAutomator automator;

    @Before
    public void setUp() {
        automator = new ReliefWorkflowAutomator();
    }

    @Test
    public void onDistributorAcceptRequest_sufficientStock_reservesInventoryAndNotifies() {
        ReliefRequest request = new ReliefRequest(
                "survivor-101", "Rahim", "01700000000",
                23.8103, 90.4125, "Mirpur-10, Dhaka",
                "Rice 10kg", "Food", 5, "high", "Urgent family rations"
        );
        request.setRequestId("req-test-1");
        request.setStatus(ReliefRequest.STATUS_PENDING);

        InventoryItem inventory = new InventoryItem(
                "dist-202", "Rice 10kg", "Food", 20, "bags", "Depot stock"
        );
        inventory.setItemId("inv-item-1");

        ReliefWorkflowAutomator.AutomationResult result = automator.onDistributorAcceptRequest(
                request, inventory, "dist-202", "Team Alpha Leader"
        );

        assertTrue("Workflow execution should succeed", result.isSuccess());
        assertEquals(ReliefRequest.STATUS_ACCEPTED, request.getStatus());
        assertEquals("dist-202", request.getDistributorId());
        assertEquals(15, inventory.getQuantityAvailable());
        assertEquals(5, inventory.getQuantityReserved());

        Notification notif = result.getGeneratedNotification();
        assertNotNull(notif);
        assertEquals("survivor-101", notif.getUserId());
        assertEquals(Notification.TYPE_REQUEST_ACCEPTED, notif.getType());
    }

    @Test
    public void onDistributorAcceptRequest_insufficientStock_failsGracefully() {
        ReliefRequest request = new ReliefRequest(
                "survivor-101", "Rahim", "01700000000",
                23.8103, 90.4125, "Mirpur-10, Dhaka",
                "Rice 10kg", "Food", 30, "high", "Need 30 bags"
        );
        request.setRequestId("req-test-2");
        request.setStatus(ReliefRequest.STATUS_PENDING);

        InventoryItem inventory = new InventoryItem(
                "dist-202", "Rice 10kg", "Food", 10, "bags", "Depot stock"
        );

        ReliefWorkflowAutomator.AutomationResult result = automator.onDistributorAcceptRequest(
                request, inventory, "dist-202", "Team Alpha Leader"
        );

        assertFalse("Workflow must reject request when stock is insufficient", result.isSuccess());
        assertEquals(ReliefRequest.STATUS_PENDING, request.getStatus());
        assertEquals(10, inventory.getQuantityAvailable());
        assertEquals(0, inventory.getQuantityReserved());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void onStartDelivery_fromAcceptedState_succeeds() {
        ReliefRequest request = new ReliefRequest();
        request.setRequestId("req-test-3");
        request.setSurvivorId("survivor-55");
        request.setStatus(ReliefRequest.STATUS_ACCEPTED);

        ReliefWorkflowAutomator.AutomationResult result = automator.onStartDelivery(request);

        assertTrue(result.isSuccess());
        assertEquals(ReliefRequest.STATUS_IN_PROGRESS, request.getStatus());
        assertNotNull(result.getGeneratedNotification());
        assertEquals(Notification.TYPE_REQUEST_IN_PROGRESS, result.getGeneratedNotification().getType());
    }

    @Test
    public void onDeliveryCompleted_deductsReservedStockAndNotifies() {
        ReliefRequest request = new ReliefRequest();
        request.setRequestId("req-test-4");
        request.setSurvivorId("survivor-55");
        request.setItemName("Emergency Water");
        request.setQuantity(4);
        request.setStatus(ReliefRequest.STATUS_IN_PROGRESS);

        InventoryItem inventory = new InventoryItem();
        inventory.setQuantityAvailable(16);
        inventory.setQuantityReserved(4);

        ReliefWorkflowAutomator.AutomationResult result = automator.onDeliveryCompleted(request, inventory);

        assertTrue(result.isSuccess());
        assertEquals(ReliefRequest.STATUS_COMPLETED, request.getStatus());
        assertEquals(16, inventory.getQuantityAvailable());
        assertEquals(0, inventory.getQuantityReserved());
        assertNotNull(result.getGeneratedNotification());
        assertEquals(Notification.TYPE_REQUEST_COMPLETED, result.getGeneratedNotification().getType());
    }

    @Test
    public void onRequestCancelled_restoresReservedStockToAvailable() {
        ReliefRequest request = new ReliefRequest();
        request.setRequestId("req-test-5");
        request.setSurvivorId("survivor-55");
        request.setItemName("Medicine Kits");
        request.setQuantity(2);
        request.setStatus(ReliefRequest.STATUS_ACCEPTED);

        InventoryItem inventory = new InventoryItem();
        inventory.setQuantityAvailable(8);
        inventory.setQuantityReserved(2);

        ReliefWorkflowAutomator.AutomationResult result = automator.onRequestCancelled(request, inventory);

        assertTrue(result.isSuccess());
        assertEquals(ReliefRequest.STATUS_CANCELLED, request.getStatus());
        assertEquals(10, inventory.getQuantityAvailable());
        assertEquals(0, inventory.getQuantityReserved());
        assertNotNull(result.getGeneratedNotification());
        assertEquals(Notification.TYPE_REQUEST_CANCELLED, result.getGeneratedNotification().getType());
    }

    @Test
    public void checkInventoryThreshold_triggersAlertWhenAtOrBelowThreshold() {
        InventoryItem item = new InventoryItem();
        item.setItemName("Water Purification Tablets");
        item.setLocation("Warehouse A");
        item.setDistributorId("dist-1");
        item.setThreshold(10);
        item.setQuantityAvailable(5); // Breaches threshold

        Notification alert = automator.checkInventoryThreshold(item);
        assertNotNull(alert);
        assertEquals(Notification.TYPE_INVENTORY_LOW, alert.getType());

        item.setQuantityAvailable(50); // Above threshold
        Notification safe = automator.checkInventoryThreshold(item);
        assertNull(safe);
    }
}
