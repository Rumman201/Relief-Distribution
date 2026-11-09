package com.example.reliefdistribution.ui.map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.reliefdistribution.data.model.LocationTelemetry;
import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.data.repository.FirebaseLocationRepository;
import com.example.reliefdistribution.data.repository.LocationRepository;
import com.example.reliefdistribution.data.repository.RepositoryCallback;

/**
 * ViewModel managing GPS distribution mode, IoT telemetry streaming,
 * and Realtime Database location synchronization with disconnect protection.
 */
public class MapViewModel extends ViewModel {

    private final LocationRepository locationRepository;

    private final MutableLiveData<Boolean> distributeMode = new MutableLiveData<>(false);
    private final MutableLiveData<Resource<Void>> locationSyncState = new MutableLiveData<>();

    /**
     * Default constructor for Android ViewModelProvider.
     */
    public MapViewModel() {
        this(new FirebaseLocationRepository());
    }

    /**
     * Constructor for dependency injection and unit testing.
     *
     * @param locationRepository Location repository instance.
     */
    public MapViewModel(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LiveData<Boolean> getDistributeMode() {
        return distributeMode;
    }

    public LiveData<Resource<Void>> getLocationSyncState() {
        return locationSyncState;
    }

    /**
     * Updates distribution broadcast mode.
     *
     * @param enabled True to enable location broadcasting.
     */
    public void setDistributeMode(boolean enabled) {
        distributeMode.setValue(enabled);
    }

    /**
     * Updates coordinates in Realtime Database when in distribute mode.
     *
     * @param userId    User UID.
     * @param latitude  Current latitude.
     * @param longitude Current longitude.
     */
    public void updateLocation(String userId, double latitude, double longitude) {
        locationSyncState.setValue(Resource.loading(null));

        locationRepository.updateLocation(userId, latitude, longitude, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                locationSyncState.setValue(Resource.success(null));
            }

            @Override
            public void onError(Exception exception) {
                String errorMsg = exception.getMessage() != null ? exception.getMessage() : "Failed to update location";
                locationSyncState.setValue(Resource.error(errorMsg, null));
            }
        });
    }

    /**
     * Streams validated IoT GPS telemetry to cloud backend.
     */
    public void updateTelemetry(LocationTelemetry telemetry) {
        locationSyncState.setValue(Resource.loading(null));

        locationRepository.updateTelemetry(telemetry, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                locationSyncState.setValue(Resource.success(null));
            }

            @Override
            public void onError(Exception exception) {
                String errorMsg = exception.getMessage() != null ? exception.getMessage() : "Failed to update telemetry";
                locationSyncState.setValue(Resource.error(errorMsg, null));
            }
        });
    }

    /**
     * Configures cloud automated teardown if connection is abruptly dropped.
     */
    public void setupDisconnectCleanup(String userId) {
        locationRepository.setupOnDisconnectCleanup(userId, null);
    }

    /**
     * Removes the broadcasted user location from Realtime Database.
     *
     * @param userId User UID.
     */
    public void removeLocation(String userId) {
        locationRepository.cancelOnDisconnectCleanup(userId);
        locationRepository.removeLocation(userId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                locationSyncState.setValue(Resource.success(null));
            }

            @Override
            public void onError(Exception exception) {
                String errorMsg = exception.getMessage() != null ? exception.getMessage() : "Failed to remove location";
                locationSyncState.setValue(Resource.error(errorMsg, null));
            }
        });
    }
}
