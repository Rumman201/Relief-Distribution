package com.example.reliefdistribution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliefdistribution.data.repository.ConnectionRepository;
import com.example.reliefdistribution.data.repository.FirebaseConnectionRepository;
import com.example.reliefdistribution.databinding.ActivitySurvivorDashboardBinding;

/**
 * SurvivorDashboard provides relief point discovery, emergency assistance dialing,
 * live connection state, and disaster information for affected citizens.
 * Upgraded in Phase 4 with Material 3 styling, 48dp touch targets, and entrance motion.
 */
public class SurvivorDashboard extends AppCompatActivity {

    private ActivitySurvivorDashboardBinding binding;
    private ConnectionRepository connectionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurvivorDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Animate entrance
        binding.linearLayout4.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

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

        // Safe emergency dialing without requiring dangerous CALL_PHONE permission
        binding.linearLayout4.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999"));
            startActivity(intent);
        });

        // Information advisory screen
        binding.linearLayout2.setOnClickListener(view -> {
            Intent intent = new Intent(this, Informations.class);
            startActivity(intent);
        });

        // Emergency assistance request
        binding.linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateReliefRequestActivity.class);
            startActivity(intent);
        });

        // Open Map in survivor mode
        binding.linearLayout3.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("distributeMode", false);
            startActivity(intent);
        });

        // My submitted relief requests
        binding.linearLayout5.setOnClickListener(view -> {
            Intent intent = new Intent(this, MyReliefRequestsActivity.class);
            startActivity(intent);
        });

        // Settings / Profile
        binding.linearLayout6.setOnClickListener(view -> {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        });

        binding.ivProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(SurvivorDashboard.this, Profile.class);
            startActivity(intent);
        });

        binding.ivHomeIcon.setOnClickListener(view -> {
            Toast.makeText(this, "Home Dashboard", Toast.LENGTH_SHORT).show();
        });

        binding.ivChatIcon.setOnClickListener(view -> {
            Toast.makeText(this, "Emergency Community Channel: Broadcast live alerts", Toast.LENGTH_SHORT).show();
        });

        binding.ivNotificationIcon.setOnClickListener(view -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });
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
