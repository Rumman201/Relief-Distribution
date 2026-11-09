package com.example.reliefdistribution.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Realtime Database implementation of ConnectionRepository monitoring /.info/connected.
 */
public class FirebaseConnectionRepository implements ConnectionRepository {

    private final DatabaseReference connectedRef;
    private final MutableLiveData<Boolean> connectedLiveData = new MutableLiveData<>(false);
    private ValueEventListener connectionListener;

    public FirebaseConnectionRepository() {
        this(FirebaseDatabase.getInstance().getReference(".info/connected"));
    }

    public FirebaseConnectionRepository(@NonNull DatabaseReference connectedRef) {
        this.connectedRef = connectedRef;
    }

    @Override
    public LiveData<Boolean> isConnected() {
        return connectedLiveData;
    }

    @Override
    public void startMonitoring() {
        if (connectionListener != null) {
            return; // Already monitoring
        }

        connectionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                connectedLiveData.postValue(Boolean.TRUE.equals(connected));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                connectedLiveData.postValue(false);
            }
        };

        connectedRef.addValueEventListener(connectionListener);
    }

    @Override
    public void stopMonitoring() {
        if (connectionListener != null) {
            connectedRef.removeEventListener(connectionListener);
            connectionListener = null;
        }
    }
}

