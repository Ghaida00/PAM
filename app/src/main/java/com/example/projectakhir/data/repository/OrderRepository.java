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
}
