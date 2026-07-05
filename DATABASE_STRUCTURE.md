# Firebase Realtime Database Structure - Relief Distribution App

## Overview
This document describes the Firebase Realtime Database structure for the Relief Distribution Android application, including the new features: Relief Requests, Inventory Management, and Notifications.

---

## Database Structure

```
relief-distribution-app/
├── users/
│   └── {uid}/
│       ├── role: "survivor" | "distributor"
│       ├── name: string
│       ├── email: string
│       ├── phone: string
│       ├── createdAt: timestamp
│       └── fcmToken: string (optional, for push notifications)
│
├── ReliefRequests/
│   └── {requestId}/
│       ├── requestId: string
│       ├── survivorId: string (UID of survivor who created request)
│       ├── survivorName: string
│       ├── survivorPhone: string
│       ├── survivorLatitude: double
│       ├── survivorLongitude: double
│       ├── survivorAddress: string
│       ├── itemName: string
│       ├── itemCategory: string
│       ├── quantity: int
│       ├── unit: string (e.g., "pieces", "kg", "liters", "boxes")
│       ├── priority: "low" | "medium" | "high" | "urgent"
│       ├── status: "pending" | "accepted" | "in_progress" | "completed" | "cancelled"
│       ├── distributorId: string (UID of distributor who accepted, null if pending)
│       ├── distributorName: string
│       ├── locationAddress: string (delivery address)
│       ├── latitude: double (delivery location latitude)
│       ├── longitude: double (delivery location longitude)
│       ├── category: string (same as itemCategory for filtering)
│       ├── notes: string (optional)
│       ├── timestamp: long (creation timestamp)
│       ├── createdAt: long (creation timestamp)
│       └── updatedAt: long (last update timestamp)
│
├── Inventory/
│   └── {itemId}/
│       ├── itemId: string
│       ├── name: string
│       ├── category: "FOOD" | "WATER" | "MEDICINE" | "CLOTHING" | "SHELTER" | "HYGIENE" | "OTHER"
│       ├── quantity: int (total quantity)
│       ├── availableQuantity: int (quantity available for distribution)
│       ├── reservedQuantity: int (quantity reserved for pending requests)
│       ├── unit: string (e.g., "pieces", "kg", "liters", "boxes")
│       ├── threshold: int (low stock alert threshold)
│       ├── description: string (optional)
│       ├── location: string (storage location)
│       ├── createdAt: long
│       └── updatedAt: long
│
└── Notifications/
    └── {notificationId}/
        ├── notificationId: string
        ├── userId: string (UID of recipient)
        ├── type: "REQUEST_CREATED" | "REQUEST_ACCEPTED" | "REQUEST_IN_PROGRESS" | "REQUEST_COMPLETED" | "REQUEST_CANCELLED" | "INVENTORY_LOW" | "INVENTORY_OUT_OF_STOCK" | "GENERAL"
        ├── title: string
        ├── message: string
        ├── data: map (optional, for deep linking)
        │   ├── requestId: string (for request-related notifications)
        │   ├── itemId: string (for inventory-related notifications)
        │   └── action: string (e.g., "VIEW_REQUEST", "VIEW_INVENTORY")
        ├── timestamp: long
        └── read: boolean
```

---

## Indexes

### ReliefRequests
- `survivorId` - For querying requests by survivor
- `distributorId` - For querying requests assigned to distributor
- `status` - For filtering by status (pending, accepted, etc.)
- `priority` - For sorting by priority
- `timestamp` - For chronological ordering

### Inventory
- `category` - For filtering by category
- `quantity` - For sorting by quantity
- `availableQuantity` - For low stock queries

### Notifications
- `userId` - For querying user's notifications
- `timestamp` - For chronological ordering
- `read` - For filtering unread notifications

---

## Security Rules Summary

### Users
- Users can only read/write their own profile
- Role must be either "survivor" or "distributor"
- Email must contain "@"
- Phone must be at least 10 characters

### ReliefRequests
- **Read**: Survivors can read their own requests; Distributors can read all requests
- **Write**: Survivors can create requests; Distributors can update status/distributorId
- **Validation**: Required fields, valid status/priority values, timestamps not in future

### Inventory
- **Read/Write**: Only distributors
- **Validation**: Required fields, valid category, non-negative quantities

### Notifications
- **Read**: Users can only read their own notifications
- **Write**: System creates notifications; users can mark as read
- **Validation**: Required fields, valid notification types

---

## Data Flow Examples

### 1. Survivor Creates Relief Request
```
1. Survivor fills form in CreateReliefRequestActivity
2. App creates ReliefRequest object with:
   - survivorId = currentUser.uid
   - status = "pending"
   - timestamp = ServerValue.TIMESTAMP
   - createdAt = ServerValue.TIMESTAMP
3. Push to ReliefRequests/{requestId}
4. Cloud Function triggers: Create notification for all distributors (type: REQUEST_CREATED)
```

### 2. Distributor Accepts Request
```
1. Distributor taps "Accept" in ManageRequestsActivity
2. Update ReliefRequests/{requestId}:
   - status = "accepted"
   - distributorId = currentUser.uid
   - distributorName = currentUser.name
   - updatedAt = ServerValue.TIMESTAMP
3. Cloud Function triggers: Create notification for survivor (type: REQUEST_ACCEPTED)
```

### 3. Distributor Starts Delivery
```
1. Distributor taps "Start Delivery" in ManageRequestsActivity
2. Update ReliefRequests/{requestId}:
   - status = "in_progress"
   - updatedAt = ServerValue.TIMESTAMP
3. Cloud Function triggers: Create notification for survivor (type: REQUEST_IN_PROGRESS)
```

### 4. Distributor Completes Delivery
```
1. Distributor taps "Complete" in ManageRequestsActivity
2. Update ReliefRequests/{requestId}:
   - status = "completed"
   - updatedAt = ServerValue.TIMESTAMP
3. Update Inventory/{itemId}:
   - availableQuantity -= request.quantity
   - reservedQuantity -= request.quantity
   - updatedAt = ServerValue.TIMESTAMP
4. Cloud Function triggers: 
   - Create notification for survivor (type: REQUEST_COMPLETED)
   - Check inventory levels, create INVENTORY_LOW/OUT_OF_STOCK if needed
```

### 5. Inventory Management
```
1. Distributor adds/updates item in InventoryManagementActivity
2. Write to Inventory/{itemId} with:
   - availableQuantity = quantity (new items)
   - reservedQuantity = 0
   - threshold = 10 (default)
3. When request accepted:
   - reservedQuantity += request.quantity
   - availableQuantity -= request.quantity
4. When request completed/cancelled:
   - reservedQuantity -= request.quantity
   - availableQuantity += request.quantity (if cancelled)
```

---

## Cloud Functions Needed

### 1. onRequestCreated (Trigger: ReliefRequests/{requestId} onCreate)
- Send push notification to all distributors (topic: "distributors")
- Create in-app notification for each distributor

### 2. onRequestStatusChanged (Trigger: ReliefRequests/{requestId} onUpdate)
- Detect status change
- Send appropriate notification to survivor
- If completed: update inventory quantities

### 3. onInventoryUpdated (Trigger: Inventory/{itemId} onUpdate)
- Check if availableQuantity <= threshold → INVENTORY_LOW notification
- Check if availableQuantity == 0 → INVENTORY_OUT_OF_STOCK notification

### 4. sendPushNotification (Callable)
- Input: { tokens: string[], title: string, body: string, data: map }
- Send FCM message to tokens

---

## FCM Topics
- `distributors` - All distributor users
- `survivors` - All survivor users
- `request_{requestId}` - Specific request updates (optional)

---

## Migration Notes

### Existing Data
- Existing `users` node structure remains compatible
- New fields in ReliefRequest are optional (backward compatible)
- Inventory and Notifications are new nodes

### Deployment Order
1. Deploy security rules (`firebase deploy --only database:rules`)
2. Deploy Cloud Functions (`firebase deploy --only functions`)
3. Update Android app with new Firebase dependencies
4. Test all user flows

---

## Testing Checklist

### Security Rules
- [ ] Survivor cannot read other survivors' requests
- [ ] Survivor cannot write to Inventory
- [ ] Distributor can read all requests
- [ ] Distributor can write to Inventory
- [ ] Users can only read their own notifications
- [ ] Invalid status/priority values rejected
- [ ] Negative quantities rejected

### Data Flow
- [ ] Survivor creates request → appears in distributor's ManageRequestsActivity
- [ ] Distributor accepts → survivor gets notification, request moves to "Accepted" tab
- [ ] Distributor starts delivery → survivor gets notification, request moves to "In Progress"
- [ ] Distributor completes → survivor gets notification, inventory updated, request moves to "Completed"
- [ ] Low inventory → distributor gets notification

### Real-time Updates
- [ ] Request list updates in real-time when status changes
- [ ] Inventory updates in real-time
- [ ] Notification badge updates in real-time