package com.example.projectakhir.data.repository;

import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.Order;
import com.google.android.gms.tasks.Task;
import java.util.List;

public class OrderRepository {
    private FirestoreSource firestoreSource;

    public OrderRepository() {
        firestoreSource = new FirestoreSource();
    }

    public Task<Void> placeOrder(Order order) {
        return firestoreSource.placeOrder(order);
    }

    public LiveData<List<Order>> getOrderHistory(String userId) {
        return firestoreSource.getOrderHistory(userId);
    }

    public void saveOrder(Order order) {
        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("orders").document(order.getId()).set(order)
            .addOnSuccessListener(aVoid -> android.util.Log.d("OrderDebug", "Order " + order.getId() + " berhasil dicatat"))
            .addOnFailureListener(e -> android.util.Log.e("OrderDebug", "Gagal mencatat order: " + e.getMessage()));
    }
}
