package com.example.projectakhir.data.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@IgnoreExtraProperties
public class Order {
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_PACKED = "PACKED";
    public static final String STATUS_SHIPPED = "SHIPPED";
    public static final String STATUS_RECEIVED = "RECEIVED";

    private String orderId;
    private String userId;
    private List<String> productIds;
    private double totalAmount;
    private String paymentMethod;
    private String status;
    private long timestamp;
    private Map<String, Integer> productQuantities;
    private String id; // For Firestore compatibility
    private long orderDate; // For Firestore compatibility
    private Map<String, Object> items; // Changed back to Map since Firestore stores it as HashMap

    public Order() {
        // Required empty constructor for Firestore
    }

    public Order(String orderId, String userId, List<String> productIds, double totalAmount, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.productIds = productIds;
        this.items = new HashMap<>(); // Initialize empty map
        for (String productId : productIds) {
            this.items.put(productId, true); // or any other default value
        }
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = STATUS_PAID; // Default status
        this.timestamp = System.currentTimeMillis();
        this.orderDate = System.currentTimeMillis();
        this.id = orderId;
    }

    public Order(String orderId, String userId, List<String> productIds, double totalAmount, 
                String paymentMethod, Map<String, Integer> productQuantities) {
        this(orderId, userId, productIds, totalAmount, paymentMethod);
        this.productQuantities = productQuantities;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
        this.id = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
        // Update items map
        if (this.items == null) {
            this.items = new HashMap<>();
        }
        this.items.clear();
        for (String productId : productIds) {
            this.items.put(productId, true);
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Integer> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(Map<String, Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.orderId = id;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }
}