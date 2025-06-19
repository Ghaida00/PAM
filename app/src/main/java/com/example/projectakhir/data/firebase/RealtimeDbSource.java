package com.example.projectakhir.data.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RealtimeDbSource {

    private DatabaseReference productsRef;
    private DatabaseReference notificationsRef;
    private DatabaseReference reviewsRef;

    public RealtimeDbSource() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");
        notificationsRef = database.getReference("notifications").child("notifications");
        reviewsRef = database.getReference("reviews");
    }

    public LiveData<List<Product>> getProducts() {
        MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null) {
                        // Set the document key as the product ID
                        product.setId(postSnapshot.getKey());
                        products.add(product);
                    }
                }
                productsLiveData.setValue(products);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                productsLiveData.setValue(new ArrayList<>()); // Kirim list kosong saat error
            }
        });
        return productsLiveData;
    }

    // Metode untuk mendapatkan produk berdasarkan ID (opsional, bisa juga filter dari LiveData)
    public LiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();
        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                android.util.Log.d("NotifDebug", "productId: " + productId);
                android.util.Log.d("NotifDebug", "snapshot.exists(): " + snapshot.exists());
                android.util.Log.d("NotifDebug", "snapshot.getValue(): " + snapshot.getValue());
                Product product = snapshot.getValue(Product.class);
                android.util.Log.d("NotifDebug", "Product object: " + product);
                if (product != null) {
                    // Set the document key as the product ID
                    product.setId(snapshot.getKey());
                }
                productLiveData.setValue(product);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                android.util.Log.e("NotifDebug", "onCancelled: " + error.getMessage());
                productLiveData.setValue(null);
            }
        });
        return productLiveData;
    }

    // Metode untuk menambahkan produk (jika admin ingin menambahkan)
    public void addProduct(Product product) {
        productsRef.child(product.getId()).setValue(product);
    }

    
    public LiveData<List<Notification>> getNotifications(String userId) {
        MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
        notificationsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Notification notification = postSnapshot.getValue(Notification.class);
                    if (notification != null) {
                        // Set the document key as the notification ID
                        notification.setId(postSnapshot.getKey());
                        notifications.add(notification);
                    }
                }
                notificationsLiveData.setValue(notifications);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                notificationsLiveData.setValue(new ArrayList<>());
            }
        });
        return notificationsLiveData;
    }

    public void markNotificationAsRead(String userId, String notificationId) {
        notificationsRef.child(notificationId).child("isRead").setValue(true);
    }

    public void addNotification(Notification notification) {
        String key = notificationsRef.push().getKey();
        if (key != null) {
            notification.setId(key);
            notificationsRef.child(key).setValue(notification);
        }
    }

    public void deleteNotification(String notificationId) {
        notificationsRef.child(notificationId).removeValue();
    }

    // ========== REVIEW METHODS ==========
    
    public void addReview(Review review) {
        reviewsRef.child(review.getId()).setValue(review);
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();
        reviewsRef.orderByChild("productId").equalTo(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Review> reviews = new ArrayList<>();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Review review = postSnapshot.getValue(Review.class);
                    if (review != null) {
                        review.setId(postSnapshot.getKey());
                        reviews.add(review);
                    }
                }
                reviewsLiveData.setValue(reviews);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                reviewsLiveData.setValue(new ArrayList<>());
            }
        });
        return reviewsLiveData;
    }
}