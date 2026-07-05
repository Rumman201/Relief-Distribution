package com.example.reliefdistribution;

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

public class MyReliefRequestsActivity extends AppCompatActivity {

    private RecyclerView rvRequests;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabCreateRequest;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private RequestAdapter adapter;
    private List<ReliefRequest> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_relief_requests);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ReliefRequests");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadRequests();
    }

    private void initViews() {
        rvRequests = findViewById(R.id.rvRequests);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabCreateRequest = findViewById(R.id.fabCreateRequest);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        requestList = new ArrayList<>();
        adapter = new RequestAdapter(requestList);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        fabCreateRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateReliefRequestActivity.class));
        });
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        rvRequests.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        databaseReference.orderByChild("survivorId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                        progressBar.setVisibility(View.GONE);
                        if (requestList.isEmpty()) {
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
                        Toast.makeText(MyReliefRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                    }
                });
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
            }
        }
    }
}