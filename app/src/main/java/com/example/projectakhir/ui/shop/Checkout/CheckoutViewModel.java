package com.example.projectakhir.ui.shop.Checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CheckoutViewModel extends ViewModel {

    private KeranjangRepository cartRepository;

    private MutableLiveData<Double> _totalPrice = new MutableLiveData<>();
    public LiveData<Double> getTotalPrice() {
        return _totalPrice;
    }

    private MutableLiveData<String> _deliveryAddress = new MutableLiveData<>();
    public LiveData<String> getDeliveryAddress() {
        return _deliveryAddress;
    }

    public CheckoutViewModel() {
        cartRepository = new KeranjangRepository();
        loadCheckoutData();
    }

    private void loadCheckoutData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Ambil item keranjang untuk menghitung total
            cartRepository.getCartItems(currentUser.getUid()).observeForever(cartItems -> {
                calculateTotalPrice(cartItems);
            });

            // Set alamat default untuk sementara
            _deliveryAddress.postValue("Alamat pengiriman akan diambil dari profil pengguna");
        } else {
            _totalPrice.postValue(0.0);
            _deliveryAddress.postValue("Silakan login untuk melihat alamat.");
        }
    }

    private void calculateTotalPrice(List<KeranjangItem> items) {
        double subtotal = 0.0;
        if (items != null) {
            for (KeranjangItem item : items) {
                subtotal += item.getProductPrice() * item.getQuantity();
            }
        }
        
        // Add delivery fee (flat rate Rp 10.000)
        double deliveryFee = 10000.0;
        double total = subtotal + deliveryFee;
        
        _totalPrice.postValue(total);
    }
}
