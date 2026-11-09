package com.example.reliefdistribution;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.reliefdistribution.data.model.LocationTelemetry;
import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.data.repository.LocationRepository;
import com.example.reliefdistribution.data.repository.RepositoryCallback;
import com.example.reliefdistribution.ui.map.MapViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class MapViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private LocationRepository locationRepository;

    @Captor
    private ArgumentCaptor<RepositoryCallback<Void>> callbackCaptor;

    private MapViewModel mapViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapViewModel = new MapViewModel(locationRepository);
    }

    @Test
    public void setDistributeMode_updatesLiveData() {
        mapViewModel.setDistributeMode(true);
        assertTrue(Boolean.TRUE.equals(mapViewModel.getDistributeMode().getValue()));
    }

    @Test
    public void updateLocation_callsRepository_emitsSuccess() {
        String userId = "user-123";
        double lat = 23.8103;
        double lng = 90.4125;

        mapViewModel.updateLocation(userId, lat, lng);

        verify(locationRepository).updateLocation(eq(userId), eq(lat), eq(lng), callbackCaptor.capture());
        callbackCaptor.getValue().onSuccess(null);

        Resource<Void> state = mapViewModel.getLocationSyncState().getValue();
        assertNotNull(state);
        assertEquals(Resource.Status.SUCCESS, state.getStatus());
    }

    @Test
    public void updateLocation_failure_emitsError() {
        String userId = "user-123";
        double lat = 23.8103;
        double lng = 90.4125;

        mapViewModel.updateLocation(userId, lat, lng);

        verify(locationRepository).updateLocation(eq(userId), eq(lat), eq(lng), callbackCaptor.capture());
        callbackCaptor.getValue().onError(new Exception("Network timeout"));

        Resource<Void> state = mapViewModel.getLocationSyncState().getValue();
        assertNotNull(state);
        assertEquals(Resource.Status.ERROR, state.getStatus());
        assertEquals("Network timeout", state.getMessage());
    }

    @Test
    public void updateTelemetry_callsRepository_emitsSuccess() {
        LocationTelemetry telemetry = new LocationTelemetry("u1", 23.8, 90.4, 1000L, 5.0f, 1.0f, true);

        mapViewModel.updateTelemetry(telemetry);

        verify(locationRepository).updateTelemetry(eq(telemetry), callbackCaptor.capture());
        callbackCaptor.getValue().onSuccess(null);

        Resource<Void> state = mapViewModel.getLocationSyncState().getValue();
        assertNotNull(state);
        assertEquals(Resource.Status.SUCCESS, state.getStatus());
    }

    @Test
    public void setupDisconnectCleanup_callsRepository() {
        mapViewModel.setupDisconnectCleanup("user-123");
        verify(locationRepository).setupOnDisconnectCleanup(eq("user-123"), eq(null));
    }

    @Test
    public void removeLocation_cancelsCleanupAndCallsRepository() {
        String userId = "user-123";

        mapViewModel.removeLocation(userId);

        verify(locationRepository).cancelOnDisconnectCleanup(eq(userId));
        verify(locationRepository).removeLocation(eq(userId), callbackCaptor.capture());
        callbackCaptor.getValue().onSuccess(null);

        Resource<Void> state = mapViewModel.getLocationSyncState().getValue();
        assertNotNull(state);
        assertEquals(Resource.Status.SUCCESS, state.getStatus());
    }
}
