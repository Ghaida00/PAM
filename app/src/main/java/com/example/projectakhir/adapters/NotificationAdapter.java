package com.example.projectakhir.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Import Glide
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Notification;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onGiveRatingClick(Notification notification); // New listener method for rating button
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notifications.clear();
        this.notifications.addAll(newNotifications);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
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
        TextView notificationTitle;
        ImageView notificationImage; // RENAMED from notificationMessage
        TextView notificationDate;
        Button btnGiveRating; // NEW: Button for giving rating

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            notificationImage = itemView.findViewById(R.id.ivNotificationImage); // RENAMED and assigned
            notificationDate = itemView.findViewById(R.id.tvNotificationTimestamp);
            btnGiveRating = itemView.findViewById(R.id.btnGiveRating); // NEW: Assigned
        }

        public void bind(Notification notification) {
            notificationTitle.setText(notification.getTitle());

            // Load image using Glide if URL exists, otherwise hide ImageView
            if (notification.getImageUrl() != null && !notification.getImageUrl().isEmpty()) {
                notificationImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(notification.getImageUrl())
                        .placeholder(R.drawable.ic_placeholder_image)
                        .into(notificationImage);
            } else {
                notificationImage.setVisibility(View.GONE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
            notificationDate.setText(sdf.format(new Date(notification.getTimestamp())));

            // Tampilan berbeda jika belum dibaca
            if (!notification.isRead()) {
                itemView.setBackgroundResource(R.color.unread_notification_background); // Define this color in colors.xml
            } else {
                itemView.setBackgroundResource(android.R.color.transparent);
            }

            // Logic to show/hide and handle click for "Beri Penilaian" button
            // ASSUMPTION: Notification model has a getType() or isOrderDelivered() method
            // for example, if notification.getType().equals("order_delivered")
            if (notification.getType() != null && notification.getType().equals("order_delivered")) { // Example condition
                btnGiveRating.setVisibility(View.VISIBLE);
                btnGiveRating.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onGiveRatingClick(notification);
                    }
                });
            } else {
                btnGiveRating.setVisibility(View.GONE);
                btnGiveRating.setOnClickListener(null); // Clear listener if button is hidden
            }

            // Set overall item click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });
        }
    }
}
