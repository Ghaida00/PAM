package com.example.projectakhir.ui.shop.Checkout;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.model.Order;
import com.example.projectakhir.ui.auth.AuthManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.projectakhir.ui.checkout.CheckoutState;
import com.example.projectakhir.data.repository.OrderRepository;

public class CheckoutViewModel extends ViewModel {
    private final OrderRepository orderRepository;
    private final FirestoreSource firestoreSource;
    private final AuthManager authManager;
    private final MutableLiveData<CheckoutState> _checkoutState = new MutableLiveData<>();
    public LiveData<CheckoutState> checkoutState = _checkoutState;

    public CheckoutViewModel(FirestoreSource firestoreSource, AuthManager authManager) {
        this.firestoreSource = firestoreSource;
        this.authManager = authManager;
        this.orderRepository = new OrderRepository();
    }

    public void placeOrder(String userId, List<String> productIds, double totalAmount, 
                          String paymentMethod, Map<String, Integer> productQuantities,
                          OnSuccessListener<DocumentReference> onSuccess) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, userId, productIds, totalAmount, paymentMethod, productQuantities);
        
        orderRepository.placeOrder(order)
            .addOnSuccessListener(aVoid -> {
                // Start observing order status after placing order
                orderRepository.observeOrderStatus(userId);
                if (onSuccess != null) {
                    onSuccess.onSuccess(null);
                }
            });
    }
}
