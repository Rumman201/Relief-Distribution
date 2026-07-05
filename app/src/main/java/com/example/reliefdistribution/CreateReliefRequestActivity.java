package com.example.reliefdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateReliefRequestActivity extends AppCompatActivity {

    private AutoCompleteTextView actvCategory, actvPriority;
    private TextInputLayout tilItemName, tilQuantity, tilUnit, tilLocation, tilNotes;
    private EditText etItemName, etQuantity, etUnit, etLocation, etNotes;
    private Button btnSelectLocation, btnSubmit;
    private ImageButton btnBack;

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
        setContentView(R.layout.activity_create_relief_request);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReliefRequests");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        initViews();
        setupAdapters();
        setupClickListeners();
    }

    private void initViews() {
        actvCategory = findViewById(R.id.actvCategory);
        actvPriority = findViewById(R.id.actvPriority);
        tilItemName = findViewById(R.id.tilItemName);
        tilQuantity = findViewById(R.id.tilQuantity);
        tilUnit = findViewById(R.id.tilUnit);
        tilLocation = findViewById(R.id.tilLocation);
        tilNotes = findViewById(R.id.tilNotes);
        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etLocation = findViewById(R.id.etLocation);
        etNotes = findViewById(R.id.etNotes);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupAdapters() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, priorities);
        actvPriority.setAdapter(priorityAdapter);

        actvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actvCategory.setError(null);
            }
        });

        actvPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actvPriority.setError(null);
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, GoogleMap.class);
            intent.putExtra("PICK_LOCATION", true);
            startActivityForResult(intent, LOCATION_PICKER_REQUEST);
        });

        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String category = actvCategory.getText().toString().trim();
        String priority = actvPriority.getText().toString().trim();
        String itemName = etItemName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(category)) {
            actvCategory.setError("Please select a category");
            isValid = false;
        } else {
            actvCategory.setError(null);
        }

        if (TextUtils.isEmpty(priority)) {
            actvPriority.setError("Please select a priority");
            isValid = false;
        } else {
            actvPriority.setError(null);
        }

        if (TextUtils.isEmpty(itemName)) {
            tilItemName.setError("Item name is required");
            isValid = false;
        } else {
            tilItemName.setError(null);
        }

        if (TextUtils.isEmpty(quantityStr)) {
            tilQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    isValid = false;
                } else {
                    tilQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Invalid quantity");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(unit)) {
            tilUnit.setError("Unit is required");
            isValid = false;
        } else {
            tilUnit.setError(null);
        }

        if (TextUtils.isEmpty(location) && selectedLocation == null) {
            tilLocation.setError("Please select a location");
            isValid = false;
        } else {
            tilLocation.setError(null);
        }

        if (!isValid) {
            return;
        }

        submitRequest(category, priority, itemName, Integer.parseInt(quantityStr), unit, location, notes);
    }

    private void submitRequest(String category, String priority, String itemName, int quantity,
                               String unit, String location, String notes) {
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        String requestId = databaseReference.push().getKey();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        long timestamp = System.currentTimeMillis();

        ReliefRequest request = new ReliefRequest();
        request.setRequestId(requestId);
        request.setSurvivorId(currentUserId);
        request.setCategory(category);
        request.setPriority(priority);
        request.setItemName(itemName);
        request.setQuantity(quantity);
        request.setUnit(unit);
        request.setLocationAddress(location.isEmpty() ? selectedAddress : location);
        request.setNotes(notes);
        request.setStatus(ReliefRequest.STATUS_PENDING);
        request.setTimestamp(timestamp);
        request.setCreatedAt(timestamp);

        if (selectedLocation != null) {
            request.setLatitude(selectedLocation.latitude);
            request.setLongitude(selectedLocation.longitude);
        }

        databaseReference.child(requestId).setValue(request.toMap())
                .addOnSuccessListener(aVoid -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit Request");
                    Toast.makeText(this, "Relief request submitted successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit Request");
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

            etLocation.setText(selectedAddress);
            tilLocation.setError(null);
        }
    }
}