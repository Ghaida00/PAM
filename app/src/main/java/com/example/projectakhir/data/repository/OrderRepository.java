package com.example.projectakhir.data.repository;

import android.text.TextUtils;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.model.Order;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.UUID;

public class OrderRepository {
    private final FirestoreSource firestoreSource;
    private final RealtimeDbSource realtimeDbSource;
    private static final String TAG = "OrderRepository";

    public OrderRepository() {
        this.firestoreSource = new FirestoreSource();
        this.realtimeDbSource = new RealtimeDbSource();
    }

    public Task<Void> placeOrder(Order order) {
        return firestoreSource.placeOrder(order);
    }

    public LiveData<List<Order>> getOrderHistory(String userId) {
        return firestoreSource.getOrderHistory(userId);
    }

    public void saveOrder(Order order) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("orders").document(order.getOrderId()).set(order)
            .addOnSuccessListener(aVoid -> android.util.Log.d("OrderDebug", "Order " + order.getOrderId() + " berhasil dicatat"))
            .addOnFailureListener(e -> android.util.Log.e("OrderDebug", "Gagal mencatat order: " + e.getMessage()));
    }

    public void observeOrderStatus(String userId) {
        FirebaseFirestore.getInstance().collection("orders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener((snapshots, error) -> {
                if (error != null) {
                    Log.e(TAG, "Listen failed.", error);
                    return;
                }

                if (snapshots == null) return;

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.MODIFIED || 
                        change.getType() == DocumentChange.Type.ADDED) {
                        Order updatedOrder = change.getDocument().toObject(Order.class);
                        generateNotificationForOrder(updatedOrder);
                    }
                }
            });
    }

    private void generateNotificationForOrder(Order order) {
        String title;
        String message;
        String type;
        String actionType = Notification.ACTION_REVIEW;

        // Selalu set status ke PAID untuk testing
        order.setStatus(Order.STATUS_PAID);
        
        switch (order.getStatus()) {
            case Order.STATUS_PAID:
                title = "Pesanan Diterima";
                message = "Pesanan #" + order.getOrderId() + " telah diterima dan sedang diproses";
                type = Notification.TYPE_ORDER_RECEIVED;
                break;
            case Order.STATUS_PACKED:
                title = "Pesanan Diproses";
                message = "Pesanan #" + order.getOrderId() + " sedang dikemas";
                type = Notification.TYPE_ORDER_COMPLETED;
                break;
            case Order.STATUS_SHIPPED:
                title = "Pesanan Dikirim";
                message = "Pesanan #" + order.getOrderId() + " telah dikirim dan dalam perjalanan";
                type = Notification.TYPE_ORDER_SHIPPED;
                break;
            case Order.STATUS_RECEIVED:
                title = "Berikan Ulasan";
                message = "Bagaimana pengalaman Anda dengan produk yang baru dibeli?";
                type = Notification.TYPE_REVIEW_REQUEST;
                break;
            default:
                return;
        }

        // Handle null productIds
        List<String> productIds = order.getProductIds();
        String productIdsString = "";
        if (productIds != null && !productIds.isEmpty()) {
            productIdsString = TextUtils.join(",", productIds);
        }

        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            order.getUserId(),
            title,
            message,
            actionType,
            productIdsString,
            System.currentTimeMillis(),
            false,
            type
        );

        // Use RealtimeDbSource instead of FirestoreSource
        realtimeDbSource.addNotification(notification);
    }
}
