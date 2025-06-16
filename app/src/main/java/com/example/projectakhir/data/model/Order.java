package com.example.projectakhir.data.model;

import java.util.List;

public class Order {
    private String id;
    private String userId;
    private List<KeranjangItem> items; // Bisa juga menyimpan hanya productId dan quantity
    private double totalAmount;
    private String status; // e.g., "Pending", "Shipped", "Delivered", "Cancelled"
    private String paymentMethod;
    private long orderDate; // Timestamp

    public Order() {
        // Diperlukan untuk Firestore
    }

    public Order(String id, String userId, List<KeranjangItem> items, double totalAmount, String status, String paymentMethod, long orderDate) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<KeranjangItem> getItems() { return items; }
    public void setItems(List<KeranjangItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
}