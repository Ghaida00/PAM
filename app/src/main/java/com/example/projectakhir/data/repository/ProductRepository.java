package com.example.projectakhir.data.repository;

import androidx.lifecycle.LiveData;
import com.example.projectakhir.data.firebase.RealtimeDbSource;
import com.example.projectakhir.data.model.Product;
import java.util.List;

public class ProductRepository {
    private RealtimeDbSource realtimeDbSource;

    public ProductRepository() {
        realtimeDbSource = new RealtimeDbSource();
    }

    public LiveData<List<Product>> getProducts() {
        return realtimeDbSource.getProducts();
    }

    public LiveData<Product> getProductById(String productId) {
        return realtimeDbSource.getProductById(productId);
    }
}
