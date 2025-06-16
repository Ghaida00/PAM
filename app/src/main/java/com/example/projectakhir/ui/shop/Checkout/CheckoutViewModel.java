package com.example.projectakhir.ui.shop.Checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.model.User;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.example.projectakhir.data.repository.UserRepository; // Jika Anda memiliki UserRepository
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CheckoutViewModel extends ViewModel {

    private KeranjangRepository cartRepository;
    private UserRepository userRepository; // Opsi jika Anda ingin mengambil data user/alamat di sini

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
        userRepository = new UserRepository(); // Inisialisasi jika digunakan

        loadCheckoutData();
    }

    private void loadCheckoutData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Ambil item keranjang untuk menghitung total
            cartRepository.getCartItems(currentUser.getUid()).observeForever(cartItems -> {
                calculateTotalPrice(cartItems);
            });

            // Ambil alamat pengiriman pengguna
            userRepository.getUserProfile(currentUser.getUid()).addOnSuccessListener(user -> {
                if (user != null) {
                    // Asumsi User model punya getAddress() atau sejenisnya
                    _deliveryAddress.postValue(user.getAddress() != null ? user.getAddress() : "Alamat belum diatur");
                }
            }).addOnFailureListener(e -> {
                _deliveryAddress.postValue("Gagal memuat alamat.");
            });

        } else {
            _totalPrice.postValue(0.0);
            _deliveryAddress.postValue("Silakan login untuk melihat alamat.");
        }
    }

    private void calculateTotalPrice(List<KeranjangItem> items) {
        double total = 0.0;
        if (items != null) {
            for (KeranjangItem item : items) {
                total += item.getProductPrice() * item.getQuantity();
            }
        }
        _totalPrice.postValue(total);
    }
}
