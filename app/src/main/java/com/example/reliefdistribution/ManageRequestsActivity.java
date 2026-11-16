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

import com.example.reliefdistribution.data.ai.ReliefTriageAdvisor;
import com.example.reliefdistribution.databinding.ActivityManageRequestsBinding;
import com.example.reliefdistribution.databinding.ItemReliefRequestBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
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

/**
 * Activity for distributors to triage, accept, track, and complete survivor relief requests.
 * Modernized in Phase 10.1 with ViewBinding, AI triage assessment, and distributor assignment tracking.
 */
public class ManageRequestsActivity extends AppCompatActivity {

    private ActivityManageRequestsBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private RequestAdapter adapter;
    private List<ReliefRequest> allRequests;
    private List<ReliefRequest> filteredRequests;
    private String currentStatusFilter = "All";

    private final String[] statusFilters = {"All", "Pending", "Accepted", "In Progress", "Completed", "Cancelled"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReliefRequests");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Distributor";

        setupTabs();
        setupRecyclerView();
        setupClickListeners();
        loadRequests();
    }

    private void setupTabs() {
        for (String status : statusFilters) {
            TabLayout.Tab tab = binding.tabLayoutStatus.newTab().setText(status);
            binding.tabLayoutStatus.addTab(tab);
        }

        binding.tabLayoutStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    currentStatusFilter = tab.getText().toString();
                    filterRequests();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        allRequests = new ArrayList<>();
        filteredRequests = new ArrayList<>();
        adapter = new RequestAdapter(filteredRequests, currentUserId, this::updateRequestStatus, this::viewRequestDetails);
        binding.rvRequests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRequests.setHasFixedSize(true);
        binding.rvRequests.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadRequests() {
        binding.rvRequests.setVisibility(View.GONE);
        binding.llEmptyState.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                allRequests.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ReliefRequest request = dataSnapshot.getValue(ReliefRequest.class);
                    if (request != null) {
                        allRequests.add(request);
                    }
                }

                updateStats();
                filterRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding == null) return;
                binding.llEmptyState.setVisibility(View.VISIBLE);
                Toast.makeText(ManageRequestsActivity.this, "Failed to load requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats() {
        int pending = 0, accepted = 0, inProgress = 0, completed = 0;

        for (ReliefRequest request : allRequests) {
            String status = request.getStatus();
            if (ReliefRequest.STATUS_PENDING.equals(status)) {
                pending++;
            } else if (ReliefRequest.STATUS_ACCEPTED.equals(status)) {
                accepted++;
            } else if (ReliefRequest.STATUS_IN_PROGRESS.equals(status)) {
                inProgress++;
            } else if (ReliefRequest.STATUS_COMPLETED.equals(status)) {
                completed++;
            }
        }

        binding.tvPendingCount.setText(String.valueOf(pending));
        binding.tvAcceptedCount.setText(String.valueOf(accepted));
        binding.tvInProgressCount.setText(String.valueOf(inProgress));
        binding.tvCompletedCount.setText(String.valueOf(completed));
    }

    private void filterRequests() {
        filteredRequests.clear();
        if ("All".equals(currentStatusFilter)) {
            filteredRequests.addAll(allRequests);
        } else {
            String targetStatus;
            switch (currentStatusFilter) {
                case "Pending": targetStatus = ReliefRequest.STATUS_PENDING; break;
                case "Accepted": targetStatus = ReliefRequest.STATUS_ACCEPTED; break;
                case "In Progress": targetStatus = ReliefRequest.STATUS_IN_PROGRESS; break;
                case "Completed": targetStatus = ReliefRequest.STATUS_COMPLETED; break;
                case "Cancelled": targetStatus = ReliefRequest.STATUS_CANCELLED; break;
                default: targetStatus = currentStatusFilter; break;
            }
            for (ReliefRequest request : allRequests) {
                if (targetStatus.equalsIgnoreCase(request.getStatus())) {
                    filteredRequests.add(request);
                }
            }
        }

        // Sort by timestamp descending (newest first)
        Collections.sort(filteredRequests, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));

        adapter.notifyDataSetChanged();

        if (filteredRequests.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvRequests.setVisibility(View.GONE);
        } else {
            binding.rvRequests.setVisibility(View.VISIBLE);
            binding.llEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateRequestStatus(ReliefRequest request, String newStatus) {
        DatabaseReference requestRef = databaseReference.child(request.getRequestId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("updatedAt", System.currentTimeMillis());

        if (ReliefRequest.STATUS_ACCEPTED.equals(newStatus)) {
            updates.put("distributorId", currentUserId);
            request.setDistributorId(currentUserId);
        }

        requestRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    request.setStatus(newStatus);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    createNotification(request, newStatus);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void createNotification(ReliefRequest request, String newStatus) {
        if (request.getSurvivorId() == null || request.getSurvivorId().isEmpty()) return;

        DatabaseReference notifRef = FirebaseDatabase.getInstance()
                .getReference("Notifications")
                .child(request.getSurvivorId())
                .push();

        String type;
        String title;
        String message;

        switch (newStatus) {
            case ReliefRequest.STATUS_ACCEPTED:
                type = "REQUEST_ACCEPTED";
                title = "Request Accepted";
                message = "Your request for " + request.getItemName() + " has been accepted by distributor " + currentUserId;
                break;
            case ReliefRequest.STATUS_IN_PROGRESS:
                type = "REQUEST_IN_PROGRESS";
                title = "Delivery In Progress";
                message = "Your relief supplies (" + request.getItemName() + ") are out for delivery";
                break;
            case ReliefRequest.STATUS_COMPLETED:
                type = "REQUEST_COMPLETED";
                title = "Delivery Completed";
                message = "Your request for " + request.getItemName() + " has been fulfilled";
                break;
            default:
                type = "REQUEST_UPDATE";
                title = "Request Update";
                message = "Your relief request status is now " + newStatus;
                break;
        }

        Notification notification = new Notification();
        notification.setNotificationId(notifRef.getKey());
        notification.setUserId(request.getSurvivorId());
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTimestamp(System.currentTimeMillis());
        notification.setRead(false);

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", request.getRequestId());
        data.put("type", type);
        notification.setData(data);

        notifRef.setValue(notification.toMap());
    }

    private void viewRequestDetails(ReliefRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Item: ").append(request.getItemName()).append("\n");
        sb.append("Category: ").append(request.getCategory() != null ? request.getCategory() : "General").append("\n");
        sb.append("Quantity: ").append(request.getQuantity()).append(" ").append(request.getUnit() != null ? request.getUnit() : "units").append("\n");
        sb.append("Status: ").append(request.getStatus()).append("\n");
        sb.append("Priority: ").append(request.getPriority()).append("\n");
        if (request.getAiPriorityTier() != null) {
            sb.append("AI Urgency Score: ").append(request.getAiUrgencyScore()).append(" (Tier: ").append(request.getAiPriorityTier()).append(")\n");
        }
        if (request.getLocationAddress() != null && !request.getLocationAddress().isEmpty()) {
            sb.append("Address: ").append(request.getLocationAddress()).append("\n");
        }
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            sb.append("Notes: ").append(request.getNotes()).append("\n");
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle("Relief Request Details")
                .setMessage(sb.toString())
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

    interface StatusUpdateListener {
        void onUpdate(ReliefRequest request, String status);
    }

    interface DetailsListener {
        void onDetails(ReliefRequest request);
    }

    private static class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

        private final List<ReliefRequest> requests;
        private final String currentUserId;
        private final StatusUpdateListener statusListener;
        private final DetailsListener detailsListener;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        private final ReliefTriageAdvisor triageAdvisor = new ReliefTriageAdvisor();

        RequestAdapter(List<ReliefRequest> requests,
                       String currentUserId,
                       StatusUpdateListener statusListener,
                       DetailsListener detailsListener) {
            this.requests = requests;
            this.currentUserId = currentUserId;
            this.statusListener = statusListener;
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
                itemBinding.tvCategory.setText(request.getCategory() != null ? request.getCategory() : "Food");
                itemBinding.tvQuantity.setText(String.format(Locale.getDefault(), "Qty: %d %s",
                        request.getQuantity(), request.getUnit() != null ? request.getUnit() : ""));

                // Evaluate AI triage priority
                ReliefTriageAdvisor.TriageAssessment assessment = triageAdvisor.evaluate(
                        request.getCategory(),
                        request.getItemName(),
                        request.getQuantity(),
                        request.getNotes(),
                        request.getTimestamp()
                );
                itemBinding.tvPriority.setText(String.format(Locale.getDefault(), "Priority: %s (AI Score: %d)",
                        assessment.getPriorityTier(), assessment.getUrgencyScore()));
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
                    itemBinding.tvDistributor.setText("Unassigned");
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
                    if (detailsListener != null) detailsListener.onDetails(request);
                });

                // Cancel button hidden for distributor view
                itemBinding.btnCancelRequest.setVisibility(View.GONE);

                // Setup action buttons based on status
                setupActionButtons(request);
            }

            private void setupActionButtons(ReliefRequest request) {
                String status = request.getStatus();

                itemBinding.btnAccept.setVisibility(View.GONE);
                itemBinding.btnStartDelivery.setVisibility(View.GONE);
                itemBinding.btnComplete.setVisibility(View.GONE);
                itemBinding.btnViewMap.setVisibility(View.GONE);

                if (ReliefRequest.STATUS_PENDING.equals(status)) {
                    itemBinding.btnAccept.setVisibility(View.VISIBLE);
                    itemBinding.btnAccept.setOnClickListener(v -> {
                        if (statusListener != null) statusListener.onUpdate(request, ReliefRequest.STATUS_ACCEPTED);
                    });
                } else if (ReliefRequest.STATUS_ACCEPTED.equals(status)) {
                    itemBinding.btnStartDelivery.setVisibility(View.VISIBLE);
                    itemBinding.btnStartDelivery.setOnClickListener(v -> {
                        if (statusListener != null) statusListener.onUpdate(request, ReliefRequest.STATUS_IN_PROGRESS);
                    });
                    itemBinding.btnViewMap.setVisibility(View.VISIBLE);
                    itemBinding.btnViewMap.setOnClickListener(v -> viewOnMap(request));
                } else if (ReliefRequest.STATUS_IN_PROGRESS.equals(status)) {
                    itemBinding.btnComplete.setVisibility(View.VISIBLE);
                    itemBinding.btnComplete.setOnClickListener(v -> {
                        if (statusListener != null) statusListener.onUpdate(request, ReliefRequest.STATUS_COMPLETED);
                    });
                    itemBinding.btnViewMap.setVisibility(View.VISIBLE);
                    itemBinding.btnViewMap.setOnClickListener(v -> viewOnMap(request));
                } else if (ReliefRequest.STATUS_COMPLETED.equals(status)) {
                    itemBinding.btnViewMap.setVisibility(View.VISIBLE);
                    itemBinding.btnViewMap.setOnClickListener(v -> viewOnMap(request));
                }
            }

            private void viewOnMap(ReliefRequest request) {
                if (request.getLatitude() != 0 && request.getLongitude() != 0) {
                    try {
                        Uri gmmIntentUri = Uri.parse("geo:" + request.getLatitude() + "," + request.getLongitude() +
                                "?q=" + request.getLatitude() + "," + request.getLongitude() +
                                "(" + Uri.encode(request.getItemName() != null ? request.getItemName() : "Relief Aid Target") + ")");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(itemView.getContext().getPackageManager()) != null) {
                            itemView.getContext().startActivity(mapIntent);
                            return;
                        }
                    } catch (Exception ignored) {
                    }
                    Intent intent = new Intent(itemView.getContext(), MapActivity.class);
                    intent.putExtra("LATITUDE", request.getLatitude());
                    intent.putExtra("LONGITUDE", request.getLongitude());
                    intent.putExtra("ADDRESS", request.getLocationAddress());
                    intent.putExtra("REQUEST_ID", request.getRequestId());
                    itemView.getContext().startActivity(intent);
                } else {
                    Toast.makeText(itemView.getContext(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
