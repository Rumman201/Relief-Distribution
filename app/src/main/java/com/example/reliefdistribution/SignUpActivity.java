package com.example.reliefdistribution;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.reliefdistribution.data.model.Resource;
import com.example.reliefdistribution.databinding.ActivitySignUpBinding;
import com.example.reliefdistribution.ui.auth.AuthViewModel;

/**
 * SignUpActivity coordinates user registration, field validation, and profile provisioning.
 * Upgraded in Phase 4 with Material 3 styling, floating labels, and entrance motion.
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply smooth entrance motion
        binding.cardSignUpForm.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        binding.btSignUp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom));

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_items,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdownMenu.setAdapter(adapter);

        binding.btSignUp.setOnClickListener(v -> {
            String fName = binding.etFName.getText() != null ? binding.etFName.getText().toString().trim() : "";
            String lName = binding.etLName.getText() != null ? binding.etLName.getText().toString().trim() : "";
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String phone = binding.etPhoneNumber.getText() != null ? binding.etPhoneNumber.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
            String confirmPassword = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString() : "";
            String selectedRole = binding.dropdownMenu.getSelectedItem() != null
                    ? binding.dropdownMenu.getSelectedItem().toString()
                    : "Survivor";

            if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName) || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.register(fName, lName, email, phone, selectedRole, password);
        });

        authViewModel.getRegisterState().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.getStatus()) {
                case LOADING:
                    binding.btSignUp.setEnabled(false);
                    break;

                case SUCCESS:
                    binding.btSignUp.setEnabled(true);
                    User user = resource.getData();
                    if (user != null) {
                        saveUserData(user);
                        Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if ("Distributor".equals(user.getRole())) {
                            intent = new Intent(SignUpActivity.this, DistributorDashboard.class);
                        } else {
                            intent = new Intent(SignUpActivity.this, SurvivorDashboard.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                    break;

                case ERROR:
                    binding.btSignUp.setEnabled(true);
                    String message = resource.getMessage() != null ? resource.getMessage() : "Registration failed";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void saveUserData(User user) {
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRegistered", true);
        editor.putString("registeredFName", user.getFirstName());
        editor.putString("registeredLName", user.getLastName());
        editor.putString("registeredEmail", user.getEmail());
        editor.putString("registeredPhone", user.getPhone());
        editor.putString("registeredRole", user.getRole());
        editor.putString("registeredUserId", user.getUserId());
        editor.apply();
    }
}
