package com.example.reliefdistribution;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Domain Test Suite for Notification.
 * Tests notification creation, lifecycle payloads, deep linking metadata, and Firebase serialization.
 */
public class NotificationDomainTest {

    @Test
    public void newNotification_hasDefaultFieldsAndUnreadState() {
        Notification notification = new Notification("user-100", "Cyclone Warning", "Category 4 Cyclone approaching coastal zone", Notification.TYPE_GENERAL);

        assertEquals("user-100", notification.getUserId());
        assertEquals("Cyclone Warning", notification.getTitle());
        assertEquals(Notification.TYPE_GENERAL, notification.getType());
        assertFalse("New notification must default to unread", notification.isRead());
        assertTrue(notification.getTimestamp() > 0);
        assertNotNull(notification.getData());
    }

    @Test
    public void deepLinkingData_storesAndSerializesPayload() {
        Notification notification = new Notification(
                "survivor-12",
                "Request In Progress",
                "Supplies are en route",
                Notification.TYPE_REQUEST_IN_PROGRESS,
                "req-999",
                "item-55"
        );
        notification.setNotificationId("notif-1");
        notification.addData("distributorLatitude", 23.8103);
        notification.addData("distributorLongitude", 90.4125);

        Map<String, Object> map = notification.toMap();
        assertEquals("notif-1", map.get("notificationId"));
        assertEquals("survivor-12", map.get("userId"));
        assertEquals("req-999", map.get("relatedRequestId"));
        assertEquals("item-55", map.get("relatedItemId"));

        Notification restored = Notification.fromMap(map);
        assertEquals("notif-1", restored.getNotificationId());
        assertEquals("survivor-12", restored.getUserId());
        assertEquals("req-999", restored.getRelatedRequestId());
        assertEquals("item-55", restored.getRelatedItemId());
        assertEquals(Notification.TYPE_REQUEST_IN_PROGRESS, restored.getType());
        assertNotNull(restored.getData());
        assertEquals(23.8103, (Double) restored.getData().get("distributorLatitude"), 0.0001);
    }
}
