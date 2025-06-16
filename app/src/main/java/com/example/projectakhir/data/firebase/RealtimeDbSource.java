package com.example.projectakhir.data.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectakhir.data.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RealtimeDbSource {

    private DatabaseReference productsRef;

    public RealtimeDbSource() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");
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
                Product product = snapshot.getValue(Product.class);
                productLiveData.setValue(product);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                productLiveData.setValue(null);
            }
        });
        return productLiveData;
    }

    // Metode untuk menambahkan produk (jika admin ingin menambahkan)
    public void addProduct(Product product) {
        productsRef.child(product.getId()).setValue(product);
    }
}