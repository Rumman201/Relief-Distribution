package com.example.reliefdistribution;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reliefdistribution.data.util.SecuritySanitizer;
import com.example.reliefdistribution.databinding.ActivityInventoryManagementBinding;
import com.example.reliefdistribution.databinding.ItemInventoryBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for distributors to inspect depot stock levels, add rations, and reserve emergency supplies.
 * Modernized in Phase 10.1 with ViewBinding, dynamic inventory replenishment dialogs, and stock editing.
 */
public class InventoryManagementActivity extends AppCompatActivity {

    private ActivityInventoryManagementBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    private List<InventoryItem> filteredList;
    private String currentFilter = "All";

    private final String[] categories = {"All", "Food", "Water", "Medicine", "Clothing", "Shelter", "Hygiene", "Other"};
    private final String[] itemCategories = {"Food", "Water", "Medicine", "Clothing", "Shelter", "Hygiene", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInventoryManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Inventory");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Distributor";

        setupToolbar();
        setupCategoryChips();
        setupRecyclerView();
        setupClickListeners();
        loadInventory();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupCategoryChips() {
        binding.chipGroupCategories.removeAllViews();
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
            binding.chipGroupCategories.addView(chip);
        }
    }

    private void setupRecyclerView() {
        inventoryList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new InventoryAdapter(filteredList, this::showEditItemDialog, this::showReserveItemDialog, this::showDeleteItemDialog);
        binding.rvInventory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInventory.setHasFixedSize(true);
        binding.rvInventory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.fabAddItem.setOnClickListener(v -> showAddItemDialog());
    }

    private void loadInventory() {
        binding.rvInventory.setVisibility(View.GONE);
        binding.llEmptyState.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                inventoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InventoryItem item = dataSnapshot.getValue(InventoryItem.class);
                    if (item != null) {
                        inventoryList.add(item);
                    }
                }

                updateStats();
                filterInventory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding == null) return;
                binding.llEmptyState.setVisibility(View.VISIBLE);
                Toast.makeText(InventoryManagementActivity.this, "Failed to load inventory: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            } else if (item.getQuantityAvailable() <= 10) {
                lowStock++;
            }
        }

        binding.tvTotalItems.setText(String.valueOf(total));
        binding.tvLowStockItems.setText(String.valueOf(lowStock));
        binding.tvOutOfStockItems.setText(String.valueOf(outOfStock));
    }

    private void filterInventory() {
        filteredList.clear();
        if ("All".equals(currentFilter)) {
            filteredList.addAll(inventoryList);
        } else {
            for (InventoryItem item : inventoryList) {
                if (currentFilter.equalsIgnoreCase(item.getCategory())) {
                    filteredList.add(item);
                }
            }
        }

        Collections.sort(filteredList, (i1, i2) -> {
            int catCompare = (i1.getCategory() != null ? i1.getCategory() : "").compareTo(i2.getCategory() != null ? i2.getCategory() : "");
            if (catCompare != 0) return catCompare;
            return (i1.getItemName() != null ? i1.getItemName() : "").compareTo(i2.getItemName() != null ? i2.getItemName() : "");
        });

        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvInventory.setVisibility(View.GONE);
        } else {
            binding.rvInventory.setVisibility(View.VISIBLE);
            binding.llEmptyState.setVisibility(View.GONE);
        }
    }

    private void showAddItemDialog() {
        android.widget.LinearLayout dialogLayout = new android.widget.LinearLayout(this);
        dialogLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        dialogLayout.setPadding(48, 24, 48, 24);

        EditText etItemName = new EditText(this);
        etItemName.setHint("Item Name (e.g., Rice, Water 5L, First Aid)");
        dialogLayout.addView(etItemName);

        Spinner spCategory = new Spinner(this);
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemCategories);
        spCategory.setAdapter(catAdapter);
        dialogLayout.addView(spCategory);

        EditText etQty = new EditText(this);
        etQty.setHint("Quantity Available");
        etQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        dialogLayout.addView(etQty);

        EditText etUnit = new EditText(this);
        etUnit.setHint("Unit (e.g., kg, liters, boxes)");
        dialogLayout.addView(etUnit);

        EditText etDepot = new EditText(this);
        etDepot.setHint("Depot Location (e.g., Warehouse A)");
        dialogLayout.addView(etDepot);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add Relief Supplies")
                .setView(dialogLayout)
                .setPositiveButton("Add to Stock", (dialog, which) -> {
                    String rawName = etItemName.getText().toString().trim();
                    String rawQty = etQty.getText().toString().trim();
                    String rawUnit = etUnit.getText().toString().trim();
                    String rawDepot = etDepot.getText().toString().trim();
                    String category = (String) spCategory.getSelectedItem();

                    if (TextUtils.isEmpty(rawName) || TextUtils.isEmpty(rawQty)) {
                        Toast.makeText(this, "Item name and quantity are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int qty;
                    try {
                        qty = Integer.parseInt(rawQty);
                        if (qty <= 0) {
                            Toast.makeText(this, "Quantity must be greater than zero", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String sanitizedName = SecuritySanitizer.sanitizeInput(rawName, 100);
                    String sanitizedUnit = SecuritySanitizer.sanitizeInput(rawUnit.isEmpty() ? "units" : rawUnit, 30);
                    String sanitizedDepot = SecuritySanitizer.sanitizeInput(rawDepot.isEmpty() ? "Central Depot" : rawDepot, 100);

                    String itemId = databaseReference.push().getKey();
                    if (itemId == null) itemId = UUID.randomUUID().toString();

                    InventoryItem item = new InventoryItem();
                    item.setItemId(itemId);
                    item.setDistributorId(currentUserId);
                    item.setItemName(sanitizedName);
                    item.setCategory(category != null ? category : "General");
                    item.setQuantityAvailable(qty);
                    item.setQuantityReserved(0);
                    item.setUnit(sanitizedUnit);
                    item.setLocation(sanitizedDepot);
                    item.setUpdatedAt(System.currentTimeMillis());

                    databaseReference.child(itemId).setValue(item.toMap())
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Item added to inventory!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditItemDialog(InventoryItem item) {
        EditText etNewQty = new EditText(this);
        etNewQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etNewQty.setHint("New Available Quantity");
        etNewQty.setText(String.valueOf(item.getQuantityAvailable()));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Stock: " + item.getItemName())
                .setMessage("Enter the new available stock quantity:")
                .setView(etNewQty)
                .setPositiveButton("Update", (dialog, which) -> {
                    String str = etNewQty.getText().toString().trim();
                    if (!TextUtils.isEmpty(str)) {
                        try {
                            int newQty = Math.max(0, Integer.parseInt(str));
                            databaseReference.child(item.getItemId()).child("quantityAvailable").setValue(newQty);
                            item.setQuantityAvailable(newQty);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Stock updated to " + newQty, Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReserveItemDialog(InventoryItem item) {
        EditText etReserveQty = new EditText(this);
        etReserveQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etReserveQty.setHint("Quantity to Reserve (Max: " + item.getQuantityAvailable() + ")");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Reserve Stock: " + item.getItemName())
                .setMessage("Current available: " + item.getQuantityAvailable() + " " + item.getUnit())
                .setView(etReserveQty)
                .setPositiveButton("Reserve", (dialog, which) -> {
                    String str = etReserveQty.getText().toString().trim();
                    if (!TextUtils.isEmpty(str)) {
                        try {
                            int reserveCount = Integer.parseInt(str);
                            if (reserveCount <= 0 || reserveCount > item.getQuantityAvailable()) {
                                Toast.makeText(this, "Cannot reserve more than available stock", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int newAvail = item.getQuantityAvailable() - reserveCount;
                            int newRes = item.getQuantityReserved() + reserveCount;

                            Map<String, Object> map = new HashMap<>();
                            map.put("quantityAvailable", newAvail);
                            map.put("quantityReserved", newRes);
                            map.put("updatedAt", System.currentTimeMillis());

                            databaseReference.child(item.getItemId()).updateChildren(map);
                            item.setQuantityAvailable(newAvail);
                            item.setQuantityReserved(newRes);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Reserved " + reserveCount + " " + item.getUnit(), Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteItemDialog(InventoryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Inventory Item")
                .setMessage("Are you sure you want to remove " + item.getItemName() + " from the inventory database?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseReference.child(item.getItemId()).removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    interface ItemActionListener {
        void onAction(InventoryItem item);
    }

    private static class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

        private final List<InventoryItem> items;
        private final ItemActionListener editListener;
        private final ItemActionListener reserveListener;
        private final ItemActionListener deleteListener;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        InventoryAdapter(List<InventoryItem> items,
                         ItemActionListener editListener,
                         ItemActionListener reserveListener,
                         ItemActionListener deleteListener) {
            this.items = items;
            this.editListener = editListener;
            this.reserveListener = reserveListener;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemInventoryBinding itemBinding = ItemInventoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new InventoryViewHolder(itemBinding);
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
            private final ItemInventoryBinding itemBinding;

            InventoryViewHolder(@NonNull ItemInventoryBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            void bind(InventoryItem item) {
                itemBinding.tvItemName.setText(item.getItemName());
                itemBinding.tvCategory.setText(item.getCategory() != null ? item.getCategory() : "General");
                itemBinding.tvAvailableQty.setText(String.format(Locale.getDefault(), "Available: %d", item.getQuantityAvailable()));
                itemBinding.tvReservedQty.setText(String.format(Locale.getDefault(), "Reserved: %d", item.getQuantityReserved()));
                itemBinding.tvUnit.setText("Unit: " + (item.getUnit() != null ? item.getUnit() : ""));

                if (item.getLocation() != null && !item.getLocation().isEmpty()) {
                    itemBinding.tvLocation.setText("Location: " + item.getLocation());
                    itemBinding.tvLocation.setVisibility(View.VISIBLE);
                } else {
                    itemBinding.tvLocation.setVisibility(View.GONE);
                }

                if (item.getExpiryDate() > 0) {
                    itemBinding.tvExpiryDate.setText("Expires: " + dateFormat.format(new Date(item.getExpiryDate())));
                    itemBinding.tvExpiryDate.setVisibility(View.VISIBLE);
                } else {
                    itemBinding.tvExpiryDate.setVisibility(View.GONE);
                }

                String status = item.getStatus();
                itemBinding.tvStockStatus.setText(status != null ? status : InventoryItem.STATUS_IN_STOCK);

                int bgResId;
                if (InventoryItem.STATUS_OUT_OF_STOCK.equals(status)) {
                    bgResId = R.drawable.bg_status_out_of_stock;
                } else if (InventoryItem.STATUS_LOW_STOCK.equals(status)) {
                    bgResId = R.drawable.bg_status_low_stock;
                } else {
                    bgResId = R.drawable.bg_status_in_stock;
                }
                itemBinding.tvStockStatus.setBackgroundResource(bgResId);

                itemBinding.btnEditItem.setOnClickListener(v -> {
                    if (editListener != null) editListener.onAction(item);
                });

                itemBinding.btnReserveItem.setOnClickListener(v -> {
                    if (reserveListener != null) reserveListener.onAction(item);
                });

                itemBinding.btnDeleteItem.setOnClickListener(v -> {
                    if (deleteListener != null) deleteListener.onAction(item);
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
