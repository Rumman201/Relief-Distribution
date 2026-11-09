package com.example.reliefdistribution;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * ReliefApplication initializes global application services on process startup.
 * Configures Firebase Realtime Database disk persistence for resilient disaster zone operations.
 */
public class ReliefApplication extends Application {

    private static final String TAG = "ReliefApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        initializeFirebasePersistence();
    }

    /**
     * Enables offline disk caching for Firebase Realtime Database.
     * Must be invoked before any FirebaseDatabase reference is instantiated.
     */
    protected void initializeFirebasePersistence() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            Log.i(TAG, "Firebase Realtime Database offline persistence successfully enabled.");
        } catch (Exception e) {
            // Catches DatabaseException if already configured (e.g. in test suites or secondary processes)
            Log.w(TAG, "Firebase persistence initialization note: " + e.getMessage());
        }
    }
}

