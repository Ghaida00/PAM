package com.example.projectakhir.data.model;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String type;
    private String actionType;
    private long timestamp;
    private boolean isRead;
    private String imageUrl;

    public Notification() {
        // Diperlukan untuk Firestore
    }

    public Notification(String id, String title, String message, String type, long timestamp, boolean isRead, String imageUrl, String actionType) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.imageUrl = imageUrl;
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getImageUrl() { return imageUrl; } // NEW: Getter untuk imageUrl
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // NEW: Setter untuk imageUrl
}