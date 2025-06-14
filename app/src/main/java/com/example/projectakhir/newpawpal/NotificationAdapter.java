package com.example.projectakhir.newpawpal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onGiveRatingClick(Notification notification); // Untuk tombol "Beri Penilaian"
        void onNotificationItemClick(Notification notification); // Untuk klik keseluruhan item notifikasi
    }

    public NotificationAdapter(List<Notification> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotificationImage;
        TextView tvNotificationTitle;
        TextView tvNotificationTimestamp;
        Button btnGiveRating;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotificationImage = itemView.findViewById(R.id.ivNotificationImage);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationTimestamp = itemView.findViewById(R.id.tvNotificationTimestamp);
            btnGiveRating = itemView.findViewById(R.id.btnGiveRating);
        }

        void bind(final Notification notification, final OnNotificationClickListener listener) {
            tvNotificationTitle.setText(notification.getTitle());
            tvNotificationTimestamp.setText(notification.getTimestamp());

            // Muat gambar jika ada
            if (notification.getImageUrl() != null && !notification.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(notification.getImageUrl())
                        .placeholder(R.drawable.placeholder_image) // Pastikan Anda memiliki gambar placeholder
                        .error(R.drawable.placeholder_image) // Gambar jika terjadi error
                        .into(ivNotificationImage);
                ivNotificationImage.setVisibility(View.VISIBLE);
            } else {
                ivNotificationImage.setVisibility(View.GONE); // Sembunyikan jika tidak ada gambar
            }

            // Tampilkan atau sembunyikan tombol "Beri Penilaian" berdasarkan tipe notifikasi
            if (notification.isOrderNotification()) {
                btnGiveRating.setVisibility(View.VISIBLE);
                btnGiveRating.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onGiveRatingClick(notification);
                    }
                });
            } else {
                btnGiveRating.setVisibility(View.GONE);
            }

            // Set listener untuk klik keseluruhan item notifikasi
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationItemClick(notification);
                }
            });
        }
    }
}
