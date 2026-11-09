package com.example.reliefdistribution.data.repository;

import com.example.reliefdistribution.data.model.LocationTelemetry;

/**
 * Repository interface defining user location and IoT GPS telemetry operations.
 */
public interface LocationRepository {

    /**
     * Publishes current GPS coordinates to Firebase Realtime Database.
     *
     * @param userId    User UID.
     * @param latitude  Current latitude.
     * @param longitude Current longitude.
     * @param callback  Callback reporting success or failure.
     */
    void updateLocation(String userId, double latitude, double longitude, RepositoryCallback<Void> callback);

    /**
     * Publishes validated, typed LocationTelemetry to the cloud backend.
     *
     * @param telemetry Validated location telemetry model.
     * @param callback  Callback reporting success or failure.
     */
    void updateTelemetry(LocationTelemetry telemetry, RepositoryCallback<Void> callback);

    /**
     * Removes the broadcasted location entry from Firebase Realtime Database.
     *
     * @param userId   User UID.
     * @param callback Callback reporting success or failure.
     */
    void removeLocation(String userId, RepositoryCallback<Void> callback);

    /**
     * Configures Firebase onDisconnect() hook to automatically tear down
     * the broadcasted location marker when the device loses cellular connection or power.
     *
     * @param userId   User UID.
     * @param callback Callback reporting hook registration status.
     */
    void setupOnDisconnectCleanup(String userId, RepositoryCallback<Void> callback);

    /**
     * Cancels an active onDisconnect() hook.
     *
     * @param userId User UID.
     */
    void cancelOnDisconnectCleanup(String userId);
}
