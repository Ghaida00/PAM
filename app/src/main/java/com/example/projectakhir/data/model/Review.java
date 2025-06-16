package com.example.projectakhir.data.model;

public class Review {
    private String id;
    private String productId;
    private String userId; // UID dari user yang memberikan review
    private float rating; // e.g., 1.0 to 5.0
    private String comment;
    private long timestamp;
    private String imageUrl; // URL gambar dari Firebase Storage

    public Review() {
        // Diperlukan untuk Firestore
    }

    public Review(String id, String productId, String userId, float rating, String comment, long timestamp, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
