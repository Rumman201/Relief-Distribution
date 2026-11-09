package com.example.reliefdistribution;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.databinding.ActivityLoginBinding;
import com.example.reliefdistribution.ui.auth.AuthViewModel;

/**
 * LoginActivity handles user authentication and role verification.
 * Upgraded in Phase 4 with Material 3 styling, floating labels, and entrance motion.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply smooth entrance motion
        binding.cardLoginForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        binding.btLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_items,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdownMenu.setAdapter(adapter);

        binding.btLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedRole = binding.dropdownMenu.getSelectedItem() != null
                    ? binding.dropdownMenu.getSelectedItem().toString()
                    : "Survivor";

            authViewModel.login(email, password, selectedRole);
        });

        binding.tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        authViewModel.getLoginState().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    binding.btLogin.setEnabled(false);
                    break;

                case SUCCESS:
                    binding.btLogin.setEnabled(true);
                    User user = resource.getData();
                    if (user != null) {
                        saveLoginData(user);
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if ("Distributor".equals(user.getRole())) {
                            intent = new Intent(LoginActivity.this, DistributorDashboard.class);
                        } else {
                            intent = new Intent(LoginActivity.this, SurvivorDashboard.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                    break;

                case ERROR:
                    binding.btLogin.setEnabled(true);
                    String message = resource.getMessage() != null ? resource.getMessage() : "Authentication failed";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void saveLoginData(User user) {
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRegistered", true);
        editor.putString("registeredEmail", user.getEmail());
        editor.putString("registeredUserId", user.getUserId());
        editor.putString("registeredRole", user.getRole());
        editor.putString("registeredFName", user.getFirstName());
        editor.putString("registeredLName", user.getLastName());
        editor.apply();
    }
}
