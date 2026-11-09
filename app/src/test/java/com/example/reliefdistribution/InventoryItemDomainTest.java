package com.example.reliefdistribution;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Domain Test Suite for InventoryItem.
 * Tests stock calculations, thresholds, status state machines, and Firebase map conversions.
 */
public class InventoryItemDomainTest {

    @Test
    public void getStatus_calculatesCorrectInventoryState() {
        InventoryItem item = new InventoryItem("dist-1", "Water Canteen", InventoryItem.CATEGORY_WATER, 50, "cans", "Stock");
        item.setThreshold(10);

        // Above threshold
        assertEquals(InventoryItem.STATUS_IN_STOCK, item.getStatus());

        // At threshold
        item.setQuantityAvailable(10);
        assertEquals(InventoryItem.STATUS_LOW_STOCK, item.getStatus());

        // Below threshold
        item.setQuantityAvailable(3);
        assertEquals(InventoryItem.STATUS_LOW_STOCK, item.getStatus());

        // Out of stock
        item.setQuantityAvailable(0);
        assertEquals(InventoryItem.STATUS_OUT_OF_STOCK, item.getStatus());
    }

    @Test
    public void getQuantityTotal_sumsAvailableAndReserved() {
        InventoryItem item = new InventoryItem();
        item.setQuantityAvailable(35);
        item.setQuantityReserved(15);

        assertEquals(50, item.getQuantityTotal());
    }

    @Test
    public void toMapAndFromMap_preservesFullInventoryState() {
        InventoryItem item = new InventoryItem("dist-88", "Antibiotics", InventoryItem.CATEGORY_MEDICINE, 100, "boxes", "Medical depot");
        item.setItemId("med-101");
        item.setLocation("Central Clinic Zone B");
        item.setExpiryDate(1800000000000L);
        item.setThreshold(20);
        item.setQuantityReserved(10);

        Map<String, Object> map = item.toMap();
        assertEquals("med-101", map.get("itemId"));
        assertEquals("dist-88", map.get("distributorId"));
        assertEquals("Antibiotics", map.get("itemName"));
        assertEquals(100, map.get("quantityAvailable"));
        assertEquals(10, map.get("quantityReserved"));
        assertEquals(20, map.get("threshold"));
        assertEquals("Central Clinic Zone B", map.get("location"));

        InventoryItem restored = InventoryItem.fromMap(map);
        assertEquals("med-101", restored.getItemId());
        assertEquals("Antibiotics", restored.getItemName());
        assertEquals(100, restored.getQuantityAvailable());
        assertEquals(10, restored.getQuantityReserved());
        assertEquals(20, restored.getThreshold());
        assertEquals("Central Clinic Zone B", restored.getLocation());
        assertEquals(1800000000000L, restored.getExpiryDate());
        assertTrue(restored.isActive());
    }
}
