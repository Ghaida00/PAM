package com.example.projectakhir.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectakhir.R;
import com.example.projectakhir.data.model.Notification;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.bumptech.glide.Glide;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
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
        TextView notificationMessage;
        TextView notificationDate;
        android.widget.ImageView imgProduct;
        android.widget.Button btnAction;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationDate = itemView.findViewById(R.id.notification_date);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Notification notification) {
            notificationTitle.setText(notification.getTitle());
            notificationMessage.setText(notification.getMessage());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            notificationDate.setText(sdf.format(new Date(notification.getTimestamp())));

            if (notification.getProductImageUrl() != null && !notification.getProductImageUrl().isEmpty()) {
                imgProduct.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(notification.getProductImageUrl())
                        .placeholder(R.drawable.ic_placeholder_image) // Tambahkan placeholder
                        .error(R.drawable.ic_error_image) // Tambahkan error image
                        .into(imgProduct);
            } else {
                imgProduct.setVisibility(View.GONE); // Atau set default icon
                // imgProduct.setImageResource(R.drawable.ic_placeholder_image);
            }

            // Tampilkan tombol aksi jika tipe notifikasi membutuhkan aksi (misal: "Beri Penilaian")
            if (notification.getActionType() != null && notification.getActionType().equals("REVIEW")) {
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("Beri Penilaian");
                btnAction.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    // If there are multiple products, split them
                    String[] productIds = notification.getProductId().split(",");
                    bundle.putStringArray("productIds", productIds);
                    
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_notificationFragment_to_reviewFragment, bundle);
                });
            } else {
                btnAction.setVisibility(View.GONE);
                itemView.setOnClickListener(v -> listener.onNotificationClick(notification));
            }

            // Tampilan berbeda jika belum dibaca
            if (!notification.isRead()) {
                itemView.setBackgroundResource(R.color.unread_notification_background); // Define this color in colors.xml
            } else {
                itemView.setBackgroundResource(android.R.color.transparent);
            }
        }
    }
}