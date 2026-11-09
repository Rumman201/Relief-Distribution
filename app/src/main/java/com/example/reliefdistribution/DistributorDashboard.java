package com.example.reliefdistribution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliefdistribution.data.repository.ConnectionRepository;
import com.example.reliefdistribution.data.repository.FirebaseConnectionRepository;
import com.example.reliefdistribution.databinding.ActivityDistributorDashboardBinding;

/**
 * DistributorDashboard provides relief coordination controls, GPS tracking toggle,
 * live connectivity status, and emergency hotline access for relief distributors.
 * Upgraded in Phase 4 with Material 3 styling, 48dp touch targets, and entrance motion.
 */
public class DistributorDashboard extends AppCompatActivity {

    private ActivityDistributorDashboardBinding binding;
    private ConnectionRepository connectionRepository;
    private boolean isDistributeModeOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDistributorDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Animate entrance
        binding.cardDistributeMode.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        binding.lEmergency.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        connectionRepository = new FirebaseConnectionRepository();
        connectionRepository.isConnected().observe(this, isConnected -> {
            if (binding.tvConnectionStatus != null) {
                if (Boolean.TRUE.equals(isConnected)) {
                    binding.tvConnectionStatus.setText(R.string.status_online);
                    binding.tvConnectionStatus.setTextColor(getColor(R.color.status_online));
                    binding.tvConnectionStatus.setBackgroundResource(R.drawable.badge_online);
                } else {
                    binding.tvConnectionStatus.setText(R.string.status_offline);
                    binding.tvConnectionStatus.setTextColor(getColor(R.color.status_offline));
                    binding.tvConnectionStatus.setBackgroundResource(R.drawable.badge_offline);
                }
            }
        });

        binding.switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isDistributeModeOn = isChecked;
            if (isChecked) {
                Toast.makeText(this, "Distribute Mode ON - Location broadcast enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Distribute Mode OFF", Toast.LENGTH_SHORT).show();
            }
        });

        binding.lTracking.setOnClickListener(view -> {
            Intent intent = new Intent(DistributorDashboard.this, MapActivity.class);
            intent.putExtra("distributeMode", isDistributeModeOn);
            startActivity(intent);
        });

        // Safe emergency dialing without requiring dangerous CALL_PHONE permission
        binding.lEmergency.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999"));
            startActivity(intent);
        });

        binding.ivProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(DistributorDashboard.this, Profile.class);
            startActivity(intent);
        });

        binding.ivHomeIcon.setOnClickListener(view -> {
            Toast.makeText(this, "Home Dashboard", Toast.LENGTH_SHORT).show();
        });

        binding.ivChatIcon.setOnClickListener(view -> {
            Toast.makeText(this, "Emergency Dispatch Channel: Broadcast live alerts", Toast.LENGTH_SHORT).show();
        });

        binding.ivNotificationIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        if (binding.lFeatures != null) {
            binding.lFeatures.setOnClickListener(view -> {
                String[] operations = {"Manage Relief Requests", "Inventory & Cargo Depot"};
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                        .setTitle("Relief Operations")
                        .setItems(operations, (dialog, which) -> {
                            if (which == 0) {
                                startActivity(new Intent(DistributorDashboard.this, ManageRequestsActivity.class));
                            } else {
                                startActivity(new Intent(DistributorDashboard.this, InventoryManagementActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        if (binding.lSettings != null) {
            binding.lSettings.setOnClickListener(view -> {
                Intent intent = new Intent(DistributorDashboard.this, Profile.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (connectionRepository != null) {
            connectionRepository.startMonitoring();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectionRepository != null) {
            connectionRepository.stopMonitoring();
        }
    }
}
