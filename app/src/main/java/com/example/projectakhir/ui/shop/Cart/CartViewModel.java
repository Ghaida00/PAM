package com.example.projectakhir.ui.shop.Cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.projectakhir.data.model.CartItem;
import com.example.projectakhir.data.repository.CartRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CartViewModel extends ViewModel {
    private CartRepository cartRepository;
    private LiveData<List<CartItem>> _cartItems;
    private MutableLiveData<Double> _totalPrice = new MutableLiveData<>();
    public LiveData<Double> totalPrice = _totalPrice;

    public CartViewModel() {
        cartRepository = new CartRepository();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            _cartItems = cartRepository.getCartItems(currentUser.getUid());
        } else {
            _cartItems = new MutableLiveData<>();
        }
        observeCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
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

    private void calculateTotalPrice(List<CartItem> items) {
        double total = 0.0;
        if (items != null) {
            for (CartItem item : items) {
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