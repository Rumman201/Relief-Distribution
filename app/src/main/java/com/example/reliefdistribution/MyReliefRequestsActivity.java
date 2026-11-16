package com.example.reliefdistribution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reliefdistribution.data.automation.ReliefWorkflowAutomator;
import com.example.reliefdistribution.databinding.ActivityMyReliefRequestsBinding;
import com.example.reliefdistribution.databinding.ItemReliefRequestBinding;
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
import java.util.List;
import java.util.Locale;

/**
 * Activity for survivors to view, monitor, and cancel their submitted relief aid requests.
 * Modernized in Phase 10.1 with ViewBinding, 48dp touch targets, and request cancellation workflow.
 */
public class MyReliefRequestsActivity extends AppCompatActivity {

    private ActivityMyReliefRequestsBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private RequestAdapter adapter;
    private List<ReliefRequest> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReliefRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReliefRequests");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        setupRecyclerView();
        setupClickListeners();
        loadRequests();
    }

    private void setupRecyclerView() {
        requestList = new ArrayList<>();
        adapter = new RequestAdapter(requestList, this::cancelRequest, this::viewRequestDetails);
        binding.rvMyRequests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyRequests.setHasFixedSize(true);
        binding.rvMyRequests.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.fabCreateRequest.setOnClickListener(v ->
                startActivity(new Intent(this, CreateReliefRequestActivity.class)));

        binding.btnCreateFirstRequest.setOnClickListener(v ->
                startActivity(new Intent(this, CreateReliefRequestActivity.class)));
    }

    private void loadRequests() {
        binding.llEmptyState.setVisibility(View.GONE);
        binding.rvMyRequests.setVisibility(View.GONE);

        databaseReference.orderByChild("survivorId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (binding == null) return;
                        requestList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ReliefRequest request = dataSnapshot.getValue(ReliefRequest.class);
                            if (request != null) {
                                requestList.add(request);
                            }
                        }

                        // Sort by timestamp descending (newest first)
                        Collections.sort(requestList, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));

                        adapter.notifyDataSetChanged();

                        if (requestList.isEmpty()) {
                            binding.llEmptyState.setVisibility(View.VISIBLE);
                            binding.rvMyRequests.setVisibility(View.GONE);
                        } else {
                            binding.rvMyRequests.setVisibility(View.VISIBLE);
                            binding.llEmptyState.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (binding == null) return;
                        binding.llEmptyState.setVisibility(View.VISIBLE);
                        Toast.makeText(MyReliefRequestsActivity.this, "Failed to load requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelRequest(ReliefRequest request) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cancel Relief Request")
                .setMessage("Are you sure you want to cancel this request for " + request.getItemName() + "?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    databaseReference.child(request.getRequestId()).child("status")
                            .setValue(ReliefRequest.STATUS_CANCELLED)
                            .addOnSuccessListener(aVoid -> {
                                ReliefWorkflowAutomator automator = new ReliefWorkflowAutomator();
                                automator.onRequestCancelled(request);
                                request.setStatus(ReliefRequest.STATUS_CANCELLED);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Request cancelled", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to cancel request: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void viewRequestDetails(ReliefRequest request) {
        StringBuilder details = new StringBuilder();
        details.append("Category: ").append(request.getCategory() != null ? request.getCategory() : "General").append("\n");
        details.append("Quantity: ").append(request.getQuantity()).append(" ").append(request.getUnit() != null ? request.getUnit() : "units").append("\n");
        details.append("Status: ").append(request.getStatus()).append("\n");
        details.append("Priority: ").append(request.getPriority()).append("\n");
        if (request.getAiPriorityTier() != null) {
            details.append("AI Urgency Tier: ").append(request.getAiPriorityTier()).append(" (Score: ").append(request.getAiUrgencyScore()).append(")\n");
        }
        if (request.getLocationAddress() != null && !request.getLocationAddress().isEmpty()) {
            details.append("Location: ").append(request.getLocationAddress()).append("\n");
        }
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            details.append("Notes: ").append(request.getNotes()).append("\n");
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(request.getItemName())
                .setMessage(details.toString())
                .setPositiveButton("Close", null);

        if (request.getLatitude() != 0 && request.getLongitude() != 0) {
            builder.setNeutralButton("View on Map", (dialog, which) -> {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("LATITUDE", request.getLatitude());
                intent.putExtra("LONGITUDE", request.getLongitude());
                intent.putExtra("ADDRESS", request.getLocationAddress());
                intent.putExtra("REQUEST_ID", request.getRequestId());
                startActivity(intent);
            });
        }

        builder.show();
    }

    interface RequestActionListener {
        void onAction(ReliefRequest request);
    }

    private static class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

        private final List<ReliefRequest> requests;
        private final RequestActionListener cancelListener;
        private final RequestActionListener detailsListener;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        RequestAdapter(List<ReliefRequest> requests,
                       RequestActionListener cancelListener,
                       RequestActionListener detailsListener) {
            this.requests = requests;
            this.cancelListener = cancelListener;
            this.detailsListener = detailsListener;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemReliefRequestBinding itemBinding = ItemReliefRequestBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new RequestViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            ReliefRequest request = requests.get(position);
            holder.bind(request);
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        class RequestViewHolder extends RecyclerView.ViewHolder {
            private final ItemReliefRequestBinding itemBinding;

            RequestViewHolder(@NonNull ItemReliefRequestBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            void bind(ReliefRequest request) {
                itemBinding.tvItemName.setText(request.getItemName());
                itemBinding.tvCategory.setText(request.getCategory() != null ? request.getCategory() : "General");
                itemBinding.tvQuantity.setText(String.format(Locale.getDefault(), "Qty: %d %s",
                        request.getQuantity(), request.getUnit() != null ? request.getUnit() : ""));
                itemBinding.tvPriority.setText(request.getPriority() != null ? request.getPriority() : "Medium");
                itemBinding.tvDate.setText("Requested: " + dateFormat.format(new Date(request.getTimestamp())));

                String status = request.getStatus();
                itemBinding.tvStatus.setText(status != null ? status : ReliefRequest.STATUS_PENDING);

                int badgeColor;
                if (ReliefRequest.STATUS_PENDING.equals(status)) {
                    badgeColor = 0xFFFF9800; // Orange
                } else if (ReliefRequest.STATUS_ACCEPTED.equals(status)) {
                    badgeColor = 0xFF2196F3; // Blue
                } else if (ReliefRequest.STATUS_IN_PROGRESS.equals(status)) {
                    badgeColor = 0xFF9C27B0; // Purple
                } else if (ReliefRequest.STATUS_COMPLETED.equals(status)) {
                    badgeColor = 0xFF4CAF50; // Green
                } else if (ReliefRequest.STATUS_CANCELLED.equals(status)) {
                    badgeColor = 0xFFF44336; // Red
                } else {
                    badgeColor = 0xFF9E9E9E; // Gray
                }
                itemBinding.statusBadge.setBackgroundColor(badgeColor);

                if (request.getDistributorId() != null && !request.getDistributorId().isEmpty()) {
                    itemBinding.tvDistributor.setText("Distributor: " + request.getDistributorId());
                    itemBinding.tvDistributor.setVisibility(View.VISIBLE);
                } else {
                    itemBinding.tvDistributor.setText("Pending assignment");
                    itemBinding.tvDistributor.setVisibility(View.VISIBLE);
                }

                if (request.getLocationAddress() != null && !request.getLocationAddress().isEmpty()) {
                    itemBinding.tvAddress.setText("Location: " + request.getLocationAddress());
                    itemBinding.tvAddress.setVisibility(View.VISIBLE);
                } else {
                    itemBinding.tvAddress.setVisibility(View.GONE);
                }

                if (request.getNotes() != null && !request.getNotes().isEmpty()) {
                    itemBinding.tvNotes.setText("Notes: " + request.getNotes());
                    itemBinding.tvNotes.setVisibility(View.VISIBLE);
                } else {
                    itemBinding.tvNotes.setVisibility(View.GONE);
                }

                // Details button
                itemBinding.btnViewDetails.setVisibility(View.VISIBLE);
                itemBinding.btnViewDetails.setOnClickListener(v -> {
                    if (detailsListener != null) detailsListener.onAction(request);
                });

                // Cancel button visible only for pending requests
                if (ReliefRequest.STATUS_PENDING.equals(status)) {
                    itemBinding.btnCancelRequest.setVisibility(View.VISIBLE);
                    itemBinding.btnCancelRequest.setOnClickListener(v -> {
                        if (cancelListener != null) cancelListener.onAction(request);
                    });
                } else {
                    itemBinding.btnCancelRequest.setVisibility(View.GONE);
                }

                // Hide distributor action buttons for survivor view
                itemBinding.btnAccept.setVisibility(View.GONE);
                itemBinding.btnStartDelivery.setVisibility(View.GONE);
                itemBinding.btnComplete.setVisibility(View.GONE);
                itemBinding.btnViewMap.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
