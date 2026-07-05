package com.example.reliefdistribution;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Locale;

public class ManageRequestsActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ImageButton btnBack;
    private TabLayout tabLayoutStatus;
    private TextView tvPendingCount, tvAcceptedCount, tvInProgressCount, tvCompletedCount;

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
        setContentView(R.layout.activity_manage_requests);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReliefRequests");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        initViews();
        setupTabs();
        setupRecyclerView();
        setupClickListeners();
        loadRequests();
    }

    private void initViews() {
        rvRequests = findViewById(R.id.rvRequests);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBack);
        tabLayoutStatus = findViewById(R.id.tabLayoutStatus);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvAcceptedCount = findViewById(R.id.tvAcceptedCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
    }

    private void setupTabs() {
        for (String status : statusFilters) {
            TabLayout.Tab tab = tabLayoutStatus.newTab().setText(status);
            tabLayoutStatus.addTab(tab);
        }

        tabLayoutStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentStatusFilter = tab.getText().toString();
                filterRequests();
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
        adapter = new RequestAdapter(filteredRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        rvRequests.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allRequests.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ReliefRequest request = dataSnapshot.getValue(ReliefRequest.class);
                    if (request != null) {
                        allRequests.add(request);
                    }
                }

                updateStats();
                filterRequests();

                progressBar.setVisibility(View.GONE);
                if (filteredRequests.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvRequests.setVisibility(View.GONE);
                } else {
                    rvRequests.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
                tvEmptyState.setText("Failed to load requests: " + error.getMessage());
                Toast.makeText(ManageRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats() {
        int pending = 0, accepted = 0, inProgress = 0, completed = 0;

        for (ReliefRequest request : allRequests) {
            switch (request.getStatus()) {
                case ReliefRequest.STATUS_PENDING:
                    pending++;
                    break;
                case ReliefRequest.STATUS_ACCEPTED:
                    accepted++;
                    break;
                case ReliefRequest.STATUS_IN_PROGRESS:
                    inProgress++;
                    break;
                case ReliefRequest.STATUS_COMPLETED:
                    completed++;
                    break;
            }
        }

        tvPendingCount.setText(String.valueOf(pending));
        tvAcceptedCount.setText(String.valueOf(accepted));
        tvInProgressCount.setText(String.valueOf(inProgress));
        tvCompletedCount.setText(String.valueOf(completed));
    }

    private void filterRequests() {
        filteredRequests.clear();
        if ("All".equals(currentStatusFilter)) {
            filteredRequests.addAll(allRequests);
        } else {
            String statusFilter = currentStatusFilter;
            for (ReliefRequest request : allRequests) {
                if (statusFilter.equals(request.getStatus())) {
                    filteredRequests.add(request);
                }
            }
        }

        // Sort by timestamp descending (newest first)
        Collections.sort(filteredRequests, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));

        adapter.notifyDataSetChanged();

        if (filteredRequests.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvRequests.setVisibility(View.GONE);
        } else {
            rvRequests.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private static class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

        private final List<ReliefRequest> requests;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        RequestAdapter(List<ReliefRequest> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_relief_request, parent, false);
            return new RequestViewHolder(view);
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
            TextView tvStatus, tvItemName, tvQuantity, tvPriority, tvDate, tvDistributor, tvAddress, tvNotes;
            View statusBadge;
            View btnAccept, btnStartDelivery, btnComplete, btnViewMap;

            RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvItemName = itemView.findViewById(R.id.tvItemName);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvPriority = itemView.findViewById(R.id.tvPriority);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvDistributor = itemView.findViewById(R.id.tvDistributor);
                tvAddress = itemView.findViewById(R.id.tvAddress);
                tvNotes = itemView.findViewById(R.id.tvNotes);
                statusBadge = itemView.findViewById(R.id.statusBadge);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnStartDelivery = itemView.findViewById(R.id.btnStartDelivery);
                btnComplete = itemView.findViewById(R.id.btnComplete);
                btnViewMap = itemView.findViewById(R.id.btnViewMap);
            }

            void bind(ReliefRequest request) {
                tvItemName.setText(request.getItemName());
                tvQuantity.setText(String.format(Locale.getDefault(), "Qty: %d %s", request.getQuantity(), request.getUnit()));
                tvPriority.setText("Priority: " + request.getPriority());
                tvDate.setText("Requested: " + dateFormat.format(new Date(request.getTimestamp())));

                String status = request.getStatus();
                tvStatus.setText(status);

                // Set status badge color
                int badgeColor;
                switch (status) {
                    case ReliefRequest.STATUS_PENDING:
                        badgeColor = 0xFFFF9800; // Orange
                        break;
                    case ReliefRequest.STATUS_ACCEPTED:
                        badgeColor = 0xFF2196F3; // Blue
                        break;
                    case ReliefRequest.STATUS_IN_PROGRESS:
                        badgeColor = 0xFF9C27B0; // Purple
                        break;
                    case ReliefRequest.STATUS_COMPLETED:
                        badgeColor = 0xFF4CAF50; // Green
                        break;
                    case ReliefRequest.STATUS_CANCELLED:
                        badgeColor = 0xFFF44336; // Red
                        break;
                    default:
                        badgeColor = 0xFF9E9E9E; // Gray
                }
                statusBadge.setBackgroundColor(badgeColor);

                if (request.getDistributorId() != null && !request.getDistributorId().isEmpty()) {
                    tvDistributor.setText("Distributor: " + request.getDistributorId());
                    tvDistributor.setVisibility(View.VISIBLE);
                } else {
                    tvDistributor.setVisibility(View.GONE);
                }

                tvAddress.setText("Location: " + request.getLocationAddress());

                if (request.getNotes() != null && !request.getNotes().isEmpty()) {
                    tvNotes.setText("Notes: " + request.getNotes());
                    tvNotes.setVisibility(View.VISIBLE);
                } else {
                    tvNotes.setVisibility(View.GONE);
                }

                // Setup action buttons based on status
                setupActionButtons(request);
            }

            private void setupActionButtons(ReliefRequest request) {
                String status = request.getStatus();

                // Hide all buttons first
                btnAccept.setVisibility(View.GONE);
                btnStartDelivery.setVisibility(View.GONE);
                btnComplete.setVisibility(View.GONE);
                btnViewMap.setVisibility(View.GONE);

                if (ReliefRequest.STATUS_PENDING.equals(status)) {
                    btnAccept.setVisibility(View.VISIBLE);
                    btnAccept.setOnClickListener(v -> acceptRequest(request));
                } else if (ReliefRequest.STATUS_ACCEPTED.equals(status)) {
                    btnStartDelivery.setVisibility(View.VISIBLE);
                    btnStartDelivery.setOnClickListener(v -> startDelivery(request));
                    btnViewMap.setVisibility(View.VISIBLE);
                    btnViewMap.setOnClickListener(v -> viewOnMap(request));
                } else if (ReliefRequest.STATUS_IN_PROGRESS.equals(status)) {
                    btnComplete.setVisibility(View.VISIBLE);
                    btnComplete.setOnClickListener(v -> completeRequest(request));
                    btnViewMap.setVisibility(View.VISIBLE);
                    btnViewMap.setOnClickListener(v -> viewOnMap(request));
                } else if (ReliefRequest.STATUS_COMPLETED.equals(status)) {
                    btnViewMap.setVisibility(View.VISIBLE);
                    btnViewMap.setOnClickListener(v -> viewOnMap(request));
                }
            }

            private void acceptRequest(ReliefRequest request) {
                updateRequestStatus(request, ReliefRequest.STATUS_ACCEPTED);
            }

            private void startDelivery(ReliefRequest request) {
                updateRequestStatus(request, ReliefRequest.STATUS_IN_PROGRESS);
            }

            private void completeRequest(ReliefRequest request) {
                updateRequestStatus(request, ReliefRequest.STATUS_COMPLETED);
            }

            private void viewOnMap(ReliefRequest request) {
                if (request.getLatitude() != 0 && request.getLongitude() != 0) {
                    Intent intent = new Intent(itemView.getContext(), GoogleMap.class);
                    intent.putExtra("LATITUDE", request.getLatitude());
                    intent.putExtra("LONGITUDE", request.getLongitude());
                    intent.putExtra("ADDRESS", request.getLocationAddress());
                    intent.putExtra("REQUEST_ID", request.getRequestId());
                    itemView.getContext().startActivity(intent);
                } else {
                    Toast.makeText(itemView.getContext(), "Location not available", Toast.LENGTH_SHORT).show();
                }
            }

            private void updateRequestStatus(ReliefRequest request, String newStatus) {
                DatabaseReference requestRef = FirebaseDatabase.getInstance()
                        .getReference("ReliefRequests")
                        .child(request.getRequestId());

                requestRef.child("status").setValue(newStatus)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(itemView.getContext(), "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                            // Create notification for survivor
                            createNotification(request, newStatus);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(itemView.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                        });
            }

            private void createNotification(ReliefRequest request, String newStatus) {
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
                        message = "Your request for " + request.getItemName() + " has been accepted by a distributor";
                        break;
                    case ReliefRequest.STATUS_IN_PROGRESS:
                        type = "REQUEST_IN_PROGRESS";
                        title = "Delivery Started";
                        message = "Your request for " + request.getItemName() + " is now out for delivery";
                        break;
                    case ReliefRequest.STATUS_COMPLETED:
                        type = "REQUEST_COMPLETED";
                        title = "Delivery Completed";
                        message = "Your request for " + request.getItemName() + " has been delivered";
                        break;
                    default:
                        type = "REQUEST_UPDATE";
                        title = "Request Update";
                        message = "Your request status has been updated to " + newStatus;
                }

                Notification notification = new Notification();
                notification.setNotificationId(notifRef.getKey());
                notification.setUserId(request.getSurvivorId());
                notification.setType(type);
                notification.setTitle(title);
                notification.setMessage(message);
                notification.setTimestamp(System.currentTimeMillis());
                notification.setRead(false);

                // Add data for deep linking
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("requestId", request.getRequestId());
                data.put("type", type);
                notification.setData(data);

                notifRef.setValue(notification.toMap());
            }
        }
    }
}