package com.example.projectakhir.newpawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projectakhir.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Katalog extends AppCompatActivity implements ProductAdapter.OnItemClickListener, CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "KatalogActivity";

    private EditText etSearch;
    private ImageView cartIcon;
    private ImageView notificationIcon;
    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private BottomNavigationView bottomNavigationView;

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    private FirebaseDatabase database;
    private DatabaseReference productsRef;
    private Map<String, Integer> cartQuantities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        Log.d(TAG, "KatalogActivity onCreate: Activity started.");

        etSearch = findViewById(R.id.etSearch);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        cartIcon = findViewById(R.id.iconCart);
        notificationIcon = findViewById(R.id.iconNotification);

        database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        cartQuantities = new HashMap<>();

        categoryList = new ArrayList<>();
        categoryList.add(new Category(getString(R.string.category_all), true));
        categoryList.add(new Category(getString(R.string.category_body_care), false));
        categoryList.add(new Category(getString(R.string.category_accessories), false));
        categoryList.add(new Category("Makanan", false));
        categoryList.add(new Category("Mainan", false));

        categoryAdapter = new CategoryAdapter(categoryList, this);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);

        Log.d(TAG, "onCreate: Memulai fetchProductsFromRealtimeDatabase()");
        fetchProductsFromRealtimeDatabase();

        cartIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Katalog.this, Keranjang.class);
            startActivity(intent);
        });

        notificationIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Katalog.this, Notifikasi.class);
            startActivity(intent);
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void fetchProductsFromRealtimeDatabase() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Dipanggil. DataSnapshot exists: " + dataSnapshot.exists() + ", Jumlah anak: " + dataSnapshot.getChildrenCount());

                productList.clear();
                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "onDataChange: Tidak ada data ditemukan di path 'products'.");
                    Toast.makeText(Katalog.this, "Tidak ada produk ditemukan.", Toast.LENGTH_LONG).show();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: Memproses child key: " + snapshot.getKey());
                    Product product = snapshot.getValue(Product.class);

                    if (product != null) {
                        String productId = snapshot.getKey();
                        if (productId != null) {
                            product.setId(productId);
                            productList.add(product);
                            Log.d(TAG, "onDataChange: Produk berhasil diurai: " + product.getName() + " (ID: " + productId + ")");
                        } else {
                            Log.e(TAG, "onDataChange: Product ID (snapshot key) is null for a child snapshot: " + snapshot.toString());
                        }
                    } else {
                        Log.e(TAG, "onDataChange: Gagal mengurai Product dari snapshot. Objek produk adalah NULL. Path: " + snapshot.getRef().getPath().toString() + ", Value: " + snapshot.getValue());
                        Toast.makeText(Katalog.this, "Gagal memuat beberapa produk. Cek Logcat.", Toast.LENGTH_SHORT).show();
                    }
                }
                productAdapter.setProducts(productList);
                Log.d(TAG, "Products fetched successfully. Final Count: " + productList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Gagal membaca data produk.", databaseError.toException());
                Toast.makeText(Katalog.this, "Gagal mengambil data produk: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onInitialAddToCartClick(Product product) {
        // Ini dipanggil saat ikon keranjang awal ditekan
        if (product != null && product.getId() != null) {
            int currentQuantity = cartQuantities.getOrDefault(product.getId(), 0);
            if (currentQuantity == 0) {
                // Saat pertama kali klik ikon keranjang, tambahkan 1 dan tampilkan kontrol
                cartQuantities.put(product.getId(), 1); // Set kuantitas awal di keranjang
                productAdapter.updateProductQuantity(product.getId(), 1); // Update UI
                // Notifikasi keberhasilan akan muncul ketika tombol "Tambahkan ke Keranjang" ditekan
                // Toast.makeText(this, "Produk berhasil dimasukan keranjang", Toast.LENGTH_SHORT).show(); // Dihapus di sini
            } else {
                Toast.makeText(this, "Produk sudah ada di keranjang. Gunakan tombol +/- atau 'Tambahkan ke Keranjang'.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "onInitialAddToCartClick: Product atau Product ID adalah NULL.");
            Toast.makeText(this, "Tidak dapat menambahkan produk ke keranjang (ID tidak ditemukan).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddToCartFinalClick(Product product, int quantityToSet) {
        // Ini dipanggil saat tombol "Tambahkan ke Keranjang" yang muncul bersamaan dengan +/- ditekan
        // quantityToSet adalah kuantitas yang sudah diatur oleh user melalui +/-
        if (product != null && product.getId() != null) {
            if (quantityToSet > 0 && quantityToSet <= product.getStock()) {
                cartQuantities.put(product.getId(), quantityToSet); // Set kuantitas sesuai yang diinput user
                productAdapter.updateProductQuantity(product.getId(), quantityToSet); // Update UI (untuk memastikan konsistensi)
                Toast.makeText(this, "Produk berhasil dimasukan keranjang", Toast.LENGTH_SHORT).show(); // Notifikasi muncul di sini
            } else if (quantityToSet > product.getStock()) {
                Toast.makeText(this, "Stok " + product.getName() + " tidak mencukupi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Kuantitas tidak valid.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "onAddToCartFinalClick: Product atau Product ID adalah NULL.");
            Toast.makeText(this, "Tidak dapat menambahkan produk ke keranjang (ID tidak ditemukan).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddQuantityClick(Product product) {
        if (product != null && product.getId() != null) {
            int currentQuantity = cartQuantities.getOrDefault(product.getId(), 0);
            if (currentQuantity < product.getStock()) {
                int newQuantity = currentQuantity + 1;
                cartQuantities.put(product.getId(), newQuantity); // Update kuantitas di peta
                productAdapter.updateProductQuantity(product.getId(), newQuantity); // Update UI
                // Toast.makeText(this, "Kuantitas " + product.getName() + " bertambah: " + newQuantity, Toast.LENGTH_SHORT).show(); // Notifikasi dihapus di sini
            } else {
                Toast.makeText(this, "Stok " + product.getName() + " habis!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "onAddQuantityClick: Product atau Product ID adalah NULL.");
            Toast.makeText(this, "Tidak dapat menambah kuantitas (ID tidak ditemukan).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveQuantityClick(Product product) {
        if (product != null && product.getId() != null) {
            int currentQuantity = cartQuantities.getOrDefault(product.getId(), 0) ;
            if (currentQuantity > 0) {
                int newQuantity = currentQuantity - 1;
                cartQuantities.put(product.getId(), newQuantity); // Update kuantitas di peta
                productAdapter.updateProductQuantity(product.getId(), newQuantity); // Update UI
                // Toast.makeText(this, "Kuantitas " + product.getName() + " berkurang: " + newQuantity, Toast.LENGTH_SHORT).show(); // Notifikasi dihapus di sini
            }
            if (currentQuantity - 1 == 0) { // Jika kuantitas menjadi 0, hapus dari peta dan ubah kembali ke ikon awal
                cartQuantities.remove(product.getId());
                productAdapter.updateProductQuantity(product.getId(), 0); // Penting untuk merefresh tampilan ke ikon awal (kuantitas 0 akan memicu ini)
            }
        } else {
            Log.e(TAG, "onRemoveQuantityClick: Product atau Product ID adalah NULL.");
            Toast.makeText(this, "Tidak dapat mengurangi kuantitas (ID tidak ditemukan).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(Product product) {
        if (product != null && product.getId() != null) {
            Intent detailIntent = new Intent(Katalog.this, DetailProduk.class);
            detailIntent.putExtra("product_id", product.getId());
            startActivity(detailIntent);
        } else {
            Log.e(TAG, "onItemClick: Product atau Product ID adalah NULL.");
            Toast.makeText(this, "Tidak dapat membuka detail produk (ID tidak ditemukan).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCategoryClick(Category category, int position) {
        Toast.makeText(this, "Kategori dipilih: " + category.getName(), Toast.LENGTH_SHORT).show();
    }
}
