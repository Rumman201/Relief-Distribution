package com.example.reliefdistribution.data.automation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reliefdistribution.InventoryItem;
import com.example.reliefdistribution.Notification;
import com.example.reliefdistribution.ReliefRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Intelligent Lifecycle & Inventory Automation Engine.
 * Enforces business state transitions, synchronizes inventory reservations atomically,
 * and generates audit-trailed notifications upon lifecycle events.
 */
public class ReliefWorkflowAutomator {

    /**
     * Strongly-typed result of an automated state change.
     */
    public static class AutomationResult {
        private final boolean success;
        private final String updatedStatus;
        private final int updatedAvailableInventory;
        private final int updatedReservedInventory;
        private final Notification generatedNotification;
        private final String errorMessage;

        public AutomationResult(boolean success,
                                @Nullable String updatedStatus,
                                int updatedAvailableInventory,
                                int updatedReservedInventory,
                                @Nullable Notification generatedNotification,
                                @Nullable String errorMessage) {
            this.success = success;
            this.updatedStatus = updatedStatus;
            this.updatedAvailableInventory = updatedAvailableInventory;
            this.updatedReservedInventory = updatedReservedInventory;
            this.generatedNotification = generatedNotification;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        @Nullable
        public String getUpdatedStatus() {
            return updatedStatus;
        }

        public int getUpdatedAvailableInventory() {
            return updatedAvailableInventory;
        }

        public int getUpdatedReservedInventory() {
            return updatedReservedInventory;
        }

        @Nullable
        public Notification getGeneratedNotification() {
            return generatedNotification;
        }

        @Nullable
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Executes automated workflow when a distributor claims a pending relief request.
     */
    @NonNull
    public AutomationResult onDistributorAcceptRequest(@NonNull ReliefRequest request,
                                                      @NonNull InventoryItem inventoryItem,
                                                      @NonNull String distributorId,
                                                      @NonNull String distributorName) {
        String currentStatus = request.getStatus();
        if (!ReliefRequest.STATUS_PENDING.equalsIgnoreCase(currentStatus)) {
            return new AutomationResult(false, currentStatus,
                    inventoryItem.getQuantityAvailable(), inventoryItem.getQuantityReserved(),
                    null, "Cannot accept request with non-pending status: " + currentStatus);
        }

        int requestedQty = request.getQuantity();
        if (inventoryItem.getQuantityAvailable() < requestedQty) {
            return new AutomationResult(false, currentStatus,
                    inventoryItem.getQuantityAvailable(), inventoryItem.getQuantityReserved(),
                    null, "Insufficient available inventory: requested " + requestedQty +
                    ", but only " + inventoryItem.getQuantityAvailable() + " available.");
        }

        // 1. Mutate inventory reservation
        inventoryItem.setQuantityAvailable(inventoryItem.getQuantityAvailable() - requestedQty);
        inventoryItem.setQuantityReserved(inventoryItem.getQuantityReserved() + requestedQty);

        // 2. Transition request state
        request.setStatus(ReliefRequest.STATUS_ACCEPTED);
        request.setDistributorId(distributorId);
        request.setDistributorName(distributorName);
        request.setUpdatedAt(System.currentTimeMillis());

        // 3. Dispatch automated notification to survivor
        Notification notification = new Notification(
                request.getSurvivorId(),
                "Relief Request Accepted",
                "Distributor " + distributorName + " has accepted your request for " +
                        requestedQty + " " + request.getUnit() + " of " + request.getItemName() + ".",
                Notification.TYPE_REQUEST_ACCEPTED,
                request.getRequestId(),
                inventoryItem.getItemId()
        );
        notification.setNotificationId(UUID.randomUUID().toString());
        Map<String, Object> data = new HashMap<>();
        data.put("requestId", request.getRequestId());
        data.put("distributorId", distributorId);
        notification.setData(data);

        return new AutomationResult(true, ReliefRequest.STATUS_ACCEPTED,
                inventoryItem.getQuantityAvailable(), inventoryItem.getQuantityReserved(),
                notification, null);
    }

    /**
     * Executes automated workflow when a delivery starts.
     */
    @NonNull
    public AutomationResult onStartDelivery(@NonNull ReliefRequest request) {
        String currentStatus = request.getStatus();
        if (!ReliefRequest.STATUS_ACCEPTED.equalsIgnoreCase(currentStatus)) {
            return new AutomationResult(false, currentStatus, 0, 0,
                    null, "Cannot start delivery from non-accepted status: " + currentStatus);
        }

        request.setStatus(ReliefRequest.STATUS_IN_PROGRESS);
        request.setUpdatedAt(System.currentTimeMillis());

        Notification notification = new Notification(
                request.getSurvivorId(),
                "Relief Supplies En Route",
                "Your relief supplies are currently on the way to your coordinates.",
                Notification.TYPE_REQUEST_IN_PROGRESS,
                request.getRequestId(),
                null
        );
        notification.setNotificationId(UUID.randomUUID().toString());

        return new AutomationResult(true, ReliefRequest.STATUS_IN_PROGRESS,
                0, 0, notification, null);
    }

    /**
     * Executes automated workflow when relief distribution is successfully completed.
     */
    @NonNull
    public AutomationResult onDeliveryCompleted(@NonNull ReliefRequest request,
                                               @NonNull InventoryItem inventoryItem) {
        String currentStatus = request.getStatus();
        if (!ReliefRequest.STATUS_IN_PROGRESS.equalsIgnoreCase(currentStatus) &&
                !ReliefRequest.STATUS_ACCEPTED.equalsIgnoreCase(currentStatus)) {
            return new AutomationResult(false, currentStatus,
                    inventoryItem.getQuantityAvailable(), inventoryItem.getQuantityReserved(),
                    null, "Cannot complete delivery from status: " + currentStatus);
        }

        // Deduct from reserved inventory (fulfilled)
        int requestedQty = request.getQuantity();
        int newReserved = Math.max(0, inventoryItem.getQuantityReserved() - requestedQty);
        inventoryItem.setQuantityReserved(newReserved);

        request.setStatus(ReliefRequest.STATUS_COMPLETED);
        request.setUpdatedAt(System.currentTimeMillis());

        Notification notification = new Notification(
                request.getSurvivorId(),
                "Relief Delivery Completed",
                "Your relief request for " + request.getItemName() + " has been successfully delivered.",
                Notification.TYPE_REQUEST_COMPLETED,
                request.getRequestId(),
                inventoryItem.getItemId()
        );
        notification.setNotificationId(UUID.randomUUID().toString());

        return new AutomationResult(true, ReliefRequest.STATUS_COMPLETED,
                inventoryItem.getQuantityAvailable(), inventoryItem.getQuantityReserved(),
                notification, null);
    }

    /**
     * Executes automated cancellation and releases reserved inventory back to available pool.
     */
    @NonNull
    public AutomationResult onRequestCancelled(@NonNull ReliefRequest request,
                                              @Nullable InventoryItem inventoryItem) {
        String previousStatus = request.getStatus();
        if (ReliefRequest.STATUS_COMPLETED.equalsIgnoreCase(previousStatus) ||
                ReliefRequest.STATUS_CANCELLED.equalsIgnoreCase(previousStatus)) {
            return new AutomationResult(false, previousStatus, 0, 0,
                    null, "Cannot cancel request in terminal state: " + previousStatus);
        }

        if (inventoryItem != null && (ReliefRequest.STATUS_ACCEPTED.equalsIgnoreCase(previousStatus) ||
                ReliefRequest.STATUS_IN_PROGRESS.equalsIgnoreCase(previousStatus))) {
            int qty = request.getQuantity();
            int newReserved = Math.max(0, inventoryItem.getQuantityReserved() - qty);
            inventoryItem.setQuantityReserved(newReserved);
            inventoryItem.setQuantityAvailable(inventoryItem.getQuantityAvailable() + qty);
        }

        request.setStatus(ReliefRequest.STATUS_CANCELLED);
        request.setUpdatedAt(System.currentTimeMillis());

        Notification notification = new Notification(
                request.getSurvivorId(),
                "Relief Request Cancelled",
                "Your request for " + request.getItemName() + " has been cancelled.",
                Notification.TYPE_REQUEST_CANCELLED,
                request.getRequestId(),
                inventoryItem != null ? inventoryItem.getItemId() : null
        );
        notification.setNotificationId(UUID.randomUUID().toString());

        return new AutomationResult(true, ReliefRequest.STATUS_CANCELLED,
                inventoryItem != null ? inventoryItem.getQuantityAvailable() : 0,
                inventoryItem != null ? inventoryItem.getQuantityReserved() : 0,
                notification, null);
    }

    /**
     * Checks if an inventory item has breached low-stock thresholds and generates an alert.
     */
    @Nullable
    public Notification checkInventoryThreshold(@NonNull InventoryItem item) {
        if (item.getQuantityAvailable() <= item.getThreshold()) {
            Notification alert = new Notification(
                    item.getDistributorId(),
                    "Low Stock Alert: " + item.getItemName(),
                    "Critical supply: only " + item.getQuantityAvailable() + " " +
                            item.getUnit() + " remaining at " + item.getLocation() + " (Threshold: " +
                            item.getThreshold() + ").",
                    Notification.TYPE_INVENTORY_LOW,
                    null,
                    item.getItemId()
            );
            alert.setNotificationId(UUID.randomUUID().toString());
            return alert;
        }
        return null;
    }
}
