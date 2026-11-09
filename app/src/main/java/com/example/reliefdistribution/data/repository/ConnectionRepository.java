package com.example.reliefdistribution.data.repository;

import androidx.lifecycle.LiveData;

/**
 * Repository interface for observing realtime network and Firebase connection state.
 */
public interface ConnectionRepository {

    /**
     * LiveData stream emitting true when connected to Firebase Realtime Database,
     * false when offline or transitioning.
     */
    LiveData<Boolean> isConnected();

    /**
     * Begins listening to connection state changes on Firebase /.info/connected.
     */
    void startMonitoring();

    /**
     * Unregisters the connection listener to prevent memory leaks.
     */
    void stopMonitoring();
}

