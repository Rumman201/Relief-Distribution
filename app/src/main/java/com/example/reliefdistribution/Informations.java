package com.example.reliefdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliefdistribution.databinding.ActivityInformationsBinding;

/**
 * Informations activity provides disaster preparedness advisories and safety guides.
 * Upgraded in Phase 4 with Material 3 styling, structured crisis advisories, and 48dp touch targets.
 */
public class Informations extends AppCompatActivity {

    private ActivityInformationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply smooth entrance motion
        binding.tvInformations.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        binding.ivHomeIcon.setOnClickListener(v -> {
            startActivity(new Intent(Informations.this, SurvivorDashboard.class));
            finish();
        });

        binding.ivProfileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(Informations.this, Profile.class);
            startActivity(intent);
        });

        binding.ivChatIcon.setOnClickListener(view -> {
            // Placeholder for community advisory chat in Phase 5
        });

        binding.ivNotificationIcon.setOnClickListener(view -> {
            // Placeholder for real-time safety bulletins in Phase 5
        });
    }
}
