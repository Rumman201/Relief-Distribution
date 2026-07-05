package com.example.reliefdistribution;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chip;
import android.widget.ChipGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InventoryManagementActivity extends AppCompatActivity {

    private RecyclerView rvInventory;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddItem;
    private ImageButton btnBack;
    private ChipGroup chipGroupCategories;
    private TextView tvTotalItems, tvLowStock, tvOutOfStock;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    private List<InventoryItem> filteredList;
    private String currentFilter = "All";

    private final String[] categories = {"All", "Food", "Water", "Medicine", "Clothing", "Shelter", "Hygiene", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Inventory");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        initViews();
        setupCategoryChips();
        setupRecyclerView();
        setupClickListeners();
        loadInventory();
    }

    private void initViews() {
        rvInventory = findViewById(R.id.rvInventory);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddItem = findViewById(R.id.fabAddItem);
        btnBack = findViewById(R.id.btnBack);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvOutOfStock = findViewById(R.id.tvOutOfStock);
    }

    private void setupCategoryChips() {
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setChecked(category.equals("All"));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    currentFilter = category;
                    filterInventory();
                }
            });
            chipGroupCategories.addView(chip);
        }
    }

    private void setupRecyclerView() {
        inventoryList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new InventoryAdapter(filteredList);
        rvInventory.setLayoutManager(new LinearLayoutManager(this));
        rvInventory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        fabAddItem.setOnClickListener(v -> {
            // TODO: Open add/edit inventory item dialog/activity
            Toast.makeText(this, "Add item feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadInventory() {
        progressBar.setVisibility(View.VISIBLE);
        rvInventory.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        databaseReference.orderByChild("distributorId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        inventoryList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            InventoryItem item = dataSnapshot.getValue(InventoryItem.class);
                            if (item != null) {
                                inventoryList.add(item);
                            }
                        }

                        updateStats();
                        filterInventory();

                        progressBar.setVisibility(View.GONE);
                        if (filteredList.isEmpty()) {
                            tvEmptyState.setVisibility(View.VISIBLE);
                            rvInventory.setVisibility(View.GONE);
                        } else {
                            rvInventory.setVisibility(View.VISIBLE);
                            tvEmptyState.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        tvEmptyState.setVisibility(View.VISIBLE);
                        tvEmptyState.setText("Failed to load inventory: " + error.getMessage());
                        Toast.makeText(InventoryManagementActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateStats() {
        int total = inventoryList.size();
        int lowStock = 0;
        int outOfStock = 0;

        for (InventoryItem item : inventoryList) {
            if (item.getQuantityAvailable() <= 0) {
                outOfStock++;
            } else if (item.getQuantityAvailable() <= 10) { // Low stock threshold
                lowStock++;
            }
        }

        tvTotalItems.setText(String.valueOf(total));
        tvLowStock.setText(String.valueOf(lowStock));
        tvOutOfStock.setText(String.valueOf(outOfStock));
    }

    private void filterInventory() {
        filteredList.clear();
        if ("All".equals(currentFilter)) {
            filteredList.addAll(inventoryList);
        } else {
            for (InventoryItem item : inventoryList) {
                if (currentFilter.equals(item.getCategory())) {
                    filteredList.add(item);
                }
            }
        }

        // Sort by category then by name
        Collections.sort(filteredList, (i1, i2) -> {
            int catCompare = i1.getCategory().compareTo(i2.getCategory());
            if (catCompare != 0) return catCompare;
            return i1.getItemName().compareTo(i2.getItemName());
        });

        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvInventory.setVisibility(View.GONE);
        } else {
            rvInventory.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private static class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

        private final List<InventoryItem> items;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        InventoryAdapter(List<InventoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inventory, parent, false);
            return new InventoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
            InventoryItem item = items.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class InventoryViewHolder extends RecyclerView.ViewHolder {
            TextView tvItemName, tvCategory, tvQuantityAvailable, tvQuantityReserved, tvUnit, tvLocation, tvExpiry, tvStatus;
            View statusBadge;

            InventoryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvItemName = itemView.findViewById(R.id.tvItemName);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvQuantityAvailable = itemView.findViewById(R.id.tvQuantityAvailable);
                tvQuantityReserved = itemView.findViewById(R.id.tvQuantityReserved);
                tvUnit = itemView.findViewById(R.id.tvUnit);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvExpiry = itemView.findViewById(R.id.tvExpiry);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                statusBadge = itemView.findViewById(R.id.statusBadge);
            }

            void bind(InventoryItem item) {
                tvItemName.setText(item.getItemName());
                tvCategory.setText(item.getCategory());
                tvQuantityAvailable.setText(String.format(Locale.getDefault(), "Available: %d", item.getQuantityAvailable()));
                tvQuantityReserved.setText(String.format(Locale.getDefault(), "Reserved: %d", item.getQuantityReserved()));
                tvUnit.setText("Unit: " + item.getUnit());

                if (item.getLocation() != null && !item.getLocation().isEmpty()) {
                    tvLocation.setText("Location: " + item.getLocation());
                    tvLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setVisibility(View.GONE);
                }

                if (item.getExpiryDate() > 0) {
                    tvExpiry.setText("Expires: " + dateFormat.format(new Date(item.getExpiryDate())));
                    tvExpiry.setVisibility(View.VISIBLE);
                } else {
                    tvExpiry.setVisibility(View.GONE);
                }

                String status = item.getStatus();
                tvStatus.setText(status);

                int badgeColor;
                int bgResId;
                switch (status) {
                    case InventoryItem.STATUS_IN_STOCK:
                        badgeColor = 0xFF4CAF50; // Green
                        bgResId = R.drawable.bg_status_in_stock;
                        break;
                    case InventoryItem.STATUS_LOW_STOCK:
                        badgeColor = 0xFFFF9800; // Orange
                        bgResId = R.drawable.bg_status_low_stock;
                        break;
                    case InventoryItem.STATUS_OUT_OF_STOCK:
                        badgeColor = 0xFFF44336; // Red
                        bgResId = R.drawable.bg_status_out_of_stock;
                        break;
                    default:
                        badgeColor = 0xFF9E9E9E; // Gray
                        bgResId = R.drawable.bg_status_in_stock;
                }
                statusBadge.setBackgroundColor(badgeColor);
                statusBadge.setBackgroundResource(bgResId);
            }
        }
    }
}