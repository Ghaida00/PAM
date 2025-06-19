package com.example.projectakhir.data.model;

public class Notification {
    // Notification Types
    public static final String TYPE_ORDER_RECEIVED = "ORDER_RECEIVED";
    public static final String TYPE_ORDER_COMPLETED = "ORDER_COMPLETED";
    public static final String TYPE_ORDER_SHIPPED = "ORDER_SHIPPED";
    public static final String TYPE_REVIEW_REQUEST = "REVIEW_REQUEST";
    public static final String TYPE_REVIEW_REMINDER = "REVIEW_REMINDER";
    public static final String TYPE_MULTI_PRODUCT_ORDER = "MULTI_PRODUCT_ORDER";
    public static final String TYPE_PROMOTION = "PROMOTION";
    public static final String TYPE_ORDER_CANCELLED = "ORDER_CANCELLED";
    public static final String TYPE_PRODUCT_AVAILABLE = "PRODUCT_AVAILABLE";
    public static final String TYPE_REVIEW_PUBLISHED = "REVIEW_PUBLISHED";

    // Action Types
    public static final String ACTION_REVIEW = "REVIEW";
    public static final String ACTION_PROMO = "PROMO";
    public static final String ACTION_ORDER_CANCELLED = "ORDER_CANCELLED";
    public static final String ACTION_PRODUCT_AVAILABLE = "PRODUCT_AVAILABLE";
    public static final String ACTION_REVIEW_PUBLISHED = "REVIEW_PUBLISHED";

    private String id;
    private String userId;
    private String title;
    private String message;
    private String actionType;
    private String productId;
    private long timestamp;
    private boolean isRead;
    private String type;
    private String imageUrl;
    private String productName;
    private String productImageUrl;

    public Notification() {
        // Required empty constructor for Firestore
    }

    public Notification(String id, String userId, String title, String message, 
                       String actionType, String productId, long timestamp, 
                       boolean isRead, String type) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.actionType = actionType;
        this.productId = productId;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = type;
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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}