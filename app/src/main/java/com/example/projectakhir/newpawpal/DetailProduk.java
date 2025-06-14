package com.example.projectakhir.newpawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projectakhir.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProduk extends AppCompatActivity {

    private static final String TAG = "DetailProdukActivity";

    private ImageView backButton;
    private ImageView ivProductImageDetail;
    private TextView tvProductNameDetail;
    private TextView tvProductPriceDetail;
    private TextView tvProductRatingDetail;
    private TextView tvProductDescriptionDetail;
    private TextView tvProductStockDetail;
    private Button btnAddToCartDetail;
    private Button btnLihatSemuaPenilaian; // Tombol untuk melihat semua penilaian

    private String productId; // ID produk yang akan ditampilkan
    private Product currentProduct; // Objek produk saat ini

    private FirebaseDatabase database;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);

        // Inisialisasi UI
        backButton = findViewById(R.id.backButton);
        ivProductImageDetail = findViewById(R.id.ivProductImageDetail);
        tvProductNameDetail = findViewById(R.id.tvProductNameDetail);
        tvProductPriceDetail = findViewById(R.id.tvProductPriceDetail);
        tvProductRatingDetail = findViewById(R.id.tvProductRatingDetail);
        tvProductDescriptionDetail = findViewById(R.id.tvProductDescriptionDetail);
        tvProductStockDetail = findViewById(R.id.tvProductStockDetail);
        btnAddToCartDetail = findViewById(R.id.btnAddToCartDetail);
        btnLihatSemuaPenilaian = findViewById(R.id.btnLihatSemuaPenilaian);

        // Mengambil ID produk dari Intent
        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Toast.makeText(this, "ID produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish(); // Tutup activity jika ID tidak ada
            return;
        }

        database = FirebaseDatabase.getInstance();
        productRef = database.getReference("products").child(productId); // Referensi ke produk spesifik

        // Mengatur listener untuk tombol kembali
        backButton.setOnClickListener(v -> onBackPressed());

        // Mengambil detail produk dari Firebase
        fetchProductDetails();

        // Mengatur listener untuk tombol "Tambahkan ke Keranjang"
        btnAddToCartDetail.setOnClickListener(v -> {
            if (currentProduct != null) {
                // Logika tambahkan ke keranjang. Anda mungkin ingin meneruskan
                // objek currentProduct ke Activity Keranjang atau menyimpannya di SharedPrefs/Firebase
                Toast.makeText(DetailProduk.this, "Menambahkan " + currentProduct.getName() + " ke keranjang", Toast.LENGTH_SHORT).show();
            }
        });

        // Mengatur listener untuk tombol "Lihat Semua Penilaian"
        btnLihatSemuaPenilaian.setOnClickListener(v -> {
            if (currentProduct != null && currentProduct.getId() != null) {
                Intent intent = new Intent(DetailProduk.this, PenilaianProduk.class);
                intent.putExtra("product_id", currentProduct.getId()); // Teruskan ID produk
                startActivity(intent);
            } else {
                Toast.makeText(DetailProduk.this, "Detail produk tidak lengkap untuk melihat penilaian.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductDetails() {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(Product.class);
                if (currentProduct != null) {
                    currentProduct.setId(dataSnapshot.getKey()); // Set ID dari kunci snapshot
                    displayProductDetails(currentProduct);
                    Log.d(TAG, "Product details fetched successfully for ID: " + productId);
                } else {
                    Toast.makeText(DetailProduk.this, "Produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Produk tidak ditemukan untuk ID: " + productId);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailProduk.this, "Gagal memuat detail produk: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load product details: " + databaseError.getMessage());
                finish();
            }
        });
    }

    private void displayProductDetails(Product product) {
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(ivProductImageDetail);

        tvProductNameDetail.setText(product.getName());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        tvProductPriceDetail.setText(formatRupiah.format(product.getPrice()));

        // Rating
        if (product.getRating() > 0) {
            tvProductRatingDetail.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));
        } else {
            tvProductRatingDetail.setText("Belum ada rating"); // Atau sembunyikan TextView
        }

        tvProductDescriptionDetail.setText(product.getDescription());
        tvProductStockDetail.setText("Stok: " + product.getStock());

        // Sesuaikan tampilan tombol keranjang jika stok habis atau 0
        if (product.getStock() <= 0) {
            btnAddToCartDetail.setText("Stok Habis");
            btnAddToCartDetail.setEnabled(false);
            btnAddToCartDetail.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            btnAddToCartDetail.setText("Tambahkan ke Keranjang");
            btnAddToCartDetail.setEnabled(true);
            btnAddToCartDetail.setBackgroundColor(getResources().getColor(R.color.teal_primary)); // Sesuaikan dengan warna Anda
        }
    }
}
