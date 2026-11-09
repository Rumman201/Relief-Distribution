package com.example.reliefdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliefdistribution.databinding.ActivityWelcomeBinding;

/**
 * WelcomeActivity welcomes new or returning users and provides routes to Login and Registration.
 * Upgraded in Phase 4 with Material 3 styling and entrance choreography.
 */
public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply smooth entrance animations
        binding.cardWelcomeIcon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));
        binding.btLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));
        binding.btRegister.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));

        binding.btLogin.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        });

        binding.btRegister.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
            finish();
        });
    }
}
