package com.example.projectakhir.data.repository;

import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.KeranjangItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.List;

public class KeranjangRepository {
    private FirestoreSource firestoreSource;

    public KeranjangRepository() {
        firestoreSource = new FirestoreSource();
    }

    public LiveData<List<KeranjangItem>> getCartItems(String userId) {
        return firestoreSource.getCartItems(userId);
    }

    public Task<Void> addCartItem(String userId, KeranjangItem cartItem) {
        return firestoreSource.addCartItem(userId, cartItem);
    }

    public Task<Void> updateCartItemQuantity(String userId, String productId, int quantity) {
        return firestoreSource.updateCartItemQuantity(userId, productId, quantity);
    }

    public Task<Void> removeCartItem(String userId, String productId) {
        return firestoreSource.removeCartItem(userId, productId);
    }

    public Task<Void> clearCart(String userId) {
        return firestoreSource.clearCart(userId);
    }

    public void getCartItemsOnce(String userId, OnSuccessListener<List<KeranjangItem>> listener) {
        firestoreSource.getCartItemsOnce(userId, listener);
    }
}