package com.example.projectakhir;

// Model data untuk notifikasi
public class Notification {
    private String title;
    private String timestamp;
    private String imageUrl; // Opsional, untuk notifikasi dengan gambar (misal: pesanan tiba)
    private boolean isOrderNotification; // Untuk membedakan apakah ini notifikasi pesanan yang perlu tombol rating

    public Notification() {
        // Diperlukan konstruktor kosong untuk Firebase Realtime Database
    }

    public Notification(String title, String timestamp, String imageUrl, boolean isOrderNotification) {
        this.title = title;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.isOrderNotification = isOrderNotification;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isOrderNotification() {
        return isOrderNotification;
    }

    // Setters (jika diperlukan, Firebase biasanya menggunakan getter/setter)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOrderNotification(boolean orderNotification) {
        isOrderNotification = orderNotification;
    }
}
