package com.example.reliefdistribution.data.repository;

import androidx.annotation.NonNull;

import com.example.reliefdistribution.data.model.LocationTelemetry;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Production implementation of LocationRepository backed by Firebase Realtime Database.
 * Supports typed telemetry validation and disaster-zone onDisconnect teardown.
 */
public class FirebaseLocationRepository implements LocationRepository {

    private final DatabaseReference databaseReference;

    public FirebaseLocationRepository() {
        this(FirebaseDatabase.getInstance().getReference("Users"));
    }

    public FirebaseLocationRepository(@NonNull DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    @Override
    public void updateLocation(String userId, double latitude, double longitude, RepositoryCallback<Void> callback) {
        if (userId == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("User ID cannot be null"));
            }
            return;
        }

        try {
            LocationTelemetry telemetry = new LocationTelemetry(
                    userId, latitude, longitude, System.currentTimeMillis(), 0.0f, 0.0f, true
            );
            updateTelemetry(telemetry, callback);
        } catch (IllegalArgumentException e) {
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    @Override
    public void updateTelemetry(@NonNull LocationTelemetry telemetry, RepositoryCallback<Void> callback) {
        if (telemetry.getUserId() == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("Telemetry User ID cannot be null"));
            }
            return;
        }

        Map<String, Object> locationMap = telemetry.toMap();
        databaseReference.child(telemetry.getUserId()).child("location").setValue(locationMap)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onError(e);
                    }
                });
    }

    @Override
    public void removeLocation(String userId, RepositoryCallback<Void> callback) {
        if (userId == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("User ID cannot be null"));
            }
            return;
        }

        databaseReference.child(userId).child("location").removeValue()
                .addOnCompleteListener(task -> {
                    if (callback != null) {
                        if (task.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            Exception ex = task.getException() != null ? task.getException() : new Exception("Failed to remove location");
                            callback.onError(ex);
                        }
                    }
                });
    }

    @Override
    public void setupOnDisconnectCleanup(String userId, RepositoryCallback<Void> callback) {
        if (userId == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("User ID cannot be null"));
            }
            return;
        }

        databaseReference.child(userId).child("location").onDisconnect().removeValue(
                (error, ref) -> {
                    if (callback != null) {
                        if (error == null) {
                            callback.onSuccess(null);
                        } else {
                            callback.onError(error.toException());
                        }
                    }
                }
        );
    }

    @Override
    public void cancelOnDisconnectCleanup(String userId) {
        if (userId != null) {
            databaseReference.child(userId).child("location").onDisconnect().cancel();
        }
    }
}
