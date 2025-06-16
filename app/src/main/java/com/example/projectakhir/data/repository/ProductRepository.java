package com.example.projectakhir.data.repository;

import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.FirestoreSource;
import com.example.projectakhir.data.model.Product;
import java.util.List;

public class ProductRepository {
    private FirestoreSource firestoreSource;

    public ProductRepository() {
        firestoreSource = new FirestoreSource();
    }

    public LiveData<List<Product>> getProducts() {
        return firestoreSource.getProducts();
    }

    public LiveData<Product> getProductById(String productId) {
        return firestoreSource.getProductById(productId);
    }
}