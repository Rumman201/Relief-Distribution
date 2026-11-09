package com.example.reliefdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.databinding.ActivityStartBinding;
import com.example.reliefdistribution.ui.auth.AuthViewModel;

/**
 * StartActivity serves as the launcher entry point, verifying existing user sessions
 * and routing directly to the appropriate role dashboard or welcome screen.
 * Upgraded in Phase 4 with Material 3 styling and entrance motion.
 */
public class StartActivity extends AppCompatActivity {

    private ActivityStartBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply entrance motion
        binding.cardHero.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        binding.btStart.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        authViewModel.getSessionState().observe(this, resource -> {
            if (resource == null) return;

            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                User user = resource.getData();
                Intent intent;
                if ("Distributor".equals(user.getRole())) {
                    intent = new Intent(StartActivity.this, DistributorDashboard.class);
                } else {
                    intent = new Intent(StartActivity.this, SurvivorDashboard.class);
                }
                startActivity(intent);
                finish();
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                // If session is invalid or user profile not found, stay on start screen
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Trigger automated session verification on start
        authViewModel.checkSession();
    }
}
