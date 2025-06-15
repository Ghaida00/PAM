// package com.example.projectakhir.data.firebase.FirestoreSource.java

package com.example.projectakhir.data.firebase;

import android.content.Context; // NEW: Import Context
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64; // Import for Base64

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectakhir.data.model.CartItem;
import com.example.projectakhir.data.model.Notification;
import com.example.projectakhir.data.model.Order;
import com.example.projectakhir.data.model.Product;
import com.example.projectakhir.data.model.Review;
import com.example.projectakhir.data.model.User;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks; // Import Tasks for chaining
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
// REMOVED: import com.google.firebase.storage.FirebaseStorage;
// REMOVED: import com.google.firebase.storage.StorageReference;
// REMOVED: import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FirestoreSource {
    private FirebaseFirestore db;

    public Task<Void> saveUserProfile(User user) {
        return db.collection("users").document(user.getUid()).set(user);
    }

    public FirestoreSource() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Mengambil dokumen pengguna dari koleksi 'users' berdasarkan userId.
     * @param userId ID pengguna.
     * @return Task<DocumentSnapshot> yang berisi data dokumen pengguna.
     */
    public Task<DocumentSnapshot> getUserDocument(String userId) {
        return db.collection("users").document(userId).get();
    }

    /**
     * Mengonversi gambar dari Uri menjadi string Base64.
     * PENTING: Metode ini sekarang memerlukan Context untuk membaca Uri.
     * String Base64 ini dapat disimpan langsung di Firestore/Realtime Database.
     * Namun, sangat tidak disarankan untuk gambar besar karena batas ukuran dokumen dan kinerja.
     *
     * @param context Konteks aplikasi (misalnya, dari Activity/Fragment).
     * @param imageUri URI gambar yang akan diunggah.
     * @return Task<String> yang akan selesai dengan string Base64 gambar.
     */
    public Task<String> uploadReviewImage(Context context, Uri imageUri) { // reviewId parameter removed as it's not used for path
        if (imageUri == null || context == null) {
            return Tasks.forResult(null); // Mengembalikan null jika tidak ada URI gambar atau konteks
        }

        return Tasks.call(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream); // Kompresi gambar
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT); // Encode ke Base64 string
            } catch (Exception e) {
                throw new RuntimeException("Gagal mengonversi gambar ke Base64: " + e.getMessage(), e);
            }
        });
    }

    // Metode lain yang mungkin sudah ada atau akan Anda tambahkan
    public Task<Void> placeOrder(Order order) {
        // Implementasi untuk menempatkan pesanan
        return db.collection("orders").add(order).continueWith(task -> null);
    }

    public LiveData<List<Order>> getOrderHistory(String userId) {
        MutableLiveData<List<Order>> liveData = new MutableLiveData<>();
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        // Handle error
                        liveData.postValue(null);
                        return;
                    }
                    if (snapshots != null) {
                        List<Order> orders = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            orders.add(doc.toObject(Order.class));
                        }
                        liveData.postValue(orders);
                    }
                });
        return liveData;
    }

    // Contoh metode lain untuk Cart (dari CartRepository)
    public Task<Void> addCartItem(String userId, CartItem cartItem) {
        return db.collection("users").document(userId)
                .collection("cartItems").document(cartItem.getProductId()).set(cartItem);
    }

    public LiveData<List<CartItem>> getCartItems(String userId) {
        MutableLiveData<List<CartItem>> liveData = new MutableLiveData<>();
        db.collection("users").document(userId)
                .collection("cartItems")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        liveData.postValue(null);
                        return;
                    }
                    if (snapshots != null) {
                        List<CartItem> items = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            items.add(doc.toObject(CartItem.class));
                        }
                        liveData.postValue(items);
                    }
                });
        return liveData;
    }

    public Task<Void> updateCartItemQuantity(String userId, String productId, int newQuantity) {
        DocumentReference docRef = db.collection("users").document(userId)
                .collection("cartItems").document(productId);
        return docRef.update("quantity", newQuantity);
    }

    public Task<Void> removeCartItem(String userId, String productId) {
        return db.collection("users").document(userId)
                .collection("cartItems").document(productId).delete();
    }

    public Task<Void> clearCart(String userId) {
        return db.collection("users").document(userId).collection("cartItems").get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> deleteTasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            deleteTasks.add(document.getReference().delete());
                        }
                        return com.google.android.gms.tasks.Tasks.whenAll(deleteTasks);
                    } else {
                        return com.google.android.gms.tasks.Tasks.forException(task.getException());
                    }
                });
    }

    // Contoh metode untuk Product (dari ProductRepository)
    public LiveData<List<Product>> getProducts() {
        MutableLiveData<List<Product>> liveData = new MutableLiveData<>();
        db.collection("products")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        liveData.postValue(null);
                        return;
                    }
                    if (snapshots != null) {
                        List<Product> products = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            products.add(doc.toObject(Product.class));
                        }
                        liveData.postValue(products);
                    }
                });
        return liveData;
    }

    public LiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();
        db.collection("products").document(productId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        liveData.postValue(null);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        liveData.postValue(documentSnapshot.toObject(Product.class));
                    } else {
                        liveData.postValue(null);
                    }
                });
        return liveData;
    }

    // Contoh metode untuk Notification (dari NotificationRepository)
    public LiveData<List<Notification>> getNotifications(String userId) {
        MutableLiveData<List<Notification>> liveData = new MutableLiveData<>();
        db.collection("users").document(userId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        liveData.postValue(null);
                        return;
                    }
                    if (snapshots != null) {
                        List<Notification> notifications = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            notifications.add(doc.toObject(Notification.class));
                        }
                        liveData.postValue(notifications);
                    }
                });
        return liveData;
    }

    public Task<Void> markNotificationAsRead(String userId, String notificationId) {
        return db.collection("users").document(userId)
                .collection("notifications").document(notificationId)
                .update("read", true);
    }

    // Contoh metode untuk Review (dari ReviewRepository)
    public Task<Void> submitReview(Review review) {
        return db.collection("reviews").document(review.getId()).set(review);
    }

    public LiveData<List<Review>> getProductReviews(String productId) {
        MutableLiveData<List<Review>> liveData = new MutableLiveData<>();
        db.collection("reviews")
                .whereEqualTo("productId", productId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        liveData.postValue(null);
                        return;
                    }
                    if (snapshots != null) {
                        List<Review> reviews = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            reviews.add(doc.toObject(Review.class));
                        }
                        liveData.postValue(reviews);
                    }
                });
        return liveData;
    }
}
