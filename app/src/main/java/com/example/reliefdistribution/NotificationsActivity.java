package com.example.reliefdistribution;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reliefdistribution.databinding.ActivityNotificationsBinding;
import com.example.reliefdistribution.databinding.ItemNotificationBinding;
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
 * Activity for displaying user alerts and dispatch updates.
 * Modernized in Phase 10.1 with ViewBinding and compile-time null safety.
 */
public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    private NotificationAdapter adapter;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Notifications");
        currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        setupRecyclerView();
        setupClickListeners();
        loadNotifications();
    }

    private void setupRecyclerView() {
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setHasFixedSize(true);
        binding.rvNotifications.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadNotifications() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvNotifications.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);

        databaseReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                notifications.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                    }
                }

                Collections.sort(notifications, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));
                adapter.notifyDataSetChanged();

                binding.progressBar.setVisibility(View.GONE);
                if (notifications.isEmpty()) {
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    binding.rvNotifications.setVisibility(View.GONE);
                } else {
                    binding.rvNotifications.setVisibility(View.VISIBLE);
                    binding.emptyStateLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
                binding.tvEmptyState.setText("Failed to load notifications: " + error.getMessage());
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
            ItemNotificationBinding itemBinding = ItemNotificationBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new NotificationViewHolder(itemBinding);
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
            private final ItemNotificationBinding itemBinding;

            NotificationViewHolder(@NonNull ItemNotificationBinding itemBinding) {
                super(itemBinding.getRoot());
                this.itemBinding = itemBinding;
            }

            void bind(Notification notification) {
                itemBinding.tvNotificationTitle.setText(notification.getTitle());
                itemBinding.tvNotificationMessage.setText(notification.getMessage());
                itemBinding.tvNotificationTime.setText(dateFormat.format(new Date(notification.getTimestamp())));

                if (!notification.isRead()) {
                    itemBinding.vUnreadIndicator.setVisibility(View.VISIBLE);
                    itemView.setBackgroundResource(R.drawable.notification_unread_bg);
                } else {
                    itemBinding.vUnreadIndicator.setVisibility(View.GONE);
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
                            itemBinding.vUnreadIndicator.setVisibility(View.GONE);
                            itemView.setBackgroundResource(R.drawable.notification_read_bg);
                        });
            }

            private void handleNotificationClick(Notification notification) {
                if (notification.getData() != null && notification.getData().containsKey("requestId")) {
                    String requestId = (String) notification.getData().get("requestId");
                    Toast.makeText(itemView.getContext(), "Request: " + requestId, Toast.LENGTH_SHORT).show();
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
