package com.example.projectakhir.data.source;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirestoreSource {
    private final FirebaseFirestore firestore;
    private static final String USERS_COLLECTION = "users";
    private static final String PRODUCTS_COLLECTION = "products";
    private static final String ORDERS_COLLECTION = "orders";
    private static final String CART_COLLECTION = "cart";

    public FirestoreSource() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentSnapshot> getUserDocument(String userId) {
        return firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get();
    }

    public Task<QuerySnapshot> getProducts() {
        return firestore.collection(PRODUCTS_COLLECTION)
                .get();
    }

    public Task<DocumentSnapshot> getProduct(String productId) {
        return firestore.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .get();
    }

    public Task<QuerySnapshot> getCartItems(String userId) {
        return firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .get();
    }

    public Task<Void> addToCart(String userId, String productId, int quantity) {
        DocumentReference cartRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .document(productId);

        return cartRef.set(new CartItem(productId, quantity));
    }

    public Task<Void> updateCartItemQuantity(String userId, String productId, int quantity) {
        return firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .document(productId)
                .update("quantity", quantity);
    }

    public Task<Void> removeFromCart(String userId, String productId) {
        return firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CART_COLLECTION)
                .document(productId)
                .delete();
    }

    public Task<DocumentReference> createOrder(String userId, Order order) {
        return firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(ORDERS_COLLECTION)
                .add(order);
    }

    public Task<QuerySnapshot> getUserOrders(String userId) {
        return firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(ORDERS_COLLECTION)
                .get();
    }

    // CartItem class for Firestore
    public static class CartItem {
        private String productId;
        private int quantity;

        public CartItem() {
            // Required empty constructor for Firestore
        }

        public CartItem(String productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    // Order class for Firestore
    public static class Order {
        private String orderId;
        private String userId;
        private double totalAmount;
        private String status;
        private long timestamp;

        public Order() {
            // Required empty constructor for Firestore
        }

        public Order(String orderId, String userId, double totalAmount, String status) {
            this.orderId = orderId;
            this.userId = userId;
            this.totalAmount = totalAmount;
            this.status = status;
            this.timestamp = System.currentTimeMillis();
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
} 