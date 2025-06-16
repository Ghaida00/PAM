package com.example.projectakhir.data.firebase;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectakhir.data.model.KeranjangItem;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.model.Order;
import com.example.projectakhir.data.model.Product; // Perlu untuk CartItem
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirestoreSource {

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public FirestoreSource() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // --- User Profile Operations (Opsional, untuk menyimpan nama user dll) ---
    public Task<Void> saveUserProfile(User user) {
        return db.collection("users").document(user.getUid()).set(user, SetOptions.merge());
    }

    public LiveData<User> getUserProfile(String userId) {
        MutableLiveData<User> userProfileLiveData = new MutableLiveData<>();
        db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        userProfileLiveData.setValue(null);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        userProfileLiveData.setValue(documentSnapshot.toObject(User.class));
                    } else {
                        userProfileLiveData.setValue(null);
                    }
                });
        return userProfileLiveData;
    }


    // --- Cart Operations ---
    public LiveData<List<KeranjangItem>> getCartItems(String userId) {
        MutableLiveData<List<KeranjangItem>> cartItemsLiveData = new MutableLiveData<>();
        db.collection("users").document(userId).collection("cartItems")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        cartItemsLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<KeranjangItem> cartItems = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            // Firestore tidak secara otomatis mengkonversi nested object
                            // Kita harus mengambil satu per satu properti CartItem
                            String productId = doc.getString("productId");
                            String productName = doc.getString("productName");
                            double productPrice = doc.getDouble("productPrice") != null ? doc.getDouble("productPrice") : 0.0;
                            String productImageUrl = doc.getString("productImageUrl");
                            int quantity = doc.getLong("quantity") != null ? doc.getLong("quantity").intValue() : 0;
                            cartItems.add(new KeranjangItem(productId, productName, productPrice, productImageUrl, quantity));
                        }
                        cartItemsLiveData.setValue(cartItems);
                    } else {
                        cartItemsLiveData.setValue(new ArrayList<>());
                    }
                });
        return cartItemsLiveData;
    }


    public Task<Void> addCartItem(String userId, KeranjangItem cartItem) {
        // Menggunakan Map karena CartItem memiliki Product di dalamnya, yang lebih mudah di-flatten
        Map<String, Object> cartItemData = new HashMap<>();
        cartItemData.put("productId", cartItem.getProductId());
        cartItemData.put("productName", cartItem.getProductName());
        cartItemData.put("productPrice", cartItem.getProductPrice());
        cartItemData.put("productImageUrl", cartItem.getProductImageUrl());
        cartItemData.put("quantity", cartItem.getQuantity());

        return db.collection("users").document(userId)
                .collection("cartItems").document(cartItem.getProductId()) // Gunakan productId sebagai ID dokumen
                .set(cartItemData, SetOptions.merge()); // Merge untuk update kuantitas jika item sudah ada
    }

    public Task<Void> updateCartItemQuantity(String userId, String productId, int quantity) {
        return db.collection("users").document(userId)
                .collection("cartItems").document(productId)
                .update("quantity", quantity);
    }

    public Task<Void> removeCartItem(String userId, String productId) {
        return db.collection("users").document(userId)
                .collection("cartItems").document(productId)
                .delete();
    }

    public Task<Void> clearCart(String userId) {
        // Hapus semua dokumen di sub-koleksi cartItems
        CollectionReference cartItemsRef = db.collection("users").document(userId).collection("cartItems");
        return cartItemsRef.get().continueWithTask(task -> {
            List<Task<Void>> deleteTasks = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                deleteTasks.add(doc.getReference().delete());
            }
            return com.google.android.gms.tasks.Tasks.whenAll(deleteTasks);
        });
    }

    // --- Order Operations ---
    public Task<Void> placeOrder(Order order) {
        return db.collection("orders").document(order.getId()).set(order);
    }

    public LiveData<List<Order>> getOrderHistory(String userId) {
        MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        ordersLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Order> orders = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Order order = doc.toObject(Order.class);
                            if (order != null) {
                                orders.add(order);
                            }
                        }
                        ordersLiveData.setValue(orders);
                    } else {
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                });
        return ordersLiveData;
    }


    // --- Notification Operations ---
    public LiveData<List<Notification>> getNotifications(String userId) {
        MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
        db.collection("users").document(userId).collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        notificationsLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Notification notification = doc.toObject(Notification.class);
                            if (notification != null) {
                                notifications.add(notification);
                            }
                        }
                        notificationsLiveData.setValue(notifications);
                    } else {
                        notificationsLiveData.setValue(new ArrayList<>());
                    }
                });
        return notificationsLiveData;
    }

    public Task<Void> markNotificationAsRead(String userId, String notificationId) {
        return db.collection("users").document(userId)
                .collection("notifications").document(notificationId)
                .update("read", true); // Field harus "isRead" di model, jadi sesuaikan
    }

    // --- Review Operations ---
    public Task<String> uploadReviewImage(Uri imageUri, String reviewId) {
        StorageReference storageRef = storage.getReference().child("review_images/" + reviewId + "_" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = storageRef.putFile(imageUri);

        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageRef.getDownloadUrl(); // Mendapatkan URL setelah upload selesai
        }).continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().toString();
            } else {
                throw task.getException();
            }
        });
    }


    public Task<Void> submitReview(Review review) {
        return db.collection("productReviews").document(review.getId()).set(review);
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();
        db.collection("productReviews")
                .whereEqualTo("productId", productId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        reviewsLiveData.setValue(new ArrayList<>());
                        return;
                    }
                    if (querySnapshot != null) {
                        List<Review> reviews = new ArrayList<>();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Review review = doc.toObject(Review.class);
                            if (review != null) {
                                reviews.add(review);
                            }
                        }
                        reviewsLiveData.setValue(reviews);
                    } else {
                        reviewsLiveData.setValue(new ArrayList<>());
                    }
                });
        return reviewsLiveData;
    }
}