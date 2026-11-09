package com.example.reliefdistribution;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliefdistribution.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Profile activity displays user session info, role status, and handles session termination.
 * Upgraded in Phase 4 with Material 3 styling, dynamic identity cards, and smooth motion.
 */
public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply smooth entrance motion
        binding.cardIdentity.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        binding.cardDetails.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String fName = preferences.getString("registeredFName", "");
        String lName = preferences.getString("registeredLName", "");
        String email = preferences.getString("registeredEmail", "Not Provided");
        String phone = preferences.getString("registeredPhone", "Not Provided");
        String role = preferences.getString("registeredRole", "Survivor");
        String userId = preferences.getString("registeredUserId", "N/A");

        // Format name
        String fullName = (fName + " " + lName).trim();
        if (TextUtils.isEmpty(fullName)) {
            fullName = !TextUtils.isEmpty(email) ? email : "Relief User";
        }
        binding.tvUserName.setText(fullName);

        // Format initials
        String initials = "";
        if (!TextUtils.isEmpty(fName)) initials += fName.substring(0, 1).toUpperCase();
        if (!TextUtils.isEmpty(lName)) initials += lName.substring(0, 1).toUpperCase();
        if (TextUtils.isEmpty(initials)) initials = "RD";
        binding.tvAvatarInitials.setText(initials);

        // Set role & credentials
        binding.tvUserRole.setText(role);
        binding.tvUserEmail.setText(email);
        binding.tvUserPhone.setText(!TextUtils.isEmpty(phone) ? phone : "Not Provided");
        binding.tvUserId.setText(userId);

        binding.btnLogout.setOnClickListener(view -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Profile.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
