package com.example.projectakhir.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class KeranjangRepository {
    private final FirebaseFirestore db;
    private ListenerRegistration cartListener;
    private final MutableLiveData<List<KeranjangItem>> cartItemsLiveData = new MutableLiveData<>();

    public KeranjangRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<KeranjangItem>> getCartItems(String userId) {
        if (cartListener != null) {
            cartListener.remove();
        }

        cartListener = db.collection("users")
                .document(userId)
                .collection("cart")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    List<KeranjangItem> items = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            KeranjangItem item = doc.toObject(KeranjangItem.class);
                            if (item != null) {
                                items.add(item);
                            }
                        }
                    }
                    cartItemsLiveData.setValue(items);
                });

        return cartItemsLiveData;
    }

    public Task<Void> addCartItem(String userId, KeranjangItem item) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("cart")
                .document(item.getProductId())
                .set(item);
    }

    public Task<Void> updateCartItemQuantity(String userId, String productId, int newQuantity) {
        return db.collection("users")
                .document(userId)
                .collection("cart")
                .document(productId)
                .update("quantity", newQuantity);
    }

    public Task<Void> removeCartItem(String userId, String productId) {
        return db.collection("users")
                .document(userId)
                .collection("cart")
                .document(productId)
                .delete();
    }

    public Task<Void> clearCart(String userId) {
        return db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        return Tasks.forException(task.getException());
                    }

                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : task.getResult()) {
                        batch.delete(doc.getReference());
                    }
                    return batch.commit();
                });
    }

    public void cleanup() {
        if (cartListener != null) {
            cartListener.remove();
            cartListener = null;
        }
    }
}