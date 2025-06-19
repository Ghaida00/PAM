package com.example.projectakhir.ui.shop.Payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.model.Order;
import com.example.projectakhir.data.repository.OrderRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentViewModel extends ViewModel {

    private OrderRepository orderRepository;
    private MutableLiveData<Boolean> _paymentProcessStatus = new MutableLiveData<>();
    public LiveData<Boolean> paymentProcessStatus = _paymentProcessStatus;

    public PaymentViewModel() {
        orderRepository = new OrderRepository();
    }

    /**
     * Memproses pembayaran dan menyimpan pesanan ke database.
     * @param paymentMethod Metode pembayaran yang digunakan.
     * @param totalAmount Jumlah total yang dibayar.
     * @param cartItems Daftar item yang dibeli.
     */
    public void processPaymentAndPlaceOrder(String paymentMethod, double totalAmount, List<KeranjangItem> cartItems) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            _paymentProcessStatus.postValue(false);
            return;
        }

        String userId = currentUser.getUid();
        List<String> productIds = new ArrayList<>();
        Map<String, Integer> productQuantities = new HashMap<>();
        for (KeranjangItem item : cartItems) {
            productIds.add(item.getProductId());
            productQuantities.put(item.getProductId(), item.getQuantity());
        }
        Order newOrder = new Order(
            null, // orderId
            userId,
            productIds,
            totalAmount,
            paymentMethod,
            productQuantities
        );
        orderRepository.placeOrder(newOrder)
                .addOnSuccessListener(aVoid -> {
                    _paymentProcessStatus.postValue(true);
                    // Opsional: bersihkan keranjang setelah pesanan berhasil
                    // cartRepository.clearCart(currentUser.getUid());
                })
                .addOnFailureListener(e -> {
                    _paymentProcessStatus.postValue(false);
                    // Log error
                });
    }
}
