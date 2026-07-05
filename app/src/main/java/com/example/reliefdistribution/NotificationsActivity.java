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

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ImageButton btnBack;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private NotificationAdapter adapter;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Notifications");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadNotifications();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        databaseReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }

                // Sort by timestamp descending (newest first)
                Collections.sort(notifications, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
                if (notifications.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvNotifications.setVisibility(View.GONE);
                } else {
                    rvNotifications.setVisibility(View.VISIBLE);
                    tvEmptyState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
                tvEmptyState.setText("Failed to load notifications: " + error.getMessage());
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

        private final List<Notification> notifications;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        NotificationAdapter(List<Notification> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            Notification notification = notifications.get(position);
            holder.bind(notification);
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvMessage, tvTime;
            View unreadIndicator;

            NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
                tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
                tvTime = itemView.findViewById(R.id.tvNotificationTime);
                unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            }

            void bind(Notification notification) {
                tvTitle.setText(notification.getTitle());
                tvMessage.setText(notification.getMessage());
                tvTime.setText(dateFormat.format(new Date(notification.getTimestamp())));

                if (!notification.isRead()) {
                    unreadIndicator.setVisibility(View.VISIBLE);
                    itemView.setBackgroundResource(R.drawable.notification_unread_bg);
                } else {
                    unreadIndicator.setVisibility(View.GONE);
                    itemView.setBackgroundResource(R.drawable.notification_read_bg);
                }

                itemView.setOnClickListener(v -> {
                    if (!notification.isRead()) {
                        markAsRead(notification);
                    }
                    handleNotificationClick(notification);
                });
            }

            private void markAsRead(Notification notification) {
                DatabaseReference notifRef = FirebaseDatabase.getInstance()
                        .getReference("Notifications")
                        .child(notification.getUserId())
                        .child(notification.getNotificationId());

                notifRef.child("read").setValue(true)
                        .addOnSuccessListener(aVoid -> {
                            notification.setRead(true);
                            unreadIndicator.setVisibility(View.GONE);
                            itemView.setBackgroundResource(R.drawable.notification_read_bg);
                        });
            }

            private void handleNotificationClick(Notification notification) {
                // Handle deep linking based on notification type
                String type = notification.getType();
                if (notification.getData() != null && notification.getData().containsKey("requestId")) {
                    String requestId = (String) notification.getData().get("requestId");
                    // Could navigate to request details
                    Toast.makeText(itemView.getContext(), "Request: " + requestId, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}