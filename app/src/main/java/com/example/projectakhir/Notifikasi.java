package com.example.projectakhir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class Notifikasi extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifikasi);

        rvNotifications = findViewById(R.id.rvNotifications);
        backButton = findViewById(R.id.backButton); // Inisialisasi tombol kembali
        TextView tvTitle = findViewById(R.id.tvTitle); // Inisialisasi judul

        // Mengatur judul halaman
        tvTitle.setText("Notifikasi");

        // Mengatur listener untuk tombol kembali
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Kembali ke halaman sebelumnya
            }
        });

        notificationList = new ArrayList<>();
        // Dummy data notifikasi
        // Anda bisa mengambil data ini dari Firebase Realtime Database di masa depan
        notificationList.add(new Notification(
                "Pesanan Tiba di Tujuan",
                "12/4/25 12:00",
                "https://placehold.co/100x100/A0D9B4/FFFFFF?text=RING", // Placeholder image
                true // Is it an order notification?
        ));
        notificationList.add(new Notification(
                "Pesanan di Antar ke Tujuan",
                "12/4/25 10:00",
                null, // No image for this notification
                false
        ));
        notificationList.add(new Notification(
                "Pesanan Telah Diteruskan ke Penjual",
                "12/4/25 09:00",
                null,
                false
        ));
        notificationList.add(new Notification(
                "Pembayaran Berhasil!",
                "12/4/25 08:30",
                null,
                false
        ));
        // Tambahkan lebih banyak dummy data jika diperlukan
        notificationList.add(new Notification(
                "Diskon Spesial Hari Raya!",
                "11/4/25 18:00",
                "https://placehold.co/100x100/FFDDC1/000000?text=SALE", // Placeholder image
                false
        ));
        notificationList.add(new Notification(
                "Ada Produk Baru di Kategori Makanan!",
                "10/4/25 09:00",
                null,
                false
        ));

        notificationAdapter = new NotificationAdapter(notificationList, this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(notificationAdapter);
    }

    @Override
    public void onGiveRatingClick(Notification notification) {
        Intent intent = new Intent(Notifikasi.this, NilaiProduk.class);
        startActivity(intent);
    }

    @Override
    public void onNotificationItemClick(Notification notification) {
        // Implementasi logika saat item notifikasi diklik
        Toast.makeText(this, "Detail notifikasi: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        // Contoh: Arahkan ke halaman detail notifikasi
        // Intent intent = new Intent(Notifikasi.this, NotificationDetailActivity.class);
        // intent.putExtra("notification_id", notification.getId());
        // startActivity(intent);
    }
}
