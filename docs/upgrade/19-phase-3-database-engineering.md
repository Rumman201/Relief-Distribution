# Phase 3 Database Engineering & Data Integrity

**Project**: Relief Distribution Android Application  
**Package**: `com.example.reliefdistribution`  
**Phase**: Phase 3 of 10  
**Status**: ACTIVE  
**Author**: Principal Database Architect  

---

## 1. Database Architecture & Engine

- **Primary Database**: Google Firebase Realtime Database (JSON tree document store).
- **Offline Cache**: Local LevelDB storage managed by Google Play Services Firebase client library.
- **Persistence Mode**: Disk persistence enabled globally on cold boot via [`ReliefApplication`](file:///H:/Projects/Relief-Distribution-App/app/src/main/java/com/example/reliefdistribution/ReliefApplication.java):
  ```java
  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  ```

---

## 2. Declarative Security Rules Implementation (`database.rules.json`)

```json
{
  "rules": {
    "Users": {
      "$userId": {
        ".read": "auth != null",
        ".write": "auth != null && auth.uid === $userId",
        ".validate": "newData.hasChildren(['firstName', 'lastName', 'email', 'phone', 'role']) && newData.child('role').val().matches(/^(Distributor|Survivor)$/)",
        "location": {
          ".read": "auth != null",
          ".write": "auth != null && auth.uid === $userId",
          ".validate": "newData.hasChildren(['latitude', 'longitude', 'timestamp']) && newData.child('latitude').isNumber() && newData.child('latitude').val() >= -90.0 && newData.child('latitude').val() <= 90.0 && newData.child('longitude').isNumber() && newData.child('longitude').val() >= -180.0 && newData.child('longitude').val() <= 180.0 && newData.child('timestamp').isNumber()"
        }
      }
    },
    "ReliefRequests": {
      ".read": "auth != null",
      ".indexOn": ["userId", "status", "createdAt"],
      "$requestId": {
        ".write": "auth != null && (data.val() == null && newData.child('userId').val() === auth.uid || data.child('userId').val() === auth.uid || root.child('Users').child(auth.uid).child('role').val() === 'Distributor')",
        ".validate": "newData.hasChildren(['requestId', 'userId', 'survivorName', 'phone', 'emergencyType', 'status']) && newData.child('status').val().matches(/^(PENDING|ACCEPTED|EN_ROUTE|FULFILLED|CANCELLED)$/)"
      }
    },
    "DisasterAlerts": {
      ".read": "auth != null",
      ".indexOn": ["isActive", "issuedAt"],
      "$alertId": {
        ".write": "auth != null && root.child('Users').child(auth.uid).child('role').val() === 'Distributor'",
        ".validate": "newData.hasChildren(['alertId', 'title', 'severity'])"
      }
    }
  }
}
```

---

## 3. Transaction Safety & Concurrency Control

In high-concurrency disaster scenarios, multiple relief distributors may attempt to claim the same pending survivor aid request simultaneously. To prevent duplicate assignments or race conditions, [`FirebaseReliefRequestRepository.updateRequestStatus()`](file:///H:/Projects/Relief-Distribution-App/app/src/main/java/com/example/reliefdistribution/data/repository/FirebaseReliefRequestRepository.java) executes atomic updates using Firebase `runTransaction`:

```java
requestsRef.child(requestId).runTransaction(new Transaction.Handler() {
    @NonNull
    @Override
    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
        ReliefRequest request = currentData.getValue(ReliefRequest.class);
        if (request == null) return Transaction.abort();

        // Enforce valid state machine progression
        if (!ReliefRequest.isValidTransition(request.getStatus(), newStatus)) {
            return Transaction.abort();
        }

        request.transitionTo(newStatus, distributorId);
        currentData.setValue(request.toMap());
        return Transaction.success(currentData);
    }
    ...
});
```

If another distributor updates the request while a client's transaction is pending, Firebase automatically rolls back and re-executes the transaction with the latest server state, aborting if the request is already claimed.

---

## 4. Query Indexing Strategy

Indexing rules (`.indexOn`) have been declared to prevent full database tree scans during filtered queries:
- `/ReliefRequests`: Indexed by `userId`, `status`, and `createdAt` for survivor dashboard queries and distributor dispatch feeds.
- `/DisasterAlerts`: Indexed by `isActive` and `issuedAt` for filtering active emergency bulletins.

