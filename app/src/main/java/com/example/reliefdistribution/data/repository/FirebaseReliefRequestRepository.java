package com.example.reliefdistribution.data.repository;

import androidx.annotation.NonNull;

import com.example.reliefdistribution.data.model.ReliefRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Production implementation of ReliefRequestRepository with atomic state machine transactions.
 */
public class FirebaseReliefRequestRepository implements ReliefRequestRepository {

    private final DatabaseReference requestsRef;

    public FirebaseReliefRequestRepository() {
        this(FirebaseDatabase.getInstance().getReference("ReliefRequests"));
    }

    public FirebaseReliefRequestRepository(@NonNull DatabaseReference requestsRef) {
        this.requestsRef = requestsRef;
    }

    @Override
    public void createRequest(@NonNull ReliefRequest request, RepositoryCallback<ReliefRequest> callback) {
        String requestId = request.getRequestId();
        if (requestId == null || requestId.isEmpty()) {
            requestId = requestsRef.push().getKey();
            if (requestId == null) {
                if (callback != null) {
                    callback.onError(new IllegalStateException("Failed to generate unique request ID"));
                }
                return;
            }
            request.setRequestId(requestId);
        }

        requestsRef.child(requestId).setValue(request.toMap())
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) {
                        callback.onSuccess(request);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onError(e);
                    }
                });
    }

    @Override
    public void getRequest(String requestId, RepositoryCallback<ReliefRequest> callback) {
        if (requestId == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("Request ID cannot be null"));
            }
            return;
        }

        requestsRef.child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ReliefRequest request = snapshot.getValue(ReliefRequest.class);
                    if (request != null) {
                        if (callback != null) {
                            callback.onSuccess(request);
                        }
                    } else {
                        if (callback != null) {
                            callback.onError(new NullPointerException("Relief request data is null"));
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onError(new IllegalArgumentException("Relief request not found"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onError(error.toException());
                }
            }
        });
    }

    @Override
    public void updateRequestStatus(String requestId, String newStatus, String distributorId, RepositoryCallback<Void> callback) {
        if (requestId == null || newStatus == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("Request ID and status cannot be null"));
            }
            return;
        }

        requestsRef.child(requestId).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                ReliefRequest request = currentData.getValue(ReliefRequest.class);
                if (request == null) {
                    return Transaction.abort();
                }

                if (!ReliefRequest.isValidTransition(request.getStatus(), newStatus)) {
                    return Transaction.abort();
                }

                request.transitionTo(newStatus, distributorId);
                currentData.setValue(request.toMap());
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (callback != null) {
                    if (error != null) {
                        callback.onError(error.toException());
                    } else if (!committed) {
                        callback.onError(new IllegalStateException("Status transition aborted due to conflict or invalid transition"));
                    } else {
                        callback.onSuccess(null);
                    }
                }
            }
        });
    }

    @Override
    public void getRequestsForSurvivor(String userId, RepositoryCallback<List<ReliefRequest>> callback) {
        if (userId == null) {
            if (callback != null) {
                callback.onError(new IllegalArgumentException("User ID cannot be null"));
            }
            return;
        }

        requestsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReliefRequest> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ReliefRequest req = child.getValue(ReliefRequest.class);
                    if (req != null) {
                        list.add(req);
                    }
                }
                if (callback != null) {
                    callback.onSuccess(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onError(error.toException());
                }
            }
        });
    }

    @Override
    public void getPendingRequests(RepositoryCallback<List<ReliefRequest>> callback) {
        requestsRef.orderByChild("status").equalTo(ReliefRequest.STATUS_PENDING).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReliefRequest> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ReliefRequest req = child.getValue(ReliefRequest.class);
                    if (req != null) {
                        list.add(req);
                    }
                }
                if (callback != null) {
                    callback.onSuccess(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) {
                    callback.onError(error.toException());
                }
            }
        });
    }
}

