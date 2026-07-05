/**
 * Cloud Functions for Relief Distribution App
 * 
 * This file contains example Cloud Functions for the new features:
 * - Relief Requests management
 * - Inventory Management
 * - Notifications (in-app + push)
 * 
 * Deploy with: firebase deploy --only functions
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

const db = admin.database();
const messaging = admin.messaging();

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

/**
 * Send push notification to a topic
 */
async function sendToTopic(topic, title, body, data = {}) {
  const message = {
    topic: topic,
    notification: { title, body },
    data: Object.fromEntries(
      Object.entries(data).map(([k, v]) => [k, String(v)])
    ),
    android: {
      priority: 'high',
      notification: {
        channelId: 'relief_notifications',
        icon: 'ic_notification',
        color: '#2196F3'
      }
    },
    apns: {
      payload: {
        aps: {
          sound: 'default',
          badge: 1
        }
      }
    }
  };

  try {
    await messaging.send(message);
    console.log(`Push notification sent to topic: ${topic}`);
  } catch (error) {
    console.error('Error sending push notification:', error);
  }
}

/**
 * Send push notification to specific tokens
 */
async function sendToTokens(tokens, title, body, data = {}) {
  if (!tokens || tokens.length === 0) return;

  const message = {
    tokens: tokens,
    notification: { title, body },
    data: Object.fromEntries(
      Object.entries(data).map(([k, v]) => [k, String(v)])
    ),
    android: {
      priority: 'high',
      notification: {
        channelId: 'relief_notifications',
        icon: 'ic_notification',
        color: '#2196F3'
      }
    },
    apns: {
      payload: {
        aps: {
          sound: 'default',
          badge: 1
        }
      }
    }
  };

  try {
    const response = await messaging.sendEachForMulticast(message);
    console.log(`Push sent: ${response.successCount} success, ${response.failureCount} failed`);
    
    // Clean up invalid tokens
    if (response.failureCount > 0) {
      const failedTokens = [];
      response.responses.forEach((resp, idx) => {
        if (!resp.success && resp.error.code === 'messaging/invalid-registration-token') {
          failedTokens.push(tokens[idx]);
        }
      });
      if (failedTokens.length > 0) {
        await cleanupInvalidTokens(failedTokens);
      }
    }
  } catch (error) {
    console.error('Error sending multicast:', error);
  }
}

/**
 * Create in-app notification
 */
async function createInAppNotification(userId, type, title, message, data = {}) {
  const notificationRef = db.ref('Notifications').push();
  const notificationId = notificationRef.key;
  
  const notification = {
    notificationId,
    userId,
    type,
    title,
    message,
    data,
    timestamp: admin.database.ServerValue.TIMESTAMP,
    read: false
  };

  await notificationRef.set(notification);
  console.log(`In-app notification created for user ${userId}: ${notificationId}`);
  
  return notificationId;
}

/**
 * Get FCM tokens for users with specific role
 */
async function getTokensForRole(role) {
  const usersRef = db.ref('users');
  const snapshot = await usersRef.orderByChild('role').equalTo(role).once('value');
  
  const tokens = [];
  snapshot.forEach(child => {
    const token = child.val().fcmToken;
    if (token) tokens.push(token);
  });
  
  return tokens;
}

/**
 * Clean up invalid FCM tokens
 */
async function cleanupInvalidTokens(tokens) {
  const updates = {};
  tokens.forEach(token => {
    // Find user with this token and remove it
    // This would require a reverse index or scanning all users
    // For simplicity, we'll log it
    console.log(`Invalid token to clean up: ${token}`);
  });
}

// ============================================================================
// RELIEF REQUESTS TRIGGERS
// ============================================================================

/**
 * Triggered when a new relief request is created
 * Notifies all distributors
 */
exports.onRequestCreated = functions.database
  .ref('/ReliefRequests/{requestId}')
  .onCreate(async (snapshot, context) => {
    const request = snapshot.val();
    const requestId = context.params.requestId;
    
    console.log(`New relief request created: ${requestId}`, request);
    
    // Create in-app notifications for all distributors
    const distributorTokens = await getTokensForRole('distributor');
    
    // Send push notification to distributors topic
    await sendToTopic('distributors', 
      'New Relief Request',
      `${request.survivorName} needs ${request.quantity} ${request.unit} of ${request.itemName}`,
      { requestId, type: 'REQUEST_CREATED', action: 'VIEW_REQUEST' }
    );
    
    // Create in-app notifications for each distributor
    const usersRef = db.ref('users');
    const distributorsSnapshot = await usersRef.orderByChild('role').equalTo('distributor').once('value');
    
    const promises = [];
    distributorsSnapshot.forEach(child => {
      const distributorId = child.key;
      promises.push(createInAppNotification(
        distributorId,
        'REQUEST_CREATED',
        'New Relief Request',
        `${request.survivorName} needs ${request.quantity} ${request.unit} of ${request.itemName}`,
        { requestId, action: 'VIEW_REQUEST' }
      ));
    });
    
    await Promise.all(promises);
    
    return null;
  });

/**
 * Triggered when a relief request is updated (status change)
 * Notifies the survivor
 */
exports.onRequestUpdated = functions.database
  .ref('/ReliefRequests/{requestId}')
  .onUpdate(async (change, context) => {
    const before = change.before.val();
    const after = change.after.val();
    const requestId = context.params.requestId;
    
    // Check if status changed
    if (before.status === after.status) {
      console.log(`Request ${requestId} updated but status unchanged`);
      return null;
    }
    
    console.log(`Request ${requestId} status changed: ${before.status} -> ${after.status}`);
    
    const survivorId = after.survivorId;
    let notificationType, title, message;
    
    switch (after.status) {
      case 'accepted':
        notificationType = 'REQUEST_ACCEPTED';
        title = 'Request Accepted';
        message = `${after.distributorName} has accepted your request for ${after.itemName}`;
        break;
      case 'in_progress':
        notificationType = 'REQUEST_IN_PROGRESS';
        title = 'Delivery Started';
        message = `${after.distributorName} is on the way with your ${after.itemName}`;
        break;
      case 'completed':
        notificationType = 'REQUEST_COMPLETED';
        title = 'Delivery Completed';
        message = `Your request for ${after.itemName} has been delivered`;
        break;
      case 'cancelled':
        notificationType = 'REQUEST_CANCELLED';
        title = 'Request Cancelled';
        message = `Your request for ${after.itemName} was cancelled`;
        break;
      default:
        return null;
    }
    
    // Create in-app notification for survivor
    await createInAppNotification(
      survivorId,
      notificationType,
      title,
      message,
      { requestId, action: 'VIEW_REQUEST' }
    );
    
    // Send push notification to survivor
    const survivorSnapshot = await db.ref(`users/${survivorId}`).once('value');
    const survivor = survivorSnapshot.val();
    if (survivor && survivor.fcmToken) {
      await sendToTokens([survivor.fcmToken], title, message, {
        requestId,
        type: notificationType,
        action: 'VIEW_REQUEST'
      });
    }
    
    // If completed, update inventory
    if (after.status === 'completed' && before.status !== 'completed') {
      await updateInventoryOnCompletion(after);
    }
    
    return null;
  });

/**
 * Update inventory when request is completed
 */
async function updateInventoryOnCompletion(request) {
  // Find matching inventory item
  const inventoryRef = db.ref('Inventory');
  const snapshot = await inventoryRef
    .orderByChild('name')
    .equalTo(request.itemName)
    .once('value');
  
  if (!snapshot.exists()) {
    console.log(`No inventory item found for: ${request.itemName}`);
    return;
  }
  
  let itemId = null;
  let itemData = null;
  
  snapshot.forEach(child => {
    itemId = child.key;
    itemData = child.val();
  });
  
  if (!itemId || !itemData) return;
  
  const newAvailable = Math.max(0, (itemData.availableQuantity || 0) - request.quantity);
  const newReserved = Math.max(0, (itemData.reservedQuantity || 0) - request.quantity);
  
  await db.ref(`Inventory/${itemId}`).update({
    availableQuantity: newAvailable,
    reservedQuantity: newReserved,
    updatedAt: admin.database.ServerValue.TIMESTAMP
  });
  
  console.log(`Inventory updated for ${itemId}: available=${newAvailable}, reserved=${newReserved}`);
  
  // Check for low stock
  await checkInventoryLevels(itemId, itemData.name, newAvailable, itemData.threshold || 10);
}

/**
 * Check inventory levels and notify if low
 */
async function checkInventoryLevels(itemId, itemName, availableQuantity, threshold) {
  if (availableQuantity <= 0) {
    await notifyDistributorsInventory('INVENTORY_OUT_OF_STOCK', 
      'Out of Stock', 
      `${itemName} is out of stock!`,
      { itemId, action: 'VIEW_INVENTORY' }
    );
  } else if (availableQuantity <= threshold) {
    await notifyDistributorsInventory('INVENTORY_LOW',
      'Low Stock Alert',
      `${itemName} is running low (${availableQuantity} remaining)`,
      { itemId, action: 'VIEW_INVENTORY' }
    );
  }
}

/**
 * Notify all distributors about inventory status
 */
async function notifyDistributorsInventory(type, title, message, data) {
  const distributorTokens = await getTokensForRole('distributor');
  
  await sendToTopic('distributors', title, message, data);
  
  const usersRef = db.ref('users');
  const distributorsSnapshot = await usersRef.orderByChild('role').equalTo('distributor').once('value');
  
  const promises = [];
  distributorsSnapshot.forEach(child => {
    promises.push(createInAppNotification(
      child.key,
      type,
      title,
      message,
      data
    ));
  });
  
  await Promise.all(promises);
}

// ============================================================================
// INVENTORY TRIGGERS
// ============================================================================

/**
 * Triggered when inventory is updated
 * Checks for low stock alerts
 */
exports.onInventoryUpdated = functions.database
  .ref('/Inventory/{itemId}')
  .onUpdate(async (change, context) => {
    const before = change.before.val();
    const after = change.after.val();
    const itemId = context.params.itemId;
    
    // Check if available quantity changed significantly
    const beforeAvailable = before.availableQuantity || 0;
    const afterAvailable = after.availableQuantity || 0;
    const threshold = after.threshold || 10;
    
    if (afterAvailable === beforeAvailable) return null;
    
    console.log(`Inventory ${itemId} (${after.name}) changed: ${beforeAvailable} -> ${afterAvailable}`);
    
    // Check for low stock transitions
    if (afterAvailable <= 0 && beforeAvailable > 0) {
      await notifyDistributorsInventory('INVENTORY_OUT_OF_STOCK',
        'Out of Stock',
        `${after.name} is now out of stock!`,
        { itemId, action: 'VIEW_INVENTORY' }
      );
    } else if (afterAvailable <= threshold && beforeAvailable > threshold) {
      await notifyDistributorsInventory('INVENTORY_LOW',
        'Low Stock Alert',
        `${after.name} is running low (${afterAvailable} remaining)`,
        { itemId, action: 'VIEW_INVENTORY' }
      );
    }
    
    return null;
  });

/**
 * Triggered when new inventory item is created
 */
exports.onInventoryCreated = functions.database
  .ref('/Inventory/{itemId}')
  .onCreate(async (snapshot, context) => {
    const item = snapshot.val();
    const itemId = context.params.itemId;
    
    console.log(`New inventory item created: ${itemId}`, item);
    
    // Notify distributors about new item
    await notifyDistributorsInventory('GENERAL',
      'New Inventory Item Added',
      `${item.name} has been added to inventory`,
      { itemId, action: 'VIEW_INVENTORY' }
    );
    
    return null;
  });

// ============================================================================
// CALLABLE FUNCTIONS
// ============================================================================

/**
 * Callable function to send push notification
 * Can be called from Android app
 */
exports.sendPushNotification = functions.https.onCall(async (data, context) => {
  // Verify authentication
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be authenticated');
  }
  
  // Verify distributor role
  const userSnapshot = await db.ref(`users/${context.auth.uid}`).once('value');
  const user = userSnapshot.val();
  
  if (!user || user.role !== 'distributor') {
    throw new functions.https.HttpsError('permission-denied', 'Only distributors can send notifications');
  }
  
  const { tokens, title, body, data: notificationData } = data;
  
  if (!tokens || !Array.isArray(tokens) || tokens.length === 0) {
    throw new functions.https.HttpsError('invalid-argument', 'Tokens array required');
  }
  
  await sendToTokens(tokens, title, body, notificationData);
  
  return { success: true };
});

/**
 * Callable function to mark notification as read
 */
exports.markNotificationRead = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be authenticated');
  }
  
  const { notificationId } = data;
  
  if (!notificationId) {
    throw new functions.https.HttpsError('invalid-argument', 'notificationId required');
  }
  
  // Verify ownership
  const notificationSnapshot = await db.ref(`Notifications/${notificationId}`).once('value');
  const notification = notificationSnapshot.val();
  
  if (!notification || notification.userId !== context.auth.uid) {
    throw new functions.https.HttpsError('permission-denied', 'Not your notification');
  }
  
  await db.ref(`Notifications/${notificationId}`).update({ read: true });
  
  return { success: true };
});

/**
 * Callable function to get unread notification count
 */
exports.getUnreadNotificationCount = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be authenticated');
  }
  
  const snapshot = await db.ref('Notifications')
    .orderByChild('userId')
    .equalTo(context.auth.uid)
    .once('value');
  
  let unreadCount = 0;
  snapshot.forEach(child => {
    if (!child.val().read) unreadCount++;
  });
  
  return { unreadCount };
});

// ============================================================================
// SCHEDULED FUNCTIONS
// ============================================================================

/**
 * Daily cleanup of old notifications (older than 30 days)
 */
exports.cleanupOldNotifications = functions.pubsub
  .schedule('0 3 * * *') // Run at 3 AM daily
  .timeZone('UTC')
  .onRun(async () => {
    const thirtyDaysAgo = Date.now() - (30 * 24 * 60 * 60 * 1000);
    
    const snapshot = await db.ref('Notifications')
      .orderByChild('timestamp')
      .endAt(thirtyDaysAgo)
      .once('value');
    
    const updates = {};
    snapshot.forEach(child => {
      updates[`/Notifications/${child.key}`] = null;
    });
    
    if (Object.keys(updates).length > 0) {
      await db.ref().update(updates);
      console.log(`Cleaned up ${Object.keys(updates).length} old notifications`);
    }
    
    return null;
  });

/**
 * Weekly inventory report for distributors
 */
exports.weeklyInventoryReport = functions.pubsub
  .schedule('0 9 * * 1') // Every Monday at 9 AM
  .timeZone('UTC')
  .onRun(async () => {
    const snapshot = await db.ref('Inventory').once('value');
    
    const lowStockItems = [];
    const outOfStockItems = [];
    
    snapshot.forEach(child => {
      const item = child.val();
      const available = item.availableQuantity || 0;
      const threshold = item.threshold || 10;
      
      if (available <= 0) {
        outOfStockItems.push(`${item.name} (${available} ${item.unit})`);
      } else if (available <= threshold) {
        lowStockItems.push(`${item.name} (${available} ${item.unit})`);
      }
    });
    
    if (lowStockItems.length > 0 || outOfStockItems.length > 0) {
      let message = 'Weekly Inventory Report:\n\n';
      
      if (outOfStockItems.length > 0) {
        message += `❌ Out of Stock (${outOfStockItems.length}):\n${outOfStockItems.join('\n')}\n\n`;
      }
      
      if (lowStockItems.length > 0) {
        message += `⚠️ Low Stock (${lowStockItems.length}):\n${lowStockItems.join('\n')}`;
      }
      
      await sendToTopic('distributors', 'Weekly Inventory Report', message, {
        type: 'WEEKLY_REPORT',
        action: 'VIEW_INVENTORY'
      });
    }
    
    return null;
  });

// ============================================================================
// FCM TOKEN MANAGEMENT
// ============================================================================

/**
 * Callable function to register FCM token
 */
exports.registerFcmToken = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be authenticated');
  }
  
  const { token } = data;
  
  if (!token) {
    throw new functions.https.HttpsError('invalid-argument', 'Token required');
  }
  
  await db.ref(`users/${context.auth.uid}`).update({
    fcmToken: token,
    fcmTokenUpdatedAt: admin.database.ServerValue.TIMESTAMP
  });
  
  // Subscribe to role-based topic
  const userSnapshot = await db.ref(`users/${context.auth.uid}`).once('value');
  const user = userSnapshot.val();
  
  if (user && user.role) {
    try {
      await messaging.subscribeToTopic(token, user.role + 's'); // distributors, survivors
      console.log(`Subscribed ${context.auth.uid} to topic: ${user.role}s`);
    } catch (error) {
      console.error('Error subscribing to topic:', error);
    }
  }
  
  return { success: true };
});

/**
 * Callable function to unregister FCM token
 */
exports.unregisterFcmToken = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be authenticated');
  }
  
  const { token } = data;
  
  if (token) {
    // Unsubscribe from topics
    try {
      await messaging.unsubscribeFromTopic(token, 'distributors');
      await messaging.unsubscribeFromTopic(token, 'survivors');
    } catch (error) {
      console.error('Error unsubscribing from topics:', error);
    }
  }
  
  await db.ref(`users/${context.auth.uid}`).update({
    fcmToken: null
  });
  
  return { success: true };
});