package com.example.projectakhir.ui.shop.Keranjang;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.repository.KeranjangRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class KeranjangViewModel extends ViewModel {
    private final KeranjangRepository cartRepository;
    private LiveData<List<KeranjangItem>> cartItems;
    private final MutableLiveData<Double> _totalPrice = new MutableLiveData<>();
    public LiveData<Double> totalPrice = _totalPrice;
    
    private final MutableLiveData<Double> _orderTotal = new MutableLiveData<>();
    public LiveData<Double> orderTotal = _orderTotal;
    
    private final MutableLiveData<Double> _deliveryFee = new MutableLiveData<>();
    public LiveData<Double> deliveryFee = _deliveryFee;
    
    private final MutableLiveData<Double> _discount = new MutableLiveData<>();
    public LiveData<Double> discount = _discount;

    public KeranjangViewModel() {
        cartRepository = new KeranjangRepository();
        setupCartItems();
    }

    private void setupCartItems() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            cartItems = cartRepository.getCartItems(userId);
            cartItems.observeForever(items -> {
                Log.d("CartDebug", "Cart items updated: " + (items != null ? items.size() : 0));
                calculateTotalPrice(items);
                calculateOrderSummary(items);
            });
        }
    }

    public LiveData<List<KeranjangItem>> getCartItems() {
        if (cartItems == null) {
            setupCartItems();
        }
        return cartItems;
    }

    public void updateCartItemQuantity(String productId, int newQuantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            cartRepository.updateCartItemQuantity(userId, productId, newQuantity);
        }
    }

    public void removeCartItem(String productId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            cartRepository.removeCartItem(userId, productId);
        }
    }

    private void calculateTotalPrice(List<KeranjangItem> items) {
        double subtotal = 0.0;
        if (items != null) {
            for (KeranjangItem item : items) {
                subtotal += item.getProductPrice() * item.getQuantity();
            }
        }
        
        double deliveryFee = items != null && !items.isEmpty() ? 10000.0 : 0.0;
        double discount = 0.0;
        double grandTotal = subtotal + deliveryFee - discount;
        
        _totalPrice.postValue(grandTotal);
    }
    
    private void calculateOrderSummary(List<KeranjangItem> items) {
        double subtotal = 0.0;
        if (items != null) {
            for (KeranjangItem item : items) {
                subtotal += item.getProductPrice() * item.getQuantity();
            }
        }
        
        _orderTotal.postValue(subtotal);
        double deliveryFee = items != null && !items.isEmpty() ? 10000.0 : 0.0;
        _deliveryFee.postValue(deliveryFee);
        double discount = 0.0;
        _discount.postValue(discount);
    }

    public void clearCartForCheckout() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            Log.d("CartDebug", "Clearing cart for user: " + userId);
            // Immediately set empty cart for UI update
            ((MutableLiveData<List<KeranjangItem>>) cartItems).postValue(new ArrayList<>());
            
            cartRepository.clearCart(userId)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("CartDebug", "Cart cleared successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CartDebug", "Failed to clear cart: " + e.getMessage());
                        // If clearing fails, refresh to get actual state
                        setupCartItems();
                    });
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cartRepository.cleanup();
    }
}
