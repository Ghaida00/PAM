package com.example.projectakhir;

// Model data untuk ulasan produk
public class Review {
    private String userId;
    private String username; // Atau ID pengguna untuk ditampilkan
    private int rating; // 1-5 bintang
    private String comment;
    private String imageUrl; // Opsional, untuk gambar ulasan
    private long timestamp; // Waktu ulasan dibuat (misalnya System.currentTimeMillis() atau Firebase ServerValue.TIMESTAMP)

    public Review() {
        // Diperlukan konstruktor kosong untuk Firebase Realtime Database
    }

    public Review(String userId, String username, int rating, String comment, String imageUrl, long timestamp) {
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters (jika diperlukan oleh Firebase atau penggunaan lain)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
