package com.example.reliefdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reliefdistribution.data.ai.ReliefTriageAdvisor;
import com.example.reliefdistribution.data.util.SecuritySanitizer;
import com.example.reliefdistribution.databinding.ActivityCreateReliefRequestBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

/**
 * Activity for survivors to request emergency relief supplies.
 * Modernized in Phase 10.1 with ViewBinding, input sanitization, and MapActivity integration.
 */
public class CreateReliefRequestActivity extends AppCompatActivity {

    private ActivityCreateReliefRequestBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;
    private LatLng selectedLocation;
    private String selectedAddress = "";

    private static final int LOCATION_PICKER_REQUEST = 1001;

    private final String[] categories = {"Food", "Water", "Medicine", "Clothing", "Shelter", "Hygiene", "Other"};
    private final String[] priorities = {"Low", "Medium", "High", "Critical"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateReliefRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReliefRequests");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        setupAdapters();
        setupClickListeners();
    }

    private void setupAdapters() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        binding.actvCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, priorities);
        binding.actvPriority.setAdapter(priorityAdapter);

        binding.actvCategory.setOnItemClickListener((parent, view, position, id) ->
                binding.actvCategory.setError(null));

        binding.actvPriority.setOnItemClickListener((parent, view, position, id) ->
                binding.actvPriority.setError(null));
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("PICK_LOCATION", true);
            startActivityForResult(intent, LOCATION_PICKER_REQUEST);
        });

        binding.btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String category = binding.actvCategory.getText().toString().trim();
        String priority = binding.actvPriority.getText().toString().trim();
        String itemName = binding.etItemName.getText() != null ? binding.etItemName.getText().toString().trim() : "";
        String quantityStr = binding.etQuantity.getText() != null ? binding.etQuantity.getText().toString().trim() : "";
        String unit = binding.etUnit.getText() != null ? binding.etUnit.getText().toString().trim() : "";
        String location = binding.etLocation.getText() != null ? binding.etLocation.getText().toString().trim() : "";
        String notes = binding.etNotes.getText() != null ? binding.etNotes.getText().toString().trim() : "";

        boolean isValid = true;

        if (TextUtils.isEmpty(category)) {
            binding.actvCategory.setError("Please select a category");
            isValid = false;
        } else {
            binding.actvCategory.setError(null);
        }

        if (TextUtils.isEmpty(priority)) {
            binding.actvPriority.setError("Please select a priority");
            isValid = false;
        } else {
            binding.actvPriority.setError(null);
        }

        if (TextUtils.isEmpty(itemName)) {
            binding.tilItemName.setError("Item name is required");
            isValid = false;
        } else {
            binding.tilItemName.setError(null);
        }

        if (TextUtils.isEmpty(quantityStr)) {
            binding.tilQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    binding.tilQuantity.setError("Quantity must be greater than 0");
                    isValid = false;
                } else {
                    binding.tilQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                binding.tilQuantity.setError("Invalid quantity");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(unit)) {
            binding.tilUnit.setError("Unit is required");
            isValid = false;
        } else {
            binding.tilUnit.setError(null);
        }

        if (TextUtils.isEmpty(location) && selectedLocation == null) {
            binding.tilLocation.setError("Please select a location");
            isValid = false;
        } else {
            binding.tilLocation.setError(null);
        }

        if (!isValid) {
            return;
        }

        submitRequest(category, priority, itemName, Integer.parseInt(quantityStr), unit, location, notes);
    }

    private void submitRequest(String category, String priority, String itemName, int quantity,
                               String unit, String location, String notes) {
        // Enforce input sanitization on all user-submitted fields
        String sanitizedItemName = SecuritySanitizer.sanitizeInput(itemName, 100);
        String sanitizedNotes = SecuritySanitizer.sanitizeNotes(notes);
        String sanitizedUnit = SecuritySanitizer.sanitizeInput(unit, 30);
        String rawLocation = location.isEmpty() ? selectedAddress : location;
        String sanitizedLocation = SecuritySanitizer.sanitizeInput(rawLocation, 256);

        if (!SecuritySanitizer.isValidQuantity(quantity)) {
            binding.tilQuantity.setError("Quantity must be between 1 and 10,000");
            return;
        }

        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText("Submitting...");

        String requestId = databaseReference.push().getKey();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        long timestamp = System.currentTimeMillis();

        // Evaluate request urgency with AI Triage Advisor
        ReliefTriageAdvisor triageAdvisor = new ReliefTriageAdvisor();
        ReliefTriageAdvisor.TriageAssessment assessment = triageAdvisor.evaluate(
                category, sanitizedItemName, quantity, sanitizedNotes, timestamp);

        ReliefRequest request = new ReliefRequest();
        request.setRequestId(requestId);
        request.setSurvivorId(currentUserId);
        request.setCategory(category);
        request.setPriority(priority != null && !priority.isEmpty() ? priority : assessment.getPriorityTier());
        request.setAiUrgencyScore(assessment.getUrgencyScore());
        request.setAiPriorityTier(assessment.getPriorityTier());
        request.setItemName(sanitizedItemName);
        request.setQuantity(quantity);
        request.setUnit(sanitizedUnit);
        request.setLocationAddress(sanitizedLocation);
        request.setNotes(sanitizedNotes);
        request.setStatus(ReliefRequest.STATUS_PENDING);
        request.setTimestamp(timestamp);
        request.setCreatedAt(timestamp);

        if (selectedLocation != null && SecuritySanitizer.isValidCoordinates(selectedLocation.latitude, selectedLocation.longitude)) {
            request.setLatitude(selectedLocation.latitude);
            request.setLongitude(selectedLocation.longitude);
        }

        binding.btnSubmit.setEnabled(false);
        databaseReference.child(requestId).setValue(request.toMap())
                .addOnSuccessListener(aVoid -> {
                    if (binding != null) {
                        binding.btnSubmit.setEnabled(true);
                        binding.btnSubmit.setText("Submit Request");
                    }
                    Toast.makeText(this, "Relief request submitted successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        binding.btnSubmit.setEnabled(true);
                        binding.btnSubmit.setText("Submit Request");
                    }
                    Toast.makeText(this, "Failed to submit request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("LATITUDE", 0);
            double lng = data.getDoubleExtra("LONGITUDE", 0);
            String address = data.getStringExtra("ADDRESS");

            selectedLocation = new LatLng(lat, lng);
            selectedAddress = address != null ? address : "";

            binding.etLocation.setText(selectedAddress);
            binding.tilLocation.setError(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
