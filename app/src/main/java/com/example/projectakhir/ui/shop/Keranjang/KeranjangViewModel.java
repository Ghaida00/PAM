package com.example.projectakhir.ui.shop.Keranjang;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class KeranjangViewModel extends ViewModel {
    private KeranjangRepository cartRepository;
    private LiveData<List<KeranjangItem>> _cartItems;
    private MutableLiveData<Double> _totalPrice = new MutableLiveData<>();
    public LiveData<Double> totalPrice = _totalPrice;

    public KeranjangViewModel() {
        cartRepository = new KeranjangRepository();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            _cartItems = cartRepository.getCartItems(currentUser.getUid());
        } else {
            _cartItems = new MutableLiveData<>();
        }
        observeCartItems();
    }

    public LiveData<List<KeranjangItem>> getCartItems() {
        return _cartItems;
    }

    private void observeCartItems() {
        _cartItems.observeForever(cartItems -> {
            calculateTotalPrice(cartItems);
        });
    }

    public void updateCartItemQuantity(String productId, int newQuantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            cartRepository.updateCartItemQuantity(userId, productId, newQuantity)
                    .addOnCompleteListener(task -> {
                        // LiveData akan update otomatis
                    });
        }
    }

    public void removeCartItem(String productId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            cartRepository.removeCartItem(userId, productId)
                    .addOnCompleteListener(task -> {
                        // LiveData akan update otomatis
                    });
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

    public void clearCartForCheckout() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            cartRepository.clearCart(userId)
                    .addOnCompleteListener(task -> {
                        // Cart cleared, LiveData will update
                    });
        }
    }
}
