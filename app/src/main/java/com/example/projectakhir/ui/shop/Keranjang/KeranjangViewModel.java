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
    
    // Tambahan untuk checkout
    private MutableLiveData<Double> _orderTotal = new MutableLiveData<>();
    public LiveData<Double> orderTotal = _orderTotal;
    
    private MutableLiveData<Double> _deliveryFee = new MutableLiveData<>();
    public LiveData<Double> deliveryFee = _deliveryFee;
    
    private MutableLiveData<Double> _discount = new MutableLiveData<>();
    public LiveData<Double> discount = _discount;

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
            calculateOrderSummary(cartItems);
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
        double subtotal = 0.0;
        if (items != null) {
            for (KeranjangItem item : items) {
                subtotal += item.getProductPrice() * item.getQuantity();
            }
        }
        
        // Calculate delivery fee (flat rate Rp 10.000)
        double deliveryFee = 10000.0;
        
        // Calculate discount (0 for now)
        double discount = 0.0;
        
        // Calculate grand total
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
        
        // Set order total (subtotal)
        _orderTotal.postValue(subtotal);
        
        // Set delivery fee (flat rate Rp 10.000)
        double deliveryFee = 10000.0;
        _deliveryFee.postValue(deliveryFee);
        
        // Set discount (0 for now, bisa diimplementasikan nanti)
        double discount = 0.0;
        _discount.postValue(discount);
        
        // Total price sudah dihitung di calculateTotalPrice
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
