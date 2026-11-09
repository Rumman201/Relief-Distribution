package com.example.reliefdistribution;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.reliefdistribution.databinding.ActivityMapBinding;
import com.example.reliefdistribution.ui.map.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * MapActivity displays interactive Google Maps and coordinates real-time GPS tracking.
 * Upgraded in Phase 4 with Material 3 styling, live tracking status chip, and 48dp navigation touch targets.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private ActivityMapBinding binding;
    private MapViewModel mapViewModel;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker userMarker;

    private boolean isDistributeModeOn;
    private String userId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding.ivHomeIcon.setOnClickListener(v -> {
            stopLocationUpdates();
            finish();
        });

        binding.ivProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(MapActivity.this, Profile.class);
            startActivity(intent);
        });

        binding.ivChatIcon.setOnClickListener(view -> {
            Toast.makeText(this, "Relief communication coming in Phase 5", Toast.LENGTH_SHORT).show();
        });

        binding.ivNotificationIcon.setOnClickListener(view -> {
            Toast.makeText(this, "No active alerts in this zone", Toast.LENGTH_SHORT).show();
        });

        Intent intent = getIntent();
        isDistributeModeOn = intent.getBooleanExtra("distributeMode", false);
        mapViewModel.setDistributeMode(isDistributeModeOn);

        // Update tracking status badge
        if (binding.tvTrackingBadge != null) {
            if (isDistributeModeOn) {
                binding.tvTrackingBadge.setText(R.string.tracking_broadcasting);
                binding.tvTrackingBadge.setTextColor(getColor(R.color.status_online));
                binding.tvTrackingBadge.setBackgroundResource(R.drawable.badge_online);
            } else {
                binding.tvTrackingBadge.setText(R.string.tracking_browsing);
                binding.tvTrackingBadge.setTextColor(getColor(R.color.primary));
                binding.tvTrackingBadge.setBackgroundResource(R.drawable.badge_role);
            }
        }

        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = preferences.getString("registeredUserId", null);
        email = preferences.getString("registeredEmail", "User");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map fragment is null!");
        }

        mapViewModel.getDistributeMode().observe(this, enabled -> {
            if (Boolean.TRUE.equals(enabled)) {
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        LatLng bangladesh = new LatLng(23.6850, 90.3563);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bangladesh, 10));
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        if (userId != null) {
            mapViewModel.setupDisconnectCleanup(userId);
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null || !isDistributeModeOn) {
                    stopLocationUpdates();
                    return;
                }

                if (locationResult.getLastLocation() == null) {
                    return;
                }

                LatLng userLocation = new LatLng(
                        locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude()
                );

                if (googleMap != null) {
                    if (userMarker != null) {
                        userMarker.setPosition(userLocation);
                        userMarker.setTitle(email);
                    } else {
                        userMarker = googleMap.addMarker(new MarkerOptions().position(userLocation).title(email));
                    }

                    float currentZoom = googleMap.getCameraPosition().zoom;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, currentZoom));
                }

                if (userId != null) {
                    mapViewModel.updateLocation(userId, userLocation.latitude, userLocation.longitude);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        if (userMarker != null) {
            userMarker.remove();
            userMarker = null;
        }

        if (userId != null) {
            mapViewModel.removeLocation(userId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission is required for GPS tracking.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}
