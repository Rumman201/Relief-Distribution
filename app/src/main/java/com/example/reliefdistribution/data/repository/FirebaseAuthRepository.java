package com.example.reliefdistribution.data.repository;

import androidx.annotation.NonNull;

import com.example.reliefdistribution.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Production implementation of AuthRepository backed by Firebase Authentication
 * and Firebase Realtime Database.
 */
public class FirebaseAuthRepository implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;

    /**
     * Default constructor utilizing Firebase SDK singletons.
     */
    public FirebaseAuthRepository() {
        this(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance().getReference("Users"));
    }

    /**
     * Dependency injection constructor for unit testing and custom configurations.
     */
    public FirebaseAuthRepository(@NonNull FirebaseAuth firebaseAuth, @NonNull DatabaseReference databaseReference) {
        this.firebaseAuth = firebaseAuth;
        this.databaseReference = databaseReference;
    }

    @Override
    public void login(String email, String password, RepositoryCallback<String> callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            callback.onSuccess(currentUser.getUid());
                        } else {
                            callback.onError(new IllegalStateException("User session not found after sign in"));
                        }
                    } else {
                        Exception ex = task.getException() != null ? task.getException() : new Exception("Authentication failed");
                        callback.onError(ex);
                    }
                });
    }

    @Override
    public void register(String firstName, String lastName, String email, String phone,
                         String role, String password, RepositoryCallback<User> callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            User newUser = new User(userId, firstName, lastName, email, phone, role);
                            databaseReference.child(userId).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(newUser))
                                    .addOnFailureListener(callback::onError);
                        } else {
                            callback.onError(new IllegalStateException("User session not found after account creation"));
                        }
                    } else {
                        Exception ex = task.getException() != null ? task.getException() : new Exception("Registration failed");
                        callback.onError(ex);
                    }
                });
    }

    @Override
    public void getUserProfile(String userId, RepositoryCallback<User> callback) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onError(new NullPointerException("User profile data is null in database"));
                    }
                } else {
                    callback.onError(new IllegalArgumentException("User profile not found in database"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void checkUserSession(RepositoryCallback<User> callback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            getUserProfile(currentUser.getUid(), callback);
        } else {
            callback.onError(new IllegalStateException("No active user session"));
        }
    }

    @Override
    public void signOut() {
        firebaseAuth.signOut();
    }

    @Override
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}

